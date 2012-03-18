package bpmn.element.task;

import javax.swing.ImageIcon;

public class ManuallTask extends Task {

	private static final long serialVersionUID = 1L;

	private static final ImageIcon TYPE_ICON = Task.loadTypeIcon("manuall.png"); 

	public ManuallTask(String id, String name) {
		super(id, name, TYPE_ICON);
	}

}
