package com.project.todo_app;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

public class TodoTest {

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
        assertTrue(todo.hasTag("Done"));
    }

    @Test
    void testAddTag() {
        Todo todo = new Todo("Test with tag");
        Tag tag = new Tag("school");
        todo.addTag(tag);
        assertTrue(todo.getTags().contains(tag));
    }

    @Test
    void testRemoveTag() {
        Todo todo = new Todo("Clean room");
        todo.addTag(new Tag("home"));
        todo.removeTag("home");
        assertFalse(todo.hasTag("home"));
    }

    @Test
    void testGetTagsAsString() {
        Todo todo = new Todo("Task");
        todo.addTag(new Tag("one"));
        todo.addTag(new Tag("two"));
        String result = todo.getTagsAsString();
        assertTrue(result.contains("one"));
        assertTrue(result.contains("two"));
    }

    @Test
    void testHasTag() {
        Todo todo = new Todo("Do laundry");
        todo.addTag(new Tag("chores"));
        assertTrue(todo.hasTag("chores"));
        assertFalse(todo.hasTag("fun"));
    }
}
