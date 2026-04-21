package org.bighw1.big_hw_1.service;

import org.bighw1.big_hw_1.model.Recipe;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RecipeService {

    private final XmlStore xmlStore;
    private final XmlService xmlService;

    public RecipeService(XmlStore xmlStore, XmlService xmlService) {
        this.xmlStore = xmlStore;
        this.xmlService = xmlService;
    }

    private Recipe nodeToRecipe(Node node) {
        Element el = (Element) node;
        String id = el.getAttribute("id");
        String title = el.getElementsByTagName("title").item(0).getTextContent().trim();
        NodeList ctNodes = el.getElementsByTagName("cuisineType");
        Node ct1Node = ctNodes.item(0);
        Node ct2Node = ctNodes.item(1);
        String ct1 = ct1Node != null ? ((Element) ct1Node).getAttribute("type") : "";
        String ct2 = ct2Node != null ? ((Element) ct2Node).getAttribute("type") : "";
        String difficulty = el.getAttribute("difficulty");
        return new Recipe(id, title, ct1, ct2, difficulty);
    }

    public List<Recipe> getAll() throws XPathExpressionException {
        NodeList nodes = xmlService.queryNodeList(xmlStore.getRecipesDoc(), "//recipe");
        List<Recipe> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            list.add(nodeToRecipe(nodes.item(i)));
        }
        return list;
    }

    public Recipe getById(String id) throws XPathExpressionException {
        Node node = xmlService.queryNode(
                xmlStore.getRecipesDoc(), "//recipe[@id='" + id + "']");
        if (node == null) return null;
        return nodeToRecipe(node);
    }

    public void add(String title, String ct1, String ct2, String difficulty)
            throws XPathExpressionException, TransformerException {
        String id = nextId();
        Document doc = xmlStore.getRecipesDoc();
        Element root = doc.getDocumentElement();

        Element recipeEl = doc.createElement("recipe");
        recipeEl.setAttribute("id", id);
        recipeEl.setAttribute("difficulty", difficulty);

        Element titleEl = doc.createElement("title");
        titleEl.setTextContent(title);
        recipeEl.appendChild(titleEl);

        Element cuisinesEl = doc.createElement("cuisineTypes");
        Element ct1El = doc.createElement("cuisineType");
        ct1El.setAttribute("type", ct1);
        cuisinesEl.appendChild(ct1El);
        Element ct2El = doc.createElement("cuisineType");
        ct2El.setAttribute("type", ct2);
        cuisinesEl.appendChild(ct2El);
        recipeEl.appendChild(cuisinesEl);

        root.appendChild(recipeEl);
        xmlService.save(doc, xmlStore.getRecipesPath());
    }

    public List<Recipe> filterByCuisine(String cuisine) throws XPathExpressionException {
        NodeList nodes = xmlService.queryNodeList(
                xmlStore.getRecipesDoc(),
                "//recipe[cuisineTypes/cuisineType/@type='" + cuisine + "']");
        List<Recipe> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            list.add(nodeToRecipe(nodes.item(i)));
        }
        return list;
    }

    public List<Recipe> recommendBySkill(String skillLevel) throws XPathExpressionException {
        NodeList nodes = xmlService.queryNodeList(
                xmlStore.getRecipesDoc(),
                "//recipe[@difficulty='" + skillLevel + "']");
        List<Recipe> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            list.add(nodeToRecipe(nodes.item(i)));
        }
        return list;
    }

    public List<Recipe> recommendBySkillAndCuisine(String skillLevel, String cuisine) throws XPathExpressionException {
        NodeList nodes = xmlService.queryNodeList(
                xmlStore.getRecipesDoc(),
                "//recipe[@difficulty='" + skillLevel + "' and cuisineTypes/cuisineType/@type='" + cuisine + "']");
        List<Recipe> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            list.add(nodeToRecipe(nodes.item(i)));
        }
        return list;
    }

    public void clearAll() throws TransformerException {
        Document doc = xmlStore.getRecipesDoc();
        Element root = doc.getDocumentElement();
        while (root.hasChildNodes()) {
            root.removeChild(root.getFirstChild());
        }
        xmlService.save(doc, xmlStore.getRecipesPath());
    }

    public String applyDisplayXslt(String skillLevel) throws TransformerException {
        InputStream xsl = getClass().getClassLoader().getResourceAsStream("xslt/recipes-display.xsl");
        return xmlService.applyXslt(xmlStore.getRecipesDoc(), xsl, Map.of("skill-level", skillLevel));
    }

    private String nextId() throws XPathExpressionException {
        NodeList nodes = xmlService.queryNodeList(xmlStore.getRecipesDoc(), "//recipe");
        int max = 0;
        for (int i = 0; i < nodes.getLength(); i++) {
            String idVal = ((Element) nodes.item(i)).getAttribute("id");
            try {
                max = Math.max(max, Integer.parseInt(idVal.substring(1)));
            } catch (NumberFormatException | IndexOutOfBoundsException ignored) {}
        }
        return "r" + (max + 1);
    }
}
