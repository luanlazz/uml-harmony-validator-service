package com.inconsistency.javakafka.kafkajava.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class ProducerConfiguration {
	
	@Autowired
    private KafkaProperties kafkaProperties;

    @Value("${tpd.topic-name}")
    private String topicName;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props =
                new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic topicCaSD() {
        return new NewTopic(topicName + ".CaSD", 3, (short) 1);
    }
    
    @Bean
    public NewTopic topicCM() {
        return new NewTopic(topicName + ".CM", 3, (short) 1);
    }
    
    @Bean
    public NewTopic topicCnCD() {
        return new NewTopic(topicName + ".CnCD", 3, (short) 1);
    }
    
    @Bean
    public NewTopic topicCnSD() {
        return new NewTopic(topicName + ".CnSD", 3, (short) 1);
    }
    
    @Bean
    public NewTopic topicEcM() {
        return new NewTopic(topicName + ".EcM", 3, (short) 1);
    }
    
    @Bean
    public NewTopic topicED() {
        return new NewTopic(topicName + ".ED", 3, (short) 1);
    }
    
    @Bean
    public NewTopic topicEnN() {
        return new NewTopic(topicName + ".EnN", 3, (short) 1);
    }
    
    @Bean
    public NewTopic topicEpM() {
        return new NewTopic(topicName + ".EpM", 3, (short) 1);
    }
    
    @Bean
    public NewTopic topicOm() {
        return new NewTopic(topicName + ".Om", 3, (short) 1);
    }
}
