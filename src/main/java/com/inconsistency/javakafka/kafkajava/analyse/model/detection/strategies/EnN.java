package com.inconsistency.javakafka.kafkajava.analyse.model.detection.strategies;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.inconsistency.javakafka.kafkajava.analyse.model.DetectionStrategy;
import com.inconsistency.javakafka.kafkajava.entities.Inconsistency;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyError;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyType;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceMessage;

@Component
public class EnN extends DetectionStrategy {

	public EnN() {
		super(new Inconsistency(InconsistencyType.EnN, "SD", "UML"));
	}

	@Override
	@KafkaListener(topics = "${spring.kafka.topic.model-analyze}", groupId = "enn", clientIdPrefix = "enn", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(ConsumerRecord<String, String> record) {
		super.handleEvent(record);
	}

	@Override
	public void analyse() {
		for (SequenceMessage sequenceMessage : this.getUMLModel().getMessages()) {
			if (sequenceMessage.getMessageName().isEmpty()) {
				String errorMessage = messageService.get("inconsistency.message.enn");
				InconsistencyError error = new InconsistencyError(sequenceMessage.getId(),
						sequenceMessage.getParentId(), errorMessage);
				this.generateInconsistencyNotification(error);
			}
		}
	}
}
