package com.contatodo.application.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO for a user.
 */
public class UserResponse {

    private String id;
    private String userName;
    private String email;
    private String name;
    private String phoneNumber;
    private RoleResponse role;
    private CompanyResponse company;
    private String companyOid;
    private String createdByUserOid;
    private boolean isActive;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public RoleResponse getRole() {
        return role;
    }

    public void setRole(RoleResponse role) {
        this.role = role;
    }

    public CompanyResponse getCompany() {
        return company;
    }

    public void setCompany(CompanyResponse company) {
        this.company = company;
    }

    public String getCompanyOid() {
        return companyOid;
    }

    public void setCompanyOid(String companyOid) {
        this.companyOid = companyOid;
    }

    public String getCreatedByUserOid() {
        return createdByUserOid;
    }

    public void setCreatedByUserOid(String createdByUserOid) {
        this.createdByUserOid = createdByUserOid;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}