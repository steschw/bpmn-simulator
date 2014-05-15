package com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task;

import java.awt.Color;
import java.awt.Image;

import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.SendTask;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;

@SuppressWarnings("serial")
public class SendTaskShape
		extends AbstractTaskShape<SendTask> {

	static {
		Appearance.getDefault().getForElement(SendTaskShape.class).setBackground(new Color(0xFFFFB5));
	}

	public SendTaskShape(final SendTask element) {
		super(element);
	}

	@Override
	protected Image getTaskImage() {
		return Appearance.getDefault().getImage(Appearance.IMAGE_SEND);
	}

}
