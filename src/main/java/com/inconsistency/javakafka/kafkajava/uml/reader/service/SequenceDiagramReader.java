package com.inconsistency.javakafka.kafkajava.uml.reader.service;

import org.eclipse.emf.common.util.EList;

import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._package.PackageStructure;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.*;
import com.inconsistency.javakafka.kafkajava.uml.reader.ClassStructureReader;
import com.inconsistency.javakafka.kafkajava.uml.reader.PackageReader;
import com.inconsistency.javakafka.kafkajava.uml.utils.Keywords;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.*;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.internal.impl.*;

import java.io.Serializable;
import java.util.ArrayList;

public class SequenceDiagramReader implements Serializable {
	private static final long serialVersionUID = 1L;

	public static SequenceDiagram getRefModelDetails(Package _package) throws Exception {
		if (_package == null) {
			throw new Exception("[SequenceDiagram] Package is null");
		}

		EList<PackageableElement> packageableElements = _package.getPackagedElements();
		String packageName = _package.getName() != null ? _package.getName() : "";

		SequenceDiagram sequenceDiagram = new SequenceDiagram();
		sequenceDiagram.setPackage(packageName);

		for (PackageableElement element : packageableElements) {

			if (element.eClass() == UMLPackage.Literals.CLASS) {
				Class umlClass = (Class) element;
				ClassStructure structure = ClassStructureReader.readClass(umlClass, null);
				sequenceDiagram.getClasses().add(structure);
			}
			if (element.eClass() == UMLPackage.Literals.COLLABORATION) {
				CollaborationImpl collaborationImpl = (CollaborationImpl) element;

				for (Property property : collaborationImpl.getAttributes()) {
					SequenceAttribute attribute = new SequenceAttribute(property.getName(),
							property.getType().getName());
					sequenceDiagram.getAttributes().add(attribute);
				}

				if (collaborationImpl.getOwnedBehaviors() != null) {
					for (Element element2 : collaborationImpl.getOwnedBehaviors()) {
						if (element2.eClass() == UMLPackage.Literals.INTERACTION) {
							InteractionImpl interactionImpl = (InteractionImpl) element2;
							interactionReader(interactionImpl, sequenceDiagram);
						}
					}
				}
			}

			if (element.eClass() == UMLPackage.Literals.INTERACTION && element instanceof InteractionImpl) {
				InteractionImpl interactionImpl = (InteractionImpl) element;
				interactionReader(interactionImpl, sequenceDiagram);
			}

		}

		return sequenceDiagram;
	}

	/**
	 * UML Interactions Reader read interaction messages, lifeLines,Formal
	 * gates,MessageOccurrence,CombinedFragment,BehaviorExecution
	 *
	 * @param interactionImpl InteractionImpl
	 *                        gates,MessageOccurrence,CombinedFragment,BehaviorExecution
	 */

	private static void interactionReader(InteractionImpl interactionImpl, SequenceDiagram sequenceDiagram) {

		for (InteractionFragment interactionFragment : interactionImpl.getFragments()) {
			// reading behaviors
			if (interactionFragment instanceof BehaviorExecutionSpecificationImpl) {
				BehaviorExecutionSpecificationImpl fragment = (BehaviorExecutionSpecificationImpl) interactionFragment;
				sequenceDiagram.getBehaviors().add(behaviorReader(fragment));
			}

			// reading combine fragments
			if (interactionFragment instanceof CombinedFragment) {
				CombinedFragment combinedFragment = (CombinedFragment) interactionFragment;
				sequenceDiagram.getFragments().add(fragmentReader(combinedFragment));
			}
		}

		// Adding Behavior Calls
		for (SequenceBehavior behavior : sequenceDiagram.getBehaviors()) {
			addBehaviorDetail(behavior, interactionImpl.getFragments());
		}

		// FormalGates
		for (Gate gate : interactionImpl.getFormalGates()) {
			sequenceDiagram.getGates().add(gateReader(gate));
		}

		// LifLines
		for (Lifeline lifeline : interactionImpl.getLifelines()) {
			sequenceDiagram.getLifelines().add(lifelineReader(lifeline));
		}

		// Messages
		for (Message message : interactionImpl.getMessages()) {
			sequenceDiagram.getMessages().add(messageReader(message));
		}

	}

	private static void addBehaviorDetail(SequenceBehavior behavior, EList<InteractionFragment> fragments) {
		ArrayList<String> calles = new ArrayList<>();
		// MessageOccurrence
		for (InteractionFragment interactionFragment : fragments) {
			if (interactionFragment instanceof MessageOccurrenceSpecificationImpl) {
				MessageOccurrenceSpecificationImpl fragment = (MessageOccurrenceSpecificationImpl) interactionFragment;
				if (fragment.getCovereds().size() == 0) {
					continue;
				}

				SequenceLifeline lifeline = lifelineReader(fragment.getCovereds().get(0));

				if (behavior.getLifeline().getLifelineName().equals(lifeline.getLifelineName())) {

					SequenceMessage message = messageReader(fragment.getMessage());
					if (message != null && message.getMessageName() != null) {

						calles.add(message.getMessageName());

						// Break Loop if found Synchronized Message Reply
						if (behavior.getFinish() != null && message.getMessageType() != null) {
							if (behavior.getFinish().getMessageName().equals(message.getMessageName())
									&& message.getMessageType().equals(Keywords.Reply)) {
								break;
							}
						}

						// All Messages After Start Message
						if (behavior.getStart() != null && behavior.getStart().getMessageName() != null
								&& !behavior.getStart().getMessageName().equals(message.getMessageName())) {

							// If Message Type is not Reply
							if (message.getMessageType() != null) {
								if (!message.getMessageType().equals(Keywords.Reply)) {

									if (message.getReceiver() != null) {
										// Message Next Behavior Covered
										// Lifeline
										if (!message.getReceiver().getLifelineName()
												.equals(lifeline.getLifelineName())) {

											if (calles.contains(behavior.getStart().getMessageName())) {
												behavior.addCall(message);
											}
										}
									}
								}
							}
						}
					}
				}
			} else if (interactionFragment instanceof CombinedFragment) {
				CombinedFragment combinedFragment = (CombinedFragment) interactionFragment;
				SequenceCombinedFragment fragment = fragmentReader(combinedFragment);
				if (fragment != null) {
					for (SequenceLifeline lifeline : fragment.getSequenceLifelines()) {
						if (lifeline.getLifelineName().equals(behavior.getLifeline().getLifelineName())) {
							behavior.addFragment(fragment);
						}
					}
				}
			}

		}

	}

	/**
	 * fragmentReader read fragment type,condition,messages
	 *
	 * @param combinedFragment CombinedFragment
	 * @return SequenceCombinedFragment
	 */

	private static SequenceCombinedFragment fragmentReader(CombinedFragment combinedFragment) {

		SequenceCombinedFragment sequenceCombinedFragment = new SequenceCombinedFragment();
		for (Lifeline lifeline : combinedFragment.getCovereds()) {
			SequenceLifeline fragmentLifeline = lifelineReader(lifeline);
			sequenceCombinedFragment.addSequenceLifeline(fragmentLifeline);

		}

		sequenceCombinedFragment.setOperation(combinedFragment.getInteractionOperator().getName());
		for (InteractionOperand interactionOperand : combinedFragment.getOperands()) {
			if (interactionOperand.getGuard() == null) {
				continue;
			}
			
			OpaqueExpressionImpl opaqueExpressionImpl = (OpaqueExpressionImpl) interactionOperand.getGuard()
					.getSpecification();
			sequenceCombinedFragment.setCondition(opaqueExpressionImpl.getBodies().get(0));

			for (InteractionFragment Operandfragment : interactionOperand.getFragments()) {
				if (Operandfragment instanceof MessageOccurrenceSpecificationImpl) {
					MessageOccurrenceSpecificationImpl fragment = (MessageOccurrenceSpecificationImpl) Operandfragment;

					SequenceMessage message = messageReader(fragment.getMessage());

					if (!message.getSender().getLifelineName().equals("")) {
						sequenceCombinedFragment.addCall(message);
					}
				}
			}

		}

		return sequenceCombinedFragment;
	}

	/**
	 * behaviorReader read BehaviorFragment Lifeline and start and finsh event
	 *
	 * @param fragment BehaviorExecutionSpecificationImpl
	 * @return SequenceBehavior
	 */
	private static SequenceBehavior behaviorReader(BehaviorExecutionSpecificationImpl fragment) {
		SequenceBehavior behavior = new SequenceBehavior();
		for (Lifeline lifeline : fragment.getCovereds()) {
			behavior.setLifeline(lifelineReader(lifeline));
		}
		// Behavior Start Occurrence
		if (fragment.getStart() instanceof MessageOccurrenceSpecification) {
			MessageOccurrenceSpecification specification = (MessageOccurrenceSpecification) fragment.getStart();
			behavior.setStart(messageReader(specification.getMessage()));
		}
		// Behavior Finish Occurrence
		if (fragment.getFinish() instanceof MessageOccurrenceSpecification) {
			MessageOccurrenceSpecification specification = (MessageOccurrenceSpecification) fragment.getFinish();
			behavior.setFinish(messageReader(specification.getMessage()));
		}
		return behavior;
	}

	/**
	 * gate reader read sequenceGate from sequenceDiagram
	 *
	 * @param gate Gate
	 * @return SequenceGate
	 */
	private static SequenceGate gateReader(Gate gate) {
		SequenceGate sequenceGate = new SequenceGate();
		sequenceGate.setGateMessage(gate.getMessage().getName());
		MessageEnd interactionFragment = gate.getMessage().getReceiveEvent();

		if (interactionFragment instanceof MessageOccurrenceSpecificationImpl) {
			MessageOccurrenceSpecificationImpl fragment = (MessageOccurrenceSpecificationImpl) interactionFragment;
			for (Lifeline lifeline : fragment.getCovereds()) {
				SequenceLifeline sequenceLifeline = new SequenceLifeline();
				sequenceLifeline.setLifelineName(lifeline.getName());
				sequenceLifeline.setRepresents(lifeline.getRepresents().getName());
				sequenceGate.setGateLifeline(sequenceLifeline);
			}
		}
		return sequenceGate;
	}

	/**
	 * lifelineReader read lifeline name representation from Lifeline
	 *
	 * @param lifeline Lifeline
	 * @return SequenceLifeline
	 */
	private static SequenceLifeline lifelineReader(Lifeline lifeline) {
		SequenceLifeline sequenceLifeline = new SequenceLifeline();
		sequenceLifeline.setLifelineName(lifeline.getName());
		if (lifeline.getRepresents() != null) {
			sequenceLifeline.setRepresents(lifeline.getRepresents().getName());
		}
		return sequenceLifeline;
	}

	/**
	 * messageReader read sequence message and get message Name, Type, Sender and
	 * Receiver lifeLines
	 *
	 * @param message Message
	 * @return SequenceMessage
	 */
	private static SequenceMessage messageReader(Message message) {
		if (message == null) {
			return null;
		}

		SequenceMessage sequenceMessage = new SequenceMessage();
		if (message.getSignature() != null) {
			NamedElement signature = message.getSignature();
			sequenceMessage.setMessageName(signature.getName());
			sequenceMessage.setMessageType(message.getMessageSort().toString());

		} else if (message.getName() == null || message.getName().isEmpty()) {
			sequenceMessage.setMessageName("");
			sequenceMessage.setMessageType(message.getMessageSort().toString());

		} else if (!message.getName().isEmpty() && !message.getName().contains("(") && !message.getName().contains(")")
				&& !message.getName().contains(" ")) {
			sequenceMessage.setMessageName(message.getName());
			sequenceMessage.setMessageType(message.getMessageSort().toString());
		}
		// TODO mensagem com ()

		// Send Event
		if (message.getSendEvent() instanceof MessageOccurrenceSpecification) {
			MessageOccurrenceSpecification specification = (MessageOccurrenceSpecification) message.getSendEvent();
			for (Lifeline lifeline : specification.getCovereds()) {
				SequenceLifeline sequenceLifeline = new SequenceLifeline();
				sequenceLifeline.setLifelineName(lifeline.getName());
				if (lifeline.getRepresents() != null) {
					sequenceLifeline.setRepresents(lifeline.getRepresents().getName());
				}
				sequenceMessage.setSender(sequenceLifeline);
			}
		}

		// Received Event
		if (message.getReceiveEvent() instanceof MessageOccurrenceSpecification) {
			MessageOccurrenceSpecification specification = (MessageOccurrenceSpecification) message.getReceiveEvent();
			for (Lifeline lifeline : specification.getCovereds()) {
				SequenceLifeline sequenceLifeline = new SequenceLifeline();
				sequenceLifeline.setLifelineName(lifeline.getName());
				if (lifeline.getRepresents() != null) {
					sequenceLifeline.setRepresents(lifeline.getRepresents().getName());
				}
				sequenceMessage.setReceiver(sequenceLifeline);
			}
		}

		return sequenceMessage;
	}

}
