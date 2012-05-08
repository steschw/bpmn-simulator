package bpmn.element;

import java.awt.Color;
import java.net.URL;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class VisualConfig {

	private static final String ICONPATH = "../icons/";

	public static final String ICON_BUSSINESRULE = "businessrule.png";
	public static final String ICON_COLLAPSED = "collapsed.png";
	public static final String ICON_COLLECTION = "collection.png";
	public static final String ICON_EXCEPTION = "exception.png";
	public static final String ICON_LOOP = "loop.png";
	public static final String ICON_MANUAL = "manual.png";
	public static final String ICON_PARALLEL = "parallel.png";
	public static final String ICON_RECEIVE = "receive.png";
	public static final String ICON_SCRIPT = "script.png";
	public static final String ICON_SEND = "send.png";
	public static final String ICON_SEQUENTIAL = "sequential.png";
	public static final String ICON_SERVICE = "service.png";
	public static final String ICON_TIMER = "timer.png";
	public static final String ICON_USER = "user.png";

	private final Map<String, Icon> icons = new IdentityHashMap<String, Icon>();

	public enum Element {
		GATEWAY,
		TASK,
		PROCESS,
		EVENT_START,
		EVENT_END,
		EVENT_INTERMEDIATE
	}

	private static final Color DEFAULT_BACKGROUNDCOLOR = Color.WHITE;

	private final Map<Element, Color> backgroundColors = new EnumMap<Element, Color>(Element.class);

	private boolean antialiasing = true;
	private boolean showExclusiveGatewaySymbol = true;

	public static VisualConfig createDefault() {
		VisualConfig environment = new VisualConfig();
		environment.loadIcons();
		return environment;
	}

	protected static Icon loadIconFromRessource(final String name) {
		final URL url = VisualConfig.class.getResource(ICONPATH + name);
		assert url != null;
		final Icon icon = new ImageIcon(url);
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
	}

	public Icon getIcon(final String name) {
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

}
