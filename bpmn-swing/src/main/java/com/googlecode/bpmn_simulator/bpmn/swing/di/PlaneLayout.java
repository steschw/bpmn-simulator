package com.googlecode.bpmn_simulator.bpmn.swing.di;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JComponent;

class PlaneLayout
		implements LayoutManager {

	private static final Dimension MIN_SIZE = new Dimension(100, 100);

	private static final int PADDING = 10;

	private final JComponent plane;

	public PlaneLayout(final JComponent plane) {
		super();
		this.plane = plane;
	}

	@Override
	public void addLayoutComponent(final String name, final Component comp) {
	}

	@Override
	public void removeLayoutComponent(final Component comp) {
	}

	@Override
	public Dimension preferredLayoutSize(final Container parent) {
		int i = 0;
		double width = 0.;
		double height = 0.;
		for (final Component component : parent.getComponents()) {
			if (component != plane) {
				final Rectangle bounds = component.getBounds();
				width = Math.max(width, bounds.getMaxX());
				height = Math.max(height, bounds.getMaxY());
				++i;
			}
		}
		if (i > 0) {
			return new Dimension((int) width + PADDING, (int) height + PADDING);
		} else {
			return minimumLayoutSize(parent);
		}
	}

	@Override
	public Dimension minimumLayoutSize(final Container parent) {
		return MIN_SIZE;
	}

	@Override
	public void layoutContainer(final Container parent) {
		Dimension size = parent.getSize();
		System.out.println(size);
		plane.setSize(size);
	}
	
}