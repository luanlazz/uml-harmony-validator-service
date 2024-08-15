package com.inconsistency.javakafka.kafkajava.uml.reader;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.UMLPackage;

import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassInstance;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._enum.EnumStructure;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._package.PackageStructure;

public class PackageReader {

	public static PackageStructure readPackage(EList<PackageableElement> packageableElements, Package _package) {

		PackageStructure packageStructure = new PackageStructure();
		packageStructure.setId(ReaderUtils.getXMLId(_package));
		packageStructure.setName(_package.getName());
		packageStructure.setPackage(_package);

		for (PackageableElement element : packageableElements) {
			if (element.eClass() == UMLPackage.Literals.CLASS) {
				ClassStructure classStructure = ClassStructureReader.readClass(element, _package);
				packageStructure.getClasses().add(classStructure);
			} else if (element.eClass() == UMLPackage.Literals.ENUMERATION) {
				EnumStructure enumStructure = EnumerationReader.readEnumeration(element, _package);
				packageStructure.getEnums().add(enumStructure);
			} else if (element.eClass() == UMLPackage.eINSTANCE.getInstanceSpecification()) {
				ClassInstance classInstance = InstanceReader.readInstance(element, _package);
				packageStructure.getInstances().add(classInstance);
			} else if (element.eClass() == UMLPackage.Literals.PACKAGE) {
				Package _elPackage = (Package) element;
				String newPackageName;

				newPackageName = _elPackage.getName() != null ? _elPackage.getName() : _package.getName();

				_elPackage.setName(newPackageName);
				PackageStructure nustedPackageStructure = readPackage(_elPackage.getPackagedElements(), _elPackage);
				packageStructure.getPackages().add(nustedPackageStructure);
			}
		}
		return packageStructure;
	}

}
