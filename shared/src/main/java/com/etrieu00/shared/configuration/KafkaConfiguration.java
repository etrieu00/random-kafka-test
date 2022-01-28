package com.etrieu00.shared.configuration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.internals.ConsumerFactory;
import reactor.kafka.receiver.internals.DefaultKafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.internals.DefaultKafkaSender;
import reactor.kafka.sender.internals.ProducerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

  private final Map<String, Object> configPropsSender;

  public KafkaConfiguration() {
    this.configPropsSender = new HashMap<>();
    configPropsSender.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    configPropsSender.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configPropsSender.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configPropsSender.put(ProducerConfig.RETRIES_CONFIG, 10);
    configPropsSender.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000");
    configPropsSender.put(ProducerConfig.BATCH_SIZE_CONFIG, "163850"); // 163KByte
    configPropsSender.put(ProducerConfig.LINGER_MS_CONFIG, "100");
    configPropsSender.put(ProducerConfig.ACKS_CONFIG, "1");
    configPropsSender.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "1");
  }

  @Bean("receiver-one")
  public KafkaReceiver<String,String> kafkaReceiver() {
    Map<String, Object> configPropsReceiver = new HashMap<>();
    configPropsReceiver.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    configPropsReceiver.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    configPropsReceiver.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    configPropsReceiver.put(ConsumerConfig.CLIENT_ID_CONFIG, "one-client");
    configPropsReceiver.put(ConsumerConfig.GROUP_ID_CONFIG, "group-id");
    configPropsReceiver.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
    return new DefaultKafkaReceiver(ConsumerFactory.INSTANCE,
      ReceiverOptions.create(configPropsReceiver).subscription(Collections.singletonList("ONE"))
    );
  }

  @Bean("sender-one")
  public KafkaSender<String, String> kafkaSender() {
    return new DefaultKafkaSender<>(ProducerFactory.INSTANCE,
      SenderOptions.create(configPropsSender));
  }

  @Bean("receiver-two")
  public KafkaReceiver<String,String> kafkaReceiverTwo() {
    Map<String, Object> configPropsReceiver = new HashMap<>();
    configPropsReceiver.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    configPropsReceiver.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    configPropsReceiver.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    configPropsReceiver.put(ConsumerConfig.CLIENT_ID_CONFIG, "two-client");
    configPropsReceiver.put(ConsumerConfig.GROUP_ID_CONFIG, "group-id");
    configPropsReceiver.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
    return new DefaultKafkaReceiver(ConsumerFactory.INSTANCE,
      ReceiverOptions.create(configPropsReceiver).subscription(Collections.singletonList("TWO"))
    );
  }

}
