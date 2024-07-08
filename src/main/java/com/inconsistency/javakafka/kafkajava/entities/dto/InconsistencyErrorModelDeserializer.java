package com.inconsistency.javakafka.kafkajava.entities.dto;

import java.util.Map;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inconsistency.javakafka.kafkajava.analyse.model.services.AnalyseUMLModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InconsistencyErrorModelDeserializer implements Deserializer<InconsistencyErrorDTO> {
	private static final Logger logger = LoggerFactory.getLogger(AnalyseUMLModel.class);

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public InconsistencyErrorDTO deserialize(String topic, byte[] data) {
		try {
			if (data == null) {
				logger.warn("[InconsistencyErrorDTO] Null received at deserializing");
				return null;
			}
			
			return objectMapper.readValue(new String(data, "UTF-8"), InconsistencyErrorDTO.class);
		} catch (Exception e) {
			logger.error("[InconsistencyErrorDTO] Error when deserializing");
			throw new SerializationException("Error when deserializing byte[] to InconsistencyErrorModel");
		}
	}

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
	}

	@Override
	public void close() {
	}
}