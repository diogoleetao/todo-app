package com.project.todo_app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class App {
	private static JFrame frame;

    private static TodoService todoService = new TodoService();
    private static DefaultListModel<String> listModel = new DefaultListModel<>();
    
    public static JFrame getCurrentFrame() {
        return frame;
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Todo App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JTextField inputField = new JTextField();
        JButton addButton = new JButton("Add Todo");
        listModel = new DefaultListModel<>();
        JList<String> todoList = new JList<>(listModel);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(todoList), BorderLayout.CENTER);

        addButton.addActionListener((ActionEvent e) -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                todoService.addTodo(text);
                listModel.addElement(text);
                inputField.setText("");
            }
        });

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

}
