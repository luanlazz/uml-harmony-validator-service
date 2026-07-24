package com.inconsistency.javakafka.kafkajava.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.inconsistency.javakafka.kafkajava.analyse.service.AnalyseModel;
import com.inconsistency.javakafka.kafkajava.analyse.service.SseNotificationService;
import com.inconsistency.javakafka.kafkajava.controller.dto.InconsistenciesResponse;
import com.inconsistency.javakafka.kafkajava.entities.uml.dto.UMLModelDTO;
import com.inconsistency.javakafka.kafkajava.uml.reader.service.UMLModelReaderService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/validator")
public class ModelValidatorController {

	private static final Logger logger = LoggerFactory.getLogger(ModelValidatorController.class);
	private static final Long SSE_TIMEOUT = 300000L;

	private final AtomicLong counter = new AtomicLong();

	@Autowired
	private AnalyseModel analyseUMLModelService;

	@Autowired
    private SseNotificationService sseNotificationService;
	
	@ResponseBody
	@PostMapping()
	public ResponseEntity<Map<String, String>> validate(@RequestParam("file") MultipartFile file, Locale locale) {
		HashMap<String, String> responseBody = new HashMap<>();

		try {
			validateFile(file);

			File convFile = convertToFile(file);
			UMLModelDTO umlModel = UMLModelReaderService.diagramReader(convFile);
			if (umlModel == null) throw new Exception("Model is invalid");

			String clientId = System.currentTimeMillis() + String.valueOf(counter.incrementAndGet());

			this.analyseUMLModelService.analyseModel(umlModel, clientId, locale);

			responseBody.put("success", "true");
			responseBody.put("clientId", clientId);

			return new ResponseEntity<Map<String, String>>(responseBody, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("message: " + e.getMessage() + "- \nStackTrace: " + ExceptionUtils.getStackTrace(e));
			responseBody.put("success", "false");
			responseBody.put("error", "Model invalid");
		}

		return new ResponseEntity<Map<String, String>>(responseBody, HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping(value = "/stream/{clientId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamResults(@PathVariable String clientId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        sseNotificationService.addEmitter(clientId, emitter);
        
        Optional<InconsistenciesResponse> cached = sseNotificationService.getCachedResult(clientId);
        if (cached.isPresent()) {
            try {
                emitter.send(SseEmitter.event().name("analysis-complete").data(cached.get()));
                emitter.complete();
            } catch (IOException e) {
                logger.error("Error sending cached result", e);
            } finally {
            	sseNotificationService.removeEmitter(clientId);
            }
        }
        
        return emitter;
    }
	
	private File convertToFile(MultipartFile file) throws IOException {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
		file.transferTo(convFile);
		return convFile;
	}

	private void validateFile(MultipartFile file) throws Exception {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		if (fileName.contains("..")) {
			throw new Exception("Filename contains invalid path sequence " + fileName);
		}

		long fileSizeKb = file.getSize() / 1000;
		long maxSizeKb = 10 * 1024;
		if (fileSizeKb > maxSizeKb) {
			throw new Exception("File size exceeds maximum limit: 10mb");
		}
	}
}
