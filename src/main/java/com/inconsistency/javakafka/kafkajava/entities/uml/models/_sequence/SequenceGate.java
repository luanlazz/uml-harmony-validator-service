package com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence;

import com.inconsistency.javakafka.kafkajava.entities.uml.UMLElement;

public class SequenceGate extends UMLElement {

	private String gateMessage;
	private SequenceLifeline gateLifeline;

	public SequenceGate() {
		super();
	}

	public SequenceGate(String gateMessage, SequenceLifeline gateLifeline) {
		super();
		this.gateMessage = gateMessage;
		this.gateLifeline = gateLifeline;
	}

	public String getGateMessage() {
		return gateMessage;
	}

	public void setGateMessage(String gateMessage) {
		this.gateMessage = gateMessage;
	}

	public SequenceLifeline getGateLifeline() {
		return gateLifeline;
	}

	public void setGateLifeline(SequenceLifeline gateLifeline) {
		this.gateLifeline = gateLifeline;
	}

	@Override
	public String toString() {
		return "\n\n+Gate: " + super.toString();
	}
}
