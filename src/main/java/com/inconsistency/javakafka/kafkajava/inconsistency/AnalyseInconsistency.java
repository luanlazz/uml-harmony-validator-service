package com.inconsistency.javakafka.kafkajava.inconsistency;

import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;

import com.inconsistency.javakafka.kafkajava.uml.reader.diagram.DiagramProperties;

public interface AnalyseInconsistency {
	public void listenTopic(@Payload DiagramProperties payload, Acknowledgment ack);

	public void analyse();
}
