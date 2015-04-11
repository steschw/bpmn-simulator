/*
 * Copyright (C) 2015 Stefan Schweitzer
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
package com.googlecode.bpmn_simulator.gui.instances;

import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import com.googlecode.bpmn_simulator.animation.token.Instance;
import com.googlecode.bpmn_simulator.animation.token.InstancesListener;
import com.googlecode.bpmn_simulator.animation.token.RootInstances;

@SuppressWarnings("serial")
public class InstancesTree
		extends JTree
		implements InstancesListener {

	private static final String ROOT_NODE_TITLE = "Instances";

	private RootInstances instances;

	public InstancesTree() {
		super(new DefaultMutableTreeNode(ROOT_NODE_TITLE));
		setRootVisible(false);
	}

	public void setInstances(final RootInstances instances) {
		if (this.instances != null) {
			this.instances.removeInstancesListener(this);
		}
		this.instances = instances;
		clear();
		if (this.instances != null) {
			this.instances.addInstancesListener(this);
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

	private DefaultMutableTreeNode getNodeByUserObject(final Object userObject) {
		final DefaultMutableTreeNode rootNode = getRoot();
		if (userObject == null) {
			return rootNode;
		} else {
			final Enumeration<?> enumeration = rootNode.breadthFirstEnumeration();
			while (enumeration.hasMoreElements()) {
				final DefaultMutableTreeNode node = (DefaultMutableTreeNode)enumeration.nextElement();
				if (userObject.equals(node.getUserObject())) {
					return node;
				}
			}
			return null;
		}
	}

	private void addAndExpandNode(final DefaultMutableTreeNode parentNode,
			final Object userData) {
		assert parentNode != null;
		if (parentNode != null) {
			assert userData != null;
			final MutableTreeNode node = new DefaultMutableTreeNode(userData);
			getDefaultModel().insertNodeInto(node, parentNode, parentNode.getChildCount());
			expandPath(new TreePath(parentNode.getPath()));
		}
	}

	private void removeNode(final Object userData) {
		final DefaultMutableTreeNode node = getNodeByUserObject(userData);
		assert node != null;
		if (node != null) {
			assert node.getChildCount() == 0;
			getDefaultModel().removeNodeFromParent(node);
		}
	}

	@Override
	public void instanceAdded(final Instance instance) {
	}

	@Override
	public void instanceRemoved(final Instance instance) {
	}

}
