package com.contatodo.application.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for creating a module.
 */
public class CreateModuleRequest {

    @NotBlank(message = "Module name is required")
    private String name;

    @NotBlank(message = "Module link is required")
    private String link;

    /**
     * Creates an empty create module request.
     */
    public CreateModuleRequest() {
    }

    /**
     * Creates a create module request with specified values.
     *
     * @param name Module name.
     * @param link Module link.
     */
    public CreateModuleRequest(String name, String link) {
        this.name = name;
        this.link = link;
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
