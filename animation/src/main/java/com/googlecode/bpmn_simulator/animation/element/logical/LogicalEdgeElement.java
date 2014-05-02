package com.googlecode.bpmn_simulator.animation.element.logical;

import com.googlecode.bpmn_simulator.animation.element.logical.ref.Reference;

public interface LogicalEdgeElement
		extends LogicalElement {

	void setIncoming(Reference<LogicalNodeElement> incoming);

	LogicalNodeElement getIncoming();

	void setOutgoing(Reference<LogicalNodeElement> outgoing);

	LogicalNodeElement getOutgoing();

}
