import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ChatGui {

    private JTextArea textArea;
    private JTextField textField;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private ChatProducer producer;
    private String computerId;

    public ChatGui(ChatProducer producer, String computerId) {
        this.producer = producer;
        this.computerId = computerId;

        JFrame frame = new JFrame("Chat Application - " + computerId);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel();
        JButton newUserButton = new JButton("Zaloguj nowego użytkownika");
        topPanel.add(newUserButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        textArea = new JTextArea();
        textArea.setEditable(false);
        mainPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        textField = new JTextField();
        mainPanel.add(textField, BorderLayout.SOUTH);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setPreferredSize(new Dimension(150, 0));
        mainPanel.add(new JScrollPane(userList), BorderLayout.EAST);

        frame.add(mainPanel);
        frame.setVisible(true);

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textField.getText();
                if (!message.trim().isEmpty()) {
                    if (message.equals("23")) {
                        openWebPage("https://imgflip.com/i/8ubpb7");
                    } else {
                        producer.sendMessage(message);
                    }
                    textField.setText("");
                }
            }
        });

        newUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel loginPanel = new JPanel();
                JTextField newComputerIdField = new JTextField(10);
                loginPanel.add(new JLabel("Imię:"));
                loginPanel.add(newComputerIdField);

                int result = JOptionPane.showConfirmDialog(null, loginPanel, "Zaloguj nowego użytkownika", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    String newComputerId = newComputerIdField.getText().trim();
                    if (!newComputerId.isEmpty()) {
                        new ChatApp(newComputerId);
                    }
                }
            }
        });

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                producer.sendLogoutMessage();
                producer.close();
            }
        });
    }

    public void appendMessage(String message) {
        if (!message.contains("RESET_USERS") && !message.contains("RESET_MESSAGES")) {
            textArea.append(message + "\n");
        }
    }

    public void updateUserList(String newUser) {
        if (!userListModel.contains(newUser)) {
            userListModel.addElement(newUser);
        }
    }

    public void removeUserFromList(String user) {
        userListModel.removeElement(user);
    }

    public void clearUserList() {
        userListModel.clear();
    }

    public void clearMessages() {
        textArea.setText("");
    }

    private void openWebPage(String urlString) {
        try {
            URI uri = new URI(urlString);
            Desktop.getDesktop().browse(uri);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
