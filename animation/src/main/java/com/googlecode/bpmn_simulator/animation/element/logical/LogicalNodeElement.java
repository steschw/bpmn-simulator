package com.googlecode.bpmn_simulator.animation.element.logical;

import com.googlecode.bpmn_simulator.animation.element.logical.ref.References;

public interface LogicalNodeElement
		extends LogicalElement {

	void setIncoming(References<LogicalEdgeElement> incoming);

	References<LogicalEdgeElement> getIncoming();

	void setOutgoing(References<LogicalEdgeElement> outgoing);

	References<LogicalEdgeElement> getOutgoing();

}
