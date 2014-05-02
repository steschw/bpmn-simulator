package com.googlecode.bpmn_simulator.animation.execution;

import com.googlecode.bpmn_simulator.animation.token.RootInstances;

public class Animator
		extends AbstractAnimator {

	private final RootInstances instances;

	public Animator(final RootInstances instances) {
		super();
		this.instances = instances;
		start();
	}

	@Override
	public void reset() {
		// remove token
		super.reset();
	}

	@Override
	public void step(final int count) {
		
	}

}
