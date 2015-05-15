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
package com.googlecode.bpmn_simulator.animation.token;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

abstract class InstanceContainer
		implements Iterable<Instance> {

	private final InstanceContainer parent;

	private final Collection<Instance> childInstances = new ArrayList<>();

	private final Set<InstancesListener> instancesListeners = new HashSet<>();

	protected InstanceContainer(final InstanceContainer parent) {
		super();
		this.parent = parent;
	}

	public InstanceContainer getParentContainer() {
		return parent;
	}

	@Override
	public Iterator<Instance> iterator() {
		return childInstances.iterator();
	}

	public void addInstancesListener(final InstancesListener listener) {
		synchronized (instancesListeners) {
			instancesListeners.add(listener);
		}
	}

	public void removeInstancesListener(final InstancesListener listener) {
		synchronized (instancesListeners) {
			instancesListeners.remove(listener);
		}
	}

	private void notifyInstanceCreated(final Instance instance) {
		synchronized (instancesListeners) {
			for (final InstancesListener listener : instancesListeners) {
				listener.instanceAdded(instance);
			}
		}
	}

	private void notifyInstanceRemoved(final Instance instance) {
		synchronized (instancesListeners) {
			for (final InstancesListener listener : instancesListeners) {
				listener.instanceRemoved(instance);
			}
		}
	}

	protected void addChildInstance(final Instance childInstance) {
		assert childInstance != null;
		assert !childInstances.contains(childInstance);
		childInstances.add(childInstance);
		notifyInstanceCreated(childInstance);
	}

	public boolean hasChildInstances() {
		return !childInstances.isEmpty();
	}

	protected Instance createNewChildInstance() {
		return new Instance(this);
	}

	public Instance addNewChildInstance() {
		final Instance instance = createNewChildInstance();
		addChildInstance(instance);
		return instance;
	}

	protected void removeChildInstance(final Instance childInstance) {
		assert childInstances.contains(childInstance);
		childInstances.remove(childInstance);
		notifyInstanceRemoved(childInstance);
	}

	protected void removeAllChildInstances() {
		for (final Instance childInstance : new ArrayList<>(childInstances)) {
			childInstance.remove();
		}
	}

	public void clear() {
		removeAllChildInstances();
	}

	public void step(final int count) {
		for (final Instance childInstance : new ArrayList<>(childInstances)) {
			if (childInstances.contains(childInstance)) {
				childInstance.step(count);
			}
		}
	}

}
