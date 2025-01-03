package com.inconsistency.javakafka.kafkajava.entities.list.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inconsistency.javakafka.kafkajava.analyse.model.AnalyseModel;
import com.inconsistency.javakafka.kafkajava.entities.dto.InconsistencyNotificationDTO;

public class ListDeserializer<T> implements Deserializer<List<T>> {
	private static final Logger logger = LoggerFactory.getLogger(AnalyseModel.class);

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public List<T> deserialize(String topic, byte[] data) {
		try {
			return objectMapper.readValue(data, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class,
					InconsistencyNotificationDTO.class));
		} catch (Exception e) {
			logger.error("[List] Error when deserializing");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
	}

	@Override
	public void close() {
	}
}