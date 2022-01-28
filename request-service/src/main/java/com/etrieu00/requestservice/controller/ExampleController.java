package com.etrieu00.requestservice.controller;

import com.etrieu00.shared.model.Example;
import com.etrieu00.shared.service.JsonMapper;
import com.etrieu00.shared.service.KafkaSubscription;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Log4j2
@RestController("/api")
public class ExampleController {

  private final JsonMapper mapper;
  private final KafkaSubscription kafkaSubscription;
  private final KafkaSender<String, String> kafkaSender;

  @Autowired
  public ExampleController(JsonMapper mapper,
                           KafkaSubscription kafkaSubscription,
                           KafkaSender<String, String> kafkaSender) {
    this.mapper = mapper;
    this.kafkaSubscription = kafkaSubscription;
    this.kafkaSender = kafkaSender;
  }

  @PostMapping(value = "/example/{transaction}/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
  public Flux<Example> exampleEndpoint(@PathVariable String transaction, @RequestBody List<String> messages) {
    log.info(messages.toString());
    Flux.fromIterable(messages)
      .map(message -> new Example(transaction, message))
      .map(mapper::convertToString)
      .delayElements(Duration.ofSeconds(1))
      .flatMap(message -> kafkaSender.send(Mono.just(jsonToRecord(message)))
        .next()
        .map(res -> res.exception() == null ? "message sent!" : "message failed to send!"))
      .subscribe(System.out::println);
    Flux<Example> heartbeat = Flux.interval(Duration.ofSeconds(15))
      .take(10)
      .map(val -> new Example(transaction, "HEARTBEAT"));
    return kafkaSubscription
      .getEventPublisherOther()
      .filter(Objects::nonNull)
      .filter(example -> example.getId().equals(transaction))
      .mergeWith(heartbeat);
  }

  private SenderRecord<String, String, String> jsonToRecord(String json) {
    String uuid = UUID.randomUUID().toString();
    return SenderRecord.create(new ProducerRecord<>("ONE", json), uuid);
  }

}
