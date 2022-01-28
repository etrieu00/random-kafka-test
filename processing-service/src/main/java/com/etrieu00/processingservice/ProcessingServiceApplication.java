package com.etrieu00.processingservice;

import com.etrieu00.shared.model.Example;
import com.etrieu00.shared.service.JsonMapper;
import com.etrieu00.shared.service.KafkaSubscription;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.EnableKafka;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.UUID;

@Log4j2
@EnableKafka
@SpringBootApplication(scanBasePackages = {
  "com.etrieu00.processingservice",
  "com.etrieu00.shared.*"
})
public class ProcessingServiceApplication {

  private final KafkaSubscription subscription;
  private final KafkaSender<String, String> kafkaSender;
  private final JsonMapper mapper;

  public ProcessingServiceApplication(KafkaSubscription subscription,
                                      KafkaSender<String, String> kafkaSender,
                                      JsonMapper mapper) {
    this.subscription = subscription;
    this.kafkaSender = kafkaSender;
    this.mapper = mapper;
  }

  public static void main(String[] args) {
    SpringApplication.run(ProcessingServiceApplication.class, args);
  }

  @EventListener(ApplicationStartedEvent.class)
  public void startUp() {
    subscription.getEventPublisher()
      .map(example -> new Example(example.getId(), "Hello " + example.getData()))
      .map(mapper::convertToString)
      .flatMap(processed -> kafkaSender.send(Mono.just(jsonToRecord(processed)))
        .next()
        .map(res -> res.exception() == null))
      .subscribe();
  }

  private SenderRecord<String, String, String> jsonToRecord(String json) {
    String uuid = UUID.randomUUID().toString();
    return SenderRecord.create(new ProducerRecord<>("TWO", json), uuid);
  }

}
