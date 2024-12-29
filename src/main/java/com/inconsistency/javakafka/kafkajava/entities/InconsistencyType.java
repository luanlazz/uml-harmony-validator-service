package com.inconsistency.javakafka.kafkajava.entities;

public enum InconsistencyType {
	CM("Várias definições de Classes com mesmo nome"),
	Om("Várias definições de Objetos com mesmo nome"),
	CnSD("Classe não instanciada no SD"),
	CnCD("Objeto sem Classe no CD"),
	ED("Mensagem na direção Errada"),
	EnN("Mensagem sem Nome"),
	EnM("Mensagem sem Método"),
	ACSD("Classe abstrata instanciada no SD"),
	EpM("Mensagem para função privada em CD");
		
    private String description; 
  
    // getter method 
    public String getDescription() 
    { 
        return this.description; 
    } 
  
    private InconsistencyType(String description) 
    { 
        this.description = description; 
    }
    
    @Override
    public String toString() {
    	return String.format("%s (%s)",this.getDescription(), this.name());
    }
}