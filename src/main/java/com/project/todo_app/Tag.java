package com.project.todo_app;

//import java.util.Objects;

public class Tag {
	private String name;

	public Tag(String name) {
		this.name = name.toLowerCase();
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Tag)) return false;
		Tag tag = (Tag) o;
		return name.equalsIgnoreCase(tag.name);
	}

	@Override
	public int hashCode() {
		return name.toLowerCase().hashCode();
	}
}
