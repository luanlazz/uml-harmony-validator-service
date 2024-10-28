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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.inconsistency.javakafka.kafkajava.configuration.ProducerConfiguration;
import com.inconsistency.javakafka.kafkajava.entities.Inconsistency;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyError;
import com.inconsistency.javakafka.kafkajava.entities.dto.InconsistencyErrorDTO;
import com.inconsistency.javakafka.kafkajava.entities.uml.dto.UMLModelDTO;
import com.inconsistency.javakafka.kafkajava.i18n.MessageService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableKafka
@Component("receiveModifications")
public abstract class AnalyseModelInconsistency implements IAnalyseModel {

	private static final Logger logger = LoggerFactory.getLogger(Inconsistency.class);

	@Autowired
	protected MessageService messageService;

	@Value("${spring.kafka.topic.inconsistencies-errors}")
	private String topicInconsistencies;

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	private Inconsistency inconsistency;
	private UMLModelDTO umlModel;
	private String clientId;

	@Autowired
	@Qualifier(value = "UMLModelRedisTemplate")
	private RedisTemplate<String, UMLModelDTO> redisTemplate;

	@Autowired
	@Qualifier(value = "StringRedisTemplate")
	private RedisTemplate<String, String> redisTemplateString;

	@Autowired
	public AnalyseModelInconsistency(Inconsistency inconsistency) {
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
		errorModel.setDescription(error.getMessage());

		errorModel.setInconsistencyTypeCode(inconsistency.getInconsistencyType().name());
		errorModel.setInconsistencyTypeDesc(inconsistency.getInconsistencyType().getDescription());

		errorModel.setCr(inconsistency.getConsistenciesRules());

		errorModel.setElId(error.getElId());

		errorModel.setParentId(error.getDiagramId());

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
	public void listenTopic(ConsumerRecord<String, String> record) {
		throw new NotImplementedException();
	}

	@Override
	public void handleEvent(ConsumerRecord<String, String> record) {
		try {
			logger.info("[{}] Received key: {}", this.getInconsistency().getInconsistencyType().name(), record.key());

			this.setClientId(record.key());

			UMLModelDTO umlModelRedis = this.redisTemplate.opsForValue().get(record.value());
			if (umlModelRedis == null) {
				throw new EntityNotFoundException("Model not found to Analyse");
			}
			this.setUMLModel(umlModelRedis);

			String localeStr = this.redisTemplateString.opsForValue().get(record.value() + "_locale");
			this.messageService.setLocale(localeStr);

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
