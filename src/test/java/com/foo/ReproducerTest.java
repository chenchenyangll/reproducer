package com.foo;

import java.util.List;

import com.antwerkz.bottlerocket.BottleRocket;
import com.antwerkz.bottlerocket.BottleRocketTest;
import com.github.zafarkhaja.semver.Version;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import static org.junit.Assert.assertThrows;

public class ReproducerTest extends BottleRocketTest {
    private final Datastore datastore;

    public ReproducerTest() {
        MongoClient mongo = getMongoClient();
        MongoDatabase database = getDatabase();
        database.drop();

        setUp(database);

        datastore = Morphia.createDatastore(mongo, getDatabase().getName());
        datastore.getMapper().map(Book.class);
    }

    @NotNull
    @Override
    public String databaseName() {
        return "morphia_repro";
    }

    @Nullable
    @Override
    public Version version() {
        return BottleRocket.DEFAULT_VERSION;
    }

    private Document newBook(String name, Integer type) {
        Document d = new Document("name", name);
        d.append("type", type);
        return d;
    }

    private void setUp(MongoDatabase database) {
        /*
         * MUST first have these documents already in database before mapping by Morphia
         */
        database.getCollection("book").insertMany(List.of(
            newBook("A", 1),
            newBook("B", null)
        ));
        for (Document d : database.getCollection("book").find()) {
            System.out.println(d);
        }
        System.out.println("setUp pass");
    }

    @Test
    public void reproduce() {
        /*
         * NPE here
         */
        NullPointerException npe = assertThrows(NullPointerException.class, () -> {
            for (Book book : datastore.find(Book.class)) {
                System.out.println(book.name);
            }
        });
        npe.printStackTrace();
    }
}
