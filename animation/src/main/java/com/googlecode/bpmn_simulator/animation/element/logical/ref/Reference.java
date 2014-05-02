package com.googlecode.bpmn_simulator.animation.element.logical.ref;

public interface Reference<E> {

	boolean hasReference();

	E getReferenced();

}
