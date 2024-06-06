package com.inconsistency.javakafka.kafkajava.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyType;

@Configuration
public class ProducerConfiguration {
	
	@Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

	@Value("${spring.kafka.topic-name}")
	private String topicName;

	@Bean
    public <K, V> ProducerFactory<K, V> createProducerFactory(){
        Map<String,Object> config = new HashMap<>();
        config.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory(config);
    }

	@Bean
    public <K, V> KafkaTemplate<K, V> createKafkaTemplate(){
        return new KafkaTemplate<>(createProducerFactory());
    }

	@Bean
	public NewTopics inconsistenciesTopics() {
		ArrayList<NewTopic> topics = new ArrayList<>();

		for (InconsistencyType inconsistencyType : InconsistencyType.values()) {
			String topicName = this.topicName + "." + inconsistencyType.name().toLowerCase();
			NewTopic topic = TopicBuilder.name(topicName).build();
			topics.add(topic);
		}

		return new NewTopics(topics.toArray(new NewTopic[0]));
	}
}
