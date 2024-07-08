package com.inconsistency.javakafka.kafkajava.analyse.model.strategies;

import java.util.HashSet;
import java.util.Set;

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
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassDiagram;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassStructure;

@Component
public class CM extends AnalyseModel {

	public CM() {
		super(new Inconsistency(InconsistencyType.CM, Severity.LOW, Context.CLASS_DIAGRAM, "Classe", ""));
	}

	@Override
	@KafkaListener(topics = "${spring.kafka.topic.model-analyze}", groupId = "cm", clientIdPrefix = "cm", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(ConsumerRecord<String, UMLModelDTO> record) {
		super.handleEvent(record);
	}

	@Override
	public void analyse() {
		Set<String> classesName = new HashSet<String>();

		ClassDiagram classDiagram = this.getUMLModel().getClassDiagram();

		for (ClassStructure cs : classDiagram.getClasses()) {
			if (classesName.add(cs.getName()) == false) {
				String errorMessage = "A classe " + cs.getName() + " já foi definida no diagrama";
				InconsistencyError error = new InconsistencyError(cs.getName(), cs.getPackage(), errorMessage);
				this.addError(error);
			}
		}
	}
}
