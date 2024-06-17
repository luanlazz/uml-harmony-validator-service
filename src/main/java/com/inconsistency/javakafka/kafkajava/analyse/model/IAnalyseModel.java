package com.inconsistency.javakafka.kafkajava.analyse.model;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.inconsistency.javakafka.kafkajava.uml.UMLModelDTO;

public interface IAnalyseModel {
	public void listenTopic(ConsumerRecord<String, UMLModelDTO> record);

	public void handleEvent(ConsumerRecord<String, UMLModelDTO> record);

	public void analyse();
}
