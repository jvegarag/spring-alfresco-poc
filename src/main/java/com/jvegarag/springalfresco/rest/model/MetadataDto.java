package com.jvegarag.springalfresco.rest.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/*
 * // Check CMIS specification Property Definitions
 */
@Data
public class MetadataDto extends HashMap<String, Object> implements Serializable {

//    @JsonUnwrapped
//    Map<String, Object> meta = new HashMap<>();

}
