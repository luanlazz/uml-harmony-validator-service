package com.inconsistency.javakafka.kafkajava.inconsistencies.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyErrorModel;

public class ListDeserializer<T> implements Deserializer<List<T>> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<T> deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, InconsistencyErrorModel.class));
        } catch (Exception e) {
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