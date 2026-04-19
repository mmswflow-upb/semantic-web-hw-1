package org.bighw1.big_hw_1.controller;

import org.bighw1.big_hw_1.enums.CuisineType;
import org.bighw1.big_hw_1.enums.DifficultyLevel;
import org.bighw1.big_hw_1.model.Recipe;
import org.bighw1.big_hw_1.model.User;
import org.bighw1.big_hw_1.service.RecipeService;
import org.bighw1.big_hw_1.service.ScraperService;
import org.bighw1.big_hw_1.service.UserService;
import org.bighw1.big_hw_1.service.XmlService;
import org.bighw1.big_hw_1.service.XmlStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Controller
public class RecipeController {

    private final RecipeService recipeService;
    private final UserService userService;
    private final XmlService xmlService;
    private final XmlStore xmlStore;
    private final ScraperService scraper;

    public RecipeController(RecipeService recipeService, UserService userService,
                            XmlService xmlService, XmlStore xmlStore,
                            ScraperService scraper) {
        this.recipeService = recipeService;
        this.userService = userService;
        this.xmlService = xmlService;
        this.xmlStore = xmlStore;
        this.scraper = scraper;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/recipes")
    public String listRecipes(Model model) throws XPathExpressionException {
        model.addAttribute("recipes", recipeService.getAll());
        return "recipes/list";
    }

    @GetMapping("/recipes/display")
    public String displayForm(Model model) throws XPathExpressionException {
        model.addAttribute("users", userService.getAll());
        model.addAttribute("selectedUserId", "");
        model.addAttribute("xsltHtml", "");
        return "recipes/display";
    }

    @PostMapping("/recipes/display")
    public String displayResult(@RequestParam("userId") String userId, Model model)
            throws XPathExpressionException, TransformerException {
        List<User> users = userService.getAll();
        model.addAttribute("users", users);
        model.addAttribute("selectedUserId", userId);

        String skillLevel = users.stream()
                .filter(u -> u.getId().equals(userId))
                .map(User::getSkillLevel)
                .findFirst()
                .orElse(DifficultyLevel.BEGINNER.toString());

        InputStream xsl = getClass().getClassLoader().getResourceAsStream("xslt/recipes-display.xsl");
        String html = xmlService.applyXslt(xmlStore.getRecipesDoc(), xsl, Map.of("skill-level", skillLevel));
        model.addAttribute("xsltHtml", html);

        return "recipes/display";
    }

    @GetMapping("/recipes/add")
    public String addForm(Model model) {
        model.addAttribute("cuisineTypes", CuisineType.values());
        model.addAttribute("difficulties", DifficultyLevel.values());
        model.addAttribute("errorMsg", "");
        return "recipes/add";
    }

    @PostMapping("/recipes/add")
    public String addSubmit(
            @RequestParam("title") String title,
            @RequestParam("cuisineType1") String ct1,
            @RequestParam("cuisineType2") String ct2,
            @RequestParam("difficulty") String difficulty,
            Model model) throws XPathExpressionException, TransformerException {

        String error = validate(title, ct1, ct2, difficulty);
        if (error != null) {
            model.addAttribute("cuisineTypes", CuisineType.values());
            model.addAttribute("difficulties", DifficultyLevel.values());
            model.addAttribute("errorMsg", error);
            model.addAttribute("title", title);
            model.addAttribute("cuisineType1", ct1);
            model.addAttribute("cuisineType2", ct2);
            model.addAttribute("difficulty", difficulty);
            return "recipes/add";
        }

        int nextId = recipeService.getAll().size() + 1;
        String id = "r" + nextId;
        recipeService.add(new Recipe(id, title, ct1, ct2, difficulty));
        return "redirect:/recipes";
    }

    @GetMapping("/recipes/{id}")
    public String recipeDetail(@PathVariable("id") String id, Model model) throws XPathExpressionException {
        Recipe recipe = recipeService.getById(id);
        if (recipe == null) {
            return "redirect:/recipes";
        }
        model.addAttribute("recipe", recipe);
        return "recipes/detail";
    }

    @GetMapping("/recipes/cuisine")
    public String cuisineForm(Model model) {
        model.addAttribute("cuisineTypes", CuisineType.values());
        model.addAttribute("selectedCuisine", "");
        model.addAttribute("recipes", List.of());
        return "recipes/cuisine";
    }

    @PostMapping("/recipes/cuisine")
    public String cuisineFilter(@RequestParam("cuisine") String cuisine, Model model)
            throws XPathExpressionException {
        model.addAttribute("cuisineTypes", CuisineType.values());
        model.addAttribute("selectedCuisine", cuisine);
        model.addAttribute("recipes", recipeService.filterByCuisine(cuisine));
        return "recipes/cuisine";
    }

    @PostMapping("/admin/clear")
    public String clearAll() throws TransformerException {
        recipeService.clearAll();
        userService.clearAll();
        return "redirect:/";
    }

    @PostMapping("/admin/seed")
    public String seed() throws Exception {
        scraper.scrapeIfNeeded();
        userService.seedDefaultUsers();
        return "redirect:/";
    }

    private String validate(String title, String ct1, String ct2, String difficulty) {
        if (title == null || title.isBlank()) return "Title is required.";
        if (ct1 == null || ct1.isBlank() || !CuisineType.contains(ct1)) return "Select a valid cuisine type 1.";
        if (ct2 == null || ct2.isBlank() || !CuisineType.contains(ct2)) return "Select a valid cuisine type 2.";
        if (ct1.equals(ct2)) return "The two cuisine types must be different.";
        if (difficulty == null || difficulty.isBlank() || !DifficultyLevel.contains(difficulty)) return "Select a valid difficulty.";
        return null;
    }
}
