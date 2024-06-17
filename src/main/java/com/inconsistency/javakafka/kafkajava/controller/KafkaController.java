package com.inconsistency.javakafka.kafkajava.controller;

import java.io.File;
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
import org.springframework.web.multipart.MultipartFile;

import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyErrorDTO;
import com.inconsistency.javakafka.kafkajava.services.AnalyseUMLModel;
import com.inconsistency.javakafka.kafkajava.uml.UMLModelDTO;
import com.inconsistency.javakafka.kafkajava.uml.reader.service.UMLModelReaderService;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/kafka")
public class KafkaController {

	private static final Logger logger = LoggerFactory.getLogger(KafkaController.class);

	@Value("${spring.kafka.store-inconsistencies}")
	private String storeInconsistenciesClientId;

	@Autowired
	private AnalyseUMLModel analyseUMLModelService;

	private final StreamsBuilderFactoryBean factoryBean;

	private final AtomicLong counter = new AtomicLong();

	public KafkaController(StreamsBuilderFactoryBean factoryBean) {
		this.factoryBean = factoryBean;
	}

	@ResponseBody
	@PostMapping(value = "/send")
	public ResponseEntity<Map<String, String>> send(@RequestParam("file") MultipartFile file) {
		HashMap<String, String> responseBody = new HashMap<>();

		try {
			String fileName = StringUtils.cleanPath(file.getOriginalFilename());

			if (fileName.contains("..")) {
				throw new Exception("Filename contains invalid path sequence " + fileName);
			}

			long fileSizeKb = file.getSize() / 1000;
			long maxSizeKb = 10 * 1024;
			if (fileSizeKb > maxSizeKb) {
				throw new Exception("File size exceeds maximum limit: 10mb");
			}

			File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
			file.transferTo(convFile);

			UMLModelDTO umlModel = UMLModelReaderService.diagramReader(convFile);
			if (umlModel == null) {
				throw new Exception("Model is invalid");
			}

			String clientId = System.currentTimeMillis() + "-" + counter.incrementAndGet();

			this.analyseUMLModelService.createEvent(umlModel, clientId);

			responseBody.put("success", "true");
			responseBody.put("clientId", clientId);

			return new ResponseEntity<Map<String, String>>(responseBody, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			responseBody.put("error", e.getMessage());
		}

		return new ResponseEntity<Map<String, String>>(responseBody, HttpStatus.BAD_REQUEST);
	}

	@ResponseBody
	@GetMapping("/inconsistencies/{clientId}")
	public List<InconsistencyErrorDTO> getDealerSales(@PathVariable String clientId) {
		KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();

		ReadOnlyKeyValueStore<String, List<InconsistencyErrorDTO>> inconsistencies = kafkaStreams
				.store(StoreQueryParameters.fromNameAndType(this.storeInconsistenciesClientId,
						QueryableStoreTypes.keyValueStore()));

		return inconsistencies.get(clientId);
	}
}
