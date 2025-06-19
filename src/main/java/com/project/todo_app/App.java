package com.project.todo_app;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;


public class App {
    private static JFrame frame;
    private static TodoService todoService = new TodoService();
    private static JTextField inputField;
    private static JButton actionButton;
    private static JPanel tagPanel;
    private static boolean isTagMode = false;
    private static int tagTargetIndex = -1;
    private static String currentFilterTag = "All";
    private static JButton deleteTagButton;

    public static JFrame getCurrentFrame() {
        return frame;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        frame = new JFrame("Todo App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel(new BorderLayout());

        inputField = new JTextField();
        actionButton = new JButton("Add Task");
        actionButton.setEnabled(false);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(actionButton, BorderLayout.EAST);

        JPanel taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(taskListPanel);

        tagPanel = new JPanel();
        tagPanel.setLayout(new BoxLayout(tagPanel, BoxLayout.Y_AXIS));
        updateTagPanel(taskListPanel);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(tagPanel, BorderLayout.EAST);

        actionButton.addActionListener(e -> {
            String text = inputField.getText().trim();
            if (text.isEmpty()) return;

            if (isTagMode) {
                boolean success = todoService.addTagToTodo(tagTargetIndex, new Tag(text));
                if (!success) {
                    showError("This tag already exists for the task.");
                }
                isTagMode = false;
                tagTargetIndex = -1;
                actionButton.setText("Add Task");
                updateTodoList(taskListPanel);
                updateTagPanel(taskListPanel);
            } else {
                boolean success = todoService.addTodo(text);
                if (!success) {
                    showError("Task already exists.");
                }
                updateTodoList(taskListPanel);
                updateTagPanel(taskListPanel);
            }

            inputField.setText("");
            actionButton.setEnabled(false);
        });

        inputField.getDocument().addDocumentListener(new DocumentListener() {
            void update() {
                actionButton.setEnabled(!inputField.getText().trim().isEmpty());
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    private static void showError(String msg) {
        JOptionPane.showMessageDialog(frame, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static void updateTodoList(JPanel taskListPanel) {
        taskListPanel.removeAll();
        List<Todo> todos = "All".equals(currentFilterTag)
                ? todoService.getAllTodos()
                : todoService.getTodosFilteredByTag(currentFilterTag);

        for (int i = 0; i < todos.size(); i++) {
            Todo todo = todos.get(i);

            JPanel taskPanel = new JPanel(new BorderLayout());
            taskPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            JLabel label = new JLabel(todo.getDescription()
                    + (todo.isDone() ? " ✔" : "")
                    + (!todo.getTags().isEmpty() ? " [" + todo.getTagsAsString() + "]" : ""));

            JButton tagBtn = new JButton("Tags");
            tagBtn.setVisible(false);

            final int index = i;

            tagBtn.addActionListener(ev -> {
                JPopupMenu menu = new JPopupMenu();

                JMenuItem createNew = new JMenuItem("Create new tag");
                createNew.addActionListener(e -> {
                    isTagMode = true;
                    tagTargetIndex = index;
                    actionButton.setText("Add Tag");
                    inputField.requestFocus();
                });
                menu.add(createNew);

                JMenuItem markDone = new JMenuItem("Mark as Done");
                markDone.addActionListener(e -> {
                    todoService.markDone(index);
                    updateTodoList(taskListPanel);
                });
                menu.add(markDone);

                for (String tag : todoService.getAllTags()) {
                    JMenuItem tagItem = new JMenuItem(tag);
                    tagItem.addActionListener(e -> {
                        todoService.addTagToTodo(index, new Tag(tag));
                        updateTodoList(taskListPanel);
                    });
                    menu.add(tagItem);
                }

                menu.show(tagBtn, 0, tagBtn.getHeight());
            });

            taskPanel.add(label, BorderLayout.CENTER);
            taskPanel.add(tagBtn, BorderLayout.EAST);

            taskPanel.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    tagBtn.setVisible(true);
                }

                public void mouseExited(MouseEvent e) {
                    Timer timer = new Timer(100, evt -> tagBtn.setVisible(false));
                    timer.setRepeats(false);
                    timer.start();
                }
            });

            taskListPanel.add(taskPanel);
        }

        taskListPanel.revalidate();
        taskListPanel.repaint();
    }

    private static void updateTagPanel(JPanel taskListPanel) {
        tagPanel.removeAll();
        tagPanel.add(new JLabel("Tags"));

        ButtonGroup group = new ButtonGroup();

        JRadioButton allBtn = new JRadioButton("All");
        allBtn.setSelected("All".equals(currentFilterTag));
        allBtn.addActionListener(e -> {
            currentFilterTag = "All";
            deleteTagButton.setEnabled(false);
            updateTodoList(taskListPanel);
        });
        group.add(allBtn);
        tagPanel.add(allBtn);

        JRadioButton doneBtn = new JRadioButton("Done");
        doneBtn.setSelected("Done".equals(currentFilterTag));
        doneBtn.addActionListener(e -> {
            currentFilterTag = "Done";
            deleteTagButton.setEnabled(false);
            updateTodoList(taskListPanel);
        });
        group.add(doneBtn);
        tagPanel.add(doneBtn);

        for (String tag : todoService.getAllTags()) {
            if ("Done".equalsIgnoreCase(tag)) continue;

            JRadioButton tagBtn = new JRadioButton(tag);
            tagBtn.setSelected(tag.equals(currentFilterTag));
            tagBtn.addActionListener(e -> {
                currentFilterTag = tag;
                deleteTagButton.setEnabled(true);
                updateTodoList(taskListPanel);
            });
            group.add(tagBtn);
            tagPanel.add(tagBtn);
        }

        deleteTagButton = new JButton("Remove Tag");
        deleteTagButton.setEnabled(false);
        deleteTagButton.addActionListener(e -> {
            if (!"All".equals(currentFilterTag) && !"Done".equals(currentFilterTag)) {
                todoService.removeTagFromAll(currentFilterTag);
                currentFilterTag = "All";
                updateTodoList(taskListPanel);
                updateTagPanel(taskListPanel);
            }
        });
        tagPanel.add(deleteTagButton);

        tagPanel.revalidate();
        tagPanel.repaint();
    }
}
