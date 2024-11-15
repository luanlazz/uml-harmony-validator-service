package com.inconsistency.javakafka.kafkajava.uml.reader.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.impl.InteractionImpl;

import com.inconsistency.javakafka.kafkajava.entities.uml.dto.UMLModelDTO;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassDiagram;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassInstance;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._package.PackageStructure;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceDiagram;
import com.inconsistency.javakafka.kafkajava.uml.reader.PackageReader;
import com.inconsistency.javakafka.kafkajava.uml.reader.ReaderUtils;

public class PackageReaderService implements Serializable {
	private static final long serialVersionUID = 1L;

	public static UMLModelDTO getRefModelDetails(Package _package) throws Exception {
		if (_package == null) {
			throw new Exception("[Model] Package is null");
		}

		UMLModelDTO umlModel = new UMLModelDTO();

		umlModel.setId(ReaderUtils.getXMLId(_package));
		umlModel.setName(_package.getName() != null ? _package.getName() : "");

		EList<PackageableElement> packageableElements = _package.getPackagedElements();

		PackageStructure packageStructure = PackageReader.readPackage(packageableElements, _package);

		umlModel.getClassDiagram().addAll(readClassDiagramList(packageStructure));
		umlModel.getClasses().addAll(ClassDiagramReader.classStructures(packageStructure));
		umlModel.getInstances().addAll(ClassDiagramReader.classInstances(packageStructure));

		umlModel.getSequenceDiagram().addAll(readSequenceDiagramList(packageableElements));
		umlModel.getLifelines().addAll(SequenceDiagramReader.packageLifelines(umlModel.getSequenceDiagram()));
		umlModel.getMessages().addAll(SequenceDiagramReader.packageMessages(umlModel.getSequenceDiagram()));

		return umlModel;
	}

	private static List<SequenceDiagram> readSequenceDiagramList(EList<PackageableElement> packageableElements) {
		List<SequenceDiagram> sequenceDiagramList = new ArrayList<>();
		
		for (PackageableElement element : packageableElements) {
			if (element.eClass() == UMLPackage.Literals.INTERACTION && element instanceof InteractionImpl) {
				InteractionImpl interactionImpl = (InteractionImpl) element;

				SequenceDiagram sequenceDiagram = new SequenceDiagram();
				sequenceDiagram.setName(interactionImpl.getName());
				sequenceDiagram.setId(ReaderUtils.getXMLId(element));
				SequenceDiagramReader.interactionReader(interactionImpl, sequenceDiagram, element);

				sequenceDiagramList.add(sequenceDiagram);
			}
		}
		return sequenceDiagramList;
	}

	private static List<ClassDiagram> readClassDiagramList(PackageStructure packageStructure) {
		List<ClassDiagram> classDiagramList = new ArrayList<>();
		
		if (packageStructure.getPackages().size() > 0) {
			for (PackageStructure pkgStructure : packageStructure.getPackages()) {
				ClassDiagram classDiagram = new ClassDiagram();
				classDiagram.setId(pkgStructure.getId());
				classDiagram.setName(pkgStructure.getName());
				classDiagram.setVisibility(pkgStructure.getPackage().getVisibility().toString());
				classDiagram.setType(ClassDiagram.class.toString());
				classDiagram.getClasses().addAll(ClassDiagramReader.classStructures(pkgStructure));
				classDiagram.getInstances().addAll(ClassDiagramReader.classInstances(pkgStructure));

				classDiagramList.add(classDiagram);
			}
		}
		
		if (packageStructure.getClasses().size() > 0) {
			ClassDiagram classDiagram = new ClassDiagram();
			classDiagram.setId(packageStructure.getId());
			classDiagram.setName(packageStructure.getName());
			classDiagram.setVisibility(packageStructure.getPackage().getVisibility().toString());
			classDiagram.setType(ClassDiagram.class.toString());
			classDiagram.getClasses().addAll(ClassDiagramReader.classStructures(packageStructure));
			classDiagram.getInstances().addAll(ClassDiagramReader.classInstances(packageStructure));

			classDiagramList.add(classDiagram);
		}
		
		return classDiagramList;
	}
}
