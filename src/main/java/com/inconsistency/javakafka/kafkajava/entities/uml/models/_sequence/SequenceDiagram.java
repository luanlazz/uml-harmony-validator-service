package com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence;

import java.util.ArrayList;
import java.util.List;

import com.inconsistency.javakafka.kafkajava.entities.uml.UMLElement;

public class SequenceDiagram extends UMLElement {

	private List<SequenceLifeline> lifelines = new ArrayList<>();
	private List<SequenceMessage> messages = new ArrayList<>();
	private List<SequenceGate> gates = new ArrayList<>();
	private List<SequenceBehavior> behaviors = new ArrayList<>();
	private List<SequenceCombinedFragment> fragments = new ArrayList<>();

	public List<SequenceLifeline> getLifelines() {
		return lifelines;
	}

	public void setLifelines(List<SequenceLifeline> lifelines) {
		this.lifelines = lifelines;
	}

	public List<SequenceMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<SequenceMessage> messages) {
		this.messages = messages;
	}

	public List<SequenceGate> getGates() {
		return gates;
	}

	public void setGates(List<SequenceGate> gates) {
		this.gates = gates;
	}

	public List<SequenceBehavior> getBehaviors() {
		return behaviors;
	}

	public void setBehaviors(List<SequenceBehavior> behaviors) {
		this.behaviors = behaviors;
	}

	public List<SequenceCombinedFragment> getFragments() {
		return fragments;
	}

	public void setFragments(List<SequenceCombinedFragment> fragments) {
		this.fragments = fragments;
	}

	@Override
	public String toString() {
		String output = super.toString();

		output += "\n\n=== Lifelines:";

		for (SequenceLifeline lifeline : this.getLifelines()) {
			output += "\n" + lifeline.toString();
		}

		output += "\n\n=== Messages:";

		for (SequenceMessage message : this.getMessages()) {
			output += "\n" + message.toString();
		}

		output += "\n\n=== Gates:";

		for (SequenceGate gate : this.getGates()) {
			output += "\n" + gate.toString();
		}

		output += "\n\n=== Behaviors:";

		for (SequenceBehavior behavior : this.getBehaviors()) {
			output += "\n" + behavior.toString();
		}

		output += "\n\n=== Fragments:";

		for (SequenceCombinedFragment fragment : this.getFragments()) {
			output += "\n" + fragment.toString();
		}

		return output;
	}
}
