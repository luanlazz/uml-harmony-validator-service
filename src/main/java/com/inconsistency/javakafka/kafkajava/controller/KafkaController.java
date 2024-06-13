package com.inconsistency.javakafka.kafkajava.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyErrorModel;
import com.inconsistency.javakafka.kafkajava.services.ReceiveModifications;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/kafka")
public class KafkaController {

	private static final Logger logger = LoggerFactory.getLogger(KafkaController.class);

	@Value("${spring.kafka.store-inconsistencies}")
	private String storeInconsistenciesClientId;
	
	@Autowired
	private ReceiveModifications receiveModifications;

	private final StreamsBuilderFactoryBean factoryBean;
	
    private final AtomicLong counter = new AtomicLong();

	public KafkaController(StreamsBuilderFactoryBean factoryBean) {
		this.factoryBean = factoryBean;
	}

	@ResponseBody
	@PostMapping(value = "/send")
	public ResponseEntity<Map<String, String>> send(@RequestParam String filePath) {
		try {
			String clientId = System.currentTimeMillis() + "-" + counter.incrementAndGet();
            
			receiveModifications.parseUML(filePath, clientId);
			
			HashMap<String, String> responseBody = new HashMap<>();
			responseBody.put("clientId", clientId);
		    		    
			return new ResponseEntity<Map<String, String>>(responseBody, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@ResponseBody
	@GetMapping("/inconsistencies/{clientId}")
	public List<InconsistencyErrorModel> getDealerSales(@PathVariable String clientId) {
		KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();

		ReadOnlyKeyValueStore<String, List<InconsistencyErrorModel>> inconsistencies = kafkaStreams.store(
				StoreQueryParameters.fromNameAndType(this.storeInconsistenciesClientId, QueryableStoreTypes.keyValueStore()));
        
        return inconsistencies.get(clientId);
	}
}
