package com.inconsistency.javakafka.kafkajava.analyse.model.strategies;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.inconsistency.javakafka.kafkajava.analyse.model.AnalyseModel;
import com.inconsistency.javakafka.kafkajava.inconsistency.Inconsistency;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyError;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyType;
import com.inconsistency.javakafka.kafkajava.inconsistency.Severity;
import com.inconsistency.javakafka.kafkajava.uml.UMLModelDTO;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassDiagram;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassOperation;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceDiagram;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceMessage;

@Component
public class EcM extends AnalyseModel {

	public EcM() {
		super(new Inconsistency(InconsistencyType.EcM, Severity.MEDIUM));
	}

	@Override
	@KafkaListener(topics = "${spring.kafka.topic.model-analyze}", groupId = "ecm", clientIdPrefix = "ecm", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(ConsumerRecord<String, UMLModelDTO> record) {
		super.handleEvent(record);
	}

	@Override
	public void analyse() {
		Map<String, ClassStructure> classesMessageMap = new HashMap<>();

		ClassDiagram classDiagram = this.getUMLModel().getClassDiagram();

		for (ClassStructure classStructure : classDiagram.getClasses()) {
			classesMessageMap.put(classStructure.getName(), classStructure);
		}

		SequenceDiagram sequenceDiagram = this.getUMLModel().getSequenceDiagram();

		for (SequenceMessage sequenceMessage : sequenceDiagram.getMessages()) {
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
