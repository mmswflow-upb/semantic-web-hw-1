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
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
            if (existing.size() >= 20) return;

            log.info("recipes.xml has {} recipes, scraping bbcgoodfood...", existing.size());
            int scraped = scrapeFromWeb(existing.size());

            int total = existing.size() + scraped;
            if (total < 20) {
                log.info("Scrape yielded {} total, filling {} from backup.", total, 20 - total);
                addFromBackup(20 - total);
            }
        } catch (Exception e) {
            log.warn("Seed failed: {}", e.getMessage());
        }
    }

    private int scrapeFromWeb(int existingCount) {
        int added = 0;
        try {
            Document page = Jsoup.connect(SCRAPE_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(15000)
                    .get();

            Elements cards = page.select("h2.heading-4:not(.promotion-cards__title--heading)");
            if (cards.isEmpty()) cards = page.select("h2");

            CuisineType[] cuisines = CuisineType.values();
            DifficultyLevel[] difficulties = DifficultyLevel.values();

            for (Element card : cards) {
                String title = card.text().trim();
                if (title.isEmpty()) continue;
                if (title.contains("premium piece of content") || title.startsWith("App only")) continue;

                ThreadLocalRandom rng = ThreadLocalRandom.current();
                int i1 = rng.nextInt(cuisines.length);
                int i2;
                do { i2 = rng.nextInt(cuisines.length); } while (i2 == i1);

                recipeService.add(title, cuisines[i1].toString(), cuisines[i2].toString(),
                        difficulties[rng.nextInt(difficulties.length)].toString());
                added++;

                if (existingCount + added >= 20) break;
            }
            log.info("Scraper added {} recipes.", added);
        } catch (Exception e) {
            log.warn("Scraper failed, falling back to backup: {}", e.getMessage());
        }
        return added;
    }

    private void addFromBackup(int needed) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("data/backup-recipes.xml")) {
            if (in == null) {
                log.warn("backup-recipes.xml not found on classpath.");
                return;
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver((pub, sys) -> new InputSource(new StringReader("")));
            org.w3c.dom.Document doc = builder.parse(in);

            NodeList nodes = doc.getElementsByTagName("recipe");
            List<org.w3c.dom.Element> recipes = new ArrayList<>();
            for (int i = 0; i < nodes.getLength(); i++) {
                recipes.add((org.w3c.dom.Element) nodes.item(i));
            }
            int added = 0;
            for (org.w3c.dom.Element el : recipes) {
                if (added >= needed) break;
                String title = el.getElementsByTagName("title").item(0).getTextContent().trim();
                NodeList ctNodes = el.getElementsByTagName("cuisineType");
                String ct1 = ((org.w3c.dom.Element) ctNodes.item(0)).getAttribute("type");
                String ct2 = ((org.w3c.dom.Element) ctNodes.item(1)).getAttribute("type");
                String difficulty = el.getAttribute("difficulty");
                recipeService.add(title, ct1, ct2, difficulty);
                added++;
            }
            log.info("Backup added {} recipes.", added);
        } catch (Exception e) {
            log.warn("Failed to load backup recipes: {}", e.getMessage());
        }
    }
}
