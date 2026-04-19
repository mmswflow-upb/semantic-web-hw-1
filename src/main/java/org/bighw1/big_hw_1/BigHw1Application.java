package org.bighw1.big_hw_1;

import org.bighw1.big_hw_1.service.XmlStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class BigHw1Application {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(BigHw1Application.class, args);

        Path recipesPath = Paths.get("src/main/resources/data/recipes.xml");
        Path usersPath = Paths.get("src/main/resources/data/users.xml");

        XmlStore store = ctx.getBean(XmlStore.class);
        store.init(recipesPath, usersPath);
    }
}
