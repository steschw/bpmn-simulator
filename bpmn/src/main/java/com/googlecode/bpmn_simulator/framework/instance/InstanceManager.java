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

import java.util.ArrayList;
import java.util.Collection;

import com.googlecode.bpmn_simulator.animation.token.Instance;
import com.googlecode.bpmn_simulator.animation.token.Instance;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.Activity;


public final class InstanceManager
	extends Instance {

	private final ColorGenerator colorGenerator = new ColorGenerator();

	public Instance newInstance(final Activity activity) {
		final Instance instance = new Instance(this, activity, colorGenerator.next());
		addChildInstance(instance);
		return instance;
	}

	public Collection<Instance> getInstancesByActivity(final Activity activity) {
		final Collection<Instance> instances = new ArrayList<Instance>();
		for (final Instance childInstance : getChildInstances()) {
			instances.addAll(childInstance.getInstancesByActivity(activity));
		}
		return instances;
	}

}
