# Recipe Recommender - Semantic Web Big Homework 1

GitHub repository: https://github.com/mariosakka/semantic-web-hw-1

### Team

- Mirghani Abdelrahman
- Sakka Mohamad-Mario


### What each person worked on

Sakka Mohamad-Mario:
- Wrote the XML data model including the DTD files for both recipes and users
- Built the XML service layer that handles DOM loading, saving, XPath queries, and XSLT transformation
- Implemented the scraper that pulls recipe titles from BBC Good Food using Jsoup
- Created the XSLT stylesheet that colors recipes based on user skill level
- Created the shared head and sidebar fragments; defined all Tailwind component classes in head.html

Mirghani Abdelrahman:
- Created the Recipe and User model classes and the CuisineType and DifficultyLevel enums
- Built RecipeService and UserService including XPath queries, ID generation, and recommendation logic
- Designed and implemented all Thymeleaf HTML templates
- Wrote both controllers (RecipeController and UserController) that handle all routing and form processing


### How it works

The application stores all data in two XML files: recipes.xml and users.xml, both validated against DTD schemas with enumerated attributes for cuisine types, difficulty levels, and skill levels.

On startup the app only loads the XML files into memory. No data is created automatically. To populate the app, use the Seed Data button on the home page — this scrapes up to 20 recipe titles from BBC Good Food (assigning random cuisine types and difficulty levels) and creates two default users (Abd Mirghani and Mario Sakka) if none exist yet. The Clear All Data button on the same page wipes both files back to empty.

All data querying is done through XPath against the in-memory DOM. The recommendation system matches recipes to users based on cooking skill level and preferred cuisine type, running two separate queries: one for skill level only, and one for both skill level and preferred cuisine.

The recipe display page uses XSLT to transform the XML into an HTML table. Recipes matching the selected user's skill level get a yellow background; the rest get green.

Styling is centralised in a shared head.html fragment that defines named Tailwind component classes (.btn, .card, .tbl, .nav-link, etc.) using @apply. All templates reference only these short class names.


### Tech stack

- Java 21
- Spring Boot 4.0.5
- Gradle
- Thymeleaf for HTML templates
- Tailwind CSS (CDN) for styling
- Jsoup for web scraping
- XML, DTD, XPath, and XSLT for data storage and querying


### How to run it

Make sure you have Java 21 installed. Then from the project root:

    ./gradlew bootRun

The app will start on http://localhost:8080. Use the Seed Data button on the home page to populate initial data.


### Pages

- `/` - Home page with Seed Data and Clear All Data buttons
- `/recipes` - All recipes in a table
- `/recipes/add` - Add a new recipe with validation
- `/recipes/{id}` - Detail view for a single recipe
- `/recipes/cuisine` - Filter recipes by cuisine type
- `/recipes/display` - XSLT-rendered recipe table with skill-level color coding
- `/users/add` - Add a new user
- `/recommendations` - Recipe recommendations for a selected user based on skill level and preferred cuisine
