package com.project.todo_app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    }

    @Test
    void testAddTag() {
        Todo todo = new Todo("Test with tag");
        Tag tag = new Tag("school");
        todo.addTag(tag);
        assertTrue(todo.getTags().contains(tag));
    }
}
