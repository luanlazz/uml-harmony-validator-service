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
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassDiagram;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceLifeline;

@Component
public class CnSD extends AnalyseModel {

	public CnSD() {
		super(new Inconsistency(InconsistencyType.CnSD, Severity.HIGH, Context.CLASS_SEQ_DIAGRAMS, "Classe",
				"CR-65 e CR-83"));
	}

	@Override
	@KafkaListener(topics = "${spring.kafka.topic.model-analyze}", groupId = "cnsd", clientIdPrefix = "cnsd", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(ConsumerRecord<String, UMLModelDTO> record) {
		super.handleEvent(record);
	}

	@Override
	public void analyse() {
		Map<String, SequenceLifeline> sequenceLifelineMap = new HashMap<>();

		for (SequenceLifeline lifeline : this.getUMLModel().getSequenceDiagram().getLifelines()) {
			sequenceLifelineMap.put(lifeline.getLifelineName(), lifeline);
		}

		ClassDiagram classDiagram = this.getUMLModel().getClassDiagram();

		for (ClassStructure classStructure : classDiagram.getClasses()) {
			if (!classStructure.isAbstract() && sequenceLifelineMap.get(classStructure.getName()) == null) {
				String errorMessage = "A classe " + classStructure.getName()
						+ " não foi instanciada no diagrama de sequencia.";
				InconsistencyError error = new InconsistencyError(classStructure.getName(), classStructure.getPackage(),
						errorMessage);
				this.addError(error);
			}
		}
	}
}
