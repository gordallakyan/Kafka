import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class ChatProducer {

    private KafkaProducer<String, String> producer;
    private String computerId;
    private String topic;

    public ChatProducer(String topic, String computerId) {
        this.topic = topic;
        this.computerId = computerId;

        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.15:9092");
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        producer = new KafkaProducer<>(producerProperties);
    }

    public void sendMessage(String message) {
        String timestamp = getCurrentTime();
        String formattedMessage = "[" + timestamp + "] " + computerId + ": " + message;
        producer.send(new ProducerRecord<>(topic, computerId, formattedMessage));
    }

    public void sendLoginMessage() {
        String message = "LOGIN: " + computerId;
        producer.send(new ProducerRecord<>(topic, computerId, message));
    }

    public void sendLogoutMessage() {
        String message = "LOGOUT: " + computerId;
        producer.send(new ProducerRecord<>(topic, computerId, message));
    }

    public void sendResetMessage() {
        producer.send(new ProducerRecord<>(topic, "RESET_USERS"));
        producer.send(new ProducerRecord<>(topic, "RESET_MESSAGES"));
    }

    public void close() {
        producer.close();
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }
}
