package org.bighw1.big_hw_1.controller;

import org.bighw1.big_hw_1.enums.CuisineType;
import org.bighw1.big_hw_1.enums.DifficultyLevel;
import org.bighw1.big_hw_1.model.User;
import org.bighw1.big_hw_1.service.RecipeService;
import org.bighw1.big_hw_1.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    private final RecipeService recipeService;

    public UserController(UserService userService, RecipeService recipeService) {
        this.userService = userService;
        this.recipeService = recipeService;
    }

    @GetMapping("/users/add")
    public String addForm(Model model) {
        model.addAttribute("cuisineTypes", CuisineType.values());
        model.addAttribute("skillLevels", DifficultyLevel.values());
        model.addAttribute("errorMsg", "");
        return "users/add";
    }

    @PostMapping("/users/add")
    public String addSubmit(
            @RequestParam("name") String name,
            @RequestParam("surname") String surname,
            @RequestParam("skillLevel") String skillLevel,
            @RequestParam("preferredCuisine") String preferredCuisine,
            Model model) throws XPathExpressionException, TransformerException {

        String error = validate(name, surname, skillLevel, preferredCuisine);
        if (error != null) {
            model.addAttribute("cuisineTypes", CuisineType.values());
            model.addAttribute("skillLevels", DifficultyLevel.values());
            model.addAttribute("errorMsg", error);
            model.addAttribute("name", name);
            model.addAttribute("surname", surname);
            model.addAttribute("skillLevel", skillLevel);
            model.addAttribute("preferredCuisine", preferredCuisine);
            return "users/add";
        }

        userService.addUser(name, surname, skillLevel, preferredCuisine);
        return "redirect:/recommendations";
    }

    @GetMapping("/recommendations")
    public String recommendations(Model model) throws XPathExpressionException {
        List<User> users = userService.getAll();
        model.addAttribute("users", users);
        User user = userService.getFirstUser();
        model.addAttribute("selectedUserId", user != null ? user.getId() : "");
        populateRecommendations(user, model);
        return "users/recommendations";
    }

    @PostMapping("/recommendations")
    public String recommendationsPost(@RequestParam("userId") String userId, Model model)
            throws XPathExpressionException {
        List<User> users = userService.getAll();
        model.addAttribute("users", users);
        model.addAttribute("selectedUserId", userId);
        populateRecommendations(userService.getById(userId), model);
        return "users/recommendations";
    }

    private void populateRecommendations(User user, Model model) throws XPathExpressionException {
        if (user == null) {
            model.addAttribute("user", null);
            model.addAttribute("bySkill", List.of());
            model.addAttribute("bySkillAndCuisine", List.of());
        } else {
            model.addAttribute("user", user);
            model.addAttribute("bySkill", recipeService.recommendBySkill(user.getSkillLevel()));
            model.addAttribute("bySkillAndCuisine", recipeService.recommendBySkillAndCuisine(
                    user.getSkillLevel(), user.getPreferredCuisine()));
        }
    }

    private String validate(String name, String surname, String skillLevel, String preferredCuisine) {
        if (name == null || name.isBlank()) return "Name is required.";
        if (surname == null || surname.isBlank()) return "Surname is required.";
        if (skillLevel == null || skillLevel.isBlank() || !DifficultyLevel.contains(skillLevel)) return "Select a valid skill level.";
        if (preferredCuisine == null || preferredCuisine.isBlank() || !CuisineType.contains(preferredCuisine)) return "Select a valid cuisine.";
        return null;
    }
}
