package com.inconsistency.javakafka.kafkajava.services;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.inconsistency.javakafka.kafkajava.configuration.ProducerConfiguration;
import com.inconsistency.javakafka.kafkajava.uml.UMLModelDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Component("receiveModifications")
public class AnalyseUMLModel {

	private static final Logger logger = LoggerFactory.getLogger(AnalyseUMLModel.class);

	@Value("${spring.kafka.topic.model-analyze}")
	private String topicModelToAnalyze;

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	public AnalyseUMLModel() {
	}

	public void createEvent(UMLModelDTO umlModel, String clientId) throws Exception {
		KafkaProducer<String, UMLModelDTO> producer = ProducerConfiguration
				.createKafkaProducerAnalyseModel(bootstrapServers);
		ProducerRecord<String, UMLModelDTO> record = new ProducerRecord<>(topicModelToAnalyze, clientId, umlModel);
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
		}

		producer.close();
	}
}
