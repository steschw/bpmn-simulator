package bpmn.element.event.definition;

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
		return visualConfig.getIcon(inverse
				? VisualConfig.ICON_LINK_INVERSE
				: VisualConfig.ICON_LINK);
	}

}
