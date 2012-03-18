package bpmn.token;

import java.awt.Color;

public class ColorGenerator {

	private Color[] colors = new Color[] {
			new Color(0xff5c26),
			new Color(0x2626ff),
			new Color(0xffff26),
			new Color(0xc926ff),
			new Color(0x5cff26),
			new Color(0x26ff93),
			new Color(0xff2692),
			new Color(0x26c9ff)
		};

	int index = 0;

	public ColorGenerator() {
		super();
	}

	public Color next() {
		if (index >= colors.length) {
			index = 0;
		}
		return colors[index++];
	}

}
