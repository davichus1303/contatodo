package com.contatodo.application.port;

import com.contatodo.domain.model.CompanyOid;

import java.util.Optional;

/**
 * Provides the company context of the current authenticated user.
 *
 * <p>For a normal user the company identifier comes from the JWT and all
 * reads/writes must be filtered by it. For the root user (role {@code ROOT}
 * without a {@code companyOid} claim) no company filter is applied and the
 * user sees all companies.</p>
 */
public interface CompanyContextProvider {

    /**
     * Returns the current user's company identifier.
     *
     * @return Empty when the current user is root (no company filter).
     */
    Optional<CompanyOid> currentCompanyOid();

    /**
     * Returns whether the current user is root.
     *
     * @return True when the current user has role {@code ROOT} and no company.
     */
    boolean isRoot();

    /**
     * Returns whether the current request carries an authenticated user.
     *
     * <p>Used to tell a real company user without a company claim (which must
     * be denied) apart from internal or test flows with no security context
     * (which keep working unfiltered).</p>
     *
     * @return True when there is an authenticated, non-anonymous user.
     */
    boolean isAuthenticated();
}