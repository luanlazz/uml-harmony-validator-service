package com.inconsistency.javakafka.kafkajava.i18n;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageService {

	private Locale locale;

	@Autowired
	private MessageSource messageSource;

	public String get(String messageId) {
		return messageSource.getMessage(messageId, null, this.locale);
	}

	public String get(String messageId, Locale locale) {
		return messageSource.getMessage(messageId, null, locale);
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setLocale(String locale) {
		switch (locale) {
		case "pt": {
			this.locale = new Locale(locale);
			break;
		}
		default:
			this.locale = Locale.getDefault();
		}
	}
}