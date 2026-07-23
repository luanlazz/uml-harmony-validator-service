package com.inconsistency.javakafka.kafkajava.analyse.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

import com.inconsistency.javakafka.kafkajava.controller.dto.InconsistenciesResponse;
import com.inconsistency.javakafka.kafkajava.entities.dto.InconsistencyErrorDTOComparator;
import com.inconsistency.javakafka.kafkajava.entities.dto.InconsistencyNotificationDTO;
import com.inconsistency.javakafka.kafkajava.entities.uml.dto.UMLModelDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InconsistencyCompilerService {

	@Value("${spring.kafka.store-inconsistencies}")
	private String storeInconsistenciesClientId;
	
	@Autowired
	private StreamsBuilderFactoryBean factoryBean;

    @Autowired
	@Qualifier(value = "UMLModelRedisTemplate")
	private RedisTemplate<String, UMLModelDTO> redisTemplate;    
    
	public InconsistenciesResponse compile(String clientId) {
		InconsistenciesResponse inconsistenciesResponse = new InconsistenciesResponse();

		KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();

		ReadOnlyKeyValueStore<String, List<InconsistencyNotificationDTO>> inconsistencies = kafkaStreams
				.store(StoreQueryParameters.fromNameAndType(this.storeInconsistenciesClientId,
						QueryableStoreTypes.keyValueStore()));

		List<InconsistencyNotificationDTO> clientInconsistencies = new ArrayList<InconsistencyNotificationDTO>();
		clientInconsistencies = inconsistencies.get(clientId);
		if (clientInconsistencies == null || clientInconsistencies.size() == 0) return inconsistenciesResponse;

		ModelMetrics metrics = new ModelMetrics();

		UMLModelDTO umlModel = this.redisTemplate.opsForValue().get(clientId);

		metrics.computeElementsModel(clientInconsistencies, umlModel, inconsistenciesResponse);
		metrics.computeModelMetrics(clientInconsistencies, inconsistenciesResponse);
		
		Comparator<InconsistencyNotificationDTO> comparatorReverseOrder = Collections
				.reverseOrder(new InconsistencyErrorDTOComparator());
		Collections.sort(clientInconsistencies, comparatorReverseOrder);
		inconsistenciesResponse.getInconsistencies().addAll(clientInconsistencies);

		return inconsistenciesResponse;
	}
}
