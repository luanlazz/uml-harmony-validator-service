package com.inconsistency.javakafka.kafkajava.analyse.service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.inconsistency.javakafka.kafkajava.controller.dto.InconsistenciesResponse;

@Service
public class SseNotificationService {

	private static final Logger logger = LoggerFactory.getLogger(AnalyseModel.class);

	private final Map<String, SseEmitter> emitterByClientMap = new ConcurrentHashMap<>();
	private final Map<String, InconsistenciesResponse> resultCacheByClientMap = new ConcurrentHashMap<>();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

	public void registerClient(String clientId) {
		resultCacheByClientMap.remove(clientId);
		logger.info("Client registered for SSE: {}", clientId);
	}

	public void addEmitter(String clientId, SseEmitter emitter) {
		emitterByClientMap.put(clientId, emitter);
		logger.debug("SSE emitter added for clientId: {}", clientId);

		scheduler.schedule(() -> removeEmitter(clientId), 5, TimeUnit.MINUTES);
	}

	public void removeEmitter(String clientId) {
		emitterByClientMap.remove(clientId);

		scheduler.schedule(() -> resultCacheByClientMap.remove(clientId), 10, TimeUnit.MINUTES);
	}

	public void notifyResult(String clientId, InconsistenciesResponse result) {
		SseEmitter emitter = emitterByClientMap.get(clientId);
		if (emitter == null) {
			logger.debug("No active emitter for clientId: {} (result cached)", clientId);
			return;
		}

		resultCacheByClientMap.put(clientId, result);

		try {
			emitter.send(SseEmitter.event().name("analysis-complete").data(result).id(clientId));
			emitter.complete();
			logger.info("Result sent to clientId: {}", clientId);
		} catch (IOException exception) {
			logger.error("Failed to send SSE event to {}", clientId, exception);
			removeEmitter(clientId);
		}
	}

	public void notifyError(String clientId, String errorMsg) {
		SseEmitter emitter = emitterByClientMap.get(clientId);
		if (emitter == null) return;

		try {
			emitter.send(SseEmitter.event().name("analysis-error").data(Map.of("error", errorMsg)));
			emitter.complete();
		} catch (IOException e) {
			logger.error("Failed to send error notification", e);
			removeEmitter(clientId);
		}
	}

	public Optional<InconsistenciesResponse> getCachedResult(String clientId) {
		return Optional.ofNullable(resultCacheByClientMap.get(clientId));
	}
}
