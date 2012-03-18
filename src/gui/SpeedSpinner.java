package gui;
import java.awt.Dimension;
import java.util.Arrays;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;


public class SpeedSpinner extends JSpinner {

	private static final long serialVersionUID = 1L;

	enum Speed {
		HALF("x\u00BD", 0.5f),
		NORMAL("x1", 1.0f),
		DOUBLE("x2", 2.0f),
		TRIPLE("x3", 3.0f),
		QUADUPLE("x4", 4.0f),
		QUINTUPLE("x5", 5.0f);

		private final String name;
		private final float factor;

		private Speed(final String name, final float factor) {
			this.name = name;
			this.factor = factor;
		}

		@Override
		public String toString() {
			return name;
		}

		public final float getFactor() {
			return factor;
		}

	}

	public SpeedSpinner() {
		super(new SpinnerListModel(Arrays.asList(Speed.values())));
		setValue(Speed.NORMAL);
		((ListEditor)getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);
	}

	public float getSpeedFactor() {
		return ((Speed)getValue()).getFactor();
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension preferredSize = super.getPreferredSize(); 
		preferredSize.width = 50;
		preferredSize.height = 24;
		return preferredSize;
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

}
