package com.inconsistency.javakafka.kafkajava.analyse.model;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.inconsistency.javakafka.kafkajava.entities.uml.dto.UMLModelDTO;

public interface IAnalyseModel {
	public void listenTopic(ConsumerRecord<String, String> record);

	public void handleEvent(ConsumerRecord<String, String> record);

	public void analyse();
}
