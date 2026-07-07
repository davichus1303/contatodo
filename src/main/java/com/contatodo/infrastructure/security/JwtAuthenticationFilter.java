package com.contatodo.infrastructure.security;

import com.contatodo.shared.constants.AuthConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT authentication filter.
 *
 * <p>This filter intercepts every incoming request, extracts the JWT token
 * from the Authorization header, validates it, retrieves the authenticated
 * user, and stores the authentication in the Spring Security context.</p>
 *
 * <p>Expected Authorization header:</p>
 *
 * <pre>
 * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
 * </pre>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    /**
     * Creates a JWT authentication filter.
     *
     * @param jwtService Service used to validate and decode JWT tokens.
     */
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Processes every HTTP request and authenticates the user if a valid JWT
     * token is present.
     *
     * <p>If the Authorization header is missing, malformed, or the token is
     * invalid, the request continues without authentication.</p>
     *
     * @param request Current HTTP request.
     * @param response Current HTTP response.
     * @param filterChain Remaining filter chain.
     * @throws ServletException If the filter cannot process the request.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authorizationHeader == null
                || !authorizationHeader.startsWith(BEARER_PREFIX)) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length());

        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtService.extractSubject(token);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        Collections.emptyList()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}