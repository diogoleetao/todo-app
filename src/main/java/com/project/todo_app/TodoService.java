package com.project.todo_app;

import java.util.ArrayList;
import java.util.List;

public class TodoService {

    private List<Todo> todos = new ArrayList<>();

    public void addTodo(String description) {
        todos.add(new Todo(description));
    }

    public List<Todo> getAllTodos() {
        return new ArrayList<>(todos); // Defensive copy
    }

    public void markDone(int index) {
        if (index >= 0 && index < todos.size()) {
            todos.get(index).markDone();
        }
    }

    public void addTagToTodo(int index, Tag tag) {
        if (index >= 0 && index < todos.size()) {
            todos.get(index).addTag(tag);
        }
    }
}
