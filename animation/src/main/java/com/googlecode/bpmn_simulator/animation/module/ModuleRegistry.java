package com.googlecode.bpmn_simulator.animation.module;

import java.util.ArrayList;
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

	public Collection<Module> getModules() {
		return Collections.unmodifiableCollection(modules.values());
	}

	public Collection<Module> getModulesByFileExtension(final String extension) {
		final Collection<Module> modules = new ArrayList<>();
		if (extension != null) {
			for (final Module module : getModules()) {
				for (final String moduleExtension : module.getFileExtensions()) {
					if (extension.equalsIgnoreCase(moduleExtension)) {
						modules.add(module);
					}
				}
			}
		}
		return modules;
	}

}
