package com.inconsistency.javakafka.kafkajava.analyse.model.strategies;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.inconsistency.javakafka.kafkajava.analyse.model.AnalyseModelInconsistency;
import com.inconsistency.javakafka.kafkajava.entities.Inconsistency;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyError;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyType;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassOperation;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceDiagram;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceMessage;

@Component
public class ED extends AnalyseModelInconsistency {

	public ED() {
		super(new Inconsistency(InconsistencyType.ED, "Mensagem", "CR-63"));
	}

	@Override
	@KafkaListener(topics = "${spring.kafka.topic.model-analyze}", groupId = "ed", clientIdPrefix = "ed", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(ConsumerRecord<String, String> record) {
		super.handleEvent(record);
	}

	public void analyse() {
		for (SequenceDiagram sequenceDiagram : this.getUMLModel().getSequenceDiagram()) {
			for (SequenceMessage sequenceMessage : sequenceDiagram.getMessages()) {
				if (sequenceMessage.getMessageType() == null
						|| !(sequenceMessage.getMessageType().equals("createMessage")
								|| sequenceMessage.getMessageType().equals("synchCall")
								|| sequenceMessage.getMessageType().equals("asynchCall")
								|| sequenceMessage.getMessageType().equals("asynchSignal")
								|| sequenceMessage.getMessageType().equals("createMessage"))) {
					continue;
				}

				String messageName = sequenceMessage.getMessageName();
				String receiverName = sequenceMessage.getReceiver().getLifelineName();

				List<ClassStructure> classesReceiver = this.getUMLModel().getClasses().stream().filter(c -> {
					return c.getName().equals(receiverName);
				}).toList();

				ClassOperation receiverOperation = null;

				for (ClassStructure _class : classesReceiver) {
					receiverOperation = _class.getOperations().stream().filter(op -> {
						return op.getName().equals(messageName);
					}).findFirst().orElse(null);

					if (receiverOperation != null) {
						break;
					}
				}

				if (receiverOperation != null) {
					continue;
				}

				String senderName = sequenceMessage.getSender().getLifelineName();

				List<ClassStructure> classesSender = this.getUMLModel().getClasses().stream().filter(c -> {
					return c.getName().equals(senderName);
				}).toList();

				ClassOperation senderOperation = null;

				for (ClassStructure _class : classesSender) {
					senderOperation = _class.getOperations().stream().filter(op -> {
						return op.getName().equals(messageName);
					}).findFirst().orElse(null);

					if (senderOperation != null) {
						break;
					}
				}

				if (senderOperation != null) {
					String errorMessage = "Mensagem " + messageName
							+ " na direção errada pois, está definida na classe " + senderName + ".";
					InconsistencyError error = new InconsistencyError(sequenceMessage.getId(),
							sequenceMessage.getParentId(), errorMessage);
					this.addError(error);
				}
			}
		}
	}
}
