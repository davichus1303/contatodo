package com.contatodo.application.dto.response;

/**
 * Response DTO for a module.
 */
public class ModuleResponse {

    private String id;
    private String name;
    private String link;

    /**
     * Creates an empty module response.
     */
    public ModuleResponse() {
    }

    /**
     * Creates a module response with specified values.
     *
     * @param id Module identifier.
     * @param name Module name.
     * @param link Module link.
     */
    public ModuleResponse(String id, String name, String link) {
        this.id = id;
        this.name = name;
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
