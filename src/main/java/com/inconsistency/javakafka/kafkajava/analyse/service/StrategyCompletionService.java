package com.inconsistency.javakafka.kafkajava.analyse.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.inconsistency.javakafka.kafkajava.controller.dto.InconsistenciesResponse;

@Service
public class StrategyCompletionService {

	private static final Logger logger = LoggerFactory.getLogger(AnalyseModel.class);

    private int TOTAL_STRATEGIES;
	    
    private final Map<String, AtomicInteger> completionCounterMapByClientId = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    @Autowired
    private SseNotificationService sseNotificationService;

    @Autowired
    private InconsistencyCompilerService inconsistencyCompilerService;

    @EventListener(ContextRefreshedEvent.class)
    public void onContextReady(ContextRefreshedEvent event) {
        this.TOTAL_STRATEGIES = event.getApplicationContext().getBeansOfType(IDetectionStrategy.class).size();
    }
    
    public void registerClient(String clientId) {
        completionCounterMapByClientId.put(clientId, new AtomicInteger(0));

        scheduler.schedule(() -> forceNotify(clientId), 10, TimeUnit.SECONDS);
    }

    public void markCompleted(String clientId) {
        AtomicInteger counter = completionCounterMapByClientId.get(clientId);
        if (counter == null) return;

        int completed = counter.incrementAndGet();
        logger.info("[SSE] Strategy completed {}/{} for clientId: {}", completed, TOTAL_STRATEGIES, clientId);

        if (completed >= TOTAL_STRATEGIES) notifyAndCleanup(clientId);        
    }
    
    private void notifyAndCleanup(String clientId) {
        try {
            Thread.sleep(200);

            InconsistenciesResponse result = inconsistencyCompilerService.compile(clientId);

            sseNotificationService.notifyResult(clientId, result);
        } catch (Exception e) {
            logger.error("[SSE] Failed to notify clientId: {}", clientId, e);
            sseNotificationService.notifyError(clientId, e.getMessage());
        } finally {
            completionCounterMapByClientId.remove(clientId);
        }
    }
    
    private void forceNotify(String clientId) {
        if (completionCounterMapByClientId.containsKey(clientId)) {
            logger.warn("[SSE] Timeout reached, forcing notification for clientId: {}", clientId);
            notifyAndCleanup(clientId);
        }
    }
}
