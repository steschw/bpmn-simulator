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
package bpmn;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;

import bpmn.element.BaseElement;
import bpmn.element.Collaboration;
import bpmn.element.activity.ExpandedProcess;

public class DiagramFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	private static final Icon ICON_PROCESS = loadFrameIcon("process.png");
	private static final Icon ICON_COLLABORATION = loadFrameIcon("collaboration.png");

	public DiagramFrame(final BaseElement container) {
		super(container.getName(), true, false, true);
		setContentPane(new JScrollPane(container));
		setFrameIcon(container);
	}

	private static Icon loadFrameIcon(final String filename) {
		final URL url = DiagramFrame.class.getResource(filename);
		if (url != null) {
			return new ImageIcon(url);
		}
		return null;
	}

	protected void setFrameIcon(final BaseElement container) {
		Icon icon = null;
		if (container instanceof ExpandedProcess) {
			icon = ICON_PROCESS;
		} else if (container instanceof Collaboration) {
			icon = ICON_COLLABORATION;
		}
		setFrameIcon(icon);
	}

	public void showFrame() {
		setLocation(0, 0);
		setVisible(true);
		pack();
	}

}
