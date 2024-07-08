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
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassOperation;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceMessage;

@Component
public class EpM extends AnalyseModel {

	public EpM() {
		super(new Inconsistency(InconsistencyType.EpM, Severity.MEDIUM, Context.CLASS_SEQ_DIAGRAMS, "Mensagem",
				"CR-84"));
	}

	@Override
	@KafkaListener(topics = "${spring.kafka.topic.model-analyze}", groupId = "epm", clientIdPrefix = "epm", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(ConsumerRecord<String, UMLModelDTO> record) {
		super.handleEvent(record);
	}

	@Override
	public void analyse() {
		Map<String, ClassStructure> classesMessageMap = new HashMap<>();

		for (ClassStructure classStructure : this.getUMLModel().getClassDiagram().getClasses()) {
			classesMessageMap.put(classStructure.getName(), classStructure);
		}

		for (SequenceMessage sequenceMessage : this.getUMLModel().getSequenceDiagram().getMessages()) {
			if (sequenceMessage.getMessageType() != null && (sequenceMessage.getMessageType().equals("createMessage")
					|| sequenceMessage.getMessageType().equals("synchCall")
					|| sequenceMessage.getMessageType().equals("asynchCall"))) {
				String messageName = sequenceMessage.getMessageName();

				String receiverName = sequenceMessage.getReceiver().getLifelineName();
				ClassStructure classReceiver = classesMessageMap.get(receiverName);
				ClassOperation classOperation = classReceiver.getOperations().stream()
						.filter(op -> op.getName().equals(messageName)).findFirst().orElse(null);

				if (classOperation != null && classOperation.getVisibility().equals("private")) {
					String errorMessage = "A mensagem " + messageName + " é privada na classe "
							+ classReceiver.getName() + ".";
					InconsistencyError error = new InconsistencyError(messageName, classReceiver.getPackage(),
							errorMessage);
					this.addError(error);
				}
			}

		}
	}
}
