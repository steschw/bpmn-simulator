package bpmn.element.task;

import javax.swing.ImageIcon;

public class ScriptTask extends Task {

	private static final long serialVersionUID = 1L;

	private static final ImageIcon TYPE_ICON = Task.loadTypeIcon("script.png"); 

	public ScriptTask(String id, String name) {
		super(id, name, TYPE_ICON);
	}

}
