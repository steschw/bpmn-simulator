package bpmn.element.event;

import bpmn.element.Graphics;
import bpmn.element.Rectangle;
import bpmn.token.InstanceController;
import bpmn.token.Token;

public class EndEvent extends Event {

	private static final long serialVersionUID = 1L;

	private boolean termination = false;

	public EndEvent(final String id, final String name,
			final InstanceController tockenController) {
		super(id, name, tockenController);
	}

	public void setTermination(final boolean termination) {
		this.termination = termination;
	}

	public boolean isTermination() {
		return termination;
	}

	@Override
	protected int getBorderWidth() {
		return 3;
	}

	@Override
	protected void paintElement(Graphics g) {
		super.paintElement(g);

		if (isTermination()) {
			final Rectangle bounds = getElementInnerBounds();
			bounds.grow(-4, -4);
			g.fillOval(bounds);
		}
	}

	@Override
	protected void tokenForward(Token token) {
		if (isTermination()) {
			token.getInstance().removeAllOtherTokens(token);
		}
		super.tokenForward(token);
	}

}
