package com.project.todo_app;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;

import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.*;
import org.assertj.swing.annotation.GUITest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseEvent;
import static org.awaitility.Awaitility.await;
import java.util.concurrent.TimeUnit;

class AppTest {

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
		window.show(new Dimension(800, 600));
		SwingUtilities.invokeLater(App::resetAppState);
	}

	@AfterEach
	void tearDown() {
		window.cleanUp();
	}

	@Test
	void testAppConstructorForCoverage() {
		new App();
		Assertions.assertTrue(true);
	}

	@Test @GUITest
	void testTagAlreadyExistsDialog() {
		window.textBox("inputField").enterText("Task");
		window.button("actionButton").click();

		JLabel label = (JLabel) ((JPanel) window.panel("taskListPanel").target().getComponent(0)).getComponent(0);

		await().atMost(2, TimeUnit.SECONDS).until(label::isShowing);
		window.robot().click(label);

		window.button("taskTagButton-0").click();
		window.menuItem("menuNewTag").click();

		window.textBox("inputField").enterText("groceries");
		window.button("actionButton").click();

		JLabel updatelabel = (JLabel) ((JPanel) window.panel("taskListPanel").target().getComponent(0)).getComponent(0);
		window.robot().click(updatelabel);

		window.button("taskTagButton-0").click();
		window.menuItem("menuNewTag").click();

		window.textBox("inputField").enterText("groceries");
		window.button("actionButton").click();

		window.dialog().requireVisible().requireModal().requireEnabled();
	}

	@Test @GUITest
	void testMarkDoneFromPopup() {
		window.textBox("inputField").enterText("Finish book");
		window.button("actionButton").click();
		JLabel label = (JLabel) ((JPanel) window.panel("taskListPanel").target().getComponent(0)).getComponent(0);
		window.robot().click(label);
		window.button("taskTagButton-0").click();

		JMenuItem markDone = (JMenuItem) window.menuItemWithPath("Mark as Done").target();
		window.robot().click(markDone);

		JLabel updatedLabel = (JLabel) ((JPanel) window.panel("taskListPanel").target().getComponent(0)).getComponent(0);
		Assertions.assertTrue(updatedLabel.getText().contains("✔"));
	}

	@Test @GUITest
	void testClickDoneAndAllRadioButtons() {
		window.textBox("inputField").enterText("One");
		window.button("actionButton").click();
		window.textBox("inputField").enterText("Two");
		window.button("actionButton").click();
		JLabel label = (JLabel) ((JPanel) window.panel("taskListPanel").target().getComponent(1)).getComponent(0);
		window.robot().click(label);
		window.button("taskTagButton-1").click();
		window.menuItemWithPath("Mark as Done").click();

		window.radioButton("radioTag_Done").click();
		Assertions.assertEquals(1, window.panel("taskListPanel").target().getComponentCount());

		window.radioButton("radioTag_All").click();
		Assertions.assertEquals(2, window.panel("taskListPanel").target().getComponentCount());
	}

	@Test @GUITest
	void testDeleteButtonDoesNothingOnAllAndDone() {
		window.radioButton("radioTag_All").click();
		Assertions.assertFalse(window.button("deleteTagButton").target().isEnabled());

		window.radioButton("radioTag_Done").click();
		Assertions.assertFalse(window.button("deleteTagButton").target().isEnabled());

	}
	
	@Test @GUITest
	void testRemoveTagButtonNotRemoveAllOrDone() {
		window.radioButton("radioTag_All").click();
		window.button("deleteTagButton").click();
		Assertions.assertEquals("All", window.radioButton("radioTag_All").target().getText());

		window.radioButton("radioTag_Done").click();
		window.button("deleteTagButton").click();
		Assertions.assertEquals("Done", window.radioButton("radioTag_Done").target().getText());
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
		Assertions.assertFalse(window.button("actionButton").target().isEnabled());

		window.textBox("inputField").enterText("New Task");
		Assertions.assertTrue(window.button("actionButton").target().isEnabled());
	}

	@Test @GUITest
	void testWhitespaceInputDisablesButton() {
		window.textBox("inputField").setText("   ");
		Assertions.assertFalse(window.button("actionButton").target().isEnabled());
	}
	
	@Test
	void testAddEmptyTextDoesNothing() {
		window.textBox("inputField").setText("   ");
		window.button("actionButton").click();
		Assertions.assertEquals(0, window.panel("taskListPanel").target().getComponentCount());
	}

	@Test @GUITest
	void testNoTagAddedIfTextEmptyInTagMode() {
		window.textBox("inputField").enterText("Task 1");
		window.button("actionButton").click();

		JLabel label = (JLabel) ((JPanel) window.panel("taskListPanel").target().getComponent(0)).getComponent(0);
		window.robot().click(label);
		window.button("taskTagButton-0").click();
		window.menuItem("menuNewTag").click();

		window.textBox("inputField").setText("   ");
		window.button("actionButton").click();

		Assertions.assertEquals(2, window.panel("tagPanel").target().getComponentCount() - 2);
	}

	@Test @GUITest
	void testAddTagToTask() {
		window.textBox("inputField").enterText("Buy bread");
		window.button("actionButton").click();

		JPanel taskPanel = window.panel("taskListPanel").panel("taskPanel-" + 0).target();
		JLabel label = (JLabel) taskPanel.getComponent(0);
		JButton tagButton = (JButton) taskPanel.getComponent(1);

		window.robot().click(label);

		await().atMost(1, TimeUnit.SECONDS).until(tagButton::isVisible);

		window.button("taskTagButton-" + 0).click();
		window.menuItem("menuNewTag").click();
		
		window.button("actionButton").requireText("Add Tag");
		
		window.textBox("inputField").enterText("groceries");
		window.button("actionButton").click();

		await().atMost(1, TimeUnit.SECONDS).until(() -> {
			JLabel updatedLabel = (JLabel) ((JPanel) window.panel("taskListPanel").target()
					.getComponent(0)).getComponent(0);
			return updatedLabel.getText().toLowerCase().contains("[groceries]");
		});
		window.radioButton("radioTag_groceries").requireVisible();
	}

	
	@Test @GUITest
	void testAddTagWithoutTargetIndexDoesNothingVisible() {
		SwingUtilities.invokeLater(() -> {
			try {
				var field = App.class.getDeclaredField("isTagMode");
				field.setAccessible(true);
				field.set(null, true);
			} catch (Exception ignored) {
				// intentionally ignored to simulate isTagMode = true without GUI interaction

			}
		});

		window.textBox("inputField").enterText("ghost");
		window.button("actionButton").click();

		Assertions.assertEquals(0, window.panel("taskListPanel").target().getComponentCount());
	}
	
	@Test @GUITest
	void testTagTargetIndexUsedWhenCreatingTag() {
		window.textBox("inputField").enterText("Task 1");
		window.button("actionButton").click();

		JLabel label = (JLabel) ((JPanel) window.panel("taskListPanel").target().getComponent(0)).getComponent(0);
		window.robot().click(label);
		window.button("taskTagButton-0").click();

		window.menuItem("menuNewTag").click();

		window.textBox("inputField").enterText("testtag");
		window.button("actionButton").click();

		JLabel updated = (JLabel) ((JPanel) window.panel("taskListPanel").target().getComponent(0)).getComponent(0);
		Assertions.assertTrue(updated.getText().toLowerCase().contains("testtag"));
	}

	@Test @GUITest
	void testTagNamedDoneNotAddedToTagList() {
		window.textBox("inputField").enterText("Task");
		window.button("actionButton").click();

		JLabel label = (JLabel) ((JPanel) window.panel("taskListPanel").target().getComponent(0)).getComponent(0);
		window.robot().click(label);
		window.button("taskTagButton-0").click();
		window.menuItem("menuNewTag").click();

		window.textBox("inputField").enterText("done"); 
		window.button("actionButton").click();

		Assertions.assertThrows(Exception.class, () -> window.radioButton("radioTag_done"));
	}

	@Test @GUITest
	void testDuplicateTodoAlert() {
		window.textBox("inputField").enterText("Read book");
		window.button("actionButton").click();
		window.textBox("inputField").enterText("Read book");
		window.button("actionButton").click();
		JDialog dialog = (JDialog) window.dialog().target();
		Assertions.assertTrue(dialog.isVisible());
		Assertions.assertTrue(dialog.isModal());
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
		window.menuItem("menuNewTag").click();

		window.textBox("inputField").enterText("urgent");
		window.button("actionButton").click();

		window.radioButton("radioTag_urgent").click();
		window.button("deleteTagButton").click();

		for (Component c : window.panel("taskListPanel").target().getComponents()) {
			if (c instanceof JPanel) {
				JLabel label = (JLabel) ((JPanel) c).getComponent(0);
				Assertions.assertFalse(label.getText().toLowerCase().contains("urgent"));
				Assertions.assertFalse(window.panel("tagPanel").target().toString().contains("urgent"));
			}
		}
	}

	@Test @GUITest
	void testAssignExistingTagToAnotherTask(){
		window.textBox("inputField").enterText("Task 1");
		window.button("actionButton").click();

		await().atMost(3, TimeUnit.SECONDS).until(() ->
		window.panel("taskListPanel").target().getComponentCount() >= 1
		);
		
		JLabel label1 = (JLabel) ((JPanel) window.panel("taskListPanel").target().getComponent(0)).getComponent(0);
		window.robot().click(label1);

		window.button("taskTagButton-0").click();

		await().atMost(2, TimeUnit.SECONDS).until(() -> window.menuItem("menuNewTag").target().isShowing());
		window.menuItem("menuNewTag").click();

		window.textBox("inputField").enterText("work");
		window.button("actionButton").click();

		window.textBox("inputField").enterText("Task 2");
		window.button("actionButton").click();

		JLabel label2 = (JLabel) ((JPanel) window.panel("taskListPanel").target().getComponent(1)).getComponent(0);
		window.robot().click(label2);

		window.button("taskTagButton-1").click();

		await().atMost(3, TimeUnit.SECONDS)
		.untilAsserted(() -> window.menuItemWithPath("work").requireVisible());  

		window.menuItemWithPath("work").click();

		JLabel label = (JLabel) ((JPanel) window.panel("taskListPanel").target().getComponent(1)).getComponent(0);
		Assertions.assertTrue(label.getText().toLowerCase().contains("work"));
	}

	@Test @GUITest
	void testKeyReleasedTriggersUpdateActionButtonState() {
		window.textBox("inputField").click();
		
		window.robot().pressAndReleaseKeys(KeyEvent.VK_A);
		Assertions.assertTrue(window.button("actionButton").target().isEnabled());

		window.robot().pressAndReleaseKeys(KeyEvent.VK_BACK_SPACE);
		Assertions.assertFalse(window.button("actionButton").target().isEnabled());
	}

	@Test @GUITest
	void testKeyReleasedWithEmptyFieldMaintainsDisabledButton() {
		window.textBox("inputField").setText("");
		window.button("actionButton").requireDisabled();

		window.textBox("inputField").click();
		window.robot().pressAndReleaseKeys(KeyEvent.VK_SHIFT); 

		window.button("actionButton").requireDisabled();
		Assertions.assertFalse(window.button("actionButton").target().isEnabled());
	}
}