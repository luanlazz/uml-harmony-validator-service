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
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.uml.reader.diagram.DiagramProperties;

@Component
public class CM extends Inconsistency {
	
	public CM() {
		super(InconsistencyType.CM, Severity.LOW);
	}
	
	@Override
	@KafkaListener(topics = "uml.inconsistency.cm", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(@Payload DiagramProperties payload, Acknowledgment ack) {
		super.listenTopic(payload, ack);
	}
	
	@Override
	public void analyse() {		
		Set<String> classesName = new HashSet<String>();
		
		for (ClassStructure cs : this.getClassDiagram().getClasses()) {
			if (classesName.add(cs.getName()) == false) {
				String errorMessage = "A classe " + cs.getName() + " já foi definida no diagrama";
				InconsistencyError error = new InconsistencyError("class", cs.getName(), cs.getPackage(), errorMessage);
				this.addError(error);
		     }
        }		
	}
}
