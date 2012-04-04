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
package bpmn.element.gateway;

import bpmn.element.Graphics;
import bpmn.token.Token;

public class ExclusiveGateway extends Gateway {

	private static final long serialVersionUID = 1L;

	private static boolean showSymbol = true;

	public static void setShowSymbol(final boolean show) {
		ExclusiveGateway.showSymbol = show;
	}

	public ExclusiveGateway(final String id, final String name) {
		super(id, name);
	}

	@Override
	protected void paintElement(final Graphics g) {
		super.paintElement(g);

		if (showSymbol) {
			g.drawCross(getSymbolBounds(), true);
		}
	}

	@Override
	protected void tokenForward(final Token token) {
		if (forwardTokenToFirstOutgoing(token)) {
			token.remove();
			setException(false);
		} else {
			setException(true);
		}
	}

}
