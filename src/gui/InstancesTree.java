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
		removeAll();
		if (this.instanceManager != null) {
			this.instanceManager.addInstanceListener(this);
		}
	}

	private DefaultMutableTreeNode getInstanceNode(final Instance instance) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)getModel().getRoot();
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
		((DefaultTreeModel)getModel()).insertNodeInto(instanceNode, parentNode, parentNode.getChildCount());
		parentNode.add(instanceNode);
		expandPath(new TreePath(parentNode));
	}

	@Override
	public void instanceRemoved(final Instance instance) {
		final DefaultMutableTreeNode node = getInstanceNode(instance);
		if (node != null) {
			((DefaultTreeModel)getModel()).removeNodeFromParent(node);
		}
	}

}
