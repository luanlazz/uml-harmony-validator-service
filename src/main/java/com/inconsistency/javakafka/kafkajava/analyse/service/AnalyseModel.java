package com.inconsistency.javakafka.kafkajava.analyse.service;

import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.inconsistency.javakafka.kafkajava.configuration.ProducerConfiguration;
import com.inconsistency.javakafka.kafkajava.entities.uml.dto.UMLModelDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Component("receiveModifications")
public class AnalyseModel {

	private static final Logger logger = LoggerFactory.getLogger(AnalyseModel.class);

	@Value("${spring.kafka.topic.model-analyze}")
	private String topicModelToAnalyze;

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Autowired

	@Autowired
	@Qualifier(value = "UMLModelRedisTemplate")
	private RedisTemplate<String, UMLModelDTO> redisTemplate;
	
	@Autowired
	@Qualifier(value = "StringRedisTemplate")
	private RedisTemplate<String, String> redisTemplateString;

	public void analyseModel(UMLModelDTO umlModel, String clientId, Locale locale) throws Exception {
		this.redisTemplate.opsForValue().set(clientId, umlModel);
		this.redisTemplateString.opsForValue().set(clientId + "_locale", locale.toString());

		KafkaProducer<String, String> producer = ProducerConfiguration
				.createKafkaProducerAnalyseModel(bootstrapServers);
		ProducerRecord<String, String> record = new ProducerRecord<>(topicModelToAnalyze, clientId, clientId);
		Future<RecordMetadata> future = producer.send(record, new Callback() {
			@Override
			public void onCompletion(RecordMetadata metadata, Exception exception) {
				if (exception != null) {
					logger.warn("Unable to deliver message. {}", exception.getMessage());
				} else {
					logger.info("[Analyse Model] Message delivered with offset {}", metadata.topic(),
							metadata.offset());
				}
			}
		});

		try {
			RecordMetadata metadata = future.get();
		} catch (InterruptedException | ExecutionException e) {
			logger.warn("Unable to deliver message. {}", e.getMessage());
		} finally {
			producer.close();
		}
	}
}
