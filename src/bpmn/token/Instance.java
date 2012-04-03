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
package bpmn.token;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;

import bpmn.element.Graphics;

public class Instance {

	private Instance parent = null;

	private Vector<Instance> childs = new Vector<Instance>();

	private TokenCollection tokens = new TokenCollection();

	private Color color = null;

	public Instance(final Instance parent) {
		super();
		setParentInstance(parent);
	}

	public Instance(final Instance parent, final Color color) {
		this(parent);
		setColor(color);
	}

	protected final void setParentInstance(final Instance instance) {
		this.parent = instance;
	}

	public final Instance getParentInstance() {
		return parent;
	}

	public Instance getTopLevelInstance() {
		final Instance parentInstance = getParentInstance();
		if (parentInstance == null) {
			return this;
		} else {
			return parentInstance.getTopLevelInstance();
		}
	}

	protected Vector<Instance> getChildInstances() {
		return childs;
	}

	public TokenCollection getTokens() {
		return tokens;
	}

	public final void setColor(final Color color) {
		this.color = color;
	}

	public final Color getColor() {
		if (color == null) {
			final Instance parentInstance = getParentInstance();
			if (parentInstance == null) {
				return null;
			} else {
				return parentInstance.getColor();
			}
		} else {
			return color;
		}
	}

	public void remove() {
		final Instance parentInstance = getParentInstance();
		removeAllChildInstances();
		removeAllTokens();
		if (parentInstance != null) {
			parentInstance.removeChildInstance(this);
		}
	}

/*
	protected void removeIfNoTokens() {
		if (tokens.isEmpty()) {
			remove();
		}
	}
*/

	/*
	 * child instances
	 */

	public Instance newChildInstance() {
		final Instance childInstance = new Instance(this);
		addChildInstance(childInstance);
		return childInstance;
	}

	protected void addChildInstance(Instance instance) {
		assert(instance != null);
		childs.add(instance);
	}

	public int getChildInstanceCount() {
		int count = getChildInstances().size();
		for (Instance childInstance : getChildInstances()) {
			count += childInstance.getChildInstanceCount();
		}
		return count;
	}

	protected void removeChildInstance(final Instance childInstance) {
		assert(childInstance != null);
//		assert(childs.contains(childInstance));
		childs.remove(childInstance);
	}

	public void removeAllChildInstances() {
		Vector<Instance> instanceSnapshot = new Vector<Instance>(getChildInstances()); 
		for (Instance childInstance : instanceSnapshot) {
			childInstance.remove();
		}
		assert(getChildInstanceCount() == 0);
	}

	/*
	 * tokens
	 */

/*
	protected void moveTokensToInstance(final Instance instance) {
		for (Token token : getTokens()) {
			token.setInstance(instance);
		}
	}
*/

	public Token newToken(final TokenFlow tokenFlow) {
		final Token token = new Token(this, tokenFlow);
		addToken(token);
		return token;
	}

	public Token cloneToken(final Token token) {
		Token newToken = null;
		try {
			newToken = (Token)token.clone();
			addToken(newToken);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return newToken;
	}

	protected void addToken(final Token token) {
		synchronized (tokens) {
			assert(!tokens.contains(token));
			tokens.add(token);
		}
	}

	public int getTokenCount() {
		int count = getTokens().getCount();
		for (Instance childInstance : getChildInstances()) {
			count += childInstance.getTokenCount();
		}
		return count;
	}

	public void removeToken(final Token token) {
		synchronized (tokens) {
//			assert(tokens.contains(token));
			tokens.remove(token);
		}
	}

	public void removeAllOtherTokens(final Token token) {
		TokenCollection snapshotTokens = new TokenCollection(tokens);
		for (final Token snapshotToken : snapshotTokens) {
			if (!snapshotToken.equals(token)) {
				snapshotToken.remove();
			}
		}
		Vector<Instance> instanceSnapshot = new Vector<Instance>(getChildInstances()); 
		for (final Instance childInstance : instanceSnapshot) {
			childInstance.removeAllOtherTokens(token);
		}
	}

	public void removeAllTokens() {
		TokenCollection tokenSnapshot = new TokenCollection(tokens);
		for (final Token token : tokenSnapshot) {
			token.remove();
		}
		Vector<Instance> instanceSnapshot = new Vector<Instance>(getChildInstances()); 
		for (final Instance childInstance : instanceSnapshot) {
			childInstance.removeAllTokens();
		}
		assert(getTokenCount() == 0);
	}

	public void stepAllTokens(final int count) {
		/*
		 * Möglicherweise wurden einige token beim Durchlaufen bereits gelöscht (z.B. durch merge)
		 */
		TokenCollection tokenSnapshot = new TokenCollection(tokens);
		for (Token token : tokenSnapshot) {
			boolean exists = false;
			synchronized (tokens) {
				exists = tokens.contains(token);
			}
			if (exists) {
				token.step(count);
			}
		}
		Vector<Instance> childSnapshot = new Vector<Instance>(getChildInstances());
		for (Instance childInstance : childSnapshot) {
			childInstance.stepAllTokens(count);
		}
	}

	private static final Rectangle getSize(final Point center, final int radius) {
		return new Rectangle(center.x - radius, center.y - radius, radius * 2, radius * 2);
	}

	public void paint(Graphics g, final Point center) {
		if (center != null) {

			final Color color = getColor();
			if (color != null) {
				g.setStroke(new BasicStroke(0.f));
				g.setPaint(color);
				g.fillStar(getSize(center, 10), 5);
			}

			g.setStroke(new BasicStroke(1.f));
			g.setPaint(Token.HIGHLIGHT_COLOR);
			g.drawStar(getSize(center, 10), 5);
		}
	}

	public void paint(Graphics g, final Point center, final int count) {
		paint(g, center);

		if (count > 1) {
			g.setPaint(Color.BLACK);
			g.drawMultilineText(getSize(center, 10), Integer.toString(count), true, true);
		}
	}

}
