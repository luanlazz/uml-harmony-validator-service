package com.inconsistency.javakafka.kafkajava.analyse.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.inconsistency.javakafka.kafkajava.controller.dto.InconsistenciesResponse;
import com.inconsistency.javakafka.kafkajava.entities.DiagramStatistics;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyConcentration;
import com.inconsistency.javakafka.kafkajava.entities.InconsistencyConcentrationComparator;
import com.inconsistency.javakafka.kafkajava.entities.dto.InconsistencyNotificationDTO;
import com.inconsistency.javakafka.kafkajava.entities.uml.UMLElement;
import com.inconsistency.javakafka.kafkajava.entities.uml.dto.UMLModelDTO;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassDiagram;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceDiagram;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceLifeline;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceMessage;

public class ModelMetrics {

	Map<String, UMLElement> packages = new HashMap<>();
	Map<String, UMLElement> elements = new HashMap<>();

	Map<String, Integer> inconsistenciesCounterByElementId = new HashMap<String, Integer>();

	Map<String, InconsistencyConcentration> concetrationPkgs = new HashMap<String, InconsistencyConcentration>();
	Map<String, List<InconsistencyConcentration>> concetrationClassSeq = new HashMap<String, List<InconsistencyConcentration>>();

	Comparator<InconsistencyConcentration> comparatorPkgsReverseOrder = Collections
			.reverseOrder(new InconsistencyConcentrationComparator());
	
	public InconsistenciesResponse computeElementsModel(List<InconsistencyNotificationDTO> clientInconsistencies,
			UMLModelDTO umlModel, InconsistenciesResponse inconsistenciesResponse) {
		packages.clear();
		elements.clear();
		inconsistenciesCounterByElementId.clear();
		concetrationPkgs.clear();
		concetrationClassSeq.clear();

		populateModelElements(umlModel);

		for (InconsistencyNotificationDTO inconsistency : clientInconsistencies) {
			countInconsistencyToParent(inconsistency.getElId());
		}

		List<InconsistencyConcentration> concentrationPkgsList = computePkgConcentration(clientInconsistencies.size());		
		List<InconsistencyConcentration> concetrationClassSeqList = computeClassDiagramsConcentration(umlModel);
		computeDiagramsSequenceConcentration(clientInconsistencies, umlModel);

		inconsistenciesResponse.getDiagrams().addAll(concentrationPkgsList);
		inconsistenciesResponse.getDiagramsElements().addAll(concetrationClassSeqList);

		return inconsistenciesResponse;
	}

	public InconsistenciesResponse computeModelMetrics(List<InconsistencyNotificationDTO> clientInconsistencies,
			InconsistenciesResponse inconsistenciesResponse) {
		List<DiagramStatistics> diagramStatistics = new ArrayList<>();
		inconsistenciesResponse.setDiagramStatistics(diagramStatistics);

		for (UMLElement pkg : packages.values()) {
			int totalInconsistenciesPkg = pkg.getInconsistenciesCount();

			List<UMLElement> pkgElements = elements.values().stream()
					.filter(e -> e.getParentId() != null && e.getParentId().equals(pkg.getId()))
					.sorted((el1, el2) -> Integer.compare(el2.getInconsistenciesCount(), el1.getInconsistenciesCount()))
					.collect(Collectors.toList());

			int qtdElementsPkg = pkgElements.size();
			if (qtdElementsPkg <= 0) continue;

			double riskMisinterpretation = calculateRiskMisinterpretation(totalInconsistenciesPkg, qtdElementsPkg, pkgElements);
			
			long qtdInconsistentElements = pkgElements.stream().filter(e -> e.getInconsistenciesCount() > 0).count();
			double spreadRate = ((double) qtdInconsistentElements / (double) qtdElementsPkg) * 100;
			
			double concentrationInc = ((double) totalInconsistenciesPkg / (double) clientInconsistencies.size()) * 100;

			DiagramStatistics statistics = new DiagramStatistics(pkg.getId(), riskMisinterpretation, spreadRate,
					concentrationInc);

			diagramStatistics.add(statistics);
		}

		return inconsistenciesResponse;
	}
	
	private void populateModelElements(UMLModelDTO umlModel) {
		for (ClassDiagram classDiagram : umlModel.getClassDiagram()) {
			packages.put(classDiagram.getId(), classDiagram);
			elements.put(classDiagram.getId(), classDiagram);

			for (ClassStructure _class : classDiagram.getClasses()) {
				elements.put(_class.getId(), _class);

				for (var attr : _class.getAttributes()) {
					elements.put(attr.getId(), attr);
				}
				for (var oper : _class.getOperations()) {
					elements.put(oper.getId(), oper);
				}
			}
		}

		for (SequenceDiagram seqDiagram : umlModel.getSequenceDiagram()) {
			packages.put(seqDiagram.getId(), seqDiagram);
			elements.put(seqDiagram.getId(), seqDiagram);

			for (SequenceLifeline lifeline : seqDiagram.getLifelines()) {
				elements.put(lifeline.getId(), lifeline);
			}

			for (SequenceMessage seqMsg : seqDiagram.getMessages()) {
				elements.put(seqMsg.getId(), seqMsg);
			}
		}
	}
	
	private void countInconsistencyToParent(String id) {
		if (id == null) {
			return;
		}

		UMLElement el = elements.get(id);
		if (el != null) {
			el.addCount();
			countInconsistencyToParent(el.getParentId());
		}
	}
	
	private List<InconsistencyConcentration> computePkgConcentration(int totalInconsistencies) {
		for (UMLElement pkg : packages.values()) {
			if (pkg.getInconsistenciesCount() <= 0) continue;
			
			inconsistenciesCounterByElementId.put(pkg.getId(), pkg.getInconsistenciesCount());
						
			double concentration = ((double) pkg.getInconsistenciesCount() / totalInconsistencies);
			concetrationPkgs.put(pkg.getId(), new InconsistencyConcentration(pkg.getId(), pkg.getParentId(),
					pkg.getName(), pkg.getInconsistenciesCount(), concentration));
		}
		
		return concetrationPkgs.values().stream().sorted(comparatorPkgsReverseOrder).toList();
	}
	
	private List<InconsistencyConcentration> computeClassDiagramsConcentration(UMLModelDTO umlModel) {
		for (ClassDiagram classDiagram : umlModel.getClassDiagram()) {
			int classDiagramTotalInconsistencies = inconsistenciesCounterByElementId.getOrDefault(classDiagram.getId(), 0);
			if (classDiagramTotalInconsistencies <= 0) continue;			

			List<InconsistencyConcentration> classConcentrations = new ArrayList<>();
			concetrationClassSeq.put(classDiagram.getId(), classConcentrations);

			for (UMLElement _class : classDiagram.getClasses()) {
				if (_class.getInconsistenciesCount() <= 0) continue;

				inconsistenciesCounterByElementId.put(_class.getId(), _class.getInconsistenciesCount());

				double concentration = ((double) _class.getInconsistenciesCount() / classDiagramTotalInconsistencies);
				classConcentrations.add(new InconsistencyConcentration(_class.getId(), _class.getParentId(),
						_class.getName(), _class.getInconsistenciesCount(), concentration));
			}
		}
		
		return concetrationClassSeq.values().stream().flatMap(List::stream)
				.sorted(comparatorPkgsReverseOrder).collect(Collectors.toList());
	}
	
	private void computeDiagramsSequenceConcentration(List<InconsistencyNotificationDTO> clientInconsistencies, UMLModelDTO umlModel) {
		for (SequenceDiagram seqDiagram : umlModel.getSequenceDiagram()) {
			int sequenceDiagramTotalInconsistencies = inconsistenciesCounterByElementId.getOrDefault(seqDiagram.getId(), 0);
			if (sequenceDiagramTotalInconsistencies <= 0) continue;

			List<InconsistencyConcentration> seqConcentrations = new ArrayList<>();
			Map<String, InconsistencyConcentration> concetrationByElId = new HashMap<String, InconsistencyConcentration>();
			concetrationClassSeq.put(seqDiagram.getId(), seqConcentrations);

			for (UMLElement lifeline : seqDiagram.getLifelines()) {
				if (lifeline.getInconsistenciesCount() <= 0) continue;

				inconsistenciesCounterByElementId.put(lifeline.getId(), lifeline.getInconsistenciesCount());

				double lifeLineConcentration = ((double) lifeline.getInconsistenciesCount() / sequenceDiagramTotalInconsistencies);
				seqConcentrations.add(new InconsistencyConcentration(lifeline.getId(), lifeline.getParentId(),
						lifeline.getName(), lifeline.getInconsistenciesCount(), lifeLineConcentration));

				List<SequenceMessage> lifelineMessages = seqDiagram.getMessages().stream()
						.filter(m -> m.getParentId().equals(lifeline.getId()) && m.getInconsistenciesCount() > 0)
						.toList();

				for (UMLElement msg : lifelineMessages) {
					if (msg.getInconsistenciesCount() <= 0) continue;

					inconsistenciesCounterByElementId.put(msg.getId(), msg.getInconsistenciesCount());

					int lifelineTotalInconsistencies = lifeline.getInconsistenciesCount();
					double msgConcentration = ((double) msg.getInconsistenciesCount() / lifelineTotalInconsistencies);
					InconsistencyConcentration concentration = new InconsistencyConcentration(msg.getId(), msg.getParentId(), msg.getName(),
							msg.getInconsistenciesCount(), msgConcentration);
					
					seqConcentrations.add(concentration);
					concetrationByElId.put(msg.getId(), concentration);
				}
				
				List<InconsistencyNotificationDTO> lifelineInconsistencies = clientInconsistencies.stream()
						.filter(e -> e.getParentId().equals(lifeline.getId())).collect(Collectors.toList());
				for (InconsistencyNotificationDTO inconsistency : lifelineInconsistencies) {
					double inconsistencyConcentration = concetrationByElId.get(inconsistency.getElId()).getConcentration();
					inconsistency.setConcentration(inconsistencyConcentration);
				}
			}
		}
	}
	
	private double calculateRiskMisinterpretation(int totalInconsistenciesPkg, int qtdElementsPkg, List<UMLElement> pkgElements) {
		double denominator = totalInconsistenciesPkg * ((double) qtdElementsPkg - 1);

		double numerator = 0;
		for (int j = 0; j < qtdElementsPkg; j++) {
			numerator += pkgElements.get(j).getInconsistenciesCount() * j;
		}

		return ((double) 2 * (numerator / denominator)) * 100;
	}
}
