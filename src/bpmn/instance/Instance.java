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
package bpmn.instance;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import bpmn.element.Graphics;
import bpmn.element.Rectangle;
import bpmn.element.activity.Process;
import bpmn.token.Token;
import bpmn.token.TokenCollection;
import bpmn.token.TokenFlow;

public class Instance {

	private static final int STAR_SIZE = 10; 

	private static final int STAR_CORNERS = 5; 

	private final InstanceManager manager;

	private final Instance parent;

	private final Process process;

	private final Collection<InstanceListener> listeners = new LinkedList<InstanceListener>();  

	private List<Instance> correlations = new ArrayList<Instance>();

	private final Collection<Instance> childs = new Vector<Instance>();

	private final TokenCollection tokens = new TokenCollection();

	private Color color;

	public Instance(final InstanceManager manager, final Process process,
			final Color color) {
		this(manager, null, process, color);
	}

	public Instance(final Instance parent, final Process process) {
		this(null, parent, process, null);
	}

	protected Instance(final InstanceManager manager,
			final Instance parent, final Process process, final Color color) {
		super();
		this.manager = manager;
		this.parent = parent;
		this.process = process;
		this.color = color;
	}

	public void addInstanceListener(final InstanceListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void removeInstanceListener(final InstanceListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	protected void notifyInstanceRemoved(final Instance instance) {
		synchronized (listeners) {
			for (InstanceListener listener : listeners) {
				listener.instanceRemoved(instance);
			}
		}
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

	public InstanceManager getInstanceManager() {
		if (manager == null) {
			return getParentInstance().getInstanceManager();
		} else {
			return manager;
		}
	}

	public Process getProcess() {
		return process;
	}

	public Instance getCorrelationInstance(final Collection<Instance> instances) {
		for (final Instance correlationInstance : instances) {
			if (correlations.contains(correlationInstance)) {
				return correlationInstance;
			}
		}
		return null;
	}

	public boolean hasCorrelationTo(final Process process) {
		for (Instance correlationInstance : correlations) {
			if (correlationInstance.getProcess().equals(process)) {
				return true;
			}
		}
		return false;
	}

	public void createCorrelationTo(final Instance instance) {
		correlations.add(instance);
	}

	///XXX: removeCorrelationTo(...)

	public Collection<Instance> getChildInstances() {
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
		removeAllChildInstances();
		removeAllTokens();
		final Instance parentInstance = getParentInstance();
		if (parentInstance == null) {
			getInstanceManager().remove(this);
		} else {
			parentInstance.removeChildInstance(this);
		}
		notifyInstanceRemoved(this);
		getInstanceManager().notifyInstanceRemoved(this);
	}

	/*
	 * child instances
	 */

	public Instance newChildInstance(final Process process) {
		final Instance childInstance = new Instance(this, process);
		addChildInstance(childInstance);
		return childInstance;
	}

	protected void addChildInstance(final Instance childInstance) {
		assert childInstance != null;
		childs.add(childInstance);
		getInstanceManager().notifyInstanceCreated(childInstance);
	}

	public int getChildInstanceCount() {
		int count = getChildInstances().size();
		for (Instance childInstance : getChildInstances()) {
			count += childInstance.getChildInstanceCount();
		}
		return count;
	}

	protected void removeChildInstance(final Instance childInstance) {
		assert childInstance != null;
//		assert(childs.contains(childInstance));
		childs.remove(childInstance);
	}

	public void removeAllChildInstances() {
		final Collection<Instance> instanceSnapshot = new Vector<Instance>(getChildInstances()); 
		for (Instance childInstance : instanceSnapshot) {
			childInstance.remove();
		}
		assert getChildInstanceCount() == 0;
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
			assert !tokens.contains(token);
			tokens.add(token);
		}
	}

	public int getTokenCount() {
		return getTokenCount(true);
	}

	public int getTokenCount(final boolean withSubinstances) {
		int count = getTokens().getCount();
		if (withSubinstances) {
			for (Instance childInstance : getChildInstances()) {
				count += childInstance.getTokenCount(withSubinstances);
			}
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
		final TokenCollection snapshotTokens = new TokenCollection(tokens);
		for (final Token snapshotToken : snapshotTokens) {
			if (!snapshotToken.equals(token)) {
				snapshotToken.remove();
			}
		}
		final Collection<Instance> instanceSnapshot = new Vector<Instance>(getChildInstances()); 
		for (final Instance childInstance : instanceSnapshot) {
			childInstance.removeAllOtherTokens(token);
		}
	}

	public void removeAllTokens() {
		final TokenCollection tokenSnapshot = new TokenCollection(tokens);
		for (final Token token : tokenSnapshot) {
			token.remove();
		}
		final Collection<Instance> instanceSnapshot = new Vector<Instance>(getChildInstances()); 
		for (final Instance childInstance : instanceSnapshot) {
			childInstance.removeAllTokens();
		}
		assert getTokenCount() == 0;
	}

	public void stepAllTokens(final int count) {
		/*
		 * Möglicherweise wurden einige token beim Durchlaufen bereits gelöscht (z.B. durch merge)
		 */
		final TokenCollection tokenSnapshot = new TokenCollection(tokens);
		for (Token token : tokenSnapshot) {
			boolean exists = false;
			synchronized (tokens) {
				exists = tokens.contains(token);
			}
			if (exists) {
				token.step(count);
			}
		}
		final Collection<Instance> childSnapshot = new Vector<Instance>(getChildInstances());
		for (Instance childInstance : childSnapshot) {
			childInstance.stepAllTokens(count);
		}
	}

	public boolean hasTokens() {
		if (tokens.isEmpty()) {
			for (Instance childInstance : getChildInstances()) {
				if (childInstance.hasTokens()) {
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	}

	public void removeIfHasNoTokens() {
		if (!hasTokens()) {
			remove();
		}
	}

	public void paint(final Graphics g, final Point center) {
		if (center != null) {
			final Rectangle size = new Rectangle(center, STAR_SIZE); 

			final Color color = getColor();
			if (color != null) {
				g.setStroke(new BasicStroke(0.f));
				g.setPaint(color);
				g.fillStar(size, STAR_CORNERS);
			}

			g.setStroke(new BasicStroke(1.f));
			g.setPaint(Token.HIGHLIGHT_COLOR);
			g.drawStar(size, STAR_CORNERS);
		}
	}

	public void paint(final Graphics g, final Point center, final int count) {
		paint(g, center);

		assert count > 0;
		if (count > 1) {
			g.setPaint(Color.BLACK);
			g.drawText(new Rectangle(center, STAR_SIZE), Integer.toString(count));
		}
	}

	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder('[');
		buffer.append(super.toString());
		buffer.append(", ");
		buffer.append(getProcess());
		buffer.append(", ");
		buffer.append("childs:");
		buffer.append(getChildInstanceCount());
		buffer.append(", ");
		buffer.append("token:");
		buffer.append(getTokenCount());
		buffer.append(']');
		return buffer.toString();
	}

}
