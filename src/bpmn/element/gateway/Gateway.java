package bpmn.element.gateway;

import java.awt.BasicStroke;

import bpmn.element.ElementRef;
import bpmn.element.ElementWithDefaultSequenceFlow;
import bpmn.element.Graphics;
import bpmn.element.Label;
import bpmn.element.Rectangle;
import bpmn.element.SequenceFlow;
import bpmn.element.TokenFlowElement;
import bpmn.token.Instance;
import bpmn.token.Token;
import bpmn.token.TokenCollection;

public abstract class Gateway extends TokenFlowElement implements ElementWithDefaultSequenceFlow {

	private static final long serialVersionUID = 1L;

	private ElementRef<SequenceFlow> defaultSequenceFlowRef = null;

	public Gateway(final String id, final String name) {
		super(id, name);
	}

	public void setDefaultSequenceFlowRef(final ElementRef<SequenceFlow> sequenceFlowRef) {
		defaultSequenceFlowRef = sequenceFlowRef;
	}

	public ElementRef<SequenceFlow> getDefaultElementFlowRef() {
		return defaultSequenceFlowRef;
	}

	@Override
	protected int getStepCount() {
		return 10;
	}

	@Override
	protected void paintBackground(Graphics g) {
		super.paintBackground(g);

		g.fillDiamond(getElementInnerBounds());
	}

	@Override
	protected void paintElement(Graphics g) {
		g.drawDiamond(getElementInnerBounds());

		g.setStroke(new BasicStroke(3));
	}

	protected Rectangle getSymbolBounds() {
		final Rectangle bounds = getElementInnerBounds();
		final int margin = 14;
		bounds.grow(-margin, -margin);
		return bounds;
	}

	protected final void forwardMergedTokensToAllOutgoing(final TokenCollection tokens) {
		final Token mergedToken = tokens.merge();
		if (mergedToken != null) {
			forwardTokenToAllOutgoing(mergedToken);
		}
		mergedToken.remove();
	}

	protected Token getFirstTokenForIncoming(SequenceFlow sequenceFlow, Instance instance) {
		for (Token token : getTokens().byInstance(instance)) {
			if (sequenceFlow.equals(token.getPreviousFlow())) {
				return token;
			}
		}
		return null;
	}

	@Override
	protected void initLabel(Label label) {
		label.setAlignCenter(false);
		label.setLeftTopPosition(getElementRightBottom());
	}

}
