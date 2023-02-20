package com.inconsistency.javakafka.kafkajava.services;

import java.io.IOException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassDiagram;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceDiagram;
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

    @Value("${topic.name.producer}")
    private String topicName;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    public ReceiveModifications(KafkaTemplate<String, String> template) {
        this.kafkaTemplate = template;
    }

    public void parseUML(String filePath) throws Exception{
    	try {
//    		log.info("Payload enviado: {}" filePath);  
    		
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
    				
    		JSONObject json = new JSONObject();
    		json.put("class", classDiagramJSON);
    		json.put("sequence", sequenceDiagramJSON);
    		
			kafkaTemplate.send(topicName, json.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}    	
    }

}
