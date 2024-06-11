package com.inconsistency.javakafka.kafkajava.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@EnableKafka
@Configuration
public class ProducerConfiguration {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${spring.kafka.topic.model-analyze}")
	private String topicModelToAnalyze;

	@Value("${spring.kafka.topic.inconsistencies-errors}")
	private String topicInconsistencies;

  @Value("${spring.kafka.topic.inconsistencies-by-client}")
  private String topicInconsistenciesByClient;

	@Bean
	public <K, V> ProducerFactory<K, V> createProducerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<K, V>(config);
	}

	@Bean
	public <K, V> KafkaTemplate<K, V> createKafkaTemplate() {
		return new KafkaTemplate<>(createProducerFactory());
	}

	@Bean
	public NewTopics topics() {
		ArrayList<NewTopic> topics = new ArrayList<>();
		topics.add(TopicBuilder.name(this.topicModelToAnalyze).build());
		topics.add(TopicBuilder.name(this.topicInconsistencies).build());
		topics.add(TopicBuilder.name(this.topicInconsistenciesByClient).build());

		return new NewTopics(topics.toArray(new NewTopic[0]));
	}
}
