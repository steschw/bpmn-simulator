package com.googlecode.bpmn_simulator.animation.token;

public class RootInstances
		extends AbstractInstancesContainer {

	@Override
	protected Instance createNewChildInstance() {
		return new Instance();
	}

}
