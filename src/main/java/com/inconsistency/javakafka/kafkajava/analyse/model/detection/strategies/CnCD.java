package com.inconsistency.javakafka.kafkajava.analyse.model.detection.strategies;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.inconsistency.javakafka.kafkajava.analyse.model.DetectionStrategy;
import com.inconsistency.javakafka.kafkajava.entities.Inconsistency;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyError;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyType;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceLifeline;

@Component
public class CnCD extends DetectionStrategy {

	public CnCD() {
		super(new Inconsistency(InconsistencyType.CnCD, "SD,CD", "UML"));
	}

	@Override
	@KafkaListener(topics = "${spring.kafka.topic.model-analyze}", groupId = "cncd", clientIdPrefix = "cncd", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(ConsumerRecord<String, String> record) {
		super.handleEvent(record);
	}

	@Override
	public void analyse() {
		for (SequenceLifeline lifeline : this.getUMLModel().getLifelines()) {
			ClassStructure _class = this.getUMLModel().getClasses().stream().filter(c -> {
				return c.getName().equals(lifeline.getName());
			}).findFirst().orElse(null);

			if (_class == null) {
				String errorMessage = messageService.get("inconsistency.message.cncd");
				InconsistencyError error = new InconsistencyError(lifeline.getId(), lifeline.getParentId(),
						errorMessage);
				this.generateInconsistencyNotification(error);
			}
		}
	}
}
