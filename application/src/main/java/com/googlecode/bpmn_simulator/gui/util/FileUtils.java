package com.googlecode.bpmn_simulator.gui.util;

import java.awt.Component;
import java.io.File;
import java.text.MessageFormat;

import javax.swing.JOptionPane;

public final class FileUtils {

	private FileUtils() {
	}

	public static boolean isValidDirectory(final String directory) {
		if ((directory != null) && !directory.isEmpty()) {
			final File file = new File(directory);
			return file.isDirectory() && file.exists();
		}
		return false;
	}

	public static boolean canWriteFile(final Component parent, final File file) {
		return !file.exists()
				|| (JOptionPane.showConfirmDialog(parent,
						MessageFormat.format("File ''{0}'' already exists.\nDo you want to overwrite this file?", file.getName()),
						"File exists",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION);
	}

}
