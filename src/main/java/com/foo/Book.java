package com.foo;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import org.bson.types.ObjectId;

@Entity(value = "book", useDiscriminator = false)
public class Book {
    @Id
    ObjectId id;

    String name;

    Integer type;

    @PostLoad
    private void onLoaded() {
        System.out.printf("in @PostLoad: %s %d%n", name, type);
    }

    public Book() {
    }

    public Book(String name, Integer type) {
        this.name = name;
        this.type = type;
    }
}
