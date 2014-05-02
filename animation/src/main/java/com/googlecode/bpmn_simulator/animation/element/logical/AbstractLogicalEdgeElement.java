package com.googlecode.bpmn_simulator.animation.element.logical;

import com.googlecode.bpmn_simulator.animation.element.logical.ref.Reference;

public abstract class AbstractLogicalEdgeElement
		extends AbstractLogicalElement
		implements LogicalEdgeElement {

	private Reference<LogicalNodeElement> incoming;
	private Reference<LogicalNodeElement> outgoing;

	@Override
	public void setIncoming(final Reference<LogicalNodeElement> incoming) {
		this.incoming = incoming;
	}

	@Override
	public LogicalNodeElement getIncoming() {
		if (incoming != null) {
			incoming.getReferenced();
		}
		return null;
	}

	@Override
	public void setOutgoing(final Reference<LogicalNodeElement> outgoing) {
		this.outgoing = outgoing;
	}

	@Override
	public LogicalNodeElement getOutgoing() {
		if (outgoing != null) {
			outgoing.getReferenced();
		}
		return null;
	}

}
