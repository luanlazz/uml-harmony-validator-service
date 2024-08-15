package com.inconsistency.javakafka.kafkajava.analyse.model.strategies;

import java.util.HashSet;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.inconsistency.javakafka.kafkajava.analyse.model.AnalyseModelInconsistency;
import com.inconsistency.javakafka.kafkajava.entities.Inconsistency;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyError;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyType;
import com.inconsistency.javakafka.kafkajava.entities.uml.dto.UMLModelDTO;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassStructure;

@Component
public class CM extends AnalyseModelInconsistency {

	public CM() {
		super(new Inconsistency(InconsistencyType.CM, "Classe", ""));
	}

	@Override
	@KafkaListener(topics = "${spring.kafka.topic.model-analyze}", groupId = "cm", clientIdPrefix = "cm", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(ConsumerRecord<String, UMLModelDTO> record) {
		super.handleEvent(record);
	}

	@Override
	public void analyse() {
		Set<String> classesName = new HashSet<String>();

		for (ClassStructure _class : this.getUMLModel().getClasses()) {
			if (classesName.add(_class.getName()) == false) {
				String errorMessage = "A classe " + _class.getName() + " já foi definida no diagrama";
				InconsistencyError error = new InconsistencyError(_class.getId(), _class.getParentId(), errorMessage);
				this.addError(error);
			}
		}
	}
}
