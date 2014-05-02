package com.googlecode.bpmn_simulator.animation.element.logical;

import com.googlecode.bpmn_simulator.animation.element.logical.ref.ReferenceUtils;
import com.googlecode.bpmn_simulator.animation.element.logical.ref.References;

public abstract class AbstractLogicalNodeElement
		extends AbstractLogicalElement
		implements LogicalNodeElement {

	private static final References<LogicalEdgeElement> EMPTY_REFERENCES
			= ReferenceUtils.<LogicalEdgeElement>emptyReferences();

	private References<LogicalEdgeElement> incoming;
	private References<LogicalEdgeElement> outgoing;

	@Override
	public void setIncoming(final References<LogicalEdgeElement> incoming) {
		this.incoming = incoming;
	}

	@Override
	public References<LogicalEdgeElement> getIncoming() {
		if (incoming != null) {
			return incoming;
		}
		return EMPTY_REFERENCES;
	}

	@Override
	public void setOutgoing(final References<LogicalEdgeElement> outgoing) {
		this.outgoing = outgoing;
	}

	@Override
	public References<LogicalEdgeElement> getOutgoing() {
		if (outgoing != null) {
			return outgoing;
		}
		return EMPTY_REFERENCES;
	}

}
