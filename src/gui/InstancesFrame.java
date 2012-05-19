package gui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import bpmn.token.InstanceManager;

@SuppressWarnings("serial")
public class InstancesFrame extends JFrame {

	private final InstancesTree treeInstances = new InstancesTree();

	public InstancesFrame() {
		super(Messages.getString("Instances.instances")); //$NON-NLS-1$

		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setSize(200, 400);

		create();

		setVisible(true);
	}

	public void setInstanceManager(final InstanceManager instanceManager) {
		treeInstances.setInstanceManager(instanceManager);
	}

	protected void create() {
		getContentPane().add(new JScrollPane(treeInstances));
	}

}
