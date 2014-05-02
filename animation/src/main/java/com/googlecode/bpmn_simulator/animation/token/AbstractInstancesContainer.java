package com.googlecode.bpmn_simulator.animation.token;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

abstract class AbstractInstancesContainer
		implements Iterable<Instance> {

	private final Collection<Instance> childs = new ArrayList<Instance>();

	private final Set<InstancesListener> instancesListeners = new HashSet<InstancesListener>();

	@Override
	public Iterator<Instance> iterator() {
		return null;
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
