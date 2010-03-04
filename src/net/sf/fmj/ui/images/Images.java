package net.sf.fmj.ui.images;

import java.util.HashMap;

import javax.swing.ImageIcon;

/**
 * 
 * @author Warren Bloomer
 * 
 */
public class Images {

	public static final String SLIDER_THUMB_HORIZ = "slider_thumb_horiz.png";
	public static final String SLIDER_THUMB_VERT = "slider_thumb_vert.png";

	public static final String MEDIA_PLAY = "Play24.gif";
	public static final String MEDIA_STOP = "Stop24.gif";
	public static final String MEDIA_PAUSE = "Pause24.gif";
	public static final String MEDIA_REWIND = "Rewind24.gif";
	public static final String MEDIA_FASTFORWARD = "FastForward24.gif";
	public static final String MEDIA_STEPBACK = "StepBack24.gif";
	public static final String MEDIA_STEPFORWARD = "StepForward24.gif";

	public static ImageIcon get(String name) {
		return singleton.doGet(name);
	}

	public static void flush() {
		singleton.doFlush();
	}

	private static final Images singleton = new Images();

	private static final String basePath = "/net/sf/fmj/ui/images/";

	private HashMap images = new HashMap();

	private void doFlush() {
		images.clear();
	}

	private ImageIcon doGet(String imageName) {
		ImageIcon icon = (ImageIcon) images.get(imageName);
		if (icon == null) {

			icon = new ImageIcon(getClass().getResource(basePath + imageName));
			if (icon != null) {
				images.put(imageName, icon);
			}
		}

		return icon;
	}

}
