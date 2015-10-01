package com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task;


import java.awt.Image;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.ReceiveTask;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;

@SuppressWarnings("serial")
public class ReceiveTaskShape
		extends AbstractTaskShape<ReceiveTask> {

	public ReceiveTaskShape(final ReceiveTask element) {
		super(element);
	}

	@Override
	protected Image getTaskIcon() {
		return Appearance.getDefault().getImage(Appearance.IMAGE_RECEIVE);
	}

}
