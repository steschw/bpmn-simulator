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
package bpmn.element.event;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import bpmn.element.ExpandedProcess;
import bpmn.element.Graphics;
import bpmn.token.InstanceController;

public class StartEvent extends Event implements MouseListener {

	private static final long serialVersionUID = 1L;

	public StartEvent(final String id, final String name,
			final InstanceController tockenController) {
		super(id, name, tockenController);
		addMouseListener(this);
	}

	@Override
	protected void paintBackground(Graphics g) {
		super.paintBackground(g);
	}

	@Override
	protected void setTokenController(InstanceController controller) {
		super.setTokenController(controller);
		updateCursor();
	}

	@Override
	public void setParentProcess(ExpandedProcess parentProcess) {
		super.setParentProcess(parentProcess);
		updateCursor();
	}

	public boolean canStartManuell() {
		final ExpandedProcess process = getParentProcess();
		return (getInstanceController() != null) && (process != null) && !process.hasIncoming(); 
	}

	protected void updateCursor() {
		setCursor(canStartManuell() ?
				new Cursor(Cursor.HAND_CURSOR)
				: Cursor.getDefaultCursor());
	}

	public void start() {
		final InstanceController instanceController = getInstanceController();
		if (instanceController != null) {
			instanceController.newInstance().newToken(this);
		} else {
			assert(false);
		}
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		if (canStartManuell()) {
			start();
		}
	}

	@Override
	public void mouseEntered(MouseEvent event) {
	}

	@Override
	public void mouseExited(MouseEvent event) {
	}

	@Override
	public void mousePressed(MouseEvent event) {
	}

	@Override
	public void mouseReleased(MouseEvent event) {
	}

}
