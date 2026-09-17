package com.contatodo.application.dto.request;

/**
 * Request DTO for creating a user.
 */
public class CreateUserRequest {

    private String userName;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private String roleId;
    private String createdByUserOid;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getCreatedByUserOid() {
        return createdByUserOid;
    }

    public void setCreatedByUserOid(String createdByUserOid) {
        this.createdByUserOid = createdByUserOid;
    }
}
