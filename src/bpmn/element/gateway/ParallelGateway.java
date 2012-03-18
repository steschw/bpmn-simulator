package bpmn.element.gateway;

import java.awt.Point;

import bpmn.element.ElementRef;
import bpmn.element.Graphics;
import bpmn.element.Rectangle;
import bpmn.element.SequenceFlow;
import bpmn.token.Instance;
import bpmn.token.Token;
import bpmn.token.TokenCollection;

public class ParallelGateway extends Gateway {

	private static final long serialVersionUID = 1L;

	public ParallelGateway(final String id, final String name) {
		super(id, name);
	}

	@Override
	protected void paintElement(Graphics g) {
		super.paintElement(g);

		g.drawCross(getSymbolBounds(), false);
	}

	protected synchronized void forwardTokenParallel(Instance instance) {
		TokenCollection popTokens = new TokenCollection();
		for (ElementRef<SequenceFlow> incoming : getIncoming()) {
			if (incoming.hasElement()) {
				final Token incomingToken = getFirstTokenForIncoming(incoming.getElement(), instance);
				if (incomingToken == null) {
					// es sind nicht für jeden eingang ein token vorhanden
					return;
				}
				popTokens.add(incomingToken);
			}
		}
		forwardMergedTokensToAllOutgoing(popTokens);
	}

	public final boolean isForMerging() {
		return (getIncoming().size() > 1);
	}

	@Override
	protected void tokenForward(Token token) {
		if (isForMerging()) {
			forwardTokenParallel(token.getInstance());
		} else {
			super.tokenForward(token);
		}
	}

	@Override
	protected void paintTokens(Graphics g) {
		if (isForMerging()) {
			final Rectangle bounds = getElementInnerBounds();
			int y = bounds.y;
			for (Instance tokenInstance : getTokens().getInstances()) {
				int x = bounds.x + (int)bounds.getWidth();
				for (ElementRef<SequenceFlow> incoming : getIncoming()) {
					if (incoming.hasElement()) {
						final int count = getTokens().byInstance(tokenInstance).byPreviousFlow(incoming.getElement()).getCount();
						if (count > 0) {
							tokenInstance.paint(g, new Point(x, y), count);
						}
					}
					x -= 5;
				}
				y += 5;
			}
		} else {
			super.paintTokens(g);
		}
	}

}
