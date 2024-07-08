package com.inconsistency.javakafka.kafkajava.entities.list.dto;

import java.util.List;
import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inconsistency.javakafka.kafkajava.analyse.model.services.AnalyseUMLModel;

public class ListSerializer<T> implements Serializer<List<T>> {
	private static final Logger logger = LoggerFactory.getLogger(AnalyseUMLModel.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, List<T> data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
        	logger.error("[List] Error when serializing");
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