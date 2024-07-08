package com.inconsistency.javakafka.kafkajava.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.inconsistency.javakafka.kafkajava.configuration.ProducerConfiguration;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyErrorDTO;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyErrorDTOComparator;
import com.inconsistency.javakafka.kafkajava.uml.UMLModelDTO;
import com.inconsistency.javakafka.kafkajava.uml.reader.service.UMLModelReaderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Component("receiveModifications")
public class AnalyseUMLModel {

	private static final Logger logger = LoggerFactory.getLogger(AnalyseUMLModel.class);

	@Value("${spring.kafka.topic.model-analyze}")
	private String topicModelToAnalyze;

	@Value("${spring.kafka.store-inconsistencies}")
	private String storeInconsistenciesClientId;

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	private final AtomicLong counter = new AtomicLong();

	private final StreamsBuilderFactoryBean factoryBean;

	public AnalyseUMLModel(StreamsBuilderFactoryBean factoryBean) {
		this.factoryBean = factoryBean;
	}

	public String analyseModelsByFile(MultipartFile file) throws Exception {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
		file.transferTo(convFile);

		UMLModelDTO umlModel = UMLModelReaderService.diagramReader(convFile);
		if (umlModel == null) {
			throw new Exception("Model is invalid");
		}

		String clientId = System.currentTimeMillis() + String.valueOf(counter.incrementAndGet());

		KafkaProducer<String, UMLModelDTO> producer = ProducerConfiguration
				.createKafkaProducerAnalyseModel(bootstrapServers);
		ProducerRecord<String, UMLModelDTO> record = new ProducerRecord<>(topicModelToAnalyze, clientId, umlModel);
		Future<RecordMetadata> future = producer.send(record, new Callback() {
			@Override
			public void onCompletion(RecordMetadata metadata, Exception exception) {
				if (exception != null) {
					logger.warn("Unable to deliver message. {}", exception.getMessage());
				} else {
					logger.info("[Analyse Model] Message delivered with offset {}", metadata.topic(),
							metadata.offset());
				}
			}
		});

		try {
			RecordMetadata metadata = future.get();
		} catch (InterruptedException | ExecutionException e) {
			logger.warn("Unable to deliver message. {}", e.getMessage());
		} finally {
			producer.close();
		}

		return clientId;
	}

	public List<InconsistencyErrorDTO> getInconsistenciesByClientId(String clientId) {
		KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();

		ReadOnlyKeyValueStore<String, List<InconsistencyErrorDTO>> inconsistencies = kafkaStreams
				.store(StoreQueryParameters.fromNameAndType(this.storeInconsistenciesClientId,
						QueryableStoreTypes.keyValueStore()));

		List<InconsistencyErrorDTO> clientInconsistencies = new ArrayList<InconsistencyErrorDTO>();
		clientInconsistencies = inconsistencies.get(clientId);
		if (clientInconsistencies == null || clientInconsistencies.size() == 0) {
			return clientInconsistencies;
		}

		Comparator<InconsistencyErrorDTO> comparatorReverseOrder = Collections
				.reverseOrder(new InconsistencyErrorDTOComparator());

		Collections.sort(clientInconsistencies, comparatorReverseOrder);

		return clientInconsistencies;
	}
}
