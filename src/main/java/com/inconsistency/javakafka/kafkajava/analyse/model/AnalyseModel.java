package com.inconsistency.javakafka.kafkajava.analyse.model;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.inconsistency.javakafka.kafkajava.inconsistency.Inconsistency;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyError;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyErrorModel;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassDiagram;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceDiagram;
import com.inconsistency.javakafka.kafkajava.uml.reader.diagram.DiagramProperties;
import com.inconsistency.javakafka.kafkajava.uml.utils.JSONHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableKafka
@Component("receiveModifications")
public abstract class AnalyseModel implements IAnalyseModel {

	private static final Logger logger = LoggerFactory.getLogger(Inconsistency.class);
	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${spring.kafka.topic.inconsistencies-errors}")
	private String topicInconsistencies;

	private Inconsistency inconsistency;
	private ClassDiagram classDiagram;
	private SequenceDiagram sequenceDiagram;
	private String clientId;

	@Autowired
	public AnalyseModel(KafkaTemplate<String, Object> kafkaTemplate, Inconsistency inconsistency) {
		this.kafkaTemplate = kafkaTemplate;
		this.inconsistency = inconsistency;
	}

	public Inconsistency getInconsistency() {
		return inconsistency;
	}

	public void setInconsistency(Inconsistency inconsistency) {
		this.inconsistency = inconsistency;
	}

	public void setClassDiagram(ClassDiagram classDiagram) {
		this.classDiagram = classDiagram;
	}

	public void setSequenceDiagram(SequenceDiagram sequenceDiagram) {
		this.sequenceDiagram = sequenceDiagram;
	}

	public ClassDiagram getClassDiagram() {
		return classDiagram;
	}

	public SequenceDiagram getSequenceDiagram() {
		return sequenceDiagram;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getTopicInconsistencies() {
		return topicInconsistencies;
	}

	public KafkaTemplate<String, Object> getKafkaTemplate() {
		return kafkaTemplate;
	}

	public void addError(InconsistencyError error) {
		InconsistencyErrorModel errorModel = new InconsistencyErrorModel();
		errorModel.setClientId(this.getClientId());
		errorModel.setInconsistencyType(this.getInconsistency().getInconsistencyType().name());
		errorModel.setSeverity(this.getInconsistency().getSeverity().name());
		errorModel.setDiagram(error.getPropertyType());
		errorModel.setPropertyName(error.getPropertyName());
		errorModel.setPropertyType(error.getPropertyType());
		errorModel.setPropertyType(error.getPropertyType());
		errorModel.setUmlPackage(error.getUmlPackage());
		errorModel.setDescription(error.getMessage());

		this.getKafkaTemplate().send(this.getTopicInconsistencies(), errorModel);
	}

	@Override
	public void listenTopic(@Payload DiagramProperties payload, Acknowledgment ack) {
		throw new NotImplementedException();
	}

	@Override
	public void handleEvent(@Payload DiagramProperties payload, Acknowledgment ack) {
		try {
			logger.info("Handle Message: strategy {} - Received ack: {}", this.getInconsistency().getInconsistencyType(), ack);

			ClassDiagram classDiagram = JSONHelper.classDiagramFromJSON(payload.classDiagram());
			SequenceDiagram sequenceDiagram = JSONHelper.sequenceDiagramFromJSON(payload.sequenceDiagram());

			this.setClassDiagram(classDiagram);
			this.setSequenceDiagram(sequenceDiagram);
			this.setClientId(payload.clientId());

			this.analyse();
		} catch (Exception e) {
			logger.error("Logger strategy: {} - Error message: {}", this.getInconsistency().getInconsistencyType(),
					e.getMessage());
		}
	}

	@Override
	public void analyse() {
		throw new NotImplementedException();
	}
}
