package com.inconsistency.javakafka.kafkajava.entities.uml.dto;

import java.util.Map;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inconsistency.javakafka.kafkajava.analyse.model.services.AnalyseUMLModel;

public class UMLModelDTOSerializer implements Serializer<UMLModelDTO> {
	private static final Logger logger = LoggerFactory.getLogger(AnalyseUMLModel.class);

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public byte[] serialize(String topic, UMLModelDTO data) {
		try {
			if (data == null) {
				logger.warn("[UMLModelDTO] Null received at serializing");
				return null;
			}

			return objectMapper.writeValueAsBytes(data);
		} catch (Exception e) {
			logger.error("[UMLModelDTO] Error when serializing");
			throw new SerializationException("Error when serializing UMLModel to byte[]");
		}
	}

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
	}

	@Override
	public void close() {
	}
}