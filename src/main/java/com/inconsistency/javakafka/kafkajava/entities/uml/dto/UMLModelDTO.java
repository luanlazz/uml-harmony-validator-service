package com.inconsistency.javakafka.kafkajava.entities.uml.dto;

import java.util.ArrayList;
import java.util.List;

import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassDiagram;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassInstance;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._enum.EnumStructure;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceDiagram;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceLifeline;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceMessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UMLModelDTO {

	private String id;
	private String name;
	private List<ClassDiagram> classDiagrams = new ArrayList<>();
	private List<SequenceDiagram> sequenceDiagrams = new ArrayList<>();
	private List<ClassStructure> classes = new ArrayList<>();
	private List<ClassInstance> instances = new ArrayList<>();
	private List<EnumStructure> enumerations = new ArrayList<>();
	private List<SequenceLifeline> lifelines = new ArrayList<>();
	private List<SequenceMessage> messages = new ArrayList<>();

	public UMLModelDTO() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ClassDiagram> getClassDiagram() {
		return classDiagrams;
	}

	public void setClassDiagram(List<ClassDiagram> classDiagrams) {
		this.classDiagrams = classDiagrams;
	}

	public List<SequenceDiagram> getSequenceDiagram() {
		return sequenceDiagrams;
	}

	public void setSequenceDiagram(List<SequenceDiagram> sequenceDiagrams) {
		this.sequenceDiagrams = sequenceDiagrams;
	}

	public List<ClassStructure> getClasses() {
		return classes;
	}

	public void setClasses(ArrayList<ClassStructure> classes) {
		this.classes = classes;
	}

	public List<ClassInstance> getInstances() {
		return instances;
	}

	public void setInstances(ArrayList<ClassInstance> instances) {
		this.instances = instances;
	}

	public List<EnumStructure> getEnumerations() {
		return enumerations;
	}

	public void setEnumerations(ArrayList<EnumStructure> enumerations) {
		this.enumerations = enumerations;
	}

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
}
