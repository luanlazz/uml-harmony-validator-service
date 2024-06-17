package com.inconsistency.javakafka.kafkajava.uml.reader;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.InstanceSpecification;
import org.eclipse.uml2.uml.InstanceValue;
import org.eclipse.uml2.uml.LiteralBoolean;
import org.eclipse.uml2.uml.LiteralInteger;
import org.eclipse.uml2.uml.LiteralReal;
import org.eclipse.uml2.uml.LiteralSpecification;
import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Slot;
import org.eclipse.uml2.uml.StructuralFeature;
import org.eclipse.uml2.uml.ValueSpecification;

import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassInstance;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.uml.models._class.InstanceAttribute;

public class InstanceReader {

	public static ClassInstance readInstance(PackageableElement element, String packageName) {
		ClassInstance classInstance = new ClassInstance();
		InstanceSpecification instance = (InstanceSpecification) element;
		if (instance.getName() != null && !instance.getName().isEmpty()) {
			classInstance.setName(instance.getName());
			classInstance.set_package(packageName);

			for (Slot slot : instance.getSlots()) {
				StructuralFeature feature = slot.getDefiningFeature();

				InstanceAttribute attribute = new InstanceAttribute();
				attribute.setName(feature.getName());
				attribute.setType(feature.getType().getName());

				List<Object> values = new ArrayList<>();
				for (ValueSpecification valueSpecification : slot.getValues()) {
					if (valueSpecification instanceof InstanceValue) {
						attribute.setClass(true);
						InstanceValue instanceValue = (InstanceValue) valueSpecification;

						InstanceSpecification valueInstanceSpecification = instanceValue.getInstance();

						if (valueInstanceSpecification != null) {
							values.add(valueInstanceSpecification.getName());
						}
					} else if (valueSpecification instanceof LiteralSpecification) {
						LiteralSpecification literalSpecification = (LiteralSpecification) valueSpecification;

						if (literalSpecification instanceof LiteralString) {
							LiteralString literal = (LiteralString) literalSpecification;
							values.add(literal.getValue());

						} else if (literalSpecification instanceof LiteralInteger) {
							LiteralInteger literal = (LiteralInteger) literalSpecification;
							values.add(literal.getValue());

						} else if (literalSpecification instanceof LiteralBoolean) {
							LiteralBoolean literal = (LiteralBoolean) literalSpecification;
							values.add(literal.isValue());

						} else if (literalSpecification instanceof LiteralReal) {
							LiteralReal literal = (LiteralReal) literalSpecification;
							values.add(literal.getValue());

						} else if (literalSpecification instanceof LiteralUnlimitedNatural) {
							LiteralUnlimitedNatural literal = (LiteralUnlimitedNatural) literalSpecification;
							values.add(literal.getValue());
						}
					}
				}

				attribute.setValues(values.toArray());
				classInstance.addAttribute(attribute);
			}

			for (Classifier classifier : instance.getClassifiers()) {
				if (instance.getName() != null && classifier.getName() != null) {
					ClassStructure classStructure = new ClassStructure();
					classStructure.setName(classifier.getName());
					classStructure.setPackage(classifier.getPackage().getName());
					classInstance.getClasses().add(classStructure);
				}
			}
		}

		return classInstance;
	}
}
