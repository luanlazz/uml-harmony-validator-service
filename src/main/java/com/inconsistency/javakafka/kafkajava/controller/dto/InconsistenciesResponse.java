package com.inconsistency.javakafka.kafkajava.controller.dto;

import java.util.ArrayList;
import java.util.List;

import com.inconsistency.javakafka.kafkajava.entities.DiagramStatistics;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyConcentration;
import com.inconsistency.javakafka.kafkajava.entities.dto.InconsistencyNotificationDTO;

public class InconsistenciesResponse {

	List<InconsistencyNotificationDTO> inconsistencies = new ArrayList<>();

	List<InconsistencyConcentration> diagrams = new ArrayList<>();
	List<InconsistencyConcentration> diagramsElements = new ArrayList<>();

	List<DiagramStatistics> diagramStatistics = new ArrayList<>();

	public InconsistenciesResponse() {
	}

	public List<InconsistencyNotificationDTO> getInconsistencies() {
		return inconsistencies;
	}

	public void setInconsistencies(List<InconsistencyNotificationDTO> inconsistencies) {
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
