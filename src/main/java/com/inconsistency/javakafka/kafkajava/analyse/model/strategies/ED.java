package com.inconsistency.javakafka.kafkajava.analyse.model.strategies;

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
public class ED extends AnalyseModel {

	public ED() {
		super(new Inconsistency(InconsistencyType.ED, Severity.MEDIUM, Context.CLASS_SEQ_DIAGRAMS, "Mensagem",
				"CR-63"));
	}

	@Override
	@KafkaListener(topics = "${spring.kafka.topic.model-analyze}", groupId = "ed", clientIdPrefix = "ed", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(ConsumerRecord<String, UMLModelDTO> record) {
		super.handleEvent(record);
	}

	public void analyse() {
		Map<String, List<ClassOperation>> classMethodsMap = new HashMap<>();
		for (ClassStructure classStructure : this.getUMLModel().getClassDiagram().getClasses()) {
			classMethodsMap.put(classStructure.getName(), classStructure.getOperations());
		}

		for (SequenceMessage sequenceMessage : this.getUMLModel().getSequenceDiagram().getMessages()) {
			if (sequenceMessage.getMessageType() == null || !(sequenceMessage.getMessageType().equals("createMessage")
					|| sequenceMessage.getMessageType().equals("synchCall")
					|| sequenceMessage.getMessageType().equals("asynchCall")
					|| sequenceMessage.getMessageType().equals("asynchSignal")
					|| sequenceMessage.getMessageType().equals("createMessage"))) {
				continue;
			}

			String messageName = sequenceMessage.getMessageName();
			String receiverName = sequenceMessage.getReceiver().getLifelineName();

			List<ClassOperation> receiverMethods = classMethodsMap.get(receiverName);
			ClassOperation receiverOperation = receiverMethods.stream().filter(op -> {
				return op.getName().equals(messageName);
			}).findFirst().orElse(null);

			if (receiverOperation != null) {
				continue;
			}

			String senderName = sequenceMessage.getSender().getLifelineName();

			List<ClassOperation> senderMethods = classMethodsMap.get(senderName);
			ClassOperation senderOperation = senderMethods.stream().filter(op -> {
				return op.getName().equals(messageName);
			}).findFirst().orElse(null);

			if (senderOperation != null) {
				String errorMessage = "A mensagem " + messageName
						+ " está na direção errada pois, está definida na classe " + senderName + ".";
				InconsistencyError error = new InconsistencyError(messageName,
						this.getUMLModel().getSequenceDiagram().getPackage(), errorMessage);
				this.addError(error);
			}
		}
	}
}
