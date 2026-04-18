package org.bighw1.big_hw_1.service;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;

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

    public void init(Path recipesPath, Path usersPath) throws ParserConfigurationException, SAXException, IOException {
        this.recipesPath = recipesPath;
        this.usersPath = usersPath;
        this.recipesDoc = xmlService.load(recipesPath);
        this.usersDoc = xmlService.load(usersPath);
    }

    public Document getRecipesDoc() { return recipesDoc; }
    public Document getUsersDoc() { return usersDoc; }
    public Path getRecipesPath() { return recipesPath; }
    public Path getUsersPath() { return usersPath; }
}
