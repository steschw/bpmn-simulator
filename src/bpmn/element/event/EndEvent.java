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

import java.awt.Color;

import javax.swing.Icon;

import bpmn.element.VisualConfig;
import bpmn.token.InstanceController;
import bpmn.token.Token;

@SuppressWarnings("serial")
public class EndEvent extends Event {

	public EndEvent(final String id, final String name,
			final InstanceController tockenController) {
		super(id, name, tockenController);
	}

	@Override
	protected Color getElementDefaultBackground() {
		return getVisualConfig().getBackground(VisualConfig.Element.EVENT_END);
	}

	@Override
	protected int getBorderWidth() {
		return 3;
	}

	@Override
	protected void tokenForward(final Token token) {
		if (isTerminate()) {
			token.getInstance().removeAllOtherTokens(token);
		}
		super.tokenForward(token);
	}

	@Override
	protected Icon getTypeIcon() {
		return getDefinition().getIcon(getVisualConfig(), true);
	}

}
