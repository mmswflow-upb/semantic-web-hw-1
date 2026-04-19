package org.bighw1.big_hw_1.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class XmlStore {

    private Document recipesDoc;
    private Document usersDoc;
    private Path recipesPath;
    private Path usersPath;

    private final XmlService xmlService;

    public XmlStore(XmlService xmlService) {
        this.xmlService = xmlService;
    }

    @PostConstruct
    public void init() {
        recipesPath = Paths.get("src/main/resources/data/recipes.xml");
        usersPath   = Paths.get("src/main/resources/data/users.xml");
        try {
            recipesDoc = xmlService.load(recipesPath);
            usersDoc   = xmlService.load(usersPath);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalStateException("Failed to load XML data files", e);
        }
    }

    public Document getRecipesDoc() { return recipesDoc; }
    public Document getUsersDoc()   { return usersDoc; }
    public Path getRecipesPath()    { return recipesPath; }
    public Path getUsersPath()      { return usersPath; }
}
