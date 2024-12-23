package com.inconsistency.javakafka.kafkajava.analyse.model;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface IDetectionStrategy {
	public void listenTopic(ConsumerRecord<String, String> record);

	public void handleEvent(ConsumerRecord<String, String> record);

	public void analyse();
}
