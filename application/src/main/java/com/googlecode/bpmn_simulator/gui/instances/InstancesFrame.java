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

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.googlecode.bpmn_simulator.animation.token.RootInstances;
import com.googlecode.bpmn_simulator.gui.Messages;
import com.googlecode.bpmn_simulator.gui.instances.InstancesTree.InstanceNode;
import com.googlecode.bpmn_simulator.gui.instances.InstancesTree.TokenNode;

@SuppressWarnings("serial")
public class InstancesFrame
		extends JFrame
		implements TreeSelectionListener {

	private static final int DEFAULT_WIDTH = 400;
	private static final int DEFAULT_HEIGHT = 400;

	private static final String EMPTY_CARD = "empty";
	private static final String INSTANCE_CARD = "instanceinfo";
	private static final String TOKEN_CARD = "tokeninfo";

	private final InstancesTree treeInstances = new InstancesTree();

	private final  InstancePanel instancePanel = new InstancePanel();
	private final  TokenPanel tokenPanel = new TokenPanel();

	private final JPanel infoPanel = new JPanel(new CardLayout());

	public InstancesFrame(final RootInstances instances) {
		super(Messages.getString("Instances.instances")); //$NON-NLS-1$

		setDefaultCloseOperation(HIDE_ON_CLOSE);

		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

		setAlwaysOnTop(true);

		create();

		treeInstances.setInstances(instances);
		treeInstances.addTreeSelectionListener(this);
	}

	private JComponent createInfoComponent() {
		infoPanel.add(new JPanel(), EMPTY_CARD);
		instancePanel.create();
		infoPanel.add(instancePanel, INSTANCE_CARD);
		tokenPanel.create();
		infoPanel.add(tokenPanel, TOKEN_CARD);
		setInfoPanel(null);
		return infoPanel;
	}

	protected void create() {
		getContentPane().setLayout(new BorderLayout());
		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				new JScrollPane(treeInstances), createInfoComponent());
		splitPane.setResizeWeight(0.5);
		getContentPane().add(splitPane, BorderLayout.CENTER);
	}

	private void setInfoPanel(final String cardName) {
		((CardLayout) infoPanel.getLayout()).show(infoPanel, cardName);
	}

	@Override
	public void valueChanged(final TreeSelectionEvent event) {
		final Object node = event.getPath().getLastPathComponent();
		if (node instanceof InstanceNode) {
			final InstanceNode instanceNode = (InstanceNode) node;
			instancePanel.setInstance(instanceNode.getInstance());
			setInfoPanel(INSTANCE_CARD);
		} else if (node instanceof TokenNode) {
			final TokenNode tokenNode = (TokenNode) node;
			tokenPanel.setToken(tokenNode.getToken());
			setInfoPanel(TOKEN_CARD);
		} else {
			setInfoPanel(EMPTY_CARD);
		}
	}

}
