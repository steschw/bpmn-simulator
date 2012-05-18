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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import bpmn.Model;
import bpmn.element.event.StartEvent;

@SuppressWarnings("serial")
public class StartButton extends JButton implements ActionListener {

	private Model model;

	public StartButton(final Icon icon) {
		super(icon);
		addActionListener(this);
	}

	public void setModel(final Model model) {
		this.model = model;
		setEnabled(model != null);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (model != null) {
			final Collection<StartEvent> startEvents = model.getManuallStartEvents();
			final Iterator<StartEvent> iterator = startEvents.iterator();
			if (startEvents.size() == 1) {
				iterator.next().happen(null);
			} else {
				final JPopupMenu menu = new JPopupMenu();
				while (iterator.hasNext()) {
					final StartEvent startEvent = iterator.next();
					final JMenuItem menuItem = new JMenuItem(startEvent.getElementName());
					menuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(final ActionEvent e) {
							startEvent.happen(null);
						}
					});
					menu.add(menuItem);
				}
				menu.show(this, 0, getHeight());
			}
		}
	}

}
