package com.inconsistency.javakafka.kafkajava.analyse.model;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang.NotImplementedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.inconsistency.javakafka.kafkajava.configuration.ProducerConfiguration;
import com.inconsistency.javakafka.kafkajava.entities.Inconsistency;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyError;
import com.inconsistency.javakafka.kafkajava.entities.dto.InconsistencyErrorDTO;
import com.inconsistency.javakafka.kafkajava.entities.uml.dto.UMLModelDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableKafka
@Component("receiveModifications")
public abstract class AnalyseModel implements IAnalyseModel {

	private static final Logger logger = LoggerFactory.getLogger(Inconsistency.class);

	@Value("${spring.kafka.topic.inconsistencies-errors}")
	private String topicInconsistencies;

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	private Inconsistency inconsistency;
	private UMLModelDTO umlModel;
	private String clientId;

	@Autowired
	public AnalyseModel(Inconsistency inconsistency) {
		this.inconsistency = inconsistency;
	}

	public Inconsistency getInconsistency() {
		return inconsistency;
	}

	public void setInconsistency(Inconsistency inconsistency) {
		this.inconsistency = inconsistency;
	}

	public void setUMLModel(UMLModelDTO umlModel) {
		this.umlModel = umlModel;
	}

	public UMLModelDTO getUMLModel() {
		return umlModel;
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

	public void addError(InconsistencyError error) {
		InconsistencyErrorDTO errorModel = new InconsistencyErrorDTO();
		Inconsistency inconsistency = this.getInconsistency();

		errorModel.setClientId(this.getClientId());
		errorModel.setInconsistencyTypeCode(inconsistency.getInconsistencyType().name());
		errorModel.setInconsistencyTypeDesc(inconsistency.getInconsistencyType().getDescription());
		errorModel.setSeverity(inconsistency.getSeverity().getValue());
		errorModel.setSeverityLabel(inconsistency.getSeverity().name());
		errorModel.setCr(inconsistency.getConsistenciesRules());
		errorModel.setDiagram(inconsistency.getContext().getValue());
		errorModel.setPropertyType(inconsistency.getElementType());
		errorModel.setPropertyName(error.getPropertyName());
		errorModel.setUmlPackage(error.getUmlPackage());
		errorModel.setDescription(error.getMessage());

		sendError(errorModel);
	}

	private void sendError(InconsistencyErrorDTO errorModel) {
		KafkaProducer<String, InconsistencyErrorDTO> producer = ProducerConfiguration
				.createKafkaProducerInconsistencyErrorModel(bootstrapServers);
		ProducerRecord<String, InconsistencyErrorDTO> record = new ProducerRecord<>(topicInconsistencies, clientId,
				errorModel);
		Future<RecordMetadata> future = producer.send(record, new Callback() {
			@Override
			public void onCompletion(RecordMetadata metadata, Exception exception) {
				if (exception != null) {
					logger.warn("[InconsistencyError] Unable to deliver message. {}", exception.getMessage());
				} else {
					logger.info("[InconsistencyError] Message delivered with offset {}", metadata.offset());
				}
			}
		});

		try {
			RecordMetadata metadata = future.get();
		} catch (InterruptedException | ExecutionException e) {
			logger.warn("[InconsistencyError] Unable to deliver message. {}", e.getMessage());
		}

		producer.close();
	}

	@Override
	public void listenTopic(ConsumerRecord<String, UMLModelDTO> record) {
		throw new NotImplementedException();
	}

	@Override
	public void handleEvent(ConsumerRecord<String, UMLModelDTO> record) {
		try {
			logger.info("[{}] Received key: {}", this.getInconsistency().getInconsistencyType().name(), record.key());

			this.setClientId(record.key());
			this.setUMLModel(record.value());

			this.analyse();
		} catch (Exception e) {
			logger.error("[{}] Error message: {}", this.getInconsistency().getInconsistencyType().name(),
					e.getMessage());
		}
	}

	@Override
	public void analyse() {
		throw new NotImplementedException();
	}
}
