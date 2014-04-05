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
package com.googlecode.bpmn_simulator.framework.instance;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;

public abstract class AbstractChildinstancesContainer {

	private final Collection<Instance> childInstances = new Vector<Instance>();

	private final Collection<InstanceListener> instanceListeners = new LinkedList<InstanceListener>();

	public Collection<Instance> getChildInstances() {
		return childInstances;
	}

	public void addInstanceListener(final InstanceListener listener) {
		synchronized (instanceListeners) {
			instanceListeners.add(listener);
		}
	}

	public void removeInstanceListener(final InstanceListener listener) {
		synchronized (instanceListeners) {
			instanceListeners.remove(listener);
		}
	}

	protected void notifyInstanceCreated(final Instance instance) {
		synchronized (instanceListeners) {
			for (final InstanceListener listener : instanceListeners) {
				listener.instanceAdded(instance);
			}
		}
	}

	protected void notifyInstanceRemoved(final Instance instance) {
		synchronized (instanceListeners) {
			for (final InstanceListener listener : instanceListeners) {
				listener.instanceRemoved(instance);
			}
		}
	}

	protected void addChildInstance(final Instance childInstance) {
		assert childInstance != null;
		childInstances.add(childInstance);
		notifyInstanceCreated(childInstance);
	}

	public int getChildInstanceCount() {
		int count = getChildInstances().size();
		for (final Instance childInstance : getChildInstances()) {
			count += childInstance.getChildInstanceCount();
		}
		return count;
	}

	protected void removeChildInstance(final Instance childInstance) {
		assert childInstance != null;
		childInstances.remove(childInstance);
		notifyInstanceRemoved(childInstance);
	}

	public void removeAllChildInstances() {
		final Collection<Instance> instanceSnapshot = new Vector<Instance>(getChildInstances());
		for (final Instance childInstance : instanceSnapshot) {
			childInstance.remove();
		}
		assert getChildInstanceCount() == 0;
		childInstances.clear();
	}

	public void executeAllChildInstances(final int stepCount) {
		final Collection<Instance> instanceSnapshot = new Vector<Instance>(childInstances);
		for (final Instance instance : instanceSnapshot) {
			instance.executeAllChildInstances(stepCount);
		}
	}

}
