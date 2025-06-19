package com.project.todo_app;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.*;
import org.assertj.swing.annotation.GUITest;
import javax.swing.*;
import static org.awaitility.Awaitility.await;
import java.util.concurrent.TimeUnit;

public class AppTest {

    private FrameFixture window;

    @BeforeAll
    static void setupOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    void setUp() {
        SwingUtilities.invokeLater(() -> App.main(null));
        await().atMost(2, TimeUnit.SECONDS).until(() -> App.getCurrentFrame() != null);
        window = new FrameFixture(App.getCurrentFrame());
        window.show();
    }

    @AfterEach
    void tearDown() {
        window.cleanUp();
    }

    @Test @GUITest
    void testControlInitialStates() {
        window.textBox("inputField").requireEnabled();
        window.button("actionButton").requireDisabled();
        window.list("taskList").requireItemCount(0);
        window.panel("tagPanel").requireVisible();
        window.button("deleteTagButton").requireDisabled();
    }

    @Test @GUITest
    void testAddTodoItem() {
        window.textBox("inputField").enterText("Write report");
        window.button("actionButton").click();
        window.list("taskList").requireItemCount(1);
        Assertions.assertTrue(window.list("taskList").contents()[0].contains("Write report"));
    }

    @Test @GUITest
    void addButtonDisabledWhenTextEmpty() {
        window.textBox("inputField").setText("");
        window.button("actionButton").requireDisabled();

        window.textBox("inputField").enterText("New Task");
        window.button("actionButton").requireEnabled();
    }

    @Test @GUITest
    void testWhitespaceInputDisablesButton() {
        window.textBox("inputField").setText("   ");
        window.button("actionButton").requireDisabled();
    }

    @Test @GUITest
    void testToggleModeSwitch() {
        // Simulate clicking the toggle button
        window.button("modeSwitchButton").click();
        window.button("actionButton").requireText("Add Tag");
        window.button("modeSwitchButton").click();
        window.button("actionButton").requireText("Add Task");
    }

    @Test @GUITest
    void testAddTagToTask() {
        window.textBox("inputField").enterText("Buy bread");
        window.button("actionButton").click();

        window.list("taskList").clickItem(0);
        window.button("taskTagMenuButton-0").click();
        window.menuItem("menuNewTag").click();

        window.textBox("inputField").enterText("grocery");
        window.button("actionButton").click();

        Assertions.assertTrue(window.list("taskList").contents()[0].contains("grocery"));
    }
}
