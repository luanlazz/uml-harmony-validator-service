package com.inconsistency.javakafka.kafkajava.entities.dto;

import java.util.Map;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inconsistency.javakafka.kafkajava.analyse.model.services.AnalyseModel;

public class InconsistencyErrorModelSerializer implements Serializer<InconsistencyErrorDTO> {
	private static final Logger logger = LoggerFactory.getLogger(AnalyseModel.class);

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public byte[] serialize(String topic, InconsistencyErrorDTO data) {
		try {
			if (data == null) {
				logger.warn("[InconsistencyErrorDTO] Null received at serializing");
				return null;
			}

			return objectMapper.writeValueAsBytes(data);
		} catch (Exception e) {
			logger.error("[InconsistencyErrorDTO] Error when serializing");
			throw new SerializationException("Error when serializing InconsistencyErrorModel to byte[]");
		}
	}

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
	}

	@Override
	public void close() {
	}
}