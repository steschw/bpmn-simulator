package bpmn.element.event;

import javax.swing.Icon;

import bpmn.element.VisualConfig;

public interface EventDefinition {

	Icon getIcon(final VisualConfig visualConfig, final boolean inverse);

}
