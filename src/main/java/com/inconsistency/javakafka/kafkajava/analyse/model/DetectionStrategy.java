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
import com.inconsistency.javakafka.kafkajava.entities.dto.InconsistencyNotificationDTO;
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
public abstract class DetectionStrategy implements IDetectionStrategy {

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
	private RedisTemplate<String, UMLModelDTO> modelRepositoryRedisTemplate;

	@Autowired
	@Qualifier(value = "StringRedisTemplate")
	private RedisTemplate<String, String> userLocaleRedisTemplate;

	@Autowired
	public DetectionStrategy(Inconsistency inconsistency) {
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

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void generateInconsistencyNotification(InconsistencyError error) {
		InconsistencyNotificationDTO inconsistencyNotification = new InconsistencyNotificationDTO();
		Inconsistency inconsistency = this.getInconsistency();

		inconsistencyNotification.setClientId(this.clientId);
		inconsistencyNotification.setDescription(error.getMessage());
		inconsistencyNotification.setInconsistencyTypeCode(inconsistency.getInconsistencyType().name());
		inconsistencyNotification.setInconsistencyTypeDesc(inconsistency.getInconsistencyType().getDescription());
		inconsistencyNotification.setCr(inconsistency.getConsistenciesRules());
		inconsistencyNotification.setElId(error.getElId());
		inconsistencyNotification.setParentId(error.getDiagramId());

		sendNotification(inconsistencyNotification);
	}

	private void sendNotification(InconsistencyNotificationDTO inconsistencyNotification) {
		KafkaProducer<String, InconsistencyNotificationDTO> producer = ProducerConfiguration
				.createKafkaProducerInconsistencyErrorModel(bootstrapServers);
		ProducerRecord<String, InconsistencyNotificationDTO> record = new ProducerRecord<>(topicInconsistencies, clientId,
				inconsistencyNotification);
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

			UMLModelDTO umlModelRedis = this.modelRepositoryRedisTemplate.opsForValue().get(record.value());
			if (umlModelRedis == null) {
				throw new EntityNotFoundException("Model not found to Analyse");
			}
			this.setUMLModel(umlModelRedis);

			String localeStr = this.userLocaleRedisTemplate.opsForValue().get(record.value() + "_locale");
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
