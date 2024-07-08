package com.inconsistency.javakafka.kafkajava.uml.models._sequence;

public class SequenceMessage {
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
		return "\nMessage \n name: " + this.getMessageName() + " - type: " + this.getMessageType() + " - reciver: "
				+ this.getReceiver().getLifelineName() + " - sender: " + this.getSender().getLifelineName();
	}
}
