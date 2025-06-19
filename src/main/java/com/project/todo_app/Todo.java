package com.project.todo_app;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
        this.tags.add(new Tag("Done"));
    }

    public boolean addTag(Tag tag) {
        return tags.add(tag);
    }

    public void removeTag(String tagName) {
        tags.removeIf(tag -> tag.getName().equalsIgnoreCase(tagName));
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public boolean hasTag(String tagName) {
        return tags.stream().anyMatch(tag -> tag.getName().equalsIgnoreCase(tagName));
    }

    public String getTagsAsString() {
        return tags.stream().map(Tag::getName).collect(Collectors.joining(", "));
    }
}
