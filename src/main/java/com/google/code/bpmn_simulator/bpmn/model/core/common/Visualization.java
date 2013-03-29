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
package com.google.code.bpmn_simulator.bpmn.model.core.common;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.net.URL;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Visualization {

	public static final String ICON_BUSSINESRULE = "businessrule.png"; //$NON-NLS-1$
	public static final String ICON_COLLAPSED = "collapsed.png"; //$NON-NLS-1$
	public static final String ICON_COLLECTION = "collection.png"; //$NON-NLS-1$
	public static final String ICON_EXCEPTION = "exception.png"; //$NON-NLS-1$
	public static final String ICON_LOOP = "loop.png"; //$NON-NLS-1$
	public static final String ICON_MANUAL = "manual.png"; //$NON-NLS-1$
	public static final String ICON_PARALLEL = "parallel.png"; //$NON-NLS-1$
	public static final String ICON_RECEIVE = "receive.png"; //$NON-NLS-1$
	public static final String ICON_SCRIPT = "script.png"; //$NON-NLS-1$
	public static final String ICON_SEND = "send.png"; //$NON-NLS-1$
	public static final String ICON_SEQUENTIAL = "sequential.png"; //$NON-NLS-1$
	public static final String ICON_SERVICE = "service.png"; //$NON-NLS-1$
	public static final String ICON_TIMER = "timer.png"; //$NON-NLS-1$
	public static final String ICON_USER = "user.png"; //$NON-NLS-1$
	public static final String ICON_TERMINATE = "terminate.png"; //$NON-NLS-1$
	public static final String ICON_LINK = "link.png"; //$NON-NLS-1$
	public static final String ICON_LINK_INVERSE = "link_inverse.png"; //$NON-NLS-1$
	public static final String ICON_MESSAGE = "send.png"; //$NON-NLS-1$
	public static final String ICON_MESSAGE_INVERSE = "receive.png"; //$NON-NLS-1$
	public static final String ICON_SIGNAL = "signal.png"; //$NON-NLS-1$
	public static final String ICON_SIGNAL_INVERSE = "signal_inverse.png"; //$NON-NLS-1$
	public static final String ICON_ERROR = "error.png"; //$NON-NLS-1$
	public static final String ICON_ERROR_INVERSE = "error_inverse.png"; //$NON-NLS-1$
	public static final String ICON_CONDITIONAL = "conditional.png"; //$NON-NLS-1$

	private static final String ICONPATH = "bpmn/icons/"; //$NON-NLS-1$

	private static final Color DEFAULT_BACKGROUNDCOLOR = Color.WHITE;

	private final Map<String, Icon> icons = new IdentityHashMap<String, Icon>();

	public enum Element {
		GATEWAY,
		TASK,
		PROCESS,
		EVENT_START,
		EVENT_END,
		EVENT_INTERMEDIATE,
		EVENT_BOUNDARY,
		DATA_OBJECT,
		DATA_STORAGE,
	}

	private final Map<Element, Color> backgroundColors = new EnumMap<Element, Color>(Element.class);

	private boolean ignoreColors;

	private boolean antialiasing = true;
	private boolean showExclusiveGatewaySymbol = true;

	public Visualization() {
		super();
		loadIcons();
	}

	protected static Icon loadIconFromRessource(final String name) {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final URL url = classLoader.getResource(ICONPATH + name);
		assert url != null;
		Icon icon = null;
		if (url != null) {
			icon = new ImageIcon(url);
		}
		assert icon != null;
		return icon;
	}

	protected void loadIcon(final String name) {
		icons.put(name, loadIconFromRessource(name));
	}

	public void loadIcons() {
		loadIcon(ICON_BUSSINESRULE);
		loadIcon(ICON_COLLAPSED);
		loadIcon(ICON_COLLECTION);
		loadIcon(ICON_EXCEPTION);
		loadIcon(ICON_LOOP);
		loadIcon(ICON_MANUAL);
		loadIcon(ICON_PARALLEL);
		loadIcon(ICON_RECEIVE);
		loadIcon(ICON_SCRIPT);
		loadIcon(ICON_SEND);
		loadIcon(ICON_SEQUENTIAL);
		loadIcon(ICON_SERVICE);
		loadIcon(ICON_TIMER);
		loadIcon(ICON_USER);
		loadIcon(ICON_TERMINATE);
		loadIcon(ICON_LINK);
		loadIcon(ICON_LINK_INVERSE);
		loadIcon(ICON_SIGNAL);
		loadIcon(ICON_SIGNAL_INVERSE);
		loadIcon(ICON_ERROR);
		loadIcon(ICON_ERROR_INVERSE);
		loadIcon(ICON_CONDITIONAL);
	}

	public Icon getIcon(final String name) {
		assert icons.containsKey(name);
		return icons.get(name);
	}

	public Color getBackground(final Element element) {
		return backgroundColors.containsKey(element)
				? backgroundColors.get(element)
				: DEFAULT_BACKGROUNDCOLOR;
	}

	public void setBackground(final Element element, final Color color) {
		backgroundColors.put(element, color);
	}

	public void setAntialiasing(final boolean antialiasing) {
		this.antialiasing = antialiasing;
	}

	public boolean isAntialiasing() {
		return antialiasing;
	}

	public void setShowExclusiveGatewaySymbol(final boolean show) {
		this.showExclusiveGatewaySymbol = show;
	}

	public boolean getShowExclusiveGatewaySymbol() {
		return showExclusiveGatewaySymbol;
	}

	public void setIgnoreColors(final boolean ignore) {
		this.ignoreColors = ignore;
	}

	public boolean getIgnoreColors() {
		return ignoreColors;
	}

	public Stroke createStrokeSolid(final int width) {
		return new BasicStroke(1.f);
	}

	public Stroke createStrokeDotted(final int width) {
		return new BasicStroke(width,
				BasicStroke.CAP_SQUARE,
				BasicStroke.JOIN_MITER,
				1.f,
				new float[] { 1.f, 6.f },
				0);
	}

	public Stroke createStrokeDashed(final int width) {
		return new BasicStroke(width,
				BasicStroke.CAP_SQUARE,
				BasicStroke.JOIN_MITER,
				1.f,
				new float[] { 3.f, 6.f },
				0);
	}

	public Stroke createStrokeDashedDotted(final int width) {
		return new BasicStroke(width,
				BasicStroke.CAP_SQUARE,
				BasicStroke.JOIN_MITER,
				1.f,
				new float[] { 8.f, 5.f, 1.f, 5.f },
				0);
	}

}
