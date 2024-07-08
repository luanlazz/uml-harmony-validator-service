package com.inconsistency.javakafka.kafkajava.analyse.model.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class EcM extends AnalyseModel {

	public EcM() {
		super(new Inconsistency(InconsistencyType.EcM, Severity.MEDIUM, Context.CLASS_SEQ_DIAGRAMS, "Mensagem",
				"CR-78"));
	}

	@Override
	@KafkaListener(topics = "${spring.kafka.topic.model-analyze}", groupId = "ecm", clientIdPrefix = "ecm", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(ConsumerRecord<String, UMLModelDTO> record) {
		super.handleEvent(record);
	}

	@Override
	public void analyse() {
		Map<String, ClassStructure> classMethodsMap = new HashMap<>();
		for (ClassStructure classStructure : this.getUMLModel().getClassDiagram().getClasses()) {
			classMethodsMap.put(classStructure.getName(), classStructure);
		}

		for (SequenceMessage sequenceMessage : this.getUMLModel().getSequenceDiagram().getMessages()) {
			if (sequenceMessage.getMessageType() == null || sequenceMessage.getMessageName().isEmpty()
					|| !(sequenceMessage.getMessageType().equals("createMessage")
							|| sequenceMessage.getMessageType().equals("synchCall")
							|| sequenceMessage.getMessageType().equals("asynchCall")
							|| sequenceMessage.getMessageType().equals("asynchSignal")
							|| sequenceMessage.getMessageType().equals("createMessage"))) {
				continue;
			}

			String messageName = sequenceMessage.getMessageName();
			String receiverName = sequenceMessage.getReceiver().getLifelineName();
			ClassStructure classReceiver = classMethodsMap.get(receiverName);
			List<ClassOperation> receiverMethods = new ArrayList<>();
			if (classReceiver != null) {
				receiverMethods = classReceiver.getOperations();
			}

			ClassOperation receiverOperation = receiverMethods.stream().filter(op -> {
				return op.getName().equals(messageName);
			}).findFirst().orElse(null);

			if (receiverOperation == null) {
				String errorMessage = "A mensagem " + messageName + " não foi definida na classe "
						+ (classReceiver != null ? classReceiver.getName() : receiverName) + ".";
				InconsistencyError error = new InconsistencyError(messageName, this.getUMLModel().getSequenceDiagram().getPackage(), errorMessage);
				this.addError(error);
			}

		}
	}
}
