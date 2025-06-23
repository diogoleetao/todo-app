package com.project.todo_app;

import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {

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
	void testEqualsHandlesWrongType() {
		Tag tag = new Tag("tag");
		assertNotEquals("notTag", tag);
	}
	
	@Test
	void testEqualsHandlesNull() {
		Tag tag = new Tag("tag");
		//assertNotEquals(tag, null);
		assertFalse(tag.equals(null));
	}

	@Test
	void testEqualsWithSameObject() {
		Tag tag = new Tag("repeat");
		assertEquals(tag, tag);
	}

	@Test
	void testHashCode() {
		Tag tag1 = new Tag("abc");
		Tag tag2 = new Tag("ABC");
		assertEquals(tag1.hashCode(), tag2.hashCode());
	}

	@Test
	void testHashCodeInHashSet() {
		Set<Tag> set = new HashSet<>();
		Tag tag1 = new Tag("abc");
		Tag tag2 = new Tag("ABC");
		set.add(tag1);
		assertTrue(set.contains(tag2));
	}

	@Test
	void testHashCodeIsNotTrivial() {
		Tag tag = new Tag("abc");
		assertNotEquals(0, tag.hashCode());
	}
}
