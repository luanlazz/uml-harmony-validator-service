package com.inconsistency.javakafka.kafkajava.uml.reader.diagram;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DiagramProperties(@JsonProperty("class") String classDiagram,
		@JsonProperty("sequence") String sequenceDiagram, @JsonProperty("clientId") int clientId) {
}
