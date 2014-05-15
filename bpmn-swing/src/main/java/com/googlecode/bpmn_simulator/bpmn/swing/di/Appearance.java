/*
 * Copyright (C) 2014 Stefan Schweitzer
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
package com.googlecode.bpmn_simulator.bpmn.swing.di;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Stroke;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class Appearance {

	public static final String IMAGE_BUSSINESRULE = "businessrule.png"; //$NON-NLS-1$
	public static final String IMAGE_COLLAPSED = "collapsed.png"; //$NON-NLS-1$
	public static final String IMAGE_COLLECTION = "collection.png"; //$NON-NLS-1$
	public static final String IMAGE_EXCEPTION = "exception.png"; //$NON-NLS-1$
	public static final String IMAGE_LOOP = "loop.png"; //$NON-NLS-1$
	public static final String IMAGE_MANUAL = "manual.png"; //$NON-NLS-1$
	public static final String IMAGE_PARALLEL = "parallel.png"; //$NON-NLS-1$
	public static final String IMAGE_RECEIVE = "receive.png"; //$NON-NLS-1$
	public static final String IMAGE_SCRIPT = "script.png"; //$NON-NLS-1$
	public static final String IMAGE_SEND = "send.png"; //$NON-NLS-1$
	public static final String IMAGE_SEQUENTIAL = "sequential.png"; //$NON-NLS-1$
	public static final String IMAGE_SERVICE = "service.png"; //$NON-NLS-1$
	public static final String IMAGE_TIMER = "timer.png"; //$NON-NLS-1$
	public static final String IMAGE_USER = "user.png"; //$NON-NLS-1$
	public static final String IMAGE_TERMINATE = "terminate.png"; //$NON-NLS-1$
	public static final String IMAGE_LINK = "link.png"; //$NON-NLS-1$
	public static final String IMAGE_LINK_INVERSE = "link_inverse.png"; //$NON-NLS-1$
	public static final String IMAGE_MESSAGE = "send.png"; //$NON-NLS-1$
	public static final String IMAGE_MESSAGE_INVERSE = "receive.png"; //$NON-NLS-1$
	public static final String IMAGE_SIGNAL = "signal.png"; //$NON-NLS-1$
	public static final String IMAGE_SIGNAL_INVERSE = "signal_inverse.png"; //$NON-NLS-1$
	public static final String IMAGE_ERROR = "error.png"; //$NON-NLS-1$
	public static final String IMAGE_ERROR_INVERSE = "error_inverse.png"; //$NON-NLS-1$
	public static final String IMAGE_CONDITIONAL = "conditional.png"; //$NON-NLS-1$

	private static final String ICONPATH = "com/googlecode/bpmn_simulator/bpmn/icons/"; //$NON-NLS-1$

	private static final float[] DASH_DOTTED = new float[] {
			1.f,
			6.f,
			};

	private static final float[] DASH_DASHED = new float[] {
			3.f,
			6.f,
			};

	private static final float[] DASH_DASHEDDOTTED = new float[] {
			8.f,
			5.f,
			1.f,
			5.f,
			};

	private static final int ARC_SIZE = 20;

	private static Appearance instance;

	private final Map<String, Image> images = new IdentityHashMap<String, Image>();

	private final Map<Class<?>, ElementAppearance> elements = new HashMap<Class<?>, ElementAppearance>();

	private boolean ignoreExplicitColors;

	private boolean showExclusiveGatewaySymbol = true;

	private Appearance() {
		super();
		loadImages();
	}

	public static Appearance getDefault() {
		if (instance == null) {
			synchronized (Appearance.class) {
				if (instance == null) {
					instance = new Appearance();
				}
			}
		}
		return instance;
	}

	private static Image loadImageFromRessource(final String name) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) {
			cl = ClassLoader.getSystemClassLoader();
		}
		if (cl != null) {
			final URL url = cl.getResource(ICONPATH + name);
			if (url != null) {
				try {
					return ImageIO.read(url);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			assert false;
			
		}
		return null;
	}

	private void loadImage(final String name) {
		images.put(name, loadImageFromRessource(name));
	}

	public final void loadImages() {
		loadImage(IMAGE_BUSSINESRULE);
		loadImage(IMAGE_COLLAPSED);
		loadImage(IMAGE_COLLECTION);
		loadImage(IMAGE_EXCEPTION);
		loadImage(IMAGE_LOOP);
		loadImage(IMAGE_MANUAL);
		loadImage(IMAGE_PARALLEL);
		loadImage(IMAGE_RECEIVE);
		loadImage(IMAGE_SCRIPT);
		loadImage(IMAGE_SEND);
		loadImage(IMAGE_SEQUENTIAL);
		loadImage(IMAGE_SERVICE);
		loadImage(IMAGE_TIMER);
		loadImage(IMAGE_USER);
		loadImage(IMAGE_TERMINATE);
		loadImage(IMAGE_LINK);
		loadImage(IMAGE_LINK_INVERSE);
		loadImage(IMAGE_SIGNAL);
		loadImage(IMAGE_SIGNAL_INVERSE);
		loadImage(IMAGE_ERROR);
		loadImage(IMAGE_ERROR_INVERSE);
		loadImage(IMAGE_CONDITIONAL);
	}

	public Image getImage(final String name) {
		assert images.containsKey(name);
		return images.get(name);
	}

	public ElementAppearance getForElement(final Class<?> elementClass) {
		ElementAppearance elementAppearance = elements.get(elementClass);
		if (elementAppearance == null) {
			elementAppearance = new ElementAppearance();
			elements.put(elementClass, elementAppearance);
		}
		return elementAppearance;
	}

	public int getArcSize() {
		return ARC_SIZE;
	}

	public void setShowExclusiveGatewaySymbol(final boolean show) {
		this.showExclusiveGatewaySymbol = show;
	}

	public boolean getShowExclusiveGatewaySymbol() {
		return showExclusiveGatewaySymbol;
	}

	public void setIgnoreExplicitColors(final boolean ignore) {
		this.ignoreExplicitColors = ignore;
	}

	public boolean getIgnoreExplicitColors() {
		return ignoreExplicitColors;
	}

	public Stroke createStrokeSolid(final int width) {
		return new BasicStroke(width);
	}

	public Stroke createStrokeDotted(final int width) {
		return new BasicStroke(width,
				BasicStroke.CAP_SQUARE,
				BasicStroke.JOIN_MITER,
				1.f,
				DASH_DOTTED,
				0);
	}

	public Stroke createStrokeDashed(final int width) {
		return new BasicStroke(width,
				BasicStroke.CAP_SQUARE,
				BasicStroke.JOIN_MITER,
				1.f,
				DASH_DASHED,
				0);
	}

	public Stroke createStrokeDashedDotted(final int width) {
		return new BasicStroke(width,
				BasicStroke.CAP_SQUARE,
				BasicStroke.JOIN_MITER,
				1.f,
				DASH_DASHEDDOTTED,
				0);
	}

	public static class ElementAppearance {

		private static final Color DEFAULT_BACKGROUNDCOLOR = Color.WHITE;
		private static final Color DEFAULT_FOREGROUNDCOLOR = Color.BLACK;

		private Color background = DEFAULT_BACKGROUNDCOLOR;
		private Color foreground = DEFAULT_FOREGROUNDCOLOR;

		public void setBackground(final Color color) {
			background = color;
		}

		public Color getBackground() {
			return background;
		}

		public void setForeground(final Color color) {
			foreground = color;
		}

		public Color getForeground() {
			return foreground;
		}

	}

}