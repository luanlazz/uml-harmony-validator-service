package com.inconsistency.javakafka.kafkajava.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.inconsistency.javakafka.kafkajava.entities.uml.dto.UMLModelDTO;
import com.inconsistency.javakafka.kafkajava.entities.uml.dto.UMLModelRedisSerializer;

@Configuration
public class RedisConfig {

	@Bean
	public JedisConnectionFactory redisConnectionFactory(final String host, final int port) {
		return new JedisConnectionFactory(new RedisStandaloneConfiguration(host, port));
	}

	@Bean(name = "UMLModelRedisTemplate")
	public RedisTemplate<String, UMLModelDTO> redisTemplate(@Value("${spring.redis.host}") final String host,
			@Value("${spring.redis.port}") final int port) {
		RedisTemplate<String, UMLModelDTO> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory(host, port));
		template.setDefaultSerializer(new UMLModelRedisSerializer());
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new UMLModelRedisSerializer());
		template.setHashValueSerializer(new UMLModelRedisSerializer());

		return template;
	}

}
