package com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task;


import java.awt.Image;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.SendTask;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;

@SuppressWarnings("serial")
public class SendTaskShape
		extends AbstractTaskShape<SendTask> {

	public SendTaskShape(final SendTask element) {
		super(element);
	}

	@Override
	protected Image getTaskIcon() {
		return Appearance.getDefault().getImage(Appearance.IMAGE_SEND);
	}

}
