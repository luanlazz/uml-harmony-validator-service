package com.inconsistency.javakafka.kafkajava.configuration;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class I18nConfig extends AcceptHeaderLocaleResolver {

	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		List<Locale> locales = Arrays.asList(new Locale("en"), new Locale("pt"));
		String headerLanguage = request.getHeader("Accept-Language");
		return headerLanguage == null || headerLanguage.isEmpty() ? Locale.getDefault()
				: Locale.lookup(Locale.LanguageRange.parse(headerLanguage), locales);
	}

	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasename("lang/messages");
		source.setDefaultEncoding(StandardCharsets.UTF_8.name());
		source.setDefaultLocale(new Locale("en"));
		return source;
	}
}
