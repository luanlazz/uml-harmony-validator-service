package com.inconsistency.javakafka.kafkajava.analyse.model.strategies;

import java.util.HashSet;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.inconsistency.javakafka.kafkajava.analyse.model.AnalyseModelInconsistency;
import com.inconsistency.javakafka.kafkajava.entities.Inconsistency;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyError;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyType;
import com.inconsistency.javakafka.kafkajava.entities.uml.dto.UMLModelDTO;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceLifeline;

@Component
public class Om extends AnalyseModelInconsistency {

	public Om() {
		super(new Inconsistency(InconsistencyType.Om, "Objeto", ""));
	}

	@Override
	@KafkaListener(topics = "${spring.kafka.topic.model-analyze}", groupId = "om", clientIdPrefix = "om", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(ConsumerRecord<String, UMLModelDTO> record) {
		super.handleEvent(record);
	}

	@Override
	public void analyse() {
		Set<String> classesName = new HashSet<String>();

		for (SequenceLifeline lifeline : this.getUMLModel().getLifelines()) {
			if (classesName.add(lifeline.getLifelineName()) == false) {
				String errorMessage = "O objeto " + lifeline.getLifelineName() + " já foi definido no diagrama.";
				InconsistencyError error = new InconsistencyError(lifeline.getId(), lifeline.getParentId(),
						errorMessage);
				this.addError(error);
			}
		}
	}
}
