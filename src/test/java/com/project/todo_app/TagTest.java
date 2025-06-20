package com.project.todo_app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TagTest {

    @Test
    void testTagCreation() {
        Tag tag = new Tag("school");
        assertEquals("school", tag.getName());
    }

    @Test
    void testTagEquality() {
        Tag tag1 = new Tag("home");
        Tag tag2 = new Tag("home");
        assertEquals(tag1, tag2);
    }

    @Test
    void testTagInequality() {
        Tag tag1 = new Tag("work");
        Tag tag2 = new Tag("fun");
        assertNotEquals(tag1, tag2);
    }
    
    @Test
    void testEqualsHandlesNullAndWrongType() {
        Tag tag = new Tag("tag");
        assertNotEquals(tag, null);
        assertNotEquals(tag, "notTag");
    }
    
    @Test
    void testEqualsWithSameObject() {
        Tag tag = new Tag("repeat");
        assertEquals(tag, tag);
    }
}
