package com.project.todo_app;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void testAddTodoRejectsEmpty() {
        TodoService service = new TodoService();
        assertThrows(IllegalArgumentException.class, () -> {
            service.addTodo("");
        });
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
    
    @Test
    void testAddTodoWithSpacesOnly() {
        TodoService service = new TodoService();
        assertThrows(IllegalArgumentException.class, () -> service.addTodo("   "));
    }

    @Test
    void testAddDuplicateTodos() {
    	TodoService service = new TodoService();
        assertThat(service.addTodo("DuplicateTask")).isTrue();
        assertThat(service.addTodo("DuplicateTask")).isFalse();
    }

    @Test
    void testMarkDoneOutOfBounds() {
        TodoService service = new TodoService();
        service.markDone(99); 
    }
    
    @Test
    void testAddAndFilterByTag() {
        TodoService service = new TodoService();
        service.addTodo("Study");
        service.addTagToTodo(0, new Tag("school"));

        var filtered = service.getTodosFilteredByTag("school");
        assertThat(filtered).hasSize(1);
        assertThat(filtered.get(0).getDescription()).isEqualTo("Study");
    }

    @Test
    void testRemoveTagFromAll() {
        TodoService service = new TodoService();
        service.addTodo("A");
        service.addTodo("B");
        service.addTagToTodo(0, new Tag("work"));
        service.addTagToTodo(1, new Tag("work"));

        service.removeTagFromAll("work");

        assertThat(service.getAllTodos().get(0).getTags()).isEmpty();
        assertThat(service.getAllTodos().get(1).getTags()).isEmpty();
    }

    @Test
    void testDoneFilter() {
        TodoService service = new TodoService();
        service.addTodo("A");
        service.addTodo("B");
        service.markDone(1);

        var done = service.getTodosFilteredByTag("Done");
        assertThat(done).hasSize(1);
        assertThat(done.get(0).getDescription()).isEqualTo("B");
    }
}
