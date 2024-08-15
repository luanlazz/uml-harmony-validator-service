package com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence;

import com.inconsistency.javakafka.kafkajava.entities.uml.UMLElement;

public class SequenceLifeline extends UMLElement {

	private String lifelineName;
	private String represents;

	public SequenceLifeline() {
	}

	public SequenceLifeline(String lifelineName, String represents) {
		super();
		this.lifelineName = lifelineName;
		this.represents = represents;
	}

	public String getLifelineName() {
		return lifelineName;
	}

	public void setLifelineName(String lifelineName) {
		this.lifelineName = lifelineName;
	}

	public String getRepresents() {
		return represents;
	}

	public void setRepresents(String represents) {
		this.represents = represents;
	}

	@Override
	public String toString() {
		return "\n\n+LifeLine:" + super.toString() + "\n lifeline name: " + this.getLifelineName() + " - represents: "
				+ this.getRepresents();
	}
}
