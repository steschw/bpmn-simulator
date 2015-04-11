package com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task;

import java.awt.Image;

import com.googlecode.bpmn_simulator.animation.element.visual.swing.Colors;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.SendTask;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;

@SuppressWarnings("serial")
public class SendTaskShape
		extends AbstractTaskShape<SendTask> {

	static {
		Appearance.setDefaultColor(SendTaskShape.class, Colors.YELLOW);
	}

	public SendTaskShape(final SendTask element) {
		super(element);
	}

	@Override
	protected Image getTaskImage() {
		return Appearance.getDefault().getImage(Appearance.IMAGE_SEND);
	}

}
