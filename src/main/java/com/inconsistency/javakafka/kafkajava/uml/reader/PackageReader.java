package com.inconsistency.javakafka.kafkajava.uml.reader;

import org.eclipse.emf.common.util.EList;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassInstance;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.uml.models._enum.EnumStructure;
import com.inconsistency.javakafka.kafkajava.uml.models._package.PackageStructure;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.UMLPackage;

public class PackageReader {

    public static PackageStructure readPackage(EList<PackageableElement> packageableElements, String packageName) {

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
                Package _package = (Package) element;
                String newPackageName;

                if (packageName.equals("")) {
                    newPackageName = _package.getName() != null
                            ? _package.getName()
                            : packageName;
                } else {
                    newPackageName = _package.getName() != null
                            ? packageName + "." + _package.getName()
                            : packageName;
                }
                PackageStructure nustedPackageStructure = readPackage(_package.getPackagedElements(), newPackageName);
                packageStructure.getPackages().add(nustedPackageStructure);
            }
        }
        return packageStructure;
    }


}
