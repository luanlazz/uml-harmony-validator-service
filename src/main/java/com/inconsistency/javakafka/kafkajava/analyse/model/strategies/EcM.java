package com.inconsistency.javakafka.kafkajava.analyse.model.strategies;

import java.util.HashMap;
import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.inconsistency.javakafka.kafkajava.analyse.model.AnalyseModel;
import com.inconsistency.javakafka.kafkajava.inconsistency.Inconsistency;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyError;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyType;
import com.inconsistency.javakafka.kafkajava.inconsistency.Severity;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassOperation;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceMessage;
import com.inconsistency.javakafka.kafkajava.uml.reader.diagram.DiagramProperties;

@Component
public class EcM extends AnalyseModel {

	public EcM(KafkaTemplate<String, Object> kafkaTemplate) {
		super(kafkaTemplate, new Inconsistency(InconsistencyType.EcM, Severity.MEDIUM));
	}

	@Override
	@KafkaListener(
		topics = "${spring.kafka.topic.model-analyze}", 
		groupId = "ecm", 
		clientIdPrefix = "ecm", 
		containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(@Payload DiagramProperties payload, Acknowledgment ack) {
		super.handleEvent(payload, ack);
	}

	@Override
	public void analyse() {
		Map<String, ClassStructure> classesMessageMap = new HashMap<>();

		for (ClassStructure classStructure : this.getClassDiagram().getClasses()) {
			classesMessageMap.put(classStructure.getName(), classStructure);
		}

		for (SequenceMessage sequenceMessage : this.getSequenceDiagram().getMessages()) {
			if (sequenceMessage.getMessageType() != null && (sequenceMessage.getMessageType().equals("createMessage")
					|| sequenceMessage.getMessageType().equals("synchCall")
					|| sequenceMessage.getMessageType().equals("asynchCall"))) {
				String message = sequenceMessage.getMessageName();
				String receiver = sequenceMessage.getReciver().getLifelineName();

				ClassStructure classReceiver = classesMessageMap.get(receiver);

				if (classReceiver != null) {
					boolean hasOperation = false;

					for (ClassOperation classOperation : classReceiver.getOperations()) {
						if (classOperation.getName().equals(message)) {
							hasOperation = true;
							break;
						}
					}

					if (!hasOperation) {
						String errorMessage = "A mensagem " + message + " não foi definida na classe "
								+ classReceiver.getName() + ".";
						InconsistencyError error = new InconsistencyError("message", message,
								classReceiver.getPackage(), errorMessage);
						this.addError(error);
					}
				}
			}

		}
	}
}
