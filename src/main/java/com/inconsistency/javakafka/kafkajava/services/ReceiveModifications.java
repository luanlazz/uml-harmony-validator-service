package com.inconsistency.javakafka.kafkajava.services;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyType;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassDiagram;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceDiagram;
import com.inconsistency.javakafka.kafkajava.uml.reader.diagram.DiagramProperties;
import com.inconsistency.javakafka.kafkajava.uml.reader.service.ClassDiagramReaderService;
import com.inconsistency.javakafka.kafkajava.uml.reader.service.SequenceDiagramReaderService;
import com.inconsistency.javakafka.kafkajava.uml.utils.JSONHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Component("receiveModifications")
public class ReceiveModifications {

	private static final Logger logger = LoggerFactory.getLogger(ReceiveModifications.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topicNameBase;

    public ReceiveModifications(
            final KafkaTemplate<String, Object> template,
            @Value("${tpd.topic-name}") final String topicName
    ) {
        this.kafkaTemplate = template;
        this.topicNameBase = topicName;
    }

    public void parseUML(String filePath, String version) throws Exception{
    	try {    		
    		ClassDiagram classDiagram = ClassDiagramReaderService.classDiagramReader(filePath);
    		if (classDiagram == null) {
    			throw new Exception("Diagrama de classes não encontrado.");
    		}
    		
    		SequenceDiagram sequenceDiagram = SequenceDiagramReaderService.sequenceDiagramReader(filePath);
    		if (sequenceDiagram == null) {
    			throw new Exception("Diagrama de sequencia não encontrado.");
    		}    	    		
    		
    		String classDiagramJSON = JSONHelper.classDiagramToJSON(classDiagram);
    		String sequenceDiagramJSON = JSONHelper.sequenceDiagramToJSON(sequenceDiagram);
    		
    		DiagramProperties diagramProperties = new DiagramProperties(classDiagramJSON, sequenceDiagramJSON);
    		
    		for (InconsistencyType inconsistencyType : InconsistencyType.values()) {
    			String topicName = this.topicNameBase + "." + inconsistencyType.getTag();
                this.kafkaTemplate.send(topicName, version, diagramProperties);
    		}
    		            
            logger.info("Payload recebido: {}", filePath);  		
		} catch (IOException e) {
			e.printStackTrace();
		}    	
    }
}
