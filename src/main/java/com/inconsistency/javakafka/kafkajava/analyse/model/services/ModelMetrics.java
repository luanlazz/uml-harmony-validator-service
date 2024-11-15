package com.inconsistency.javakafka.kafkajava.analyse.model.services;

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
import com.inconsistency.javakafka.kafkajava.entities.dto.InconsistencyErrorDTO;
import com.inconsistency.javakafka.kafkajava.entities.uml.UMLElement;
import com.inconsistency.javakafka.kafkajava.entities.uml.dto.UMLModelDTO;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassDiagram;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceDiagram;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceLifeline;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceMessage;

public class ModelMetrics {

	int numInconsistencies = 0;

	Map<String, UMLElement> packages = new HashMap<>();
	Map<String, UMLElement> elements = new HashMap<>();

	Map<String, Integer> count = new HashMap<String, Integer>();

	Map<String, InconsistencyConcentration> concetrationPkgs = new HashMap<String, InconsistencyConcentration>();
	Map<String, List<InconsistencyConcentration>> concetrationClassSeq = new HashMap<String, List<InconsistencyConcentration>>();

	public InconsistenciesResponse computeElementsModel(List<InconsistencyErrorDTO> clientInconsistencies,
			UMLModelDTO umlModel, InconsistenciesResponse inconsistenciesResponse) {
		numInconsistencies = clientInconsistencies.size();

		packages.clear();
		elements.clear();

		count.clear();

		concetrationPkgs.clear();
		concetrationClassSeq.clear();

		readModelElements(umlModel);

		for (InconsistencyErrorDTO inconsistency : clientInconsistencies) {
			computeInconsistency(inconsistency.getElId());
		}

		computePkgEl();

		computeClasses(umlModel);

		computeSequences(umlModel);

		Comparator<InconsistencyConcentration> comparatorPkgsReverseOrder = Collections
				.reverseOrder(new InconsistencyConcentrationComparator());

		List<InconsistencyConcentration> concentrationPkgsList = concetrationPkgs.values().stream()
				.sorted(comparatorPkgsReverseOrder).toList();
		inconsistenciesResponse.getDiagrams().addAll(concentrationPkgsList);

		List<InconsistencyConcentration> combinedList = concetrationClassSeq.values().stream().flatMap(List::stream)
				.sorted(comparatorPkgsReverseOrder).collect(Collectors.toList());
		inconsistenciesResponse.getDiagramsElements().addAll(combinedList);

		computeConcentrationByInconsistency(clientInconsistencies, combinedList);

		return inconsistenciesResponse;
	}

	private void computeConcentrationByInconsistency(List<InconsistencyErrorDTO> clientInconsistencies,
			List<InconsistencyConcentration> combinedList) {
		for (InconsistencyErrorDTO inconsistency : clientInconsistencies) {
			InconsistencyConcentration concentration = combinedList.stream()
					.filter(e -> e.getId().equals(inconsistency.getElId())).findFirst().orElse(null);
			if (concentration != null) {
				inconsistency.setConcentration(concentration.getConcentration());
			}
		}
	}

	private void computeSequences(UMLModelDTO umlModel) {
		for (SequenceDiagram seqDiagram : umlModel.getSequenceDiagram()) {
			numInconsistencies = count.getOrDefault(seqDiagram.getId(), 0);
			if (numInconsistencies <= 0) {
				continue;
			}

			List<InconsistencyConcentration> concentrations = new ArrayList<>();
			concetrationClassSeq.put(seqDiagram.getId(), concentrations);

			for (UMLElement lifeline : seqDiagram.getLifelines()) {
				if (lifeline.getInconsistenciesCount() <= 0) {
					continue;
				}

				count.put(lifeline.getId(), lifeline.getInconsistenciesCount());

				double concentration = ((double) lifeline.getInconsistenciesCount() / numInconsistencies);
				concentrations.add(new InconsistencyConcentration(lifeline.getId(), lifeline.getParentId(),
						lifeline.getName(), lifeline.getInconsistenciesCount(), concentration));

				List<SequenceMessage> lifelineMessages = seqDiagram.getMessages().stream()
						.filter(m -> m.getParentId().equals(lifeline.getId()) && m.getInconsistenciesCount() > 0)
						.toList();

				for (UMLElement msg : lifelineMessages) {
					int numInconsistenciesLifeline = lifeline.getInconsistenciesCount();
					if (msg.getInconsistenciesCount() <= 0) {
						continue;
					}

					count.put(msg.getId(), msg.getInconsistenciesCount());

					double concentrationMsg = ((double) msg.getInconsistenciesCount() / numInconsistenciesLifeline);
					concentrations.add(new InconsistencyConcentration(msg.getId(), msg.getParentId(), msg.getName(),
							msg.getInconsistenciesCount(), concentrationMsg));
				}
			}
		}
	}

	private void computeClasses(UMLModelDTO umlModel) {
		for (ClassDiagram classDiagram : umlModel.getClassDiagram()) {
			numInconsistencies = count.getOrDefault(classDiagram.getId(), 0);
			if (numInconsistencies <= 0) {
				continue;
			}

			List<InconsistencyConcentration> concentrations = new ArrayList<>();
			concetrationClassSeq.put(classDiagram.getId(), concentrations);

			for (UMLElement _class : classDiagram.getClasses()) {
				if (_class.getInconsistenciesCount() <= 0) {
					continue;
				}

				count.put(_class.getId(), _class.getInconsistenciesCount());

				double concentration = ((double) _class.getInconsistenciesCount() / numInconsistencies);
				concentrations.add(new InconsistencyConcentration(_class.getId(), _class.getParentId(),
						_class.getName(), _class.getInconsistenciesCount(), concentration));
			}
		}
	}

	private void computePkgEl() {
		for (UMLElement pkg : packages.values()) {
			if (pkg.getInconsistenciesCount() <= 0) {
				continue;
			}
			count.put(pkg.getId(), pkg.getInconsistenciesCount());

			double concentration = ((double) pkg.getInconsistenciesCount() / numInconsistencies);
			concetrationPkgs.put(pkg.getId(), new InconsistencyConcentration(pkg.getId(), pkg.getParentId(),
					pkg.getName(), pkg.getInconsistenciesCount(), concentration));
		}
	}

	private void readModelElements(UMLModelDTO umlModel) {
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

	public InconsistenciesResponse computeModelMetrics(List<InconsistencyErrorDTO> clientInconsistencies,
			InconsistenciesResponse inconsistenciesResponse) {
		List<DiagramStatistics> diagramStatistics = new ArrayList<>();
		inconsistenciesResponse.setDiagramStatistics(diagramStatistics);

		for (UMLElement pkg : packages.values()) {
			int qtdInc = pkg.getInconsistenciesCount();

			List<UMLElement> pkgElements = elements.values().stream()
					.filter(e -> e.getParentId() != null && e.getParentId().equals(pkg.getId()))
					.sorted((el1, el2) -> Integer.compare(el2.getInconsistenciesCount(), el1.getInconsistenciesCount()))
					.collect(Collectors.toList());

			int qtdEl = pkgElements.size();

			if (qtdEl <= 0) {
				continue;
			}

			double denominator = qtdInc * ((double) qtdEl - 1);

			double numerator = 0;
			for (int j = 0; j < pkgElements.size(); j++) {
				numerator += pkgElements.get(j).getInconsistenciesCount() * j;
			}

			double riskMisinterpretation = ((double) 2 * (numerator / denominator)) * 100;
			long qtdElInc = pkgElements.stream().filter(e -> e.getInconsistenciesCount() > 0).count();
			double spreadRate = ((double) qtdElInc / (double) qtdEl) * 100;
			double concentrationInc = ((double) qtdInc / (double) qtdEl) * 100;

			DiagramStatistics statistics = new DiagramStatistics(pkg.getId(), riskMisinterpretation, spreadRate,
					concentrationInc);

			diagramStatistics.add(statistics);
		}

		return inconsistenciesResponse;
	}

	private void computeInconsistency(String id) {
		if (id == null) {
			return;
		}

		UMLElement el = elements.get(id);
		if (el != null) {
			el.addCount();
			computeInconsistency(el.getParentId());
		}
	}
}
