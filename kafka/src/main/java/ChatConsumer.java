import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

public class ChatConsumer {

    private KafkaConsumer<String, String> consumer;
    private ChatGui gui;
    private Set<String> activeUsers;

    public ChatConsumer(String topic, ChatGui gui) {
        this.gui = gui;
        this.activeUsers = new HashSet<>();

        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.15:9092");
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-" + Math.random());

        consumer = new KafkaConsumer<>(consumerProperties);
        consumer.subscribe(Collections.singletonList(topic));
    }

    public void startListening() {
        new Thread(() -> {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    String message = record.value();
                    if (message.equals("RESET_USERS")) {
                        gui.clearUserList();
                        activeUsers.clear();
                    } else if (message.equals("RESET_MESSAGES")) {
                        gui.clearMessages();
                    } else if (message.startsWith("LOGIN: ")) {
                        String newUser = message.split(": ")[1];
                        if (activeUsers.add(newUser)) {
                            gui.updateUserList(newUser);
                            gui.appendMessage("[" + getCurrentTime() + "] " + newUser + " has logged in.");
                        }
                    } else if (message.startsWith("LOGOUT: ")) {
                        String userLeft = message.split(": ")[1];
                        if (activeUsers.remove(userLeft)) {
                            gui.removeUserFromList(userLeft);
                            gui.appendMessage("[" + getCurrentTime() + "] " + userLeft + " has logged out.");
                        }
                    } else {
                        gui.appendMessage(message);
                    }
                }
            }
        }).start();
    }



    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }
}
