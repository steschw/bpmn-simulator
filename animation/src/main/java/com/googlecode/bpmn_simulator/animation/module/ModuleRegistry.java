package com.googlecode.bpmn_simulator.animation.module;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModuleRegistry {

	private static final ModuleRegistry instance = new ModuleRegistry();

	private final Map<String, Module> modules = new HashMap<>();

	public static ModuleRegistry getDefault() {
		return instance;
	}

	public void registerModule(final Module module) {
		if ((module == null) || modules.containsKey(module.getName())) {
			throw new IllegalArgumentException();
		}
		modules.put(module.getName(), module);
	}

	public Collection<Module> getRegistredModules() {
		return Collections.unmodifiableCollection(modules.values());
	}

}
