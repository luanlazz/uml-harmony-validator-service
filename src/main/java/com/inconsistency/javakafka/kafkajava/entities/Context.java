package com.inconsistency.javakafka.kafkajava.entities;

import java.util.HashMap;
import java.util.Map;

public enum Context {
	CLASS_DIAGRAM("Diagrama de classes"), SEQUENCE_DIAGRAM("Diagrama de sequência"),
	CLASS_SEQ_DIAGRAMS("Diagramas de classes e sequência");

	private String value;
	private static Map<String, Context> map = new HashMap<String, Context>();

	private Context(String value) {
		this.value = value;
	}

	static {
		for (Context context : Context.values()) {
			map.put(context.value, context);
		}
	}

	public String getValue() {
		return value;
	}
}
