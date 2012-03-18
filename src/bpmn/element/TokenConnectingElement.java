package bpmn.element;

import java.awt.Color;

import bpmn.token.Instance;
import bpmn.token.Token;
import bpmn.token.TokenCollection;
import bpmn.token.TokenFlow;

public abstract class TokenConnectingElement extends ConnectingElement
		implements TokenFlow {

	private static final long serialVersionUID = 1L;

	private TokenCollection tokens = new TokenCollection();

	public TokenConnectingElement(final String id, final String name,
			ElementRef<FlowElement> source, ElementRef<FlowElement> target) {
		super(id, name, source, target);
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
			forwardToken(token);
		}
		repaint();
	}

	@Override
	public void tokenExit(Token token) {
		removeToken(token);
	}

	protected boolean canForwardToken(Token token) {
		return (token.getSteps() >= getLength());
	}

	protected void addToken(Token token) {
		getTokens().add(token);
		repaint();
	}

	protected void removeToken(Token token) {
		getTokens().remove(token);
		repaint();
	}

	protected void forwardToken(Token token) {
		ElementRef<FlowElement> targetRef = getTargetRef();
		if ((targetRef != null) && targetRef.hasElement()) {
			final FlowElement flowElement = targetRef.getElement();
			if (flowElement instanceof TokenFlow) {
				token.passTo((TokenFlow)flowElement);
				token.remove();
			}
		} else {
			assert(false);
		}
	}

	public boolean hasIncomingPathWithActiveToken(Instance instance) {
		if (getTokens().byInstance(instance).getCount() > 0) {
			// Entweder das Element selbst hat noch Token dieser Instanz
			return true;
		} else {
			// oder eines der eingehenden
			final ElementRef<FlowElement> sourceRef = getSourceRef();
			if ((sourceRef != null) && sourceRef.hasElement()) {
				FlowElement flowElement = sourceRef.getElement();
				if (flowElement instanceof TokenFlow) {
					return ((TokenFlow)flowElement).hasIncomingPathWithActiveToken(instance);
				}
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
		final TokenCollection tokens = getTokens();
		synchronized (tokens) {
			for (Token token : tokens) {
				token.getInstance().paint(g, waypointToRelative(getPosition(token.getSteps())));
			}
		}
	}

}
