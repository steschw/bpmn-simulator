package gui;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class BPMNSimulatorApplication {

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
		initLookAndFeel();
		new BPMNSimulatorFrame();
	}

}
