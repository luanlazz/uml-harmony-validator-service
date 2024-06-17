package com.inconsistency.javakafka.kafkajava.analyse.model.strategies;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.inconsistency.javakafka.kafkajava.analyse.model.AnalyseModel;
import com.inconsistency.javakafka.kafkajava.inconsistency.Inconsistency;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyError;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyType;
import com.inconsistency.javakafka.kafkajava.inconsistency.Severity;
import com.inconsistency.javakafka.kafkajava.uml.UMLModelDTO;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassDiagram;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceDiagram;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceLifeline;

@Component
public class CnCD extends AnalyseModel {

	public CnCD() {
		super(new Inconsistency(InconsistencyType.CnCD, Severity.HIGH));
	}

	@Override
	@KafkaListener(topics = "${spring.kafka.topic.model-analyze}", groupId = "cncd", clientIdPrefix = "cncd", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(ConsumerRecord<String, UMLModelDTO> record) {
		super.handleEvent(record);
	}

	@Override
	public void analyse() {
		Map<String, ClassStructure> classesMessageMap = new HashMap<>();

		ClassDiagram classDiagram = this.getUMLModel().getClassDiagram();

		for (ClassStructure classStructure : classDiagram.getClasses()) {
			classesMessageMap.put(classStructure.getName(), classStructure);
		}

		SequenceDiagram sequenceDiagram = this.getUMLModel().getSequenceDiagram();

		for (SequenceLifeline lifeline : sequenceDiagram.getLifelines()) {
			if (classesMessageMap.get(lifeline.getLifelineName()) == null) {
				String errorMessage = "O objeto " + lifeline.getLifelineName()
						+ " não foi definido no diagrama de classes.";
				InconsistencyError error = new InconsistencyError("object", lifeline.getLifelineName(), null,
						errorMessage);
				this.addError(error);
			}
		}
	}
}
