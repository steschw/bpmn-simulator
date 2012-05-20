/*
 * Copyright (C) 2012 Stefan Schweitzer
 *
 * This software was created by Stefan Schweitzer as a student's project at
 * Fachhochschule Kaiserslautern (University of Applied Sciences).
 * Supervisor: Professor Dr. Thomas Allweyer. For more information please see
 * http://www.fh-kl.de/~allweyer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this Software except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gui;

import java.awt.Component;
import java.util.Enumeration;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import bpmn.token.Instance;
import bpmn.token.InstanceManager;
import bpmn.token.InstanceListener;

@SuppressWarnings("serial")
public class InstancesTree extends JTree implements InstanceListener {

	private static class InstancesTreeCellRenderer extends DefaultTreeCellRenderer {

		@Override
		public Component getTreeCellRendererComponent(final JTree tree,
				final Object value, final boolean sel, final boolean expanded,
				final boolean leaf, final int row, final boolean hasFocus) {
			final Component component = super.getTreeCellRendererComponent(tree,
					value, sel, expanded, leaf, row, hasFocus);
			if (value instanceof DefaultMutableTreeNode) {
				final JLabel label = (JLabel)component;
				final DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
				final Object userObject = node.getUserObject();
				if (userObject instanceof Instance) {
					final Instance instance = (Instance)userObject;
					label.setOpaque(true);
					label.setBackground(instance.getColor());
				}
			}
			return component;
		}
		
	}

	private InstanceManager instanceManager;

	public InstancesTree() {
		super(new DefaultMutableTreeNode("Instances"));
		setRootVisible(false);
		setCellRenderer(new InstancesTreeCellRenderer());
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
		return (DefaultMutableTreeNode)getModel().getRoot();
	}

	public void clear() {
		getRoot().removeAllChildren();
		getDefaultModel().reload();
	}

	private DefaultMutableTreeNode getInstanceNode(final Instance instance) {
		final DefaultMutableTreeNode rootNode = getRoot();
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
