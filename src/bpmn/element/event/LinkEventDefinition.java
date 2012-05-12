package bpmn.element.event;

import javax.swing.Icon;

import bpmn.element.VisualConfig;

public class LinkEventDefinition implements EventDefinition {

	private final String name;

	public LinkEventDefinition(final String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public Icon getIcon(final VisualConfig visualConfig, final boolean inverse) {
		return inverse
				? visualConfig.getIcon(VisualConfig.ICON_LINK_INVERSE)
				: visualConfig.getIcon(VisualConfig.ICON_LINK);
	}

}
