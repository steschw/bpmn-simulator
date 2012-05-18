package bpmn.element.event;

import bpmn.token.Instance;

public interface CatchEvent extends Event {

	void happen(final Instance instance);

}
