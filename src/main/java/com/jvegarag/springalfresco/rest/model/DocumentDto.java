package com.jvegarag.springalfresco.rest.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DocumentDto implements Serializable {

    private String name;
    private String type;
    private String parentFolderId;
    private MetadataDto metadata = new MetadataDto();

}
