package bpmn.element.gateway;

import bpmn.element.ElementRef;
import bpmn.element.Graphics;
import bpmn.element.SequenceFlow;
import bpmn.token.Instance;
import bpmn.token.Token;
import bpmn.token.TokenCollection;

public class InclusiveGateway extends Gateway {

	private static final long serialVersionUID = 1L;

	public InclusiveGateway(final String id, final String name) {
		super(id, name);
	}

	@Override
	protected void paintElement(Graphics g) {
		super.paintElement(g);

		g.drawOval(getSymbolBounds());
	}

	@Override
	public boolean hasIncomingPathWithActiveToken(Instance instance) {
		return false;
	}

	protected synchronized void forwardTokenParallel(Instance instance) {
		TokenCollection popTokens = new TokenCollection();
		for (ElementRef<SequenceFlow> incoming : getIncoming()) {
			if (incoming.hasElement()) {
				final SequenceFlow incomingSequenceFlow = (SequenceFlow)incoming.getElement();
				final Token incomingToken = getFirstTokenForIncoming(incoming.getElement(), instance);
				if (incomingToken == null) {
					// für diesen eingang ist noch kein token vorhanden
					if (incomingSequenceFlow.hasIncomingPathWithActiveToken(instance)) {
						// aber es kann noch eines ankommen
						return;
					}
				} else {
					popTokens.add(incomingToken);
				}
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

}
