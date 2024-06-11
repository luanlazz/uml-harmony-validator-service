package com.inconsistency.javakafka.kafkajava.inconsistencies.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyErrorModel;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class InconsistencyErrorModelSerde implements Serde<InconsistencyErrorModel> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Serializer<InconsistencyErrorModel> serializer() {
        return new Serializer<InconsistencyErrorModel>() {
            @Override
            public byte[] serialize(String topic, InconsistencyErrorModel data) {
                try {
                    return objectMapper.writeValueAsBytes(data);
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
        };
    }

    @Override
    public Deserializer<InconsistencyErrorModel> deserializer() {
        return new Deserializer<InconsistencyErrorModel>() {
            @Override
            public InconsistencyErrorModel deserialize(String topic, byte[] data) {
                try {
                    return objectMapper.readValue(data, InconsistencyErrorModel.class);
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
        };
    }
}
