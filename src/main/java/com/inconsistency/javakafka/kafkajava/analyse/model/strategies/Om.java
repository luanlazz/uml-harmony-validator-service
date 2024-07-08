package com.inconsistency.javakafka.kafkajava.analyse.model.strategies;

import java.util.HashSet;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.inconsistency.javakafka.kafkajava.analyse.model.AnalyseModel;
import com.inconsistency.javakafka.kafkajava.inconsistency.Context;
import com.inconsistency.javakafka.kafkajava.inconsistency.Inconsistency;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyError;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyType;
import com.inconsistency.javakafka.kafkajava.inconsistency.Severity;
import com.inconsistency.javakafka.kafkajava.uml.UMLModelDTO;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceDiagram;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceLifeline;

@Component
public class Om extends AnalyseModel {

	public Om() {
		super(new Inconsistency(InconsistencyType.Om, Severity.LOW, Context.SEQUENCE_DIAGRAM, "Objeto", ""));
	}

	@Override
	@KafkaListener(topics = "${spring.kafka.topic.model-analyze}", groupId = "om", clientIdPrefix = "om", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(ConsumerRecord<String, UMLModelDTO> record) {
		super.handleEvent(record);
	}

	@Override
	public void analyse() {
		Set<String> classesName = new HashSet<String>();

		SequenceDiagram sequenceDiagram = this.getUMLModel().getSequenceDiagram();

		for (SequenceLifeline lifeline : sequenceDiagram.getLifelines()) {
			if (classesName.add(lifeline.getLifelineName()) == false) {
				String errorMessage = "O objeto " + lifeline.getLifelineName() + " já foi definido no diagrama.";
				InconsistencyError error = new InconsistencyError(lifeline.getLifelineName(), this.getUMLModel().getSequenceDiagram().getPackage(), errorMessage);
				this.addError(error);
			}
		}
	}
}
