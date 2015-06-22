/*
 * Copyright (C) 2015 Stefan Schweitzer
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
package com.googlecode.bpmn_simulator.bpmn.model.core.common;

import com.googlecode.bpmn_simulator.animation.ref.Reference;
import com.googlecode.bpmn_simulator.animation.ref.ReferenceSet;
import com.googlecode.bpmn_simulator.animation.ref.References;
import com.googlecode.bpmn_simulator.animation.token.Token;
import com.googlecode.bpmn_simulator.animation.token.Tokens;

public abstract class AbstractFlowNode
		extends AbstractFlowElement
		implements FlowNode {

	private final ReferenceSet<SequenceFlow> incoming = new ReferenceSet<>();
	private final ReferenceSet<SequenceFlow> outgoing = new ReferenceSet<>();

	public AbstractFlowNode(final String id, final String name) {
		super(id, name);
	}

	@Override
	public void addIncoming(final Reference<SequenceFlow> incoming) {
		this.incoming.add(incoming);
	}

	protected References<SequenceFlow> getIncoming() {
		return incoming;
	}

	@Override
	public void addOutgoing(final Reference<SequenceFlow> outgoing) {
		this.outgoing.add(outgoing);
	}

	protected References<SequenceFlow> getOutgoing() {
		return outgoing;
	}

	private static boolean copyTokenToDefaultOutgoing(final Token token, final DefaultSequenceFlowElement element) {
		final SequenceFlow defaultSequenceFlow = element.getDefaultSequenceFlow();
		if (defaultSequenceFlow != null) {
			token.copyTo(defaultSequenceFlow);
			return true;
		}
		return false;
	}

	protected final void copyTokenToAllOutgoing(final Token token) {
		copyTokenToOutgoing(token, false, null);
	}

	protected final void copyTokenToFirstOutgoing(final Token token) {
		copyTokenToOutgoing(token, true, null);
	}

	protected final void copyTokenToOutgoing(final Token token,
			final boolean firstOnly,
			final DefaultSequenceFlowElement defaultSequenceFlowElement) {
		int conditionalCount = 0;
		for (final SequenceFlow outgoing : getOutgoing()) {
			if (!outgoing.isDefault()) {
				final boolean conditional = outgoing.isConditional();
				if (!conditional
						|| outgoing.getConditionExpression().getResult()) {
					token.copyTo(outgoing);
					if (conditional) {
						++conditionalCount;
					}
					if (firstOnly) {
						break; 
					}
				}
			}
		}
		if ((conditionalCount == 0) && (defaultSequenceFlowElement != null)) {
			if (copyTokenToDefaultOutgoing(token, defaultSequenceFlowElement)) {
				++conditionalCount;
			}
		}
	}

	protected void forwardToken(final Token token) {
		copyTokenToAllOutgoing(token);
	}

	@Override
	protected void onTokenComplete(final Token token) {
		forwardToken(token);
		token.remove();
	}

	/**
	 * @return doesn't contains the forToken. returns null if not every incoming has a token
	 */
	protected Tokens getTokenForEveryIncoming(final Token forToken) {
		final Tokens tokens = new Tokens();
		final Tokens availableTokens = getCompleteTokens().getByInstance(forToken.getInstance());
		for (final SequenceFlow incoming : getIncoming()) {
			if (incoming.equals(forToken.getPreviousTokenFlow())) {
				continue;
			}
			final Tokens incomingTokens = availableTokens.getByPreviousTokenFlow(incoming);
			if (incomingTokens.isEmpty()) {
				return null;
			}
			tokens.add(incomingTokens.get(0));
		}
		return tokens;
	}

}
