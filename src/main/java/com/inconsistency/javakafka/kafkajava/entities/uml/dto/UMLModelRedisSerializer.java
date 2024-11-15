package com.inconsistency.javakafka.kafkajava.entities.uml.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inconsistency.javakafka.kafkajava.analyse.model.services.AnalyseModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UMLModelRedisSerializer implements RedisSerializer<UMLModelDTO> {
	private static final Logger logger = LoggerFactory.getLogger(AnalyseModel.class);

	private final ObjectMapper mapper;

	public UMLModelRedisSerializer() {

		this.mapper = new ObjectMapper();
		this.mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL,
				JsonTypeInfo.As.PROPERTY);
	}

	@Override
	public byte[] serialize(UMLModelDTO object) throws SerializationException {
		if (object == null) {
			return new byte[0];
		}
		try {
			return mapper.writeValueAsBytes(object);
		} catch (JsonProcessingException e) {
			logger.error("JsonProcessingException while compressing value to redis {}", e.getMessage(), e);
		}
		return new byte[0];
	}

	@Override
	public UMLModelDTO deserialize(byte[] bytes) throws SerializationException {
		if (bytes == null || bytes.length == 0) {
			return null;
		}

		try {
			return mapper.readValue(bytes, UMLModelDTO.class);
		} catch (Exception ex) {
			logger.error("JsonProcessingException while descompressing value to redis {}", ex.getMessage(), ex);
			throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
		}
	}
}
