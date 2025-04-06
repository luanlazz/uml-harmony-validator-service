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

	Map<String, UMLElement> packagesMap = new HashMap<>();
	Map<String, UMLElement> elementsMap = new HashMap<>();

	Map<String, Integer> inconsistenciesCounterByElementIdMap = new HashMap<String, Integer>();

	Map<String, InconsistencyConcentration> concPkgsMap = new HashMap<String, InconsistencyConcentration>();
	Map<String, List<InconsistencyConcentration>> concElementsMap = new HashMap<String, List<InconsistencyConcentration>>();

	Comparator<InconsistencyConcentration> comparatorPkgsReverseOrder = Collections
			.reverseOrder(new InconsistencyConcentrationComparator());
	
	public InconsistenciesResponse computeElementsModel(List<InconsistencyNotificationDTO> clientInconsistencies,
			UMLModelDTO umlModel, InconsistenciesResponse inconsistenciesResponse) {
		packagesMap.clear();
		elementsMap.clear();
		inconsistenciesCounterByElementIdMap.clear();
		concPkgsMap.clear();
		concElementsMap.clear();

		populateModelElements(umlModel);

		clientInconsistencies.forEach(inconsistency -> countInconsistencyToParent(inconsistency.getElId()));

		List<InconsistencyConcentration> concPkgsList = computePkgConcentration(clientInconsistencies.size());		
		inconsistenciesResponse.getDiagrams().addAll(concPkgsList);

		Map<String, List<InconsistencyConcentration>> concClassDiagramMap = computeClassDiagramsConcentration(umlModel);
		concElementsMap.putAll(concClassDiagramMap);
		inconsistenciesResponse.getDiagramsElements().addAll(combineAndSortConcentrations(concClassDiagramMap, comparatorPkgsReverseOrder));
		
		Map<String, List<InconsistencyConcentration>> concSequenceDiagramMap = computeDiagramsSequenceConcentration(clientInconsistencies, umlModel);
		concElementsMap.putAll(concSequenceDiagramMap);
		inconsistenciesResponse.getDiagramsElements().addAll(combineAndSortConcentrations(concSequenceDiagramMap, comparatorPkgsReverseOrder));

		return inconsistenciesResponse;
	}

	public InconsistenciesResponse computeModelMetrics(List<InconsistencyNotificationDTO> clientInconsistencies,
			InconsistenciesResponse inconsistenciesResponse) {
		List<DiagramStatistics> diagramStatisticList = new ArrayList<>();
		inconsistenciesResponse.setDiagramStatistics(diagramStatisticList);

		for (UMLElement pkg : packagesMap.values()) {
			int totalInconsistenciesPkg = pkg.getInconsistenciesCount();

			List<UMLElement> pkgElementList = elementsMap.values().stream()
					.filter(e -> e.getParentId() != null && e.getParentId().equals(pkg.getId()))
					.sorted((el1, el2) -> Integer.compare(el2.getInconsistenciesCount(), el1.getInconsistenciesCount()))
					.collect(Collectors.toList());

			int qtdElementsPkg = pkgElementList.size();
			if (qtdElementsPkg <= 0) continue;

			double riskMisinterpretation = calculateRiskMisinterpretation(totalInconsistenciesPkg, qtdElementsPkg, pkgElementList);
			
			long qtdInconsistentElements = pkgElementList.stream().filter(e -> e.getInconsistenciesCount() > 0).count();
			double spreadRate = ((double) qtdInconsistentElements / (double) qtdElementsPkg) * 100;
			
			double concentrationInc = ((double) totalInconsistenciesPkg / (double) clientInconsistencies.size()) * 100;

			DiagramStatistics statistics = new DiagramStatistics(pkg.getId(), riskMisinterpretation, spreadRate,
					concentrationInc);

			diagramStatisticList.add(statistics);
		}

		return inconsistenciesResponse;
	}
	
	private void populateModelElements(UMLModelDTO umlModel) {
		for (ClassDiagram classDiagram : umlModel.getClassDiagram()) {
			packagesMap.put(classDiagram.getId(), classDiagram);
			elementsMap.put(classDiagram.getId(), classDiagram);

			for (ClassStructure _class : classDiagram.getClasses()) {
				elementsMap.put(_class.getId(), _class);

				for (var attr : _class.getAttributes()) {
					elementsMap.put(attr.getId(), attr);
				}
				for (var oper : _class.getOperations()) {
					elementsMap.put(oper.getId(), oper);
				}
			}
		}

		for (SequenceDiagram seqDiagram : umlModel.getSequenceDiagram()) {
			packagesMap.put(seqDiagram.getId(), seqDiagram);
			elementsMap.put(seqDiagram.getId(), seqDiagram);

			for (SequenceLifeline lifeline : seqDiagram.getLifelines()) {
				elementsMap.put(lifeline.getId(), lifeline);
			}

			for (SequenceMessage seqMsg : seqDiagram.getMessages()) {
				elementsMap.put(seqMsg.getId(), seqMsg);
			}
		}
	}
	
	private void countInconsistencyToParent(String id) {
		if (id == null) return;

		UMLElement el = elementsMap.get(id);
		if (el != null) {
			el.addCount();
			countInconsistencyToParent(el.getParentId());
		}
	}
	
	private List<InconsistencyConcentration> computePkgConcentration(int totalInconsistencies) {
		for (UMLElement pkg : packagesMap.values()) {
			if (pkg.getInconsistenciesCount() <= 0) continue;
			
			inconsistenciesCounterByElementIdMap.put(pkg.getId(), pkg.getInconsistenciesCount());
						
			double concentration = ((double) pkg.getInconsistenciesCount() / totalInconsistencies);
			concPkgsMap.put(pkg.getId(), new InconsistencyConcentration(pkg.getId(), pkg.getParentId(),
					pkg.getName(), pkg.getInconsistenciesCount(), concentration));
		}
		
		return concPkgsMap.values().stream().sorted(comparatorPkgsReverseOrder).toList();
	}
	
	private Map<String, List<InconsistencyConcentration>> computeClassDiagramsConcentration(UMLModelDTO umlModel) {
		Map<String, List<InconsistencyConcentration>> concetrationClassDiagramMap = new HashMap<String, List<InconsistencyConcentration>>();
		
		for (ClassDiagram classDiagram : umlModel.getClassDiagram()) {
			int classDiagramTotalInconsistencies = inconsistenciesCounterByElementIdMap.getOrDefault(classDiagram.getId(), 0);
			if (classDiagramTotalInconsistencies <= 0) continue;			

			List<InconsistencyConcentration> classConcentrationList = new ArrayList<>();
			concetrationClassDiagramMap.put(classDiagram.getId(), classConcentrationList);

			for (UMLElement _class : classDiagram.getClasses()) {
				if (_class.getInconsistenciesCount() <= 0) continue;

				inconsistenciesCounterByElementIdMap.put(_class.getId(), _class.getInconsistenciesCount());

				double concentration = ((double) _class.getInconsistenciesCount() / classDiagramTotalInconsistencies);
				classConcentrationList.add(new InconsistencyConcentration(_class.getId(), _class.getParentId(),
						_class.getName(), _class.getInconsistenciesCount(), concentration));
			}
		}
		
		return concetrationClassDiagramMap;
	}
	
	private Map<String, List<InconsistencyConcentration>> computeDiagramsSequenceConcentration(List<InconsistencyNotificationDTO> clientInconsistencies, UMLModelDTO umlModel) {
		Map<String, List<InconsistencyConcentration>> concetrationSequenceDiagramMap = new HashMap<String, List<InconsistencyConcentration>>();
		
		for (SequenceDiagram seqDiagram : umlModel.getSequenceDiagram()) {
			int sequenceDiagramTotalInconsistencies = inconsistenciesCounterByElementIdMap.getOrDefault(seqDiagram.getId(), 0);
			if (sequenceDiagramTotalInconsistencies <= 0) continue;

			List<InconsistencyConcentration> seqConcentrationList = new ArrayList<>();
			Map<String, InconsistencyConcentration> concetrationByElId = new HashMap<String, InconsistencyConcentration>();
			concetrationSequenceDiagramMap.put(seqDiagram.getId(), seqConcentrationList);

			for (UMLElement lifeline : seqDiagram.getLifelines()) {
				if (lifeline.getInconsistenciesCount() <= 0) continue;

				inconsistenciesCounterByElementIdMap.put(lifeline.getId(), lifeline.getInconsistenciesCount());

				double lifeLineConcentration = ((double) lifeline.getInconsistenciesCount() / sequenceDiagramTotalInconsistencies);
				seqConcentrationList.add(new InconsistencyConcentration(lifeline.getId(), lifeline.getParentId(),
						lifeline.getName(), lifeline.getInconsistenciesCount(), lifeLineConcentration));

				List<SequenceMessage> lifelineMessages = seqDiagram.getMessages().stream()
						.filter(m -> m.getParentId().equals(lifeline.getId()) && m.getInconsistenciesCount() > 0)
						.toList();

				for (UMLElement msg : lifelineMessages) {
					if (msg.getInconsistenciesCount() <= 0) continue;

					inconsistenciesCounterByElementIdMap.put(msg.getId(), msg.getInconsistenciesCount());

					int lifelineTotalInconsistencies = lifeline.getInconsistenciesCount();
					double msgConcentration = ((double) msg.getInconsistenciesCount() / lifelineTotalInconsistencies);
					InconsistencyConcentration concentration = new InconsistencyConcentration(msg.getId(), msg.getParentId(), msg.getName(),
							msg.getInconsistenciesCount(), msgConcentration);
					
					seqConcentrationList.add(concentration);
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
		
		return concetrationSequenceDiagramMap;
	}
	
	private double calculateRiskMisinterpretation(int totalInconsistenciesPkg, int qtdElementsPkg, List<UMLElement> pkgElements) {
		double denominator = totalInconsistenciesPkg * ((double) qtdElementsPkg - 1);

		double numerator = 0;
		for (int j = 0; j < qtdElementsPkg; j++) {
			numerator += pkgElements.get(j).getInconsistenciesCount() * j;
		}

		return ((double) 2 * (numerator / denominator)) * 100;
	}
	
	public static List<InconsistencyConcentration> combineAndSortConcentrations(
	        Map<String, List<InconsistencyConcentration>> concentrationMap,
	        Comparator<InconsistencyConcentration> comparator) {
	    
	    return concentrationMap.values().stream()
	            .flatMap(List::stream)
	            .sorted(comparator)
	            .collect(Collectors.toList());
	}
}
