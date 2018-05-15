package com.jvegarag.springalfresco.integration.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Document {

    private String name;
    private String type;
    private String parentFolderId;

    private Map<String, ?> metadata = new HashMap<>();
}
