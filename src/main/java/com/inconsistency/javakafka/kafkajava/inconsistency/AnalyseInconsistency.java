package com.inconsistency.javakafka.kafkajava.inconsistency;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.messaging.handler.annotation.Payload;

import com.inconsistency.javakafka.kafkajava.uml.reader.diagram.DiagramProperties;

public interface AnalyseInconsistency {
	public void listenTopic(ConsumerRecord<String, DiagramProperties> cr, @Payload DiagramProperties payload);

	public void analyse();
}
