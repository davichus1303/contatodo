package com.contatodo.infrastructure.security;

import com.contatodo.application.port.CompanyContextProvider;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.shared.constants.AuthConstants;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * {@link CompanyContextProvider} implementation that resolves the company
 * context from the JWT claims of the current request.
 */
@Component
public class JwtCompanyContextProvider implements CompanyContextProvider {

    private final JwtService jwtService;

    /**
     * Creates a JWT company context provider.
     *
     * @param jwtService Service used to read the current token claims.
     */
    public JwtCompanyContextProvider(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<CompanyOid> currentCompanyOid() {
        String companyOid = jwtService.getClaim(AuthConstants.JWT_CLAIM_COMPANY_OID);
        return companyOid != null
                ? Optional.of(CompanyOid.of(companyOid))
                : Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRoot() {
        return currentCompanyOid().isEmpty()
                && AuthConstants.ROOT_ROLE_CLAIM.equals(jwtService.getClaim(AuthConstants.JWT_CLAIM_ROLE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}