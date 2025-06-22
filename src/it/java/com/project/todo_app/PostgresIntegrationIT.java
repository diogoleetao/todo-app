package com.project.todo_app;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresIntegrationIT {

	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

	@BeforeAll
	public static void startContainer() {
		postgres.start();
	}

	@Test
	void testPostgresContainerRuns() {
		Assertions.assertTrue(postgres.isRunning());
	}
}

