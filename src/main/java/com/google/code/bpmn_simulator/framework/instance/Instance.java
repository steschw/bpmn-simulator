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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.google.code.bpmn_simulator.bpmn.model.process.activities.Activity;
import com.google.code.bpmn_simulator.framework.element.visual.GraphicsLayer;
import com.google.code.bpmn_simulator.framework.element.visual.geometry.Bounds;
import com.google.code.bpmn_simulator.framework.token.Token;
import com.google.code.bpmn_simulator.framework.token.TokenCollection;
import com.google.code.bpmn_simulator.framework.token.TokenFlow;
import com.google.code.bpmn_simulator.framework.token.TokenListener;



public final class Instance
	extends AbstractChildinstancesContainer {

	private static final int STAR_SIZE = 10;

	private static final int STAR_CORNERS = 5;

	private final InstanceManager manager;

	private final Instance parent;

	private final Activity activity;

	private final List<Instance> correlations = new ArrayList<Instance>();

	private final TokenCollection tokens = new TokenCollection();

	private final Collection<TokenListener> tokenListeners = new LinkedList<TokenListener>();

	private Color color;

	public Instance(final InstanceManager manager, final Activity activity,
			final Color color) {
		this(manager, null, activity, color);
	}

	public Instance(final Instance parent, final Activity activity) {
		this(null, parent, activity, null);
	}

	protected Instance(final InstanceManager manager,
			final Instance parent, final Activity activity, final Color color) {
		super();
		this.manager = manager;
		this.parent = parent;
		assert manager != null || parent != null;
		this.activity = activity;
		this.color = color;
	}

	public void addTokenListener(final TokenListener listener) {
		synchronized (tokenListeners) {
			tokenListeners.add(listener);
		}
	}

	public void removeTokenListener(final TokenListener listener) {
		synchronized (tokenListeners) {
			tokenListeners.remove(listener);
		}
	}

	protected void notifyTokenAdded(final Token token) {
		synchronized (tokenListeners) {
			for (final TokenListener listener : tokenListeners) {
				listener.tokenAdded(token);
			}
		}
	}

	protected void notifyTokenRemoved(final Token token) {
		synchronized (tokenListeners) {
			for (final TokenListener listener : tokenListeners) {
				listener.tokenRemoved(token);
			}
		}
	}

	public Instance getParent() {
		return parent;
	}

	public Instance getTopLevelInstance() {
		final Instance parentInstance = getParent();
		if (parentInstance == null) {
			return this;
		} else {
			return parentInstance.getTopLevelInstance();
		}
	}

	public InstanceManager getInstanceManager() {
		if (manager == null) {
			return getParent().getInstanceManager();
		} else {
			return manager;
		}
	}

	public Activity getActivity() {
		return activity;
	}

	public Instance getCorrelationInstance(final Collection<Instance> instances) {
		for (final Instance correlationInstance : instances) {
			if (correlations.contains(correlationInstance)) {
				return correlationInstance;
			}
		}
		return null;
	}

	public boolean hasCorrelationTo(final Activity activity) {
		for (final Instance correlationInstance : correlations) {
			if (correlationInstance.getActivity().equals(activity)) {
				return true;
			}
		}
		return false;
	}

	public void createCorrelationTo(final Instance instance) {
		correlations.add(instance);
	}

	///XXX: removeCorrelationTo(...)

	public TokenCollection getTokens() {
		return tokens;
	}

	public void setColor(final Color color) {
		this.color = color;
	}

	public Color getColor() {
		if (color == null) {
			final Instance parentInstance = getParent();
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
		final Instance parentInstance = getParent();
		if (parentInstance == null) {
			getInstanceManager().removeChildInstance(this);
		} else {
			parentInstance.removeChildInstance(this);
		}
	}

	/*
	 * child instances
	 */

	public Instance newChildInstance(final Activity activity) {
		final Instance childInstance = new Instance(this, activity);
		addChildInstance(childInstance);
		return childInstance;
	}

	/*
	 * tokens
	 */

	public Token addNewToken(final TokenFlow tokenFlow) {
		return new Token(this, tokenFlow);
	}

	public Token assignNewToken(final TokenFlow tokenFlow) {
		// Keine Benachrichtigung über das Einfügen an den TokenFlow
		final Token token = new Token(this);
		token.assignTokenFlow(tokenFlow);
		return token;
	}

	public void addToken(final Token token) {
		synchronized (tokens) {
			assert !tokens.contains(token);
			tokens.add(token);
			notifyTokenAdded(token);
		}
	}

	public int getTokenCount() {
		return getTokenCount(true);
	}

	public int getTokenCount(final boolean withSubinstances) {
		int count = getTokens().getCount();
		if (withSubinstances) {
			for (final Instance childInstance : getChildInstances()) {
				count += childInstance.getTokenCount(withSubinstances);
			}
		}
		return count;
	}

	public void removeToken(final Token token) {
		synchronized (tokens) {
//			assert(tokens.contains(token));
			notifyTokenRemoved(token);
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

	@Override
	public void executeAllChildInstances(final int stepCount) {
		executeAllInstanceTokens(stepCount);
		super.executeAllChildInstances(stepCount);
	}

	protected void executeAllInstanceTokens(final int stepCount) {
		/*
		 * Möglicherweise wurden einige Token beim Durchlaufen bereits gelöscht (z.B. durch merge)
		 */
		final TokenCollection tokenSnapshot = new TokenCollection(tokens);
		for (final Token token : tokenSnapshot) {
			boolean exists = false;
			synchronized (tokens) {
				exists = tokens.contains(token);
			}
			if (exists) {
				token.step(stepCount);
			}
		}
	}

	public boolean hasTokens() {
		if (tokens.isEmpty()) {
			for (final Instance childInstance : getChildInstances()) {
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

	public Collection<Instance> getInstancesByActivity(final Activity activity) {
		final Collection<Instance> instances = new ArrayList<Instance>();
		if (activity.equals(getActivity())) {
			instances.add(this);
		} else {
			for (final Instance childInstance : getChildInstances()) {
				instances.addAll(childInstance.getInstancesByActivity(activity));
			}
		}
		return instances;
	}

	public void paint(final GraphicsLayer g, final Point center) {
		if (center != null) {
			final Bounds size = new Bounds(center, STAR_SIZE);

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

	public void paint(final GraphicsLayer g, final Point center, final int count) {
		paint(g, center);

		assert count > 0;
		if (count > 1) {
			g.setPaint(GraphicsLayer.contrastColor(getColor()));
			g.drawText(new Bounds(center, STAR_SIZE), Integer.toString(count));
		}
	}

	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder('[');
		buffer.append(super.toString());
		buffer.append(", ");
		buffer.append(activity);
		buffer.append(", ");
		buffer.append("childs:");
		buffer.append(getChildInstanceCount());
		buffer.append(", ");
		buffer.append("token:");
		buffer.append(getTokenCount());
		buffer.append(']');
		return buffer.toString();
	}

	public boolean isEnded() {
		for (final Token token: getTokens()) {
			if (!token.hasEndNodeReached()) {
				return false;
			}
		}
		return true;
	}

}
