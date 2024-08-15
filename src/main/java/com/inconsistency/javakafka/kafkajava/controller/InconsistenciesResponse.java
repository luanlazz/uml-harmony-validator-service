package com.inconsistency.javakafka.kafkajava.controller;

import java.util.ArrayList;
import java.util.List;

import com.inconsistency.javakafka.kafkajava.entities.DiagramStatistics;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyConcentration;
import com.inconsistency.javakafka.kafkajava.entities.dto.InconsistencyErrorDTO;

public class InconsistenciesResponse {

	List<InconsistencyErrorDTO> inconsistencies = new ArrayList<>();

	List<InconsistencyConcentration> diagrams = new ArrayList<>();
	List<InconsistencyConcentration> diagramsElements = new ArrayList<>();

	List<DiagramStatistics> diagramStatistics = new ArrayList<>();

	public InconsistenciesResponse() {
	}

	public List<InconsistencyErrorDTO> getInconsistencies() {
		return inconsistencies;
	}

	public void setInconsistencies(List<InconsistencyErrorDTO> inconsistencies) {
		this.inconsistencies = inconsistencies;
	}

	public List<InconsistencyConcentration> getDiagrams() {
		return diagrams;
	}

	public void setDiagrams(List<InconsistencyConcentration> diagrams) {
		this.diagrams = diagrams;
	}

	public List<InconsistencyConcentration> getDiagramsElements() {
		return diagramsElements;
	}

	public void setDiagramsElements(List<InconsistencyConcentration> diagramsElements) {
		this.diagramsElements = diagramsElements;
	}

	public List<DiagramStatistics> getDiagramStatistics() {
		return diagramStatistics;
	}

	public void setDiagramStatistics(List<DiagramStatistics> diagramStatistics) {
		this.diagramStatistics = diagramStatistics;
	}
}
