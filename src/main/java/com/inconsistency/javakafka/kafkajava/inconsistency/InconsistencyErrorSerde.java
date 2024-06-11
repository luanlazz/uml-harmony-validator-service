package com.inconsistency.javakafka.kafkajava.inconsistency;

import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class InconsistencyErrorSerde extends Serdes.WrapperSerde<InconsistencyErrorModel> {
	public InconsistencyErrorSerde() {
		super(new JsonSerializer<>(), new JsonDeserializer<>(InconsistencyErrorModel.class));
	}
}
