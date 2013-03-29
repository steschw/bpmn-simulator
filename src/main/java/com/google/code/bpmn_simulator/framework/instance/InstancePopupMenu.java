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
package com.google.code.bpmn_simulator.framework.instance;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JPopupMenu;

import com.google.code.bpmn_simulator.bpmn.trigger.Trigger;
import com.google.code.bpmn_simulator.bpmn.trigger.TriggerCatchingElement;


@SuppressWarnings("serial")
public class InstancePopupMenu
		extends JPopupMenu {

	public static void selectToTrigger(final Component component,
			final TriggerCatchingElement catchElement,
			final Collection<Instance> instances) {
		if (!instances.isEmpty()) {
			final Iterator<Instance> i = instances.iterator();
			if (instances.size() == 1) {
				catchElement.catchTrigger(new Trigger(null, i.next()));
			} else {
				final JPopupMenu menu = new JPopupMenu();
				for (final Instance instance : instances) {
					final InstanceMenuItem menuItem = new InstanceMenuItem(instance);
					menuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(final ActionEvent e) {
							catchElement.catchTrigger(new Trigger(null, instance));
						}
					});
					menu.add(menuItem);
				}
				menu.show(component, 0, 0);
			}
		}
	}

}
