package com.inconsistency.javakafka.kafkajava.analyse.model.strategies;

import java.util.HashMap;
import java.util.Map;

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
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceDiagram;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceLifeline;

@Component
public class CnCD extends AnalyseModel {

	public CnCD() {
		super(new Inconsistency(InconsistencyType.CnCD, Severity.HIGH, Context.CLASS_SEQ_DIAGRAMS, "Objeto",
				"CR-48 e CR-61"));
	}

	@Override
	@KafkaListener(topics = "${spring.kafka.topic.model-analyze}", groupId = "cncd", clientIdPrefix = "cncd", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(ConsumerRecord<String, UMLModelDTO> record) {
		super.handleEvent(record);
	}

	@Override
	public void analyse() {
		Map<String, ClassStructure> classesMessageMap = new HashMap<>();

		for (ClassStructure classStructure : this.getUMLModel().getClassDiagram().getClasses()) {
			classesMessageMap.put(classStructure.getName(), classStructure);
		}

		SequenceDiagram sequenceDiagram = this.getUMLModel().getSequenceDiagram();

		for (SequenceLifeline lifeline : sequenceDiagram.getLifelines()) {
			if (classesMessageMap.get(lifeline.getLifelineName()) == null) {
				String errorMessage = "O objeto " + lifeline.getLifelineName()
						+ " não foi definido no diagrama de classes.";
				InconsistencyError error = new InconsistencyError(lifeline.getLifelineName(),
						this.getUMLModel().getSequenceDiagram().getPackage(), errorMessage);
				this.addError(error);
			}
		}
	}
}
