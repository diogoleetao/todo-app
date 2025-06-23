package com.project.todo_app;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TodoTest {

	@Test
	void testTodoCreation() {
		Todo todo = new Todo("Write report");
		assertEquals("Write report", todo.getDescription());
		assertFalse(todo.isDone());
	}

	@Test
	void testMarkDone() {
		Todo todo = new Todo("Write tests");
		todo.markDone();
		assertTrue(todo.isDone());
	}

	@Test
	void testAddTag() {
		Todo todo = new Todo("Test with tag");
		Tag tag = new Tag("new tag 1");
		todo.addTag(tag);
		assertTrue(todo.getTags().contains(tag));
		assertFalse(todo.hasTag("wrong tag"));
	}

	@Test
	void testAddTagDuplicate() {
		Todo todo = new Todo("Test with tag");
		Tag tag = new Tag("new tag 1");
		assertTrue(todo.addTag(tag));
		assertFalse(todo.addTag(tag));
	}

	@Test
	void testRemoveTag() {
		Todo todo = new Todo("Task to remove Tag");
		todo.addTag(new Tag("tag1"));
		todo.removeTag("tag1");
		assertFalse(todo.hasTag("tag1"));
	}

	@Test
	void testRemoveTagDoesNotAffectOtherTags() {
		Todo todo = new Todo("Task");
		todo.addTag(new Tag("tag1"));
		todo.addTag(new Tag("tag2"));
		todo.removeTag("tag1");
		assertFalse(todo.hasTag("tag1"));
		assertTrue(todo.hasTag("tag2"));
	}

	@Test
	void testGetTagsAsString() {
		Todo todo = new Todo("Task");
		todo.addTag(new Tag("tag1"));
		todo.addTag(new Tag("tag2"));
		String result = todo.getTagsAsString();
		assertTrue(result.contains("tag1"));
		assertTrue(result.contains("tag2"));
	}

	@Test
	void testHasTag() {
		Todo todo = new Todo("Task with tag1");
		todo.addTag(new Tag("tag1"));
		assertTrue(todo.hasTag("tag1"));
		assertFalse(todo.hasTag("tag2"));
	}
}
