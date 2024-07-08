package com.inconsistency.javakafka.kafkajava.analyse.model.strategies;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.inconsistency.javakafka.kafkajava.analyse.model.AnalyseModel;
import com.inconsistency.javakafka.kafkajava.entities.Context;
import com.inconsistency.javakafka.kafkajava.entities.Inconsistency;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyError;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyType;
import com.inconsistency.javakafka.kafkajava.entities.Severity;
import com.inconsistency.javakafka.kafkajava.entities.uml.dto.UMLModelDTO;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceDiagram;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceLifeline;

@Component
public class CaSD extends AnalyseModel {

	public CaSD() {
		super(new Inconsistency(InconsistencyType.CaSD, Severity.MEDIUM, Context.CLASS_SEQ_DIAGRAMS, "Objeto",
				"CR-76"));
	}

	@Override
	@KafkaListener(topics = "${spring.kafka.topic.model-analyze}", groupId = "casd", clientIdPrefix = "casd", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(ConsumerRecord<String, UMLModelDTO> record) {
		super.handleEvent(record);
	}

	@Override
	public void analyse() {
		Map<String, ClassStructure> abstractClassesMap = new HashMap<>();

		for (ClassStructure classStructure : this.getUMLModel().getClassDiagram().getClasses()) {
			if (classStructure.isAbstract()) {
				abstractClassesMap.put(classStructure.getName(), classStructure);
			}
		}

		SequenceDiagram sequenceDiagram = this.getUMLModel().getSequenceDiagram();

		for (SequenceLifeline sequenceLifeLine : sequenceDiagram.getLifelines()) {
			String seqllName = sequenceLifeLine.getLifelineName();
			ClassStructure abstractClass = abstractClassesMap.get(seqllName);
			if (abstractClass != null) {
				String errorMessage = "O objeto " + seqllName
						+ " instanciado no diagrama de sequência, se refere a uma classe abstrata definida no diagrama de classes.";
				InconsistencyError error = new InconsistencyError(seqllName, abstractClass.getPackage(), errorMessage);
				this.addError(error);
			}
		}
	}
}
