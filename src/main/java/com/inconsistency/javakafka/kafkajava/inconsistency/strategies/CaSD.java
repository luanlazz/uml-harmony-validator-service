package com.inconsistency.javakafka.kafkajava.inconsistency.strategies;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.inconsistency.javakafka.kafkajava.inconsistency.AnalyseInconsistency;
import com.inconsistency.javakafka.kafkajava.inconsistency.Inconsistency;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyError;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyType;
import com.inconsistency.javakafka.kafkajava.inconsistency.Severity;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceLifeline;
import com.inconsistency.javakafka.kafkajava.uml.reader.diagram.DiagramProperties;

@Component
public class CaSD extends Inconsistency implements AnalyseInconsistency {

	public CaSD() {
		super(InconsistencyType.CaSD, Severity.MEDIUM);
	}

	@Override
	@KafkaListener(topics = "uml.inconsistency.casd", groupId="uml-analyse-inconsistency", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(@Payload DiagramProperties payload, Acknowledgment ack) {
		super.listenTopic(payload, ack);
	}

	@Override
	public void analyse() {
		Map<String, ClassStructure> classesMessageMap = new HashMap<>();

		for (ClassStructure classStructure : this.getClassDiagram().getClasses()) {
			classesMessageMap.put(classStructure.getName(), classStructure);
		}

		for (SequenceLifeline sequenceLifeLine : this.getSequenceDiagram().getLifelines()) {
			String sequenceObj = sequenceLifeLine.getLifelineName();
			ClassStructure classObj = classesMessageMap.get(sequenceObj);

			if (classObj != null && classObj.isAbstract()) {
				String errorMessage = "A classe " + classObj.getName()
						+ " é abstrata, foi instanciada no diagrama de sequencia.";
				InconsistencyError error = new InconsistencyError("class", classObj.getName(), classObj.getPackage(),
						errorMessage);
				this.addError(error);
			}
		}
	}
}
