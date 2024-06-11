package com.inconsistency.javakafka.kafkajava.analyse.model;

import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;

import com.inconsistency.javakafka.kafkajava.uml.reader.diagram.DiagramProperties;

public interface IAnalyseModel {
	public void listenTopic(@Payload DiagramProperties payload, Acknowledgment ack);

	public void handleEvent(@Payload DiagramProperties payload, Acknowledgment ack);

	public void analyse();
}
