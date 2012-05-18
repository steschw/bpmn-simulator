package bpmn.element.event;

import bpmn.element.Element;
import bpmn.element.event.definition.EventDefinition;

public interface Event extends Element {

	EventDefinition getDefinition();

}
