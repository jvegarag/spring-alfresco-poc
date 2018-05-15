package com.jvegarag.springalfresco.integration;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.InputStreamSource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.io.InputStream;

public class SessionFactory {

    private String serviceUrl = "http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser"; // Uncomment for Browser binding
    private Session session = null;
    private String user = "admin";
    private String password = "admin";
    private String folderName;


    public Session getSession() {

        if (session == null) {
            // default factory implementation
            org.apache.chemistry.opencmis.client.api.SessionFactory factory = SessionFactoryImpl.newInstance();
            Map<String, String> parameter = new HashMap<String, String>();

            // user credentials
            parameter.put(SessionParameter.USER, user);
            parameter.put(SessionParameter.PASSWORD, password);

            // connection settings
            //parameter.put(SessionParameter.ATOMPUB_URL, getServiceUrl()); // Uncomment for Atom Pub binding
//            parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value()); // Uncomment for Atom Pub binding

            parameter.put(SessionParameter.BROWSER_URL, serviceUrl); // Uncomment for Browser binding
            parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value()); // Uncomment for Browser binding

            // Set the alfresco object factory
            // Used when using the CMIS extension for Alfresco for working with aspects and CMIS 1.0
            // This is not needed when using CMIS 1.1
            //parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");

            List<Repository> repositories = factory.getRepositories(parameter);

            this.session = repositories.get(0).createSession();
        }
        return this.session;
    }


    /**
     * Gets the object ID for a folder of a specified name which is assumed to be unique across the
     * entire repository.
     *
     * @return String
     */
    public String getFolderId(String folderName) {
        String objectId = null;
        String queryString = "select * from cmis:folder where cmis:name = '" + folderName + "'";
        ItemIterable<QueryResult> results = getSession().query(queryString, false);
        for (QueryResult qResult : results) {
            objectId = qResult.getPropertyValueByQueryName("cmis:objectId");
        }
        return objectId;
    }


    public Folder getFolderByPath(String path) {
        return (Folder) getSession().getObjectByPath(path);
    }


    public void createDocument(com.jvegarag.springalfresco.integration.model.Document document, String mimeType, InputStream source, long length) {
        Folder parent = getFolderById(document.getParentFolderId());

        // TODO
        // Check if a document with the same name already exist and generate new version if so

        ContentStream contentStream = new ContentStreamImpl(document.getName(), BigInteger.valueOf(length), mimeType, source);
        parent.createDocument(document.getMetadata(), contentStream, VersioningState.MAJOR);
    }

    private Folder getFolderById(String id) {
        CmisObject object = id==null? getSession().getRootFolder(): getSession().getObject(id);
        if (!(object instanceof Folder)) {
            throw new IllegalArgumentException("The id entered is not a folder");
        }
        return (Folder) object;
    }


    public void createDocument(String parentFolder, String filePath) throws IOException {
        Folder parent = getFolderByPath("/invoices");
        String tokens[] = filePath.split("/");
        String name = tokens[tokens.length-1];

        // properties
        // (minimal set: name and object type id)
        Map<String, Object> properties = new HashMap<>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "D:sc:invoice");
        properties.put(PropertyIds.NAME, name);

        // content
        byte[] content = FileUtils.readFileToByteArray(new File(filePath));

//      byte[] content = "Hello World!".getBytes();

        InputStream stream = new ByteArrayInputStream(content);
        ContentStream contentStream = new ContentStreamImpl(name, BigInteger.valueOf(content.length), "image/png", stream);

        // create a major version
        Document newDoc = parent.createDocument(properties, contentStream, VersioningState.MAJOR);
    }


    public Map<String, List<PropertyData<?>>>getActive() {
        String queryString = "select * from sc:webable where sc:isActive = true";
        ItemIterable<QueryResult> results = getSession().query(queryString, false);
        return toPropertyDataFlatMap(results);
    }

    public static Map<String, List<PropertyData<?>>> toPropertyDataFlatMap(Iterable<QueryResult> iterable)
    {
        Map<String, List<PropertyData<?>>> map = new HashMap<>();
        iterable.forEach( i -> {
            String docId = i.getPropertyValueByQueryName("cmis:objectId");
            map.put(docId, i.getProperties());
        });
        return map;
    }

    public ContentStream getDocument(String documentId) {
        CmisObject object = getSession().getObject(documentId);

        if (!(object instanceof Document)) {
            throw new IllegalArgumentException("The object Id entered is not a document");
        }

        Document doc = (Document) object;
        return doc.getContentStream();
    }
}
