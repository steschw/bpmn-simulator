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
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import bpmn.Model;
import bpmn.instance.Instance;
import bpmn.instance.InstanceMenuItem;
import bpmn.trigger.Instantiable;
import bpmn.trigger.InstantiableNotifiySource;
import bpmn.trigger.Trigger;
import bpmn.trigger.TriggerCatchingElement;

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

	public JMenuItem createInstancelessElementSelection(final TriggerCatchingElement element) {
		final JMenuItem menuItem = new JMenuItem(element.getElementName());
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				element.catchTrigger(new Trigger(null, null));
			}
		});
		return menuItem;
	}

	public JMenu createInstanciatedElementSelection(final TriggerCatchingElement element) {
		final Collection<Instance> instances
				= element.getTriggerDestinationInstances();
		if (!instances.isEmpty()) {
			final JMenu subMenu = new JMenu(element.getElementName());
			for (final Instance instance : instances) {
				final InstanceMenuItem menuItem = new InstanceMenuItem(instance);
				menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						element.catchTrigger(new Trigger(null, instance));
					}
				});
				subMenu.add(menuItem);
			}
			return subMenu;
		}
		return null;
	}

	private boolean requiresInstanciatedCall(final TriggerCatchingElement catchingElement) {
		final boolean instantiable = (catchingElement instanceof Instantiable)
				&& ((Instantiable)catchingElement).isInstantiable();
		final boolean instantiableNotifing = (catchingElement instanceof InstantiableNotifiySource)
				&& ((InstantiableNotifiySource)catchingElement).isInstantiableNotifying();
		return !(instantiable || instantiableNotifing);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (model != null) {
			final Collection<TriggerCatchingElement> startEvents = model.getManuallStartEvents();
			final Iterator<TriggerCatchingElement> iterator = startEvents.iterator();
			if (startEvents.size() == 1) {
				iterator.next().catchTrigger(new Trigger(null, null));
			} else {
				final JPopupMenu menu = new JPopupMenu();
				while (iterator.hasNext()) {
					final TriggerCatchingElement element = iterator.next();
					if (requiresInstanciatedCall(element)) {
						final JMenu submenu = createInstanciatedElementSelection(element);
						if (submenu != null) {
							menu.add(submenu);
						}
					} else {
						menu.add(createInstancelessElementSelection(element));
					}
				}
				menu.show(this, 0, getHeight());
			}
		}
	}

}
