package com.inconsistency.javakafka.kafkajava.analyse.model.strategies;

import java.util.ArrayList;
import java.util.List;

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
public class CaSD extends AnalyseModelInconsistency {

	public CaSD() {
		super(new Inconsistency(InconsistencyType.CaSD, "Objeto", "CR-76"));
	}

	@Override
	@KafkaListener(topics = "${spring.kafka.topic.model-analyze}", groupId = "casd", clientIdPrefix = "casd", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(ConsumerRecord<String, String> record) {
		super.handleEvent(record);
	}

	@Override
	public void analyse() {
		List<ClassStructure> abstractClasses = new ArrayList<ClassStructure>();

		for (ClassStructure _class : this.getUMLModel().getClasses()) {
			if (_class.isAbstract()) {
				abstractClasses.add(_class);
			}
		}

		for (ClassStructure abstractClass : abstractClasses) {
			SequenceLifeline lifeline = this.getUMLModel().getLifelines().stream().filter(l -> {
				return l.getName().equals(abstractClass.getName());
			}).findFirst().orElse(null);

			if (lifeline != null) {
				String errorMessage = "Objeto definido como classe abstrata no diagrama de classes.";
				InconsistencyError error = new InconsistencyError(lifeline.getId(), lifeline.getParentId(),
						errorMessage);
				this.addError(error);
			}
		}
	}
}
