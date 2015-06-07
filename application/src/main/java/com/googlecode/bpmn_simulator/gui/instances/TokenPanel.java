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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.googlecode.bpmn_simulator.animation.element.logical.LogicalElement;
import com.googlecode.bpmn_simulator.animation.token.Token;
import com.googlecode.bpmn_simulator.animation.token.TokenFlow;

@SuppressWarnings("serial")
class TokenPanel
		extends AbstractInfoPanel {

	private final ElementLabel elementLabel = new ElementLabel();

	@Override
	protected JPanel createInfoPanel() {
		final JPanel panel = new JPanel(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(8, 8, 8, 8);
		c.anchor = GridBagConstraints.LINE_START;
		c.gridy = 0;
		c.gridx = 0;
		panel.add(new JLabel("Element:"), c);
		c.gridx = 1;
		c.weightx = 0.8;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(elementLabel, c);
		return panel;
	}

	public void setToken(final Token token) {
		setData(token.getData());
		final TokenFlow currenTokenFlow = token.getCurrentTokenFlow();
		if (currenTokenFlow instanceof LogicalElement) {
			elementLabel.setElement((LogicalElement) currenTokenFlow);
		} else {
			elementLabel.setElement(null);
		}
	}

}
