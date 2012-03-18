package bpmn.element.task;

import javax.swing.ImageIcon;

public class BusinessRuleTask extends Task {

	private static final long serialVersionUID = 1L;

	private static final ImageIcon TYPE_ICON = Task.loadTypeIcon("businessRule.png"); 

	public BusinessRuleTask(String id, String name) {
		super(id, name, TYPE_ICON);
	}

}
