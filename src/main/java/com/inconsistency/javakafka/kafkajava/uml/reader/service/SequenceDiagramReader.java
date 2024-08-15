package com.inconsistency.javakafka.kafkajava.uml.reader.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Gate;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageEnd;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.impl.BehaviorExecutionSpecificationImpl;
import org.eclipse.uml2.uml.internal.impl.InteractionImpl;
import org.eclipse.uml2.uml.internal.impl.MessageOccurrenceSpecificationImpl;
import org.eclipse.uml2.uml.internal.impl.OpaqueExpressionImpl;

import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceBehavior;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceCombinedFragment;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceDiagram;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceGate;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceLifeline;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence.SequenceMessage;
import com.inconsistency.javakafka.kafkajava.uml.reader.ReaderUtils;
import com.inconsistency.javakafka.kafkajava.uml.utils.Keywords;

public class SequenceDiagramReader implements Serializable {
	private static final long serialVersionUID = 1L;

	public static List<SequenceDiagram> getRefModelDetails(Package _package) throws Exception {
		if (_package == null) {
			throw new Exception("[SequenceDiagram] Package is null");
		}

		EList<PackageableElement> packageableElements = _package.getPackagedElements();

		List<SequenceDiagram> sequenceDiagrams = new ArrayList<SequenceDiagram>();

		for (PackageableElement element : packageableElements) {
			if (element.eClass() == UMLPackage.Literals.INTERACTION && element instanceof InteractionImpl) {
				InteractionImpl interactionImpl = (InteractionImpl) element;

				SequenceDiagram sequenceDiagram = new SequenceDiagram();
				sequenceDiagram.setName(interactionImpl.getName());
				sequenceDiagram.setId(ReaderUtils.getXMLId(element));
				interactionReader(interactionImpl, sequenceDiagram, element);

				sequenceDiagrams.add(sequenceDiagram);
			}
		}

		return sequenceDiagrams;
	}

	/**
	 * UML Interactions Reader read interaction messages, lifeLines,Formal
	 * gates,MessageOccurrence,CombinedFragment,BehaviorExecution
	 *
	 * @param interactionImpl InteractionImpl
	 *                        gates,MessageOccurrence,CombinedFragment,BehaviorExecution
	 */

	protected static void interactionReader(InteractionImpl interactionImpl, SequenceDiagram sequenceDiagram,
			PackageableElement _package) {
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
			SequenceLifeline lifelineReader = lifelineReader(lifeline);
			lifelineReader.setParentId(sequenceDiagram.getId());
			sequenceDiagram.getLifelines().add(lifelineReader);
		}

		// Messages
		for (Message message : interactionImpl.getMessages()) {
			SequenceMessage messageReader = messageReader(message);

			SequenceLifeline sequenceLifeline = sequenceDiagram.getLifelines().stream()
					.filter(l -> l.getName().equals(messageReader.getSender().getLifelineName())).findFirst()
					.orElse(null);
			if (sequenceLifeline != null) {
				messageReader.setSender(sequenceLifeline);
				messageReader.setParentId(sequenceLifeline.getId());
			}

			sequenceLifeline = sequenceDiagram.getLifelines().stream()
					.filter(l -> l.getName().equals(messageReader.getReceiver().getLifelineName())).findFirst()
					.orElse(null);
			if (sequenceLifeline != null) {
				messageReader.setReceiver(sequenceLifeline);
			}

			sequenceDiagram.getMessages().add(messageReader);
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
		sequenceCombinedFragment.setId(ReaderUtils.getXMLId(combinedFragment));

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
		behavior.setId(ReaderUtils.getXMLId(fragment));
		behavior.setName(fragment.getName());
		behavior.setVisibility(fragment.getVisibility().toString());
		behavior.setType(SequenceBehavior.class.toString());

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
		sequenceGate.setId(ReaderUtils.getXMLId(gate));
		sequenceGate.setName(gate.getName());
		sequenceGate.setVisibility(gate.getVisibility().toString());
		sequenceGate.setType(SequenceGate.class.toString());
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
		sequenceLifeline.setId(ReaderUtils.getXMLId(lifeline));
		sequenceLifeline.setName(lifeline.getName());
		sequenceLifeline.setVisibility(lifeline.getVisibility().toString());
		sequenceLifeline.setType(SequenceLifeline.class.toString());
		sequenceLifeline.setLifelineName(lifeline.getName());
		if (lifeline.getRepresents() != null) {
			sequenceLifeline.setRepresents(lifeline.getRepresents().getName());
		}
		return sequenceLifeline;
	}

	protected static ArrayList<SequenceLifeline> packageLifelines(List<SequenceDiagram> sequenceDiagrams) {
		ArrayList<SequenceLifeline> sequenceLifelines = new ArrayList<>();

		for (SequenceDiagram sequenceDiagram : sequenceDiagrams) {
			for (SequenceLifeline lifeline : sequenceDiagram.getLifelines()) {
				sequenceLifelines.add(lifeline);
			}
		}

		return sequenceLifelines;
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
		sequenceMessage.setId(ReaderUtils.getXMLId(message));
		sequenceMessage.setVisibility(message.getVisibility().toString());
		sequenceMessage.setType(SequenceMessage.class.toString());

		if (message.getSignature() != null) {
			NamedElement signature = message.getSignature();
			sequenceMessage.setMessageName(signature.getName());
			sequenceMessage.setMessageType(message.getMessageSort().toString());

		} else if (message.getName() == null || message.getName().isEmpty()) {
			sequenceMessage.setMessageName("");
			sequenceMessage.setMessageType(message.getMessageSort().toString());

		} else if (!message.getName().isEmpty()) {
			String messageName = message.getName();
			
			if (message.getName().contains("(") || message.getName().contains(")")) {
				String regex = "^\\s*(?:^\\s*([a-zA-Z_]\\w+)\\s*[\\(|)]?\\s*|\\G,\\s*)(\\s*([a-zA-Z_]\\w+)\\s+([a-zA-Z_]\\w+),?)*\\)?\\s*$";
				
				Pattern pattern = Pattern.compile(regex, Pattern.COMMENTS | Pattern.MULTILINE);
		        final Matcher matcher = pattern.matcher(messageName);
		        
		        if (matcher.matches() && matcher.groupCount() > 2 && !matcher.group(1).isEmpty()) {
		        	messageName = matcher.group(1);		        	
		        }
			}
			
			sequenceMessage.setMessageName(messageName);
			sequenceMessage.setMessageType(message.getMessageSort().toString());
		}

		sequenceMessage.setName(sequenceMessage.getMessageName());

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

	protected static ArrayList<SequenceMessage> packageMessages(List<SequenceDiagram> sequenceDiagrams) {
		ArrayList<SequenceMessage> sequenceMessages = new ArrayList<>();

		for (SequenceDiagram sequenceDiagram : sequenceDiagrams) {
			for (SequenceMessage sequenceMessage : sequenceDiagram.getMessages()) {
				sequenceMessages.add(sequenceMessage);
			}
		}

		return sequenceMessages;
	}
}
