package com.balimidi.bnc.constant;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author balimiv
 *
 */
public final class UISymbols {
	private static final Display	DISPLAY			= Display.getCurrent();
	public static final Color		COLOR_RED		= DISPLAY.getSystemColor(SWT.COLOR_RED);
	public static final Color		COLOR_DARK_RED	= DISPLAY.getSystemColor(SWT.COLOR_DARK_RED);

	public static final String		IMG_RELOAD		= "icons/reload.png";
	public static final String		IMG_CHECK		= "icons/check.png";
	public static final String		IMG_BULL		= "icons/bull.png";
	public static final String		IMG_COW			= "icons/cow.png";

	private UISymbols() {
		// Singleton
	}
}
