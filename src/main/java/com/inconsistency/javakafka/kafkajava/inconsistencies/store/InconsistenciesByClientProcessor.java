package com.inconsistency.javakafka.kafkajava.inconsistencies.store;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyErrorDTO;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyErrorModelSerde;

@Component
public class InconsistenciesByClientProcessor {

	private static final Serde<String> STRING_SERDE = Serdes.String();
	private static final Serde<InconsistencyErrorDTO> INCONSISTENCY_SERDE = new InconsistencyErrorModelSerde();

	@Value("${spring.kafka.store-inconsistencies}")
	private String storeInconsistenciesClientId;

	@Value("${spring.kafka.topic.inconsistencies-errors}")
	private String topicInconsistencies;

	@Value("${spring.kafka.topic.inconsistencies-by-client}")
	private String topicInconsistenciesByClient;

	@Autowired
	public void buildPipeline(StreamsBuilder streamsBuilder) {

		final KStream<String, InconsistencyErrorDTO> stream = streamsBuilder
				.stream(this.topicInconsistencies, Consumed.with(STRING_SERDE, INCONSISTENCY_SERDE))
				.peek((key, value) -> System.out.println("[KStream] Incoming record - key " + key + " value " + value));

		KGroupedStream<String, InconsistencyErrorDTO> groupedStream = stream
				.groupBy((key, value) -> value.getClientId(), Grouped.with(STRING_SERDE, INCONSISTENCY_SERDE));

		KTable<String, List<InconsistencyErrorDTO>> aggregatedTable = groupedStream.aggregate(ArrayList::new,
				(key, value, aggregate) -> {
					aggregate.add(value);
					return aggregate;
				},
				Materialized
						.<String, List<InconsistencyErrorDTO>, KeyValueStore<Bytes, byte[]>>as(
								this.storeInconsistenciesClientId)
						.withKeySerde(STRING_SERDE).withValueSerde(new ListSerde<>(InconsistencyErrorDTO.class)));

		aggregatedTable.toStream()
				.peek((key, value) -> System.out.println("Incoming record - key " + key + " value " + value))
				.to(this.topicInconsistenciesByClient,
						Produced.with(STRING_SERDE, new ListSerde<>(InconsistencyErrorDTO.class)));
	}
}
