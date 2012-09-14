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
package gui;

import gui.preferences.Config;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public final class BPMNSimulatorApplication {

	public static final String NOTICE =
			"Copyright (C) 2012 Stefan Schweitzer\n" //$NON-NLS-1$
			+ "\n"  //$NON-NLS-1$
			+ "This software was created by Stefan Schweitzer as a student's project at\n" //$NON-NLS-1$
			+ "Fachhochschule Kaiserslautern (University of Applied Sciences).\n" //$NON-NLS-1$
			+ "Supervisor: Professor Dr. Thomas Allweyer. For more information please see\n" //$NON-NLS-1$
			+ "http://www.fh-kl.de/~allweyer\n" //$NON-NLS-1$
			+ "\n" //$NON-NLS-1$
			+ "Licensed under the Apache License, Version 2.0 (the \"License\");\n" //$NON-NLS-1$
			+ "you may not use this Software except in compliance with the License.\n" //$NON-NLS-1$
			+ "You may obtain a copy of the License at\n" //$NON-NLS-1$
			+ "\n" //$NON-NLS-1$
			+ "       http://www.apache.org/licenses/LICENSE-2.0\n" //$NON-NLS-1$
			+ "\n" //$NON-NLS-1$
			+ "Unless required by applicable law or agreed to in writing, software\n" //$NON-NLS-1$
			+ "distributed under the License is distributed on an \"AS IS\" BASIS,\n" //$NON-NLS-1$
			+ "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" //$NON-NLS-1$
			+ "See the License for the specific language governing permissions and\n" //$NON-NLS-1$
			+ "limitations under the License."; //$NON-NLS-1$

	public static final String NAME = "BPMN Simulator";  //$NON-NLS-1$
	public static final String URL = "http://code.google.com/p/bpmn-simulator/"; //$NON-NLS-1$

	private BPMNSimulatorApplication() {
		super();
	}

	public static String getVersion() {
		return BPMNSimulatorApplication.class.getPackage().getImplementationVersion();		
	}

	private static void initLookAndFeel() {
		 try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		//JFrame.setDefaultLookAndFeelDecorated(true);
	}

	public static void main(final String[] args) {

		Config.getInstance().load();

		initLookAndFeel();

		new BPMNSimulatorFrame();
	}

}
