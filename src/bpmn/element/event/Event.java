package bpmn.element.event;

import bpmn.element.Graphics;
import bpmn.element.Label;
import bpmn.element.TokenFlowElement;
import bpmn.token.InstanceController;

public abstract class Event extends TokenFlowElement {

	private static final long serialVersionUID = 1L;

	private InstanceController instanceController = null;

	public Event(final String id, final String name,
			InstanceController tockenController) {
		super(id, name);

		setTokenController(tockenController);
	}

	protected void setTokenController(final InstanceController controller) {
		instanceController = controller;
	}

	protected InstanceController getInstanceController() {
		return instanceController;
	}

	@Override
	protected int getStepCount() {
		return 5;
	}

	@Override
	protected void paintBackground(Graphics g) {
		super.paintBackground(g);

		g.fillOval(getElementInnerBounds());
	}

	@Override
	protected void paintElement(Graphics g) {
		g.drawOval(getElementInnerBounds());
	}

	@Override
	protected void initLabel(Label label) {
		label.setCenterTopPosition(getElementBottomCenter());
	}

}
