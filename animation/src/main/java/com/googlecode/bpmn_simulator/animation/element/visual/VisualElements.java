package com.googlecode.bpmn_simulator.animation.element.visual;

import java.util.HashMap;
import java.util.Map;

public final class VisualElements {

	private static final Map<Class<? extends VisualElement>, Info> infos = new HashMap<>();

	private VisualElements() {
		super();
	}

	public static void register(final Class<? extends VisualElement> element, final Info info) {
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

	public static void register(final Class<? extends VisualElement> element, final int defaultBackgroundColor) {
		register(element, new DefaultInfo(defaultBackgroundColor, Info.DEFAULT_FOREGROUND_COLOR));
	}

	public static void register(final Class<? extends VisualElement> element, final int defaultBackgroundColor, final int defaultForegroundColor) {
		register(element, new DefaultInfo(defaultBackgroundColor, defaultForegroundColor));
	}

	public static Info getInfo(final Class<? extends VisualElement> element) {
		synchronized (infos) {
			return infos.get(element);
		}
	}

	public interface Info {

		int DEFAULT_FOREGROUND_COLOR = 0x00000000;
		int DEFAULT_BACKGROUND_COLOR = 0x00ffffff;

		int getDefaultForegroundColor();

		int getDefaultBackgroundColor();

	}

	private static class DefaultInfo
			implements Info {

		private final int defaultForegroundColor;
		private final int defaultBackgroundColor;

		public DefaultInfo(final int defaultBackgroundColor, final int defaultForegroundColor) {
			super();
			this.defaultForegroundColor = defaultForegroundColor;
			this.defaultBackgroundColor = defaultBackgroundColor;
		}

		@Override
		public int getDefaultForegroundColor() {
			return defaultForegroundColor;
		}

		@Override
		public int getDefaultBackgroundColor() {
			return defaultBackgroundColor;
		}

	}

}
