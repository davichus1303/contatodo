package com.contatodo.infrastructure.persistence.document;

/**
 * MongoDB document representing a module permission for a specific user.
 */
public class ModulePermissionDocument {

    private String userOid;
    private Boolean create;
    private Boolean update;
    private Boolean edit;

    /**
     * Creates an empty module permission document.
     */
    public ModulePermissionDocument() {
    }

    /**
     * Creates a module permission document with specified values.
     *
     * @param userOid User identifier.
     * @param create Create permission.
     * @param update Update permission.
     * @param edit Edit permission.
     */
    public ModulePermissionDocument(String userOid, Boolean create, Boolean update, Boolean edit) {
        this.userOid = userOid;
        this.create = create;
        this.update = update;
        this.edit = edit;
    }

    public String getUserOid() {
        return userOid;
    }

    public void setUserOid(String userOid) {
        this.userOid = userOid;
    }

    public Boolean getCreate() {
        return create;
    }

    public void setCreate(Boolean create) {
        this.create = create;
    }

    public Boolean getUpdate() {
        return update;
    }

    public void setUpdate(Boolean update) {
        this.update = update;
    }

    public Boolean getEdit() {
        return edit;
    }

    public void setEdit(Boolean edit) {
        this.edit = edit;
    }
}
