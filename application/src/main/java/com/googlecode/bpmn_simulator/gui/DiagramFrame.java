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
package com.googlecode.bpmn_simulator.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import com.googlecode.bpmn_simulator.bpmn.di.BPMNDiagram;
import com.googlecode.bpmn_simulator.bpmn.model.collaboration.Collaboration;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.AbstractFlowElement;
import com.googlecode.bpmn_simulator.bpmn.model.core.foundation.BaseElement;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.Process;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.Subprocess;
import com.googlecode.bpmn_simulator.gui.dialogs.ExceptionDialog;

@SuppressWarnings("serial")
public class DiagramFrame
		extends JInternalFrame {

	private static final Icon ICON_PROCESS = loadFrameIcon("process.png"); //$NON-NLS-1$
	private static final Icon ICON_COLLABORATION = loadFrameIcon("collaboration.png"); //$NON-NLS-1$

	private final BPMNDiagram diagram;

	public DiagramFrame(final BPMNDiagram diagram) {
		super(diagram.getTitle(), true, false, true);
		this.diagram = diagram;

		final JScrollPane scrollPane = new JScrollPane(diagram.getPlane());
		scrollPane.getViewport().setBackground(Color.WHITE);
		setContentPane(scrollPane);

		updateFrameIcon();
	}

	private static Icon loadFrameIcon(final String filename) {
		final URL url = DiagramFrame.class.getResource(filename);
		if (url != null) {
			return new ImageIcon(url);
		}
		return null;
	}

	protected void updateFrameIcon() {
		Icon icon = null;
		final BaseElement plane = diagram.getPlane();
		if ((plane instanceof Process)
				||(plane instanceof Subprocess)) {
			icon = ICON_PROCESS;
		} else if (plane instanceof Collaboration) {
			icon = ICON_COLLABORATION;
		}
		assert icon != null;
		setFrameIcon(icon);
	}

	public void showFrame() {
		setLocation(0, 0);
		setVisible(true);
		pack();
	}

	private RenderedImage createImage() {
		final AbstractFlowElement plane = diagram.getPlane();
		final int width = plane.getWidth();
		final int height = plane.getHeight();
		final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		final Graphics graphics = image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, width, height);
		plane.paintAll(graphics);
		return image;
	}

	public void exportImage(final File file, final String formatName) {
		if (file.exists()
				&& JOptionPane.showConfirmDialog(this,
						MessageFormat.format("File ''{0}'' already exists.\nDo you want to overwrite this file?", file.getName()),
						"File exists",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
			return;
		}
		try {
			ImageIO.write(createImage(), formatName, file);
		} catch (IOException e) {
			ExceptionDialog.showExceptionDialog(this, e);
		}
	}

}
