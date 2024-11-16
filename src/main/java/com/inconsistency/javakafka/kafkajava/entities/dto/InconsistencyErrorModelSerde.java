package com.inconsistency.javakafka.kafkajava.entities.dto;

import java.util.Map;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inconsistency.javakafka.kafkajava.analyse.model.AnalyseModel;

public class InconsistencyErrorModelSerde implements Serde<InconsistencyNotificationDTO> {
	private static final Logger logger = LoggerFactory.getLogger(AnalyseModel.class);

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Serializer<InconsistencyNotificationDTO> serializer() {
		return new Serializer<InconsistencyNotificationDTO>() {
			@Override
			public byte[] serialize(String topic, InconsistencyNotificationDTO data) {
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
		};
	}

	@Override
	public Deserializer<InconsistencyNotificationDTO> deserializer() {
		return new Deserializer<InconsistencyNotificationDTO>() {
			@Override
			public InconsistencyNotificationDTO deserialize(String topic, byte[] data) {
				try {
					if (data == null) {
						logger.warn("[InconsistencyErrorDTO] Null received at deserializing");
						return null;
					}

					return objectMapper.readValue(new String(data, "UTF-8"), InconsistencyNotificationDTO.class);
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
		};
	}
}
