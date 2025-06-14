package com.project.todo_app;

import java.util.HashSet;
import java.util.Set;

public class Todo {
    private String description;
    private boolean done;
    private Set<Tag> tags = new HashSet<>();

    public Todo(String description) {
        this.description = description;
        this.done = false;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return done;
    }

    public void markDone() {
        this.done = true;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public Set<Tag> getTags() {
        return tags;
    }
}
