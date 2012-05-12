package bpmn.element.event;

import javax.swing.Icon;

import bpmn.element.VisualConfig;

public class MessageEventDefinition implements EventDefinition {

	@Override
	public Icon getIcon(final VisualConfig visualConfig, final boolean inverse) {
		return inverse
				? visualConfig.getIcon(VisualConfig.ICON_MESSAGE_INVERSE)
				: visualConfig.getIcon(VisualConfig.ICON_MESSAGE);
	}

}
