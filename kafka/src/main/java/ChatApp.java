import javax.swing.*;

public class ChatApp {

    private static String topic = "chat";
    private static boolean isFirstRun = true;

    public ChatApp(String computerId) {
        ChatProducer producer = new ChatProducer(topic, computerId);
        ChatGui gui = new ChatGui(producer, computerId);
        ChatConsumer consumer = new ChatConsumer(topic, gui);

        if (isFirstRun) {
            producer.sendResetMessage();
            isFirstRun = false;
        }

        producer.sendLoginMessage();
        consumer.startListening();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JPanel panel = new JPanel();
            JTextField computerIdField = new JTextField(10);
            panel.add(new JLabel("Imię:"));
            panel.add(computerIdField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Wprowadź Imię: ", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String computerId = computerIdField.getText().trim();
                if (!computerId.isEmpty()) {
                    new ChatApp(computerId);
                }
            } else {
                System.exit(0);
            }
        });
    }
}
