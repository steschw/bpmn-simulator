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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Stroke;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import com.google.code.bpmn_simulator.bpmn.model.BPMNModel;
import com.google.code.bpmn_simulator.bpmn.model.core.foundation.Documentation;
import com.google.code.bpmn_simulator.bpmn.model.process.activities.AbstractContainerActivity;
import com.google.code.bpmn_simulator.framework.element.visual.GraphicsLayer;
import com.google.code.bpmn_simulator.framework.element.visual.geometry.Bounds;
import com.google.code.bpmn_simulator.bpmn.Messages;


@SuppressWarnings("serial")
public abstract class AbstractFlowElement
		extends JComponent
		implements FlowElement {

	protected static final int MARGIN = 10;

	protected static final int DEFAULT_INNER_MARGIN = 4;
	protected static final int NO_INNER_BORDER = 0;

	private static Visualization defaultVisualization = new Visualization();

	private static Behavior defaultBehavior = new Behavior();

	private AbstractContainerActivity parentActivity;

	private String id;

	private Label label;

	private Documentation documentation;

	private Color background;

	private boolean exception;

	private Visualization visualization = defaultVisualization;

	private Behavior behavior = defaultBehavior;

	public AbstractFlowElement(final String id, final String name) {
		super();
		setId(id);
		setName(name);
		setFocusable(false);
		setDoubleBuffered(true);
		setToolTipText(null);
	}

	public static void setDefaultVisualization(final Visualization visualization) {
		defaultVisualization = visualization;
	}

	public static Visualization getDefaultVisualization() {
		return defaultVisualization;
	}

	public static final void setDefaultBehavior(final Behavior behavior) {
		defaultBehavior = behavior;
	}

	public static final Behavior getDefaultBehavior() {
		return defaultBehavior;
	}

	@Override
	public String getToolTipText(final MouseEvent event) {
		final StringBuilder tooltipText = new StringBuilder();
		tooltipText.append("<html><table>"); //$NON-NLS-1$
		tooltipText.append("<tr><td><b>"); //$NON-NLS-1$
		tooltipText.append(Messages.getString("baseElement.id")); //$NON-NLS-1$
		tooltipText.append(":</b></td><td>"); //$NON-NLS-1$
		tooltipText.append(getId());
		tooltipText.append("</td></tr>"); //$NON-NLS-1$
		if (hasName()) {
			tooltipText.append("<tr><td><b>"); //$NON-NLS-1$
			tooltipText.append(Messages.getString("flowElement.name")); //$NON-NLS-1$
			tooltipText.append(":</b></td><td>"); //$NON-NLS-1$
			tooltipText.append(getName());
			tooltipText.append("</td></tr>"); //$NON-NLS-1$
		}
		if (hasDocumentation()) {
			tooltipText.append("<tr><td><b>"); //$NON-NLS-1$
			tooltipText.append(Messages.getString("baseElement.documentation")); //$NON-NLS-1$
			tooltipText.append(":</b></td><td>"); //$NON-NLS-1$
			tooltipText.append(getDocumentation().toHtml());
			tooltipText.append("</td></tr>"); //$NON-NLS-1$
		}
		tooltipText.append("</table></html>"); //$NON-NLS-1$
		return tooltipText.toString();
	}

	public Visualization getVisualization() {
		return visualization;
	}

	public Behavior getBehavior() {
		return behavior;
	}

	public final void setId(final String id) {
		this.id = id;
	}

	@Override
	public final String getId() {
		return id;
	}

	@Override
	public void setDocumentation(final Documentation documentation) {
		this.documentation = documentation;
	}

	@Override
	public Documentation getDocumentation() {
		return documentation;
	}

	@Override
	public boolean hasDocumentation() {
		return getDocumentation() != null;
	}

	public boolean hasName() {
		final String name = getName();
		return (name != null) && !name.isEmpty();
	}

	protected void setException(final boolean exception) {
		this.exception = exception;
	}

	protected boolean hasException() {
		return exception;
	}

	public void setContainerActivity(
			final AbstractContainerActivity parentActivity) {
		this.parentActivity = parentActivity;
	}

	public final AbstractContainerActivity getContainerActivity() {
		return parentActivity;
	}

	@Override
	public BPMNModel getModel() {
		return (parentActivity == null) ? null : parentActivity.getModel();
	}

	public abstract String getElementName();

	@Override
	public String getFullName() {
		final StringBuilder fullName = new StringBuilder(getElementName());
		fullName.append(": "); //$NON-NLS-1$
		if (hasName()) {
			fullName.append('"');
			fullName.append(getName());
			fullName.append('"');
		} else {
			fullName.append(getId());
		}
		return fullName.toString();
	}

	public void setInnerBounds(final Bounds bounds) {
		bounds.grow(MARGIN, MARGIN);
		setBounds(bounds);
	}

	public Bounds getInnerBounds() {
		final Bounds bounds = new Bounds(getBounds());
		bounds.grow(-MARGIN, -MARGIN);
		return bounds;
	}

	public Bounds getElementInnerBounds() {
		final Bounds bounds = new Bounds(getBounds());
		return new Bounds(MARGIN, MARGIN,
				bounds.width - (2 * MARGIN), bounds.height - (2 * MARGIN));
	}

	public Bounds getElementOuterBounds() {
		return new Bounds(0, 0, getWidth(), getHeight());
	}

	@Override
	public final void paint(final java.awt.Graphics g) {
		final GraphicsLayer graphics = new GraphicsLayer((Graphics2D)g);

		synchronized (this) {

			graphics.push();

			if (getVisualization().isAntialiasing()) {
				graphics.enableAntialiasing();
			}

			final Paint paint = getBackgroundPaint();
			if (paint != null) {
				graphics.setPaint(paint);
				paintBackground(graphics);
			}

			graphics.setPaint(getForeground());
			graphics.setStroke(getStroke());
			paintElement(graphics);

			paintTokens(graphics);

			if (hasException()) {
				paintException(graphics);
			}

			graphics.pop();
		}

		super.paint(g);
	}

	public void setElementBackground(final Color color) {
		background = color;
	}

	protected Color getElementBackground() {
		return ((background == null) || getVisualization().getIgnoreColors())
				? getElementDefaultBackground()
				: background;
	}

	protected Color getElementDefaultBackground() {
		return null;
	}

	protected Paint getBackgroundPaint() {
		final Color color = getElementBackground();
		if (color != null) {
			final Bounds size = new Bounds(getBounds());
			return new RadialGradientPaint(0.f, 0.f, size.min(),
					new float[] {
							0.f,
							1.f,
							},
					new Color[] {
							Color.WHITE,
							color,
							});
		}
		return null;
	}

	protected int getBorderWidth() {
		return 1;
	}

	protected Stroke getStroke() {
		return getVisualization().createStrokeSolid(getBorderWidth());
	}

	protected void paintBackground(final GraphicsLayer g) {
	}

	protected abstract void paintElement(final GraphicsLayer g);

	protected void paintException(final GraphicsLayer g) {
		g.drawIcon(
				getVisualization().getIcon(Visualization.ICON_EXCEPTION),
				new Point(0, 0));
	}

	protected void paintTokens(final GraphicsLayer g) {
	}

	protected Point getElementCenter() {
		return getInnerBounds().getCenter();
	}

	public Label getElementLabel() {
		return label;
	}

	protected void setElementLabel(final Label label) {
		assert this.label == null;
		this.label = label;
		getParent().add(label, 0);
		updateElementLabelPosition();
	}

	protected Label createElementLabel() {
		final String name = getName();
		Label label = null;
		if ((name != null) && !name.isEmpty()) {
			label = new Label(this, name);
		}
		return label;
	}

	private void initElementLabel() {
		final Label label = createElementLabel();
		if (label != null) {
			setElementLabel(label);
		}
	}

	public void initSubElements() {
		initElementLabel();
	}

	protected void updateElementLabelPosition() {
		getElementLabel().setCenterPosition(getElementCenter());
	}

	protected Dimension calcSizeByInnerComponents() {
		int width = MARGIN;
		int height = MARGIN;
		for (Component component : getComponents()) {
			final java.awt.Rectangle rectangle = component.getBounds();
			if ((int)rectangle.getMaxX() > width) {
				width = (int)rectangle.getMaxX();
			}
			if ((int)rectangle.getMaxY() > height) {
				height = (int)rectangle.getMaxY();
			}
		}
		return new Dimension(MARGIN + width + MARGIN, MARGIN + height + MARGIN);
	}

	public int getInnerBorderMargin() {
		return NO_INNER_BORDER;
	}

	@Override
	public String toString() {
		final StringBuilder string = new StringBuilder('[');
		string.append(this.getClass().toString());
		string.append(", "); //$NON-NLS-1$
		string.append(getId());
		string.append(", "); //$NON-NLS-1$
		string.append(getName());
		string.append(']');
		return string.toString();
	}

}
