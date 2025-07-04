package com.project.todo_app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class App {
	private static JFrame frame;
	private static TodoService todoService = new TodoService();
	private static JTextField inputField;
	private static JButton actionButton;
	private static JPanel tagPanel;
	private static boolean isTagMode = false;
	private static Integer tagTargetIndex = null;
	private static String currentFilterTag = "All";
	private static JButton deleteTagButton;

	private static JPanel taskListPanel;
	private static JButton currentTagButtonShown = null;
	private static final String ADD_TASK_LABEL = "Add Task";
	
	public static JFrame getCurrentFrame() {
		return frame;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(App::createAndShowGUI);
	}

	private static void createAndShowGUI() {
		frame = new JFrame("Todo App");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(600, 400);

		JPanel panel = new JPanel(new BorderLayout());

		inputField = new JTextField();
		inputField.setName("inputField");

		actionButton = new JButton(ADD_TASK_LABEL);
		actionButton.setName("actionButton");
		actionButton.setEnabled(false);

		JPanel inputPanel = new JPanel(new BorderLayout());
		inputPanel.add(inputField, BorderLayout.CENTER);
		inputPanel.add(actionButton, BorderLayout.EAST);

		taskListPanel = new JPanel();
		taskListPanel.setName("taskListPanel");
		taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(taskListPanel);

		tagPanel = new JPanel();
		tagPanel.setName("tagPanel");
		tagPanel.setLayout(new BoxLayout(tagPanel, BoxLayout.Y_AXIS));
		updateTagPanel();

		panel.add(inputPanel, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(tagPanel, BorderLayout.EAST);

		actionButton.addActionListener(e -> {
			String text = inputField.getText().trim();

			if (isTagMode) {
				boolean success = todoService.addTagIfNew(text);
				if (!success) {
					JOptionPane.showMessageDialog(frame, "Tag already exists.", "Error", JOptionPane.ERROR_MESSAGE);
					isTagMode = false;
					actionButton.setText(ADD_TASK_LABEL);
					inputField.setText("");
					updateActionButtonState();
					actionButton.setEnabled(false);
					return; 
				}
				if (tagTargetIndex != null) {
					todoService.addTagToTodo(tagTargetIndex, new Tag(text));
					updateTodoList();
				}
				isTagMode = false;
				actionButton.setText(ADD_TASK_LABEL);
				updateTagPanel();
				updateActionButtonState();
			} else {
				boolean success = todoService.addTodo(text);
				if (!success) {
					JOptionPane.showMessageDialog(frame, "Task already exists.", "Error", JOptionPane.ERROR_MESSAGE);
					inputField.setText("");
					updateActionButtonState();
					actionButton.setEnabled(false);
					return;
				}
				isTagMode = false;
				updateTodoList();
				updateTagPanel();
			}

			inputField.setText("");
			actionButton.setEnabled(false);
			updateActionButtonState();
		});

		inputField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateActionButtonState();
			}
		});

		frame.getContentPane().add(panel);
		frame.setVisible(true);
	}

	static void updateActionButtonState() {
		actionButton.setEnabled(!inputField.getText().trim().isEmpty());
	}
	
	public static void resetAppState() {
		todoService.reset();
		currentFilterTag = "All";
		updateTodoList();
		updateTagPanel();
		updateActionButtonState();
		isTagMode = false;
		tagTargetIndex = null;
	}

	private static void updateTodoList() {
		taskListPanel.removeAll();
		List<Todo> todos = "All".equals(currentFilterTag)
				? todoService.getAllTodos()
				: todoService.getTodosFilteredByTag(currentFilterTag);

		for (int i = 0; i < todos.size(); i++) {
			Todo todo = todos.get(i);

			JPanel taskPanel = new JPanel(new BorderLayout());
			taskPanel.setName("taskPanel-" + i);
			taskPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
			taskPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			JLabel label = new JLabel(todo.getDescription()
					+ (todo.isDone() ? " ✔" : "")
					+ (!todo.getTags().isEmpty() ? " [" + todo.getTagsAsString() + "]" : ""));

			JButton tagBtn = new JButton("Tags");
			tagBtn.setName("taskTagButton-" + i);
			tagBtn.setVisible(false);

			final int index = i;

			tagBtn.addActionListener(ev -> {
				JPopupMenu menu = new JPopupMenu();

				JMenuItem createNew = new JMenuItem("Create new tag");
				createNew.setName("menuNewTag");
				createNew.addActionListener(e -> {
					isTagMode = true;
					tagTargetIndex = index;
					actionButton.setText("Add Tag");
					inputField.requestFocus();
					updateActionButtonState();
				});
				menu.add(createNew);

				JMenuItem markDone = new JMenuItem("Mark as Done");
				markDone.addActionListener(e -> {
					todoService.markDone(index);
					updateTodoList();
				});
				menu.add(markDone);

				for(String tag : todoService.getAllTags()) {
					JMenuItem tagItem = new JMenuItem(tag);
					tagItem.addActionListener(e -> {
						todoService.addTagToTodo(index, new Tag(tag));
						updateTodoList();
					});
					menu.add(tagItem);
				}
				menu.show(tagBtn, 0, tagBtn.getHeight());
			});

			taskPanel.add(label, BorderLayout.CENTER);
			taskPanel.add(tagBtn, BorderLayout.EAST);

			taskPanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (currentTagButtonShown != null) {
						currentTagButtonShown.setVisible(false);
					}
					tagBtn.setVisible(true);
					currentTagButtonShown = tagBtn;
				}
			});
			taskListPanel.add(taskPanel);
		}
		taskListPanel.revalidate();
		taskListPanel.repaint();
	}

	private static void updateTagPanel() {
		tagPanel.removeAll();
		tagPanel.add(new JLabel("Tags"));

		ButtonGroup group = new ButtonGroup();

		JRadioButton allBtn = new JRadioButton("All");
		allBtn.setName("radioTag_All");
		allBtn.setSelected("All".equals(currentFilterTag));
		allBtn.addActionListener(e -> {
			currentFilterTag = "All";
			deleteTagButton.setEnabled(false);
			updateTodoList();
		});
		group.add(allBtn);
		tagPanel.add(allBtn);

		JRadioButton doneBtn = new JRadioButton("Done");
		doneBtn.setName("radioTag_Done");
		doneBtn.setSelected("Done".equals(currentFilterTag));
		doneBtn.addActionListener(e -> {
			currentFilterTag = "Done";
			deleteTagButton.setEnabled(false);
			updateTodoList();
		});
		group.add(doneBtn);
		tagPanel.add(doneBtn);

		for (String tag : todoService.getAllTags()) {

			JRadioButton tagBtn = new JRadioButton(tag);
			tagBtn.setSelected(tag.equals(currentFilterTag));
			tagBtn.setName("radioTag_" + tag);
			tagBtn.addActionListener(e -> {
				currentFilterTag = tag;
				deleteTagButton.setEnabled(true);
				updateTodoList();
			});
			group.add(tagBtn);
			tagPanel.add(tagBtn);
		}

		deleteTagButton = new JButton("Remove Tag");
		deleteTagButton.setName("deleteTagButton");
		deleteTagButton.setEnabled(false);
		deleteTagButton.addActionListener(e -> {
					todoService.removeTagFromAll(currentFilterTag);
					currentFilterTag = "All";
					updateTodoList();
					updateTagPanel();
		});

		tagPanel.add(deleteTagButton);

		tagPanel.revalidate();
		tagPanel.repaint();
	}
}