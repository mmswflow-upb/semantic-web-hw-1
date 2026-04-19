package org.bighw1.big_hw_1.service;

import org.bighw1.big_hw_1.enums.CuisineType;
import org.bighw1.big_hw_1.enums.DifficultyLevel;
import org.bighw1.big_hw_1.model.Recipe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class ScraperService {

    private static final Logger log = LoggerFactory.getLogger(ScraperService.class);

    private static final String SCRAPE_URL =
            "https://www.bbcgoodfood.com/recipes/collection/budget-autumn";

    private final RecipeService recipeService;

    public ScraperService(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    public void scrapeIfNeeded() {
        try {
            List<Recipe> existing = recipeService.getAll();
            if (existing.size() >= 20) {
                return;
            }

            log.info("recipes.xml has {} recipes, scraping bbcgoodfood...", existing.size());

            Document page = Jsoup.connect(SCRAPE_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(15000)
                    .get();

            // bbcgoodfood recipe card titles are h2.heading-4; exclude promotion headers with the same class.
            Elements cards = page.select("h2.heading-4:not(.promotion-cards__title--heading)");
            if (cards.isEmpty()) {
                cards = page.select("h2");
            }

            CuisineType[] cuisines = CuisineType.values();
            DifficultyLevel[] difficulties = DifficultyLevel.values();
            Random rand = new Random();
            int count = existing.size();
            int added = 0;

            for (Element card : cards) {
                String title = card.text().trim();
                if (title.isEmpty()) continue;
                if (title.contains("premium piece of content") || title.startsWith("App only")) continue;

                int i1 = rand.nextInt(cuisines.length);
                int i2;
                do {
                    i2 = rand.nextInt(cuisines.length);
                } while (i2 == i1);

                String ct1 = cuisines[i1].toString();
                String ct2 = cuisines[i2].toString();
                String difficulty = difficulties[rand.nextInt(difficulties.length)].toString();

                count++;
                String id = "r" + count;
                recipeService.add(new Recipe(id, title, ct1, ct2, difficulty));
                added++;

                if (existing.size() + added >= 20) break;
            }

            log.info("Scraper added {} recipes.", added);

        } catch (Exception e) {
            log.warn("Scraper failed, continuing with existing recipes: {}", e.getMessage());
        }
    }
}
