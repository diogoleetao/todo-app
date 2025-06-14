package com.project.todo_app;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class TodoServiceTest {

    @Test
    void testAddAndGetTodos() {
        TodoService service = new TodoService();
        service.addTodo("Buy milk");

        assertThat(service.getAllTodos()).hasSize(1);
        assertThat(service.getAllTodos().get(0).getDescription()).isEqualTo("Buy milk");
    }

    @Test
    void testMarkDone() {
        TodoService service = new TodoService();
        service.addTodo("Write tests");
        service.markDone(0);

        assertThat(service.getAllTodos().get(0).isDone()).isTrue();
    }

    @Test
    void testAddTag() {
        TodoService service = new TodoService();
        service.addTodo("Call mom");
        service.addTagToTodo(0, new Tag("family"));

        assertThat(service.getAllTodos().get(0).getTags()).extracting(Tag::getName).contains("family");
    }
}
