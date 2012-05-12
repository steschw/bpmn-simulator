package bpmn.element.event;

import javax.swing.Icon;

import bpmn.element.VisualConfig;

public class TimerEventDefinition implements EventDefinition {

	@Override
	public Icon getIcon(final VisualConfig visualConfig, final boolean inverse) {
		assert !inverse;
		return visualConfig.getIcon(VisualConfig.ICON_TIMER);
	}

}
