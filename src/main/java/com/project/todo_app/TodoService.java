package com.project.todo_app;

import java.util.*;
import java.util.stream.Collectors;

public class TodoService {

    private List<Todo> todos = new ArrayList<>();

    public boolean addTodo(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description must not be empty");
        }
        if (todos.stream().anyMatch(t -> t.getDescription().equalsIgnoreCase(description))) {
            return false; // prevent duplicates
        }
        todos.add(new Todo(description));
        return true;
    }

    public List<Todo> getAllTodos() {
        return new ArrayList<>(todos); // defensive copy
    }

    public List<Todo> getTodosFilteredByTag(String tagName) {
        if (tagName.equals("All")) return getAllTodos();
        if (tagName.equals("Done")) {
            return todos.stream().filter(Todo::isDone).collect(Collectors.toList());
        }
        return todos.stream()
                .filter(todo -> todo.hasTag(tagName))
                .collect(Collectors.toList());
    }

    public void markDone(int index) {
        if (index >= 0 && index < todos.size()) {
            todos.get(index).markDone();
        }
    }

    public boolean addTagToTodo(int index, Tag tag) {
        if (index >= 0 && index < todos.size()) {
            return todos.get(index).addTag(tag);
        }
        return false;
    }

    public void removeTagFromAll(String tagName) {
        for (Todo todo : todos) {
            todo.removeTag(tagName);
        }
    }
    
    public Set<String> getAllTags() {
        Set<String> allTags = new HashSet<>();
        for (Todo todo : todos) {
            for (Tag tag : todo.getTags()) {
                allTags.add(tag.getName());
            }
        }
        return allTags;
    }
}
