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

abstract class AbstractInstancesContainer
		implements Iterable<Instance> {

	private final Collection<Instance> childs = new ArrayList<>();

	private final Set<InstancesListener> instancesListeners = new HashSet<>();

	@Override
	public Iterator<Instance> iterator() {
		return childs.iterator();
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
		assert !childs.contains(childInstance);
		childs.add(childInstance);
		notifyInstanceCreated(childInstance);
	}

	protected abstract Instance createNewChildInstance();

	public Instance addNewChildInstance() {
		final Instance instance = createNewChildInstance();
		addChildInstance(instance);
		return instance;
	}

	public void removeChildInstance(final Instance childInstance) {
		assert childs.contains(childInstance);
		childs.remove(childInstance);
		notifyInstanceRemoved(childInstance);
	}

}
