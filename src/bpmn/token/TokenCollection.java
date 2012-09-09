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

import java.awt.Point;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import bpmn.Graphics;
import bpmn.instance.Instance;

@SuppressWarnings("serial")
public class TokenCollection extends Vector<Token> {

	protected static final int TOKEN_MARGIN = 5;

	public TokenCollection() {
		super();
	}

	public TokenCollection(final Collection<? extends Token> c) {
		super(c);
	}

	@Override
	public synchronized boolean addAll(final Collection<? extends Token> c) {
		for (final Token token : c) {
			if (contains(token)) {
				assert false;
			}
		}
		return super.addAll(c);
	}

	@Override
	public synchronized boolean add(final Token token) {
		assert !contains(token);
		return super.add(token);
	}

	public synchronized boolean remove(final Token token) {
//		assert(contains(token));
		return super.remove(token);
	}

	public synchronized void moveTo(final TokenCollection collection,
			final Token token) {
		remove(token);
		collection.add(token);
	}

	public int getCount() {
		return size();
	}

	public synchronized Token merge() {
		final Iterator<Token> i = iterator();
		if (i.hasNext()) {
			final Token mergedToken = i.next();
			while (i.hasNext()) {
				mergedToken.merge(i.next());
			}
			return mergedToken;
		}
		return null;
	}

	public synchronized Collection<Instance> getInstances() {
		final Collection<Instance> instances = new Vector<Instance>();
		for (final Token token : this) {
			final Instance instance = token.getInstance(); 
			if ((instance != null) && !instances.contains(instance)) {
				instances.add(instance);
			}
		}
		return instances;
	}

	public synchronized TokenCollection byInstance(final Instance instance) {
		final TokenCollection tokens = new TokenCollection();
		for (final Token token : this) {
			if ((instance == null) || instance.equals(token.getInstance())) {
				tokens.add(token);
			}
		}
		return tokens;
	}

	public synchronized TokenCollection byCurrentFlow(final TokenFlow tokenFlow) {
		final TokenCollection tokens = new TokenCollection();
		for (Token token : this) {
			if (tokenFlow.equals(token.getCurrentFlow())) {
				tokens.add(token);
			}
		}
		return tokens;
	}

	public synchronized TokenCollection byPreviousFlow(final TokenFlow tokenFlow) {
		final TokenCollection tokens = new TokenCollection();
		for (Token token : this) {
			if (tokenFlow.equals(token.getPreviousFlow())) {
				tokens.add(token);
			}
		}
		return tokens;
	}

	public void paintVertical(final Graphics g, final Point center) {
		final Collection<Instance> instances = getInstances();
		center.translate(0, -(TOKEN_MARGIN * instances.size()) / 2);
		for (final Instance instance : instances) {
			instance.paint(g, center, byInstance(instance).getCount());
			center.translate(0, TOKEN_MARGIN);
		}
	}

	public void paintHorizontal(final Graphics g, final Point center) {
		final Collection<Instance> instances = getInstances();
		center.translate(-(TOKEN_MARGIN * instances.size()) / 2, 0);
		for (final Instance instance : instances) {
			instance.paint(g, center, byInstance(instance).getCount());
			center.translate(TOKEN_MARGIN, 0);
		}
	}

	public void paintHorizontalRight(final Graphics g, final Point point) {
		for (Instance instance : getInstances()) {
			instance.paint(g, point, byInstance(instance).getCount());
			point.translate(-TOKEN_MARGIN, 0);
		}
	}

}
