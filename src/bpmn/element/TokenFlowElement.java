package bpmn.element;

import java.awt.Color;
import java.awt.Point;

import bpmn.token.Instance;
import bpmn.token.Token;
import bpmn.token.TokenCollection;
import bpmn.token.TokenFlow;

public abstract class TokenFlowElement extends FlowElement implements TokenFlow {

	private static final long serialVersionUID = 1L;

	private TokenCollection tokens = new TokenCollection();

	public TokenFlowElement(final String id, final String name) {
		super(id, name);
	}

	protected TokenCollection getTokens() {
		return tokens;
	}

	@Override
	public void tokenEnter(Token token) {
		addToken(token);
		tokenDispatch(token);
	}

	@Override
	public void tokenDispatch(Token token) {
		assert(getTokens().contains(token));
		if (canForwardToken(token)) {
			tokenForward(token);
		}
		repaint();
	}

	@Override
	public void tokenExit(Token token) {
		removeToken(token);
	}

	protected int getStepCount() {
		return 0;
	}

	protected boolean canForwardToken(Token token) {
		return (token.getSteps() >= getStepCount());
	}

	protected void addToken(Token token) {
		getTokens().add(token);
		repaint();
	}

	protected void removeToken(Token token) {
		getTokens().remove(token);
		repaint();
	}

	protected void tokenForward(Token token) {
		if (forwardTokenToAllOutgoing(token)) {
			setException(false);
			token.remove();
		} else {
			setException(true);
		}
	}

	@Override
	public boolean hasIncomingPathWithActiveToken(Instance instance) {
		if (getTokens().byInstance(instance).getCount() > 0) {
			// Entweder das Element selbst hat noch Token dieser Instanz
			return true;
		} else {
			// oder eines der eingehenden
			for (ElementRef<SequenceFlow> incoming : getIncoming()) {
				if ((incoming != null) && incoming.hasElement()) {
					if (incoming.getElement().hasIncomingPathWithActiveToken(instance)) {
						return true;
					}
				}
			}
			return false;
		}
	}

	protected final boolean forwardTokenToFirstOutgoing(final Token token) {
		return forwardTokenToFirstOutgoing(token, token.getInstance());
	}

	protected final boolean forwardTokenToFirstOutgoing(final Token token, final Instance instance) {
		if (hasOutgoing()) {
			if (forwardTokenToFirstSequenceFlow(token, instance)) {
				return true;
			} else {
				return forwardTokenToDefaultSequenceFlow(token, instance);
			}
		} else {
			return forwardTokenToParent(token, instance);
		}
	}

	protected boolean forwardTokenToAllOutgoing(final Token token) {
		return forwardTokenToAllOutgoing(token, token.getInstance());
	}

	protected boolean forwardTokenToAllOutgoing(final Token token, final Instance instance) {
		if (hasOutgoing()) {
			if (forwardTokenToAllSequenceFlows(token, instance) == 0) {
				return forwardTokenToDefaultSequenceFlow(token, instance);
			} else {
				return true;
			}
		} else {
			return forwardTokenToParent(token, instance);
		}
	}

	private final boolean forwardTokenToParent(final Token token, final Instance instance) {
		final ExpandedProcess parentProcess = getParentProcess();
		if (parentProcess != null) {
			token.passTo(parentProcess, instance);
			return true;
		}
		return false;
	}

	private final boolean forwardTokenToFirstSequenceFlow(final Token token, final Instance instance) {
		for (ElementRef<SequenceFlow> outgoingRef : getOutgoing()) {
			if ((outgoingRef != null) && outgoingRef.hasElement()) {
				SequenceFlow sequenceFlow = outgoingRef.getElement();
				if (sequenceFlow != null) {
					if (sequenceFlow.acceptsToken() && !sequenceFlow.isDefault()) {
						token.passTo(sequenceFlow, instance);
						return true;
					}
				}
			}
		}
		return false;
	}

	private final int forwardTokenToAllSequenceFlows(final Token token, final Instance instance) {
		int forewardCount = 0;
		for (ElementRef<SequenceFlow> outgoingRef : getOutgoing()) {
			if ((outgoingRef != null) && outgoingRef.hasElement()) {
				final SequenceFlow sequenceFlow = outgoingRef.getElement();
				if (sequenceFlow.acceptsToken() && !sequenceFlow.isDefault()) {
					token.passTo(sequenceFlow, instance);
					++forewardCount;
				}
			}
		}
		return forewardCount;
	}

	private final boolean forwardTokenToDefaultSequenceFlow(final Token token, final Instance instance) {
		if (this instanceof ElementWithDefaultSequenceFlow) {
			final ElementRef<SequenceFlow> defaultSequenceFlowRef = ((ElementWithDefaultSequenceFlow)this).getDefaultElementFlowRef();
			if ((defaultSequenceFlowRef != null) && defaultSequenceFlowRef.hasElement()) {
				token.passTo(defaultSequenceFlowRef.getElement(), instance);
				return true;
			}
		}
		return false;
	}

	@Override
	public Color getForeground() {
		final TokenCollection tokens = getTokens();
		if ((tokens != null) && (tokens.getCount() > 0)) {
			return Token.HIGHLIGHT_COLOR;
		}
		return super.getForeground();
	}

	@Override
	protected void paintTokens(Graphics g) {
		Rectangle bounds = getElementInnerBounds();
		Point point = new Point((int)bounds.getMaxX(), (int)bounds.getMinY());
		for (Instance instance : getTokens().getInstances()) {
			instance.paint(g, point, getTokens().byInstance(instance).byCurrentFlow(this).getCount());
			point.translate(-5, 0);
		}
	}

}
