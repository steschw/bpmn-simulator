package com.googlecode.bpmn_simulator.animation.element.logical;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public final class LogicalElements {

	private static final Map<Class<? extends LogicalElement>, Info> infos = new HashMap<>();

	private LogicalElements() {
		super();
	}

	public static void register(final Class<? extends LogicalElement> element, final Info info) {
		synchronized (infos) {
			if (infos.containsKey(element)) {
				throw new IllegalArgumentException();
			}
			if (info == null) {
				throw new NullPointerException();
			}
			infos.put(element, info);
		}
	}

	public static void register(final Class<? extends LogicalElement> element, final String name) {
		register(element, new DefaultInfo(name, null, Info.DEFAULT_STEP_COUNT));
	}

	public static Info getInfo(final Class<? extends LogicalElement> element) {
		synchronized (infos) {
			return infos.get(element);
		}
	}

	public static String getName(final Class<? extends LogicalElement> element) {
		final Info info = getInfo(element);
		return (info == null) ? element.getSimpleName() : info.getName();
	}

	public static String getName(final LogicalElement element) {
		if (element == null) {
			throw new NullPointerException();
		}
		return getName(element.getClass());
	}

	public interface Info {

		int DEFAULT_STEP_COUNT = 90;

		String getName();

		URI getDetails();

		int getDefaultStepCount();

	}

	private static class DefaultInfo
			implements Info {

		private final String name;
		private final URI details;
		private final int defaultStepCount;

		public DefaultInfo(final String name, final URI details, final int defaultStepCount) {
			super();
			if (name == null) {
				throw new IllegalArgumentException();
			}
			this.name = name;
			this.details = details;
			this.defaultStepCount = defaultStepCount;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public URI getDetails() {
			return details;
		}

		@Override
		public int getDefaultStepCount() {
			return defaultStepCount;
		}

		@Override
		public String toString() {
			return getName();
		}

	}

}
