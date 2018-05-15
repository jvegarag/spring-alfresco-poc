package com.jvegarag.springalfresco.rest;

import com.jvegarag.springalfresco.integration.SessionFactory;
import com.jvegarag.springalfresco.integration.model.Document;
import com.jvegarag.springalfresco.rest.model.DocumentDto;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimeType;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="documents",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentController {

    @Autowired
    private ModelMapper mapper;

    @GetMapping(path="/")
    @ResponseBody
    public void list(@RequestParam(name="folder") String folderName) throws IOException {
        SessionFactory sessionFactory = new SessionFactory();

        sessionFactory.createDocument("/invoices", "/home/joaquin/dev/pisf-data/fotos_arcas/ultimo_david/ALFA.png");
    }

    @GetMapping(path = "/{id}", consumes = MediaType.ALL_VALUE)
    public HttpEntity<Resource> retrieve(@PathVariable(name="id") String documentId) {
        ContentStream content = new SessionFactory().getDocument(documentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(content.getMimeType()));
        headers.setContentDisposition(ContentDisposition.builder("inline").filename(content.getFileName()).build());
        return new HttpEntity<>(new InputStreamResource(content.getStream()), headers);
    }

    @PostMapping(path="/", consumes = "multipart/mixed")
    public void create(
            @RequestPart DocumentDto document,
            @RequestPart MultipartFile file) throws IOException {
        SessionFactory sessionFactory = new SessionFactory();

        Document doc = mapper.map(document, Document.class);
        sessionFactory.createDocument(doc, file.getContentType(), file.getInputStream(), file.getSize());
    }

    @GetMapping(path="/searchByCriteria")
    @ResponseBody
    public Map<String, List<PropertyData<?>>> searchFolder(@RequestParam(name="name") String name) {
        SessionFactory sessionFactory = new SessionFactory();
        return sessionFactory.getActive();
    }

}
