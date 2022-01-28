package com.etrieu00.shared.service;

import com.etrieu00.shared.model.Example;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.kafka.receiver.KafkaReceiver;

import javax.annotation.PostConstruct;

@Service
public class KafkaSubscription {

  private final KafkaReceiver<String, String> kafkaReceiverOne;
  private final KafkaReceiver<String, String> kafkaReceiverTwo;
  private final JsonMapper mapper;
  private ConnectableFlux<Example> eventPublisherOne;
  private ConnectableFlux<Example> eventPublisherTwo;

  public KafkaSubscription(@Qualifier("receiver-one") KafkaReceiver<String, String> kafkaReceiverOne,
                           @Qualifier("receiver-two") KafkaReceiver<String, String> kafkaReceiverTwo,
                           JsonMapper mapper) {
    this.kafkaReceiverOne = kafkaReceiverOne;
    this.kafkaReceiverTwo = kafkaReceiverTwo;
    this.mapper = mapper;
  }

  @PostConstruct
  public void init() {
    eventPublisherOne = kafkaReceiverOne.receive()
      .map(message -> mapper.convertToObject(message.value(), Example.class))
      .publish();
    eventPublisherTwo = kafkaReceiverTwo.receive()
      .map(message -> mapper.convertToObject(message.value(), Example.class))
      .publish();
    eventPublisherOne.connect();
    eventPublisherTwo.connect();
  }

  public ConnectableFlux<Example> getEventPublisher() {
    return eventPublisherOne;
  }

  public ConnectableFlux<Example> getEventPublisherOther() {
    return eventPublisherTwo;
  }
}