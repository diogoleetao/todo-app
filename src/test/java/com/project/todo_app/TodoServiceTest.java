package com.project.todo_app;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.*;

class TodoServiceTest {

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
		service.addTodo("Task");
		assertThat(service.addTagToTodo(0, new Tag("test"))).isTrue();
	}

	@Test
	void testAddTagIfNew() {
		TodoService service = new TodoService();
		service.addTodo("Call mom");
		assertThat(service.addTagIfNew("NEWTAG")).isTrue();
		assertThat(service.addTagIfNew("newtag")).isTrue();
		service.addTagToTodo(0, new Tag("NEWTAG"));
		assertThat(service.addTagIfNew("newtag")).isFalse();
		assertThat(service.addTagIfNew("NEWTAG")).isFalse();
	}

	@Test
	void testAddTagIfNewWithManyExistingTags() {
		TodoService service = new TodoService();
		service.addTodo("Task1");
		service.addTagToTodo(0, new Tag("school"));
		service.addTagToTodo(0, new Tag("home"));
		service.addTagToTodo(0, new Tag("Work"));

		assertThat(service.addTagIfNew("SCHOOL")).isFalse();
		assertThat(service.addTagIfNew("work")).isFalse();   
		assertThat(service.addTagIfNew("leisure")).isTrue(); 
	}

	@Test
	void testAddTodoWithSpacesOnly() {
		TodoService service = new TodoService();
		assertThrows(IllegalArgumentException.class, () -> service.addTodo("   "));
	}

	@Test
	void testAddTodoRejectsNull() {
		TodoService service = new TodoService();
		assertThrows(IllegalArgumentException.class, () -> service.addTodo(null));
	}

	@Test
	void testAddDuplicateTodos() {
		TodoService service = new TodoService();
		assertThat(service.addTodo("DuplicateTask")).isTrue();
		assertThat(service.addTodo("DuplicateTask")).isFalse();
	}

	@Test
	void testAddTagDuplicateReturnsFalse() {
		TodoService service = new TodoService();
		service.addTodo("task1");
		Tag tag = new Tag("tag1");
		assertThat(service.addTagToTodo(0, tag)).isTrue();
		assertThat(service.addTagToTodo(0, tag)).isFalse();
	}
	
	@Test
	void testCannotCreateReservedTagsDoneOrAll() {
		TodoService service = new TodoService();
		service.addTodo("X");

		assertThat(service.addTagIfNew("Done")).isFalse();
		assertThat(service.addTagIfNew("ALL")).isFalse();

		assertThat(service.addTagToTodo(0, new Tag("Done"))).isFalse();
		assertThat(service.addTagToTodo(0, new Tag("All"))).isFalse();

		assertThat(service.getAllTags()).doesNotContain("done");
		assertThat(service.getAllTags()).doesNotContain("all");
	}

	@Test
	void testMarkDoneOutOfBounds() {
		TodoService service = new TodoService();
		service.addTodo("A");
		service.markDone(99);
		service.markDone(-1);
		assertThat(service.getAllTodos().get(0).isDone()).isFalse();
	}

	@Test
	void testMarkDoneAtSizeIndexReturnsFalse() {
		TodoService service = new TodoService();
		service.addTodo("A");
		int size = service.getAllTodos().size();
		service.markDone(size);
		assertThat(service.getAllTodos().get(0).isDone()).isFalse();
	}

	@Test
	void testAddTagOutOfBounds() {
		TodoService service = new TodoService();
		service.addTodo("A");
		assertThat(service.addTagToTodo(99, new Tag("tag"))).isFalse();
		assertThat(service.addTagToTodo(-1, new Tag("tag"))).isFalse();
	}


	@Test
	void testAddTagAtSizeIndexReturnsFalse() {
		TodoService service = new TodoService();
		service.addTodo("A");
		int size = service.getAllTodos().size();
		assertThat(service.addTagToTodo(size, new Tag("tag"))).isFalse();
	}

	@Test
	void testFilterByAllReturnsEverything() {
		TodoService service = new TodoService();
		service.addTodo("a");
		service.addTodo("b");
		service.addTodo("c");
		service.addTodo("d");
		assertThat(service.getTodosFilteredByTag("All")).hasSize(4);
	}

	@Test
	void testAddAndFilterByTag() {
		TodoService service = new TodoService();
		service.addTodo("task1");
		service.addTagToTodo(0, new Tag("tag"));

		var filtered = service.getTodosFilteredByTag("tag");
		assertThat(filtered).hasSize(1);
		assertThat(filtered.get(0).getDescription()).isEqualTo("task1");
	}

	@Test
	void testAddAndFilterByTagWithMultipleTasks() {
		TodoService service = new TodoService();
		service.addTodo("task1");
		service.addTodo("task2");
		service.addTodo("task3");
		service.addTagToTodo(1, new Tag("tag"));

		var filtered = service.getTodosFilteredByTag("tag");
		assertThat(filtered).hasSize(1);
		assertThat(filtered.get(0).getDescription()).isEqualTo("task2");
	}

	@Test
	void testGetAllTagsAndReset() {
		TodoService service = new TodoService();
		service.addTodo("task1");
		service.addTagToTodo(0, new Tag("urgent"));

		assertThat(service.getAllTags()).containsExactly("urgent");

		service.reset();

		assertThat(service.getAllTags()).isEmpty();
		assertThat(service.getAllTodos()).isEmpty();
	}

	@Test
	void testRemoveTagFromAll() {
		TodoService service = new TodoService();
		service.addTodo("A");
		service.addTodo("B");
		service.addTagToTodo(0, new Tag("toremove"));
		service.addTagToTodo(1, new Tag("toRemove"));

		assertThat(service.getAllTags()).contains("toremove");

		service.removeTagFromAll("toRemove");

		assertThat(service.getAllTags()).doesNotContain("toremove");
		assertThat(service.getAllTags()).doesNotContain("toRemove");
		assertThat(service.getAllTodos().get(0).getTags()).isEmpty();
		assertThat(service.getAllTodos().get(1).getTags()).isEmpty();
		assertThat(service.getAllTags()).isEmpty();
	}

	@Test
	void testRemoveTagFromAllSelective() {
		TodoService service = new TodoService();
		service.addTodo("Task1");
		service.addTodo("Task2");
		service.addTodo("Task3");
		service.addTagToTodo(0, new Tag("remove"));
		service.addTagToTodo(0, new Tag("keep"));
		service.addTagToTodo(1, new Tag("remove"));
		service.addTagToTodo(1, new Tag("keep"));
		service.addTagToTodo(2, new Tag("remove"));

		service.removeTagFromAll("remove");

		assertThat(service.getAllTodos().get(0).hasTag("remove")).isFalse();
		assertThat(service.getAllTodos().get(0).hasTag("keep")).isTrue();
		assertThat(service.getAllTodos().get(1).hasTag("remove")).isFalse();
		assertThat(service.getAllTodos().get(1).hasTag("keep")).isTrue();
		assertThat(service.getAllTodos().get(2).hasTag("remove")).isFalse();
	}

	@Test
	void testDoneFilter() {
		TodoService service = new TodoService();
		service.addTodo("A");
		service.addTodo("B");
		service.addTodo("C");
		service.markDone(1);
		service.markDone(2);

		assertThat(service.getAllTodos().get(0).isDone()).isFalse();
		assertThat(service.getAllTodos().get(1).isDone()).isTrue();
		assertThat(service.getAllTodos().get(2).isDone()).isTrue();

		var done = service.getTodosFilteredByTag("Done");
		assertThat(done).isNotEmpty();
		assertThat(done).allSatisfy(todo -> assertThat(todo.isDone()).isTrue());
	}
	
	@Test
	void testDoneFilterCorrectSize() {
		TodoService service = new TodoService();
		service.addTodo("A");
		service.addTodo("B");
		service.addTodo("C");
		service.markDone(1);
		service.markDone(2);

		assertThat(service.getAllTodos().get(0).isDone()).isFalse();
		assertThat(service.getAllTodos().get(1).isDone()).isTrue();
		assertThat(service.getAllTodos().get(2).isDone()).isTrue();

		var done = service.getTodosFilteredByTag("Done");
		assertThat(done).hasSize(2);
	}
	
	@Test
	void testDoneFilterCorrectContent() {
		TodoService service = new TodoService();
		service.addTodo("A");
		service.addTodo("B");
		service.addTodo("C");
		service.markDone(1);
		service.markDone(2);

		assertThat(service.getAllTodos().get(0).isDone()).isFalse();
		assertThat(service.getAllTodos().get(1).isDone()).isTrue();
		assertThat(service.getAllTodos().get(2).isDone()).isTrue();

		var done = service.getTodosFilteredByTag("Done");
		assertThat(done).extracting(Todo::getDescription).containsExactlyInAnyOrder("B", "C");
	}

	@Test
	void testDoneFilterEmpty() {
		TodoService service = new TodoService();
		service.addTodo("A");

		var done = service.getTodosFilteredByTag("Done");
		assertThat(done).isEmpty();
	}

	@Test
	void testAddTagToInvalidIndexReturnsFalse() {
		TodoService service = new TodoService();
		service.addTodo("A");
		assertThat(service.addTagToTodo(5, new Tag("tag"))).isFalse();    
	}
}
