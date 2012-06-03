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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;

import bpmn.element.activity.Process;

public class InstanceManager {

	private final ColorGenerator colorGenerator = new ColorGenerator(); 

	private final Collection<Instance> instances = new Vector<Instance>(); 

	private final Collection<InstanceListener> listeners = new LinkedList<InstanceListener>();  

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

	protected void notifyInstanceCreated(final Instance instance) {
		synchronized (listeners) {
			for (InstanceListener listener : listeners) {
				listener.instanceAdded(instance);
			}
		}
	}

	protected void notifyInstanceRemoved(final Instance instance) {
		synchronized (listeners) {
			for (InstanceListener listener : listeners) {
				listener.instanceRemoved(instance);
			}
		}
	}

	private void add(final Instance instance) {
		synchronized (instances) {
			instances.add(instance);
		}
		notifyInstanceCreated(instance);
	}

	protected void remove(final Instance instance) {
		synchronized (instances) {
			instances.remove(instance);
		}
		notifyInstanceRemoved(instance);
	}

	public Instance newInstance(final Process process) {
		final Instance instance = new Instance(this, process, colorGenerator.next());
		add(instance);
		return instance;
	}

	public void stepAll(final int count) {
		final Collection<Instance> instanceSnapshot = new Vector<Instance>(instances); 
		for (Instance instance : instanceSnapshot) {
			instance.stepAllTokens(count);
		}
	}

	public void removeAll() {
		final Collection<Instance> instanceSnapshot = new Vector<Instance>(instances); 
		for (Instance instance : instanceSnapshot) {
			instance.remove();
		}
		instances.clear();
	}

}
