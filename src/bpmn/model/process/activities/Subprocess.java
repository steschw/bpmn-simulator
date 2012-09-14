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
package bpmn.model.process.activities;

import java.awt.Stroke;

import bpmn.model.Model;

@SuppressWarnings("serial")
public class Subprocess
		extends AbstractContainerActivity {

	private final boolean triggeredByEvent;

	private CollapsedSubprocess collapsedProcess;

	public Subprocess(final Model model, final String id, final String name,
			final boolean triggeredByEvent) {
		super(model, id, name);
		this.triggeredByEvent = triggeredByEvent;
	}

	public boolean isTriggeredByEvent() {
		return triggeredByEvent;
	}

	public CollapsedSubprocess createCollapsed() {
		assert collapsedProcess == null;
		collapsedProcess = new CollapsedSubprocess(this);
		return collapsedProcess;
	}

	@Override
	protected Stroke getStroke() {
		if (isTriggeredByEvent()) {
			return getVisualization().createStrokeDotted(getBorderWidth());
		} else {
			return super.getStroke();
		}
	}

	@Override
	public void repaint() {
		super.repaint();
		if (collapsedProcess != null) {
			collapsedProcess.repaint();
		}
	}

}
