package org.gmr.app.minidrive.config;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoConfigVerifier implements CommandLineRunner {

    @Value("${spring.mongodb.uri}")
    private String mongoUri;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) {
        System.out.println(
                "Mongo URI configurada: "
                        + mongoUri.replace("admin123", "******")
        );

        System.out.println(
                mongoTemplate
                        .getDb()
                        .runCommand(new Document("connectionStatus", 1))
                        .toJson()
        );
    }
}