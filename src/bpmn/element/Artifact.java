package bpmn.element;

import java.awt.Color;

public abstract class Artifact extends FlowElement {

	private static final long serialVersionUID = 1L;

	public Artifact(final String id) {
		super(id, null);
		setForeground(Color.DARK_GRAY);
		enableClickThrough();
	}

	@Override
	protected int getBorderWidth() {
		return 2;
	}

}
