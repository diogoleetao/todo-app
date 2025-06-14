package com.project.todo_app;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.*;

import javax.swing.*;

public class AppTest {

    private FrameFixture window;

    @BeforeAll
    static void setupOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    void setUp() {
        SwingUtilities.invokeLater(() -> App.main(null));
        window = new FrameFixture(App.getCurrentFrame());
        window.show();
    }

    @AfterEach
    void tearDown() {
        window.cleanUp();
    }

    @Test
    void testAddTodoItem() {
        window.textBox().enterText("Write report");
        window.button().click();
        window.list().requireItemCount(1);
        Assertions.assertEquals("Write report", window.list().valueAt(0));
    }
}
