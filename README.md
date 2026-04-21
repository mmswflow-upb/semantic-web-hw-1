# Recipe Recommender - Semantic Web Big Homework 1

GitHub repository: https://github.com/mariosakka/semantic-web-hw-1

### What each person worked on

Sakka Mohamad-Mario:
- Wrote the XML data model and the DTD files for recipes and users
- Built the XML service layer that handles DOM loading, saving, XPath queries, and XSLT transformation
- Implemented the scraper that pulls recipe titles from BBC Good Food using Jsoup
- Created the XSLT stylesheet that colors recipes based on the selected user's skill level
- Created the shared head and sidebar fragments and defined all Tailwind component classes in head.html

Mirghani Abdelrahman:
- Created the Recipe and User model classes and the CuisineType and DifficultyLevel enums
- Built RecipeService and UserService including XPath queries, ID generation, and recommendation logic
- Designed and implemented all Thymeleaf HTML templates
- Wrote both controllers that handle all routing and form processing