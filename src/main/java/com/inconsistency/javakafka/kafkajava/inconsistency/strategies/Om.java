package com.inconsistency.javakafka.kafkajava.inconsistency.strategies;

import java.util.HashSet;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.inconsistency.javakafka.kafkajava.inconsistency.Inconsistency;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyError;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyType;
import com.inconsistency.javakafka.kafkajava.inconsistency.Severity;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceLifeline;
import com.inconsistency.javakafka.kafkajava.uml.reader.diagram.DiagramProperties;

@Component
public class Om extends Inconsistency {
	
	public Om() {
		super(InconsistencyType.Om, Severity.LOW);
	}
	
	@Override
	@KafkaListener(topics = "uml.inconsistency.om", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(@Payload DiagramProperties payload, Acknowledgment ack) {
		super.listenTopic(payload, ack);
	}
	
	@Override
	public void analyse() {	
		Set<String> classesName = new HashSet<String>();
		
		for (SequenceLifeline lifeline : this.getSequenceDiagram().getLifelines()) {
			if (classesName.add(lifeline.getLifelineName()) == false) {
				String errorMessage = "O objeto " + lifeline.getLifelineName() + " já foi definido no diagrama.";
				InconsistencyError error = new InconsistencyError("message", lifeline.getLifelineName(), null, errorMessage);
				this.addError(error);
		     }
        }		
	}
}
