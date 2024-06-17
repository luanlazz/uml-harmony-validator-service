package com.inconsistency.javakafka.kafkajava.uml;

import org.eclipse.uml2.uml.Package;

import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassDiagram;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceDiagram;
import com.inconsistency.javakafka.kafkajava.uml.reader.ReaderUtils;
import com.inconsistency.javakafka.kafkajava.uml.reader.service.ClassDiagramReader;
import com.inconsistency.javakafka.kafkajava.uml.reader.service.SequenceDiagramReader;

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
	private ClassDiagram classDiagram;
	private SequenceDiagram sequenceDiagram;

	public UMLModelDTO() {

	}

	public UMLModelDTO(Package _package) throws Exception {
		this.setId(ReaderUtils.getXMLId(_package));
		String packageName = _package.getName() != null ? _package.getName() : "";
		this.setName(packageName);
		this.setClassDiagram(ClassDiagramReader.getRefModelDetails(_package));
		this.setSequenceDiagram(SequenceDiagramReader.getRefModelDetails(_package));
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

	public ClassDiagram getClassDiagram() {
		return classDiagram;
	}

	public void setClassDiagram(ClassDiagram classDiagram) {
		this.classDiagram = classDiagram;
	}

	public SequenceDiagram getSequenceDiagram() {
		return sequenceDiagram;
	}

	public void setSequenceDiagram(SequenceDiagram sequenceDiagram) {
		this.sequenceDiagram = sequenceDiagram;
	}
}
