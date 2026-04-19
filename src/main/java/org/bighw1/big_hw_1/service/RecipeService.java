package org.bighw1.big_hw_1.service;

import org.bighw1.big_hw_1.model.Recipe;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
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
        String ct1 = ((Element) ctNodes.item(0)).getAttribute("type");
        String ct2 = ((Element) ctNodes.item(1)).getAttribute("type");
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
                xmlStore.getRecipesDoc(), "//recipe[@id=$id]", Map.of("id", id));
        if (node == null) return null;
        return nodeToRecipe(node);
    }

    public void add(Recipe recipe) throws TransformerException {
        Document doc = xmlStore.getRecipesDoc();
        Element root = doc.getDocumentElement();

        Element recipeEl = doc.createElement("recipe");
        recipeEl.setAttribute("id", recipe.getId());
        recipeEl.setAttribute("difficulty", recipe.getDifficulty());

        Element titleEl = doc.createElement("title");
        titleEl.setTextContent(recipe.getTitle());
        recipeEl.appendChild(titleEl);

        Element cuisinesEl = doc.createElement("cuisineTypes");
        Element ct1El = doc.createElement("cuisineType");
        ct1El.setAttribute("type", recipe.getCuisineType1());
        cuisinesEl.appendChild(ct1El);
        Element ct2El = doc.createElement("cuisineType");
        ct2El.setAttribute("type", recipe.getCuisineType2());
        cuisinesEl.appendChild(ct2El);
        recipeEl.appendChild(cuisinesEl);

        root.appendChild(recipeEl);
        xmlService.save(doc, xmlStore.getRecipesPath());
    }

    public List<Recipe> filterByCuisine(String cuisine) throws XPathExpressionException {
        NodeList nodes = xmlService.queryNodeList(
                xmlStore.getRecipesDoc(),
                "//recipe[cuisineTypes/cuisineType/@type=$cuisine]",
                Map.of("cuisine", cuisine));
        List<Recipe> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            list.add(nodeToRecipe(nodes.item(i)));
        }
        return list;
    }

    public List<Recipe> recommendBySkill(String skillLevel) throws XPathExpressionException {
        NodeList nodes = xmlService.queryNodeList(
                xmlStore.getRecipesDoc(),
                "//recipe[@difficulty=$skill]",
                Map.of("skill", skillLevel));
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

    public List<Recipe> recommendBySkillAndCuisine(String skillLevel, String cuisine) throws XPathExpressionException {
        NodeList nodes = xmlService.queryNodeList(
                xmlStore.getRecipesDoc(),
                "//recipe[@difficulty=$skill and cuisineTypes/cuisineType/@type=$cuisine]",
                Map.of("skill", skillLevel, "cuisine", cuisine));
        List<Recipe> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            list.add(nodeToRecipe(nodes.item(i)));
        }
        return list;
    }
}
