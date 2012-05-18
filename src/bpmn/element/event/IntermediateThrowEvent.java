package bpmn.element.event;

import java.util.Collection;

import javax.swing.Icon;

import bpmn.Model;
import bpmn.element.VisibleElement;
import bpmn.element.activity.ExpandedProcess;
import bpmn.element.event.definition.EventDefinition;
import bpmn.element.event.definition.LinkEventDefinition;
import bpmn.element.event.definition.SignalEventDefinition;
import bpmn.token.Instance;
import bpmn.token.Token;

@SuppressWarnings("serial")
public final class IntermediateThrowEvent extends IntermediateEvent {

	public IntermediateThrowEvent(final String id, final String name) {
		super(id, name);
	}

	@Override
	protected Icon getTypeIcon() {
		return getDefinition().getIcon(getVisualConfig(), true);
	}

	protected static IntermediateCatchEvent findLinkTargetInProcess(
			final ExpandedProcess process, final String targetName) {
		for (final VisibleElement element : process.getElements()) {
			if (element instanceof IntermediateCatchEvent) {
				final IntermediateCatchEvent event = (IntermediateCatchEvent)element;
				final EventDefinition definition = event.getDefinition(); 
				if (definition instanceof LinkEventDefinition) {
					final String name = ((LinkEventDefinition)definition).getName();
					if (targetName.equals(name)) {
						return event; 
					}
				}
			}
		}
		return null;
	}

	protected IntermediateCatchEvent findLinkTarget(final String targetName) {
		IntermediateCatchEvent linkTarget
				= findLinkTargetInProcess(getProcess(), targetName);
		if (linkTarget == null) {
			for (final ExpandedProcess process : getProcess().getModel().getProcesses()) {
				linkTarget = findLinkTargetInProcess(process, targetName);
				if (linkTarget != null) {
					break;
				}
			}
		}
		return linkTarget;
	}

	@Override
	protected void tokenForward(final Token token) {
		if (isLink()) {
			final String name = ((LinkEventDefinition)getDefinition()).getName();
			final IntermediateCatchEvent targetEvent = findLinkTarget(name);
			if (targetEvent != null) {
				token.passTo(targetEvent);
				token.remove();
			}
		} else if (isSignal()) {
			final Instance instance = token.getInstance();
			final Model model = getProcess().getModel();
			final SignalEventDefinition eventDefinition = (SignalEventDefinition)getDefinition();
			final Collection<CatchEvent> catchEvents =  model.getCatchEvents();
			for (final CatchEvent catchEvent : catchEvents) {
				if (eventDefinition.equals(catchEvent.getDefinition())) {
					catchEvent.happen(instance);
				}
			}
			token.remove();
		} else {
			super.tokenForward(token);
		}
	}

}
