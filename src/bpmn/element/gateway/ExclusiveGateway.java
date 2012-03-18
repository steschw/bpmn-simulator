package bpmn.element.gateway;

import bpmn.element.Graphics;
import bpmn.token.Token;

public class ExclusiveGateway extends Gateway {

	private static final long serialVersionUID = 1L;

	private static boolean showSymbol = true;

	public static void setShowSymbol(final boolean show) {
		ExclusiveGateway.showSymbol = show;
	}

	public ExclusiveGateway(final String id, final String name) {
		super(id, name);
	}

	@Override
	protected void paintElement(Graphics g) {
		super.paintElement(g);

		if (showSymbol) {
			g.drawCross(getSymbolBounds(), true);
		}
	}

	@Override
	protected void tokenForward(Token token) {
		if (forwardTokenToFirstOutgoing(token)) {
			token.remove();
			setException(false);
		} else {
			setException(true);
		}
	}

}
