package com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence;

import com.inconsistency.javakafka.kafkajava.entities.uml.UMLElement;

public class SequenceMessage extends UMLElement {

	private String messageType;
	private String messageName;
	private SequenceLifeline sender;
	private SequenceLifeline receiver;

	public SequenceMessage(String messageType, String messageName, SequenceLifeline sender, SequenceLifeline receiver) {
		super();
		this.messageType = messageType;
		this.messageName = messageName;
		this.sender = sender;
		this.receiver = receiver;
	}

	public SequenceMessage() {
		super();
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	public SequenceLifeline getSender() {
		return sender;
	}

	public void setSender(SequenceLifeline sender) {
		this.sender = sender;
	}

	public SequenceLifeline getReceiver() {
		return receiver;
	}

	public void setReceiver(SequenceLifeline receiver) {
		this.receiver = receiver;
	}

	@Override
	public String toString() {
		return "\n\n+Message: " + super.toString() + "\n MSG Name: " + this.getMessageName() + " - MSG type: "
				+ this.getMessageType() + " - reciver: " + this.getReceiver().getLifelineName() + " - sender: "
				+ this.getSender().getLifelineName();
	}
}
