package org.bighw1.big_hw_1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.nio.file.Path;

// Holds the two DOM Documents in memory for the lifetime of the application.
// Services read and write through this shared state so changes are immediately visible.
@Component
public class XmlStore {

    private Document recipesDoc;
    private Document usersDoc;
    private Path recipesPath;
    private Path usersPath;

    @Autowired
    private XmlService xmlService;

    // Called once at startup by BigHw1Application after the data files exist on disk.
    public void init(Path recipesPath, Path usersPath) throws Exception {
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
