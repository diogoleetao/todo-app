package com.project.todo_app;

import java.util.*;
import java.util.stream.Collectors;

public class TodoService {

	private List<Todo> todos = new ArrayList<>();
	private Set<String> globalTags = new HashSet<>();

	public boolean addTodo(String description) {
		if (description == null || description.trim().isEmpty()) {
			throw new IllegalArgumentException("Description must not be empty");
		}
		if (todos.stream().anyMatch(t -> t.getDescription().equalsIgnoreCase(description))) {
			return false;
		}
		todos.add(new Todo(description));
		return true;
	}

	public List<Todo> getAllTodos() {
		return new ArrayList<>(todos);
	}

	public List<Todo> getTodosFilteredByTag(String tagName) {
		if (tagName.equals("All")) return getAllTodos();
		if (tagName.equals("Done")) {
			return todos.stream().filter(Todo::isDone).collect(Collectors.toList());
		}
		return todos.stream().filter(todo -> todo.hasTag(tagName)).collect(Collectors.toList());
	}

	public void markDone(int index) {
		if (index >= 0 && index < todos.size()) {
			todos.get(index).markDone();
		}
	}

	public boolean addTagToTodo(int index, Tag tag) {
		if (index >= 0 && index < todos.size()) {
			boolean added = todos.get(index).addTag(tag);
			if (added) {
				globalTags.add(tag.getName().toLowerCase());
			}
			return added;
		}
		return false;
	}

	public boolean addTagIfNew(String tagName) {
		return globalTags.stream().noneMatch(existing -> existing.equalsIgnoreCase(tagName));
	}

	public void removeTagFromAll(String tagName) {
		for (Todo todo : todos) {
			todo.removeTag(tagName);
		}
		globalTags.removeIf(existing -> existing.equalsIgnoreCase(tagName));
	}

	public Set<String> getAllTags() {
		return new HashSet<>(globalTags);
	}

	public void reset() {
		todos.clear();
		globalTags.clear();
	}
}
