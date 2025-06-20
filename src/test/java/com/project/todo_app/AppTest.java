package com.project.todo_app;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.*;
import org.assertj.swing.annotation.GUITest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
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
        await().atMost(3, TimeUnit.SECONDS).until(() -> App.getCurrentFrame() != null);
        window = new FrameFixture(App.getCurrentFrame());
        window.show();
        SwingUtilities.invokeLater(App::resetAppState);
    }

    @AfterEach
    void tearDown() {
        window.cleanUp();
    }

    @Test @GUITest
    void testControlInitialStates() {
        window.textBox("inputField").requireEnabled();
        window.button("actionButton").requireDisabled();
        window.panel("tagPanel").requireVisible();
        window.button("deleteTagButton").requireDisabled();
        Assertions.assertEquals(0, window.panel("taskListPanel").target().getComponentCount());
    }

    @Test @GUITest
    void testAddTodoItem() {
        int initialCount = window.panel("taskListPanel").target().getComponentCount();
        window.textBox("inputField").enterText("Write report");
        window.button("actionButton").click();
        int finalCount = window.panel("taskListPanel").target().getComponentCount();
        Assertions.assertEquals(initialCount + 1, finalCount);
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
    void testAddTagToTask() {
        // Add a new task
        window.textBox("inputField").enterText("Buy bread");
        window.button("actionButton").click();

        int taskCount = window.panel("taskListPanel").target().getComponentCount();
        int lastIndex = taskCount - 1;

        JPanel taskPanel = (JPanel) window.panel("taskListPanel").target().getComponent(lastIndex);
        JLabel label = (JLabel) taskPanel.getComponent(0);

        window.robot().click(label);

        JButton tagButton = (JButton) taskPanel.getComponent(1);
        await().atMost(1, TimeUnit.SECONDS).until(() -> tagButton.isVisible());

        window.button("taskTagButton-" + lastIndex).click();
        window.menuItem("menuNewTag").click();

        window.textBox("inputField").enterText("groceries");
        window.button("actionButton").click();

        await().atMost(1, TimeUnit.SECONDS).until(() -> {
            JLabel updatedLabel = (JLabel) ((JPanel) window.panel("taskListPanel").target()
                    .getComponent(lastIndex)).getComponent(0);
            return updatedLabel.getText().toLowerCase().contains("groceries");
        });
    }

    @Test @GUITest
    void testDuplicateTodoAlert() {
        window.textBox("inputField").enterText("Read book");
        window.button("actionButton").click();
        window.textBox("inputField").enterText("Read book");
        window.button("actionButton").click();
        window.dialog().requireVisible().requireModal().requireEnabled();
    }

    @Test @GUITest
    void testRemoveTagAffectsAllTasks() throws Exception {
        window.textBox("inputField").enterText("Task A");
        window.button("actionButton").click();
        window.textBox("inputField").enterText("Task B");
        window.button("actionButton").click();

        int index = 0;
        Component taskPanel = window.panel("taskListPanel").target().getComponent(index);

        SwingUtilities.invokeAndWait(() -> taskPanel.dispatchEvent(
            new MouseEvent(taskPanel, MouseEvent.MOUSE_CLICKED,
            System.currentTimeMillis(), 0, 10, 10, 1, false)));

        window.button("taskTagButton-" + index).click();
        Thread.sleep(200);
        window.menuItem("menuNewTag").click();

        window.textBox("inputField").setText("urgent");
        window.button("actionButton").click();

        window.radioButton("radioTag_urgent").click();
        window.button("deleteTagButton").click();

        for (Component c : window.panel("taskListPanel").target().getComponents()) {
            if (c instanceof JPanel) {
                JLabel label = (JLabel) ((JPanel) c).getComponent(0);
                Assertions.assertFalse(label.getText().toLowerCase().contains("urgent"));
            }
        }
    }
}
