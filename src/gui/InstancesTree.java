package gui;

import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import bpmn.token.Instance;
import bpmn.token.InstanceManager;
import bpmn.token.InstanceListener;

@SuppressWarnings("serial")
public class InstancesTree extends JTree implements InstanceListener {

	private InstanceManager instanceManager;

	public InstancesTree() {
		super(new DefaultMutableTreeNode("Instances"));
		setRootVisible(false);
	}

	public void setInstanceManager(final InstanceManager instanceManager) {
		if (this.instanceManager != null) {
			this.instanceManager.removeInstanceListener(this);
		}
		this.instanceManager = instanceManager;
		clear();
		if (this.instanceManager != null) {
			this.instanceManager.addInstanceListener(this);
		}
	}

	private DefaultTreeModel getDefaultModel() {
		return (DefaultTreeModel)getModel();
	}

	private DefaultMutableTreeNode getRoot() {
		return ((DefaultMutableTreeNode)getModel().getRoot());
	}

	public void clear() {
		getRoot().removeAllChildren();
		getDefaultModel().reload();
	}

	private DefaultMutableTreeNode getInstanceNode(final Instance instance) {
		DefaultMutableTreeNode rootNode = getRoot();
		if (instance == null) {
			return rootNode;
		} else {
			final Enumeration<?> enumeration = rootNode.breadthFirstEnumeration();
			while (enumeration.hasMoreElements()) {
				final DefaultMutableTreeNode node = (DefaultMutableTreeNode)enumeration.nextElement();
				if (instance.equals(node.getUserObject())) {
					return node;
				}
			}
			return null;
		}
	}

	@Override
	public void instanceAdded(final Instance instance) {
		final DefaultMutableTreeNode instanceNode = new DefaultMutableTreeNode(instance);
		final DefaultMutableTreeNode parentNode = getInstanceNode(instance.getParentInstance());
		getDefaultModel().insertNodeInto(instanceNode, parentNode, parentNode.getChildCount());
		expandPath(new TreePath(parentNode.getPath()));
	}

	@Override
	public void instanceRemoved(final Instance instance) {
		final DefaultMutableTreeNode node = getInstanceNode(instance);
		if (node != null) {
			getDefaultModel().removeNodeFromParent(node);
		}
	}

}
