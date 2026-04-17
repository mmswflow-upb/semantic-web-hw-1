package org.bighw1.big_hw_1.service;

import org.bighw1.big_hw_1.model.Recipe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

// Scrapes recipe titles from bbcgoodfood.com and writes them to recipes.xml.
// Only runs if recipes.xml has fewer than 20 entries.
@Service
public class ScraperService {

    private static final String SCRAPE_URL =
            "https://www.bbcgoodfood.com/recipes/collection/budget-autumn";

    private static final List<String> CUISINE_TYPES = List.of(
            "Italian", "Asian", "Mexican", "French", "Mediterranean",
            "Indian", "American", "British", "Middle Eastern", "Greek"
    );

    private static final List<String> DIFFICULTIES = List.of(
            "Beginner", "Intermediate", "Advanced"
    );

    @Autowired
    private RecipeService recipeService;

    public void scrapeIfNeeded() {
        try {
            List<Recipe> existing = recipeService.getAll();
            if (existing.size() >= 20) {
                return;
            }

            System.out.println("recipes.xml has " + existing.size() + " recipes, scraping bbcgoodfood...");

            // Use a browser-like User-Agent so the site does not block the request.
            Document page = Jsoup.connect(SCRAPE_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(15000)
                    .get();

            // Recipe card titles on bbcgoodfood are in h2 elements with class "heading-4".
            // Promotion section headers also have heading-4, so exclude those.
            Elements cards = page.select("h2.heading-4:not(.promotion-cards__title--heading)");
            if (cards.isEmpty()) {
                // Fallback: any h2 on the page
                cards = page.select("h2");
            }

            Random rand = new Random();
            int count = existing.size();
            int added = 0;

            for (Element card : cards) {
                String title = card.text().trim();
                if (title.isEmpty()) continue;
                if (title.contains("premium piece of content") || title.startsWith("App only")) continue;

                // Pick 2 distinct cuisine types at random
                int i1 = rand.nextInt(CUISINE_TYPES.size());
                int i2;
                do {
                    i2 = rand.nextInt(CUISINE_TYPES.size());
                } while (i2 == i1);

                String ct1 = CUISINE_TYPES.get(i1);
                String ct2 = CUISINE_TYPES.get(i2);
                String difficulty = DIFFICULTIES.get(rand.nextInt(DIFFICULTIES.size()));

                count++;
                String id = "r" + count;
                recipeService.add(new Recipe(id, title, ct1, ct2, difficulty));
                added++;

                if (existing.size() + added >= 20) break;
            }

            System.out.println("Scraper added " + added + " recipes.");

        } catch (Exception e) {
            System.err.println("Scraper failed, continuing with existing recipes: " + e.getMessage());
        }
    }
}
