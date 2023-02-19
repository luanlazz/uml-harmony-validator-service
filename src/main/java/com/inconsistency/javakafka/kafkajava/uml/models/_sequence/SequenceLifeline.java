package com.inconsistency.javakafka.kafkajava.uml.models._sequence;

public class SequenceLifeline {

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
    	return "\nLifeLines \n name: " + this.getLifelineName() +
    			" - represents: " + this.getRepresents();
    }
}
