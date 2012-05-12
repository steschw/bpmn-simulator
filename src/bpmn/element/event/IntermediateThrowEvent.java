package bpmn.element.event;

import javax.swing.Icon;

import bpmn.element.BaseElement;
import bpmn.element.activity.ExpandedProcess;
import bpmn.token.Token;

@SuppressWarnings("serial")
public class IntermediateThrowEvent extends IntermediateEvent {

	public IntermediateThrowEvent(final String id, final String name) {
		super(id, name);
	}

	@Override
	protected Icon getTypeIcon() {
		return getDefinition().getIcon(getVisualConfig(), true);
	}

	protected static IntermediateCatchEvent findLinkTargetInProcess(
			final ExpandedProcess process, final String targetName) {
		for (final BaseElement element : process.getElements()) {
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
		} else {
			super.tokenForward(token);
		}
	}

}
