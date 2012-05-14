package bpmn.element;

import bpmn.token.Instance;
import bpmn.token.Token;

@SuppressWarnings("serial")
public abstract class TokenFlowElementWithDefault
		extends TokenFlowElement
		implements ElementWithDefaultSequenceFlow {

	private ElementRef<SequenceFlow> defaultSequenceFlowRef;

	public TokenFlowElementWithDefault(final String id, final String name) {
		super(id, name);
	}

	@Override
	public void setDefaultSequenceFlowRef(
			final ElementRef<SequenceFlow> sequenceFlowRef) {
		defaultSequenceFlowRef = sequenceFlowRef;
	}

	@Override
	public ElementRef<SequenceFlow> getDefaultSequenceFlowRef() {
		return defaultSequenceFlowRef;
	}

	@Override
	public SequenceFlow getDefaultSequenceFlow() {
		final ElementRef<SequenceFlow> sequenceFlowRef = getDefaultSequenceFlowRef();
		return (sequenceFlowRef == null) ? null : sequenceFlowRef.getElement();
	}

	protected final boolean passTokenToFirstOutgoing(final Token token) {
		return passTokenToFirstOutgoing(token, token.getInstance());
	}

	protected final boolean passTokenToFirstOutgoing(final Token token, final Instance instance) {
		if (hasOutgoing()) {
			if (passTokenToFirstSequenceFlow(token, instance)) {
				return true;
			} else {
				return passTokenToDefaultSequenceFlow(token, instance);
			}
		} else {
			return passTokenToParent(token, instance);
		}
	}

	@Override
	protected boolean passTokenToAllOutgoing(final Token token, final Instance instance) {
		if (hasOutgoing()) {
			if (passTokenToAllSequenceFlows(token, instance) == 0) {
				return passTokenToDefaultSequenceFlow(token, instance);
			} else {
				return true;
			}
		} else {
			return passTokenToParent(token, instance);
		}
	}

	protected boolean passTokenToDefaultSequenceFlow(final Token token, final Instance instance) {
		final SequenceFlow defaultSequenceFlow = getDefaultSequenceFlow();
		if (defaultSequenceFlow != null) {
			token.passTo(defaultSequenceFlow, instance);
			return true;
		} else {
			return false;
		}
	}

}
