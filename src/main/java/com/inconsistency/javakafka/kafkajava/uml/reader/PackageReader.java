package com.inconsistency.javakafka.kafkajava.uml.reader;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.UMLPackage;

import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassInstance;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.uml.models._enum.EnumStructure;
import com.inconsistency.javakafka.kafkajava.uml.models._package.PackageStructure;

public class PackageReader {

	public static PackageStructure readPackage(EList<PackageableElement> packageableElements, String packageName,
			Package _package) {

		PackageStructure packageStructure = new PackageStructure();
		packageStructure.setName(packageName);

		for (PackageableElement element : packageableElements) {
			if (element.eClass() == UMLPackage.Literals.CLASS) {
				ClassStructure classStructure = ClassStructureReader.readClass(element, packageName);
				packageStructure.getClasses().add(classStructure);
			} else if (element.eClass() == UMLPackage.Literals.ENUMERATION) {
				EnumStructure enumStructure = EnumerationReader.readEnumeration(element, packageName);
				packageStructure.getEnums().add(enumStructure);
			} else if (element.eClass() == UMLPackage.eINSTANCE.getInstanceSpecification()) {
				ClassInstance classInstance = InstanceReader.readInstance(element, packageName);
				packageStructure.getInstances().add(classInstance);
			} else if (element.eClass() == UMLPackage.Literals.PACKAGE) {
				Package _elPackage = (Package) element;
				String newPackageName;

				if (packageName.equals("")) {
					newPackageName = _elPackage.getName() != null ? _elPackage.getName() : packageName;
				} else {
					newPackageName = _elPackage.getName() != null ? packageName + "." + _elPackage.getName()
							: packageName;
				}
				PackageStructure nustedPackageStructure = readPackage(_elPackage.getPackagedElements(), newPackageName,
						_elPackage);
				packageStructure.getPackages().add(nustedPackageStructure);
			}
		}
		return packageStructure;
	}

}
