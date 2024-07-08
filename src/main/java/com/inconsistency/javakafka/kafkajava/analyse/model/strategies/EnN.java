package com.inconsistency.javakafka.kafkajava.analyse.model.strategies;

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
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceMessage;

@Component
public class EnN extends AnalyseModel {

	public EnN() {
		super(new Inconsistency(InconsistencyType.EnN, Severity.HIGH, Context.SEQUENCE_DIAGRAM, "Objeto", "CR-47"));
	}

	@Override
	@KafkaListener(topics = "${spring.kafka.topic.model-analyze}", groupId = "enn", clientIdPrefix = "enn", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(ConsumerRecord<String, UMLModelDTO> record) {
		super.handleEvent(record);
	}

	@Override
	public void analyse() {
		SequenceDiagram sequenceDiagram = this.getUMLModel().getSequenceDiagram();

		for (SequenceMessage sequenceMessage : sequenceDiagram.getMessages()) {
			if (sequenceMessage.getMessageName().isEmpty()) {
				String errorMessage = "O objeto " + sequenceMessage.getSender().getLifelineName()
						+ " possui uma mensagem sem nome.";
				InconsistencyError error = new InconsistencyError(sequenceMessage.getSender().getLifelineName(),
						this.getUMLModel().getSequenceDiagram().getPackage(), errorMessage);
				this.addError(error);
			}
		}
	}
}
