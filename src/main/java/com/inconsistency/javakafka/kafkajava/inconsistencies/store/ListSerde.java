package com.inconsistency.javakafka.kafkajava.inconsistencies.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inconsistency.javakafka.kafkajava.services.AnalyseUMLModel;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ListSerde<T> implements Serde<List<T>> {
	private static final Logger logger = LoggerFactory.getLogger(AnalyseUMLModel.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> clazz;

    public ListSerde(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Serializer<List<T>> serializer() {
        return new Serializer<List<T>>() {
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
        };
    }

    @Override
    public Deserializer<List<T>> deserializer() {
        return new Deserializer<List<T>>() {
            @Override
            public List<T> deserialize(String topic, byte[] data) {
                try {
                    return objectMapper.readValue(data, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
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
        };
    }
}
