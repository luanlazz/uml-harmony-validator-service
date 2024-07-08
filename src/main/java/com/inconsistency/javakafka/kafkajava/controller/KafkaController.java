package com.inconsistency.javakafka.kafkajava.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.inconsistency.javakafka.kafkajava.analyse.model.services.AnalyseUMLModel;
import com.inconsistency.javakafka.kafkajava.entities.dto.InconsistencyErrorDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/kafka")
public class KafkaController {

	private static final Logger logger = LoggerFactory.getLogger(KafkaController.class);

	@Autowired
	private AnalyseUMLModel analyseUMLModelService;

	public KafkaController() {
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

			String clientId = this.analyseUMLModelService.analyseModelsByFile(file);

			responseBody.put("success", "true");
			responseBody.put("clientId", clientId);

			return new ResponseEntity<Map<String, String>>(responseBody, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(ExceptionUtils.getStackTrace(e));

			responseBody.put("success", "false");
			responseBody.put("error", "Model invalid");
		}

		return new ResponseEntity<Map<String, String>>(responseBody, HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/inconsistencies/{clientId}")
	public @ResponseBody Map<String, Object> getDealerSales(@PathVariable String clientId) {
		HashMap<String, Object> responseBody = new HashMap<>();

		List<InconsistencyErrorDTO> clientInconsistencies = new ArrayList<>();

		try {
			clientInconsistencies = this.analyseUMLModelService.getInconsistenciesByClientId(clientId);
			responseBody.put("success", "true");
			responseBody.put("data", clientInconsistencies);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(ExceptionUtils.getStackTrace(e));
			responseBody.put("seccess", "false");
		}

		return responseBody;
	}
}
