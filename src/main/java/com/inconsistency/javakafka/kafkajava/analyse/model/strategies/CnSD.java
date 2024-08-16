package com.inconsistency.javakafka.kafkajava.analyse.model.strategies;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.inconsistency.javakafka.kafkajava.analyse.model.AnalyseModelInconsistency;
import com.inconsistency.javakafka.kafkajava.entities.Inconsistency;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyError;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyType;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceLifeline;

@Component
public class CnSD extends AnalyseModelInconsistency {

	public CnSD() {
		super(new Inconsistency(InconsistencyType.CnSD, "Classe", "CR-65 e CR-83"));
	}

	@Override
	@KafkaListener(topics = "${spring.kafka.topic.model-analyze}", groupId = "cnsd", clientIdPrefix = "cnsd", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(ConsumerRecord<String, String> record) {
		super.handleEvent(record);
	}

	@Override
	public void analyse() {
		for (ClassStructure classStructure : this.getUMLModel().getClasses()) {
			SequenceLifeline lifeline = this.getUMLModel().getLifelines().stream().filter(l -> {
				return l.getName().equals(classStructure.getName());
			}).findFirst().orElse(null);

			if (!classStructure.isAbstract() && lifeline == null) {
				String errorMessage = "A classe " + classStructure.getName()
						+ " não foi instanciada no diagrama de sequencia.";
				InconsistencyError error = new InconsistencyError(classStructure.getId(), classStructure.getParentId(),
						errorMessage);
				this.addError(error);
			}
		}
	}
}
