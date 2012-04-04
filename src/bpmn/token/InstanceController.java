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

import java.util.HashSet;
import java.util.Set;

public class InstanceController {

	private final ColorGenerator colorGenerator = new ColorGenerator(); 

	private final Set<Instance> instances = new HashSet<Instance>(); 

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
