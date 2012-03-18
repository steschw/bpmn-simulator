package bpmn.token;

import java.util.HashSet;

public class InstanceController {

	private ColorGenerator colorGenerator = new ColorGenerator(); 

	private HashSet<Instance> instances = new HashSet<Instance>(); 

	public InstanceController() {
		super();
	}

	public synchronized Instance newInstance() {
		final Instance instance = new Instance(null, colorGenerator.next());
		instances.add(instance);
		return instance;
	}

	public synchronized void stepAll(final int count) {
		for (Instance instance : instances) {
			instance.stepAllTokens(count);
		}
	}

	public synchronized void removeAll() {
		for (Instance instance : instances) {
			instance.remove();
		}
		instances.clear();
	}

}
