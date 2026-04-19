package org.bighw1.big_hw_1.service;

import org.bighw1.big_hw_1.model.User;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final XmlStore xmlStore;
    private final XmlService xmlService;

    public UserService(XmlStore xmlStore, XmlService xmlService) {
        this.xmlStore = xmlStore;
        this.xmlService = xmlService;
    }

    private User nodeToUser(Node node) {
        Element el = (Element) node;
        String id = el.getAttribute("id");
        String name = el.getElementsByTagName("name").item(0).getTextContent().trim();
        String surname = el.getElementsByTagName("surname").item(0).getTextContent().trim();
        String skillLevel = el.getAttribute("skillLevel");
        String preferredCuisine = el.getAttribute("preferredCuisine");
        return new User(id, name, surname, skillLevel, preferredCuisine);
    }

    public List<User> getAll() throws XPathExpressionException {
        NodeList nodes = xmlService.queryNodeList(xmlStore.getUsersDoc(), "//user");
        List<User> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            list.add(nodeToUser(nodes.item(i)));
        }
        return list;
    }

    public User getFirstUser() throws XPathExpressionException {
        Node node = xmlService.queryNode(xmlStore.getUsersDoc(), "//user[1]");
        if (node == null) return null;
        return nodeToUser(node);
    }

    public User getById(String id) throws XPathExpressionException {
        Node node = xmlService.queryNode(xmlStore.getUsersDoc(), "//user[@id=$id]", Map.of("id", id));
        if (node == null) return null;
        return nodeToUser(node);
    }

    public void clearAll() throws TransformerException {
        Document doc = xmlStore.getUsersDoc();
        Element root = doc.getDocumentElement();
        while (root.hasChildNodes()) {
            root.removeChild(root.getFirstChild());
        }
        xmlService.save(doc, xmlStore.getUsersPath());
    }

    public void seedDefaultUsers() throws XPathExpressionException, TransformerException {
        if (!getAll().isEmpty()) return;
        addUser(new User("u1", "Abd", "Mirghani", "Intermediate", "Middle-Eastern"));
        addUser(new User("u2", "Mario", "Sakka", "Beginner", "Italian"));
    }

    public void addUser(User user) throws TransformerException {
        Document doc = xmlStore.getUsersDoc();
        Element root = doc.getDocumentElement();

        Element userEl = doc.createElement("user");
        userEl.setAttribute("id", user.getId());
        userEl.setAttribute("skillLevel", user.getSkillLevel());
        userEl.setAttribute("preferredCuisine", user.getPreferredCuisine());

        Element nameEl = doc.createElement("name");
        nameEl.setTextContent(user.getName());
        userEl.appendChild(nameEl);

        Element surnameEl = doc.createElement("surname");
        surnameEl.setTextContent(user.getSurname());
        userEl.appendChild(surnameEl);

        root.appendChild(userEl);
        xmlService.save(doc, xmlStore.getUsersPath());
    }
}
