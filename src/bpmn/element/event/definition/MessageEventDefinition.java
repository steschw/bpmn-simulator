package bpmn.element.event.definition;

import javax.swing.Icon;

import bpmn.element.VisualConfig;

public class MessageEventDefinition implements EventDefinition {

	@Override
	public Icon getIcon(final VisualConfig visualConfig, final boolean inverse) {
		return visualConfig.getIcon(inverse
				? VisualConfig.ICON_MESSAGE_INVERSE
				: VisualConfig.ICON_MESSAGE);
	}

}
