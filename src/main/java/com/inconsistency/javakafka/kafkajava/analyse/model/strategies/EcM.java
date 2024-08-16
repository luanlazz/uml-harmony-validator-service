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
public class EcM extends AnalyseModelInconsistency {

	public EcM() {
		super(new Inconsistency(InconsistencyType.EcM, "Mensagem", "CR-78"));
	}

	@Override
	@KafkaListener(topics = "${spring.kafka.topic.model-analyze}", groupId = "ecm", clientIdPrefix = "ecm", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(ConsumerRecord<String, String> record) {
		super.handleEvent(record);
	}

	@Override
	public void analyse() {
		for (SequenceDiagram sequenceDiagram : this.getUMLModel().getSequenceDiagram()) {
			for (SequenceMessage sequenceMessage : sequenceDiagram.getMessages()) {
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
				List<ClassStructure> classesReceiver = this.getUMLModel().getClasses().stream().filter(c -> {
					return c.getName().equals(receiverName);
				}).toList();

				ClassOperation receiverOperation = null;
				ClassStructure classReceiver = null;

				for (ClassStructure _classReceiver : classesReceiver) {
					receiverOperation = _classReceiver.getOperations().stream().filter(op -> {
						return op.getName().equals(messageName);
					}).findFirst().orElse(null);

					if (receiverOperation != null) {
						classReceiver = _classReceiver;
						break;
					}
				}

				if (receiverOperation == null) {
					String errorMessage = "A mensagem " + messageName + " não foi definida na classe "
							+ (classReceiver != null ? classReceiver.getName() : receiverName) + ".";
					InconsistencyError error = new InconsistencyError(sequenceMessage.getId(),
							sequenceMessage.getParentId(), errorMessage);
					this.addError(error);
				}
			}
		}
	}
}
