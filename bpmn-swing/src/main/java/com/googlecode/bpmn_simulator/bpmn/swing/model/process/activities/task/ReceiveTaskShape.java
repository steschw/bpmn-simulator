package com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task;

import java.awt.Color;
import java.awt.Image;

import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.ReceiveTask;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;

@SuppressWarnings("serial")
public class ReceiveTaskShape
		extends AbstractTaskShape<ReceiveTask> {

	static {
		Appearance.getDefault().getForElement(ReceiveTaskShape.class).setBackground(new Color(0xFFFFB5));
	}

	public ReceiveTaskShape(final ReceiveTask element) {
		super(element);
	}

	@Override
	protected Image getTaskImage() {
		return Appearance.getDefault().getImage(Appearance.IMAGE_RECEIVE);
	}

}
