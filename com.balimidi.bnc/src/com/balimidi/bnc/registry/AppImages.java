package com.balimidi.bnc.registry;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.balimidi.bnc.constant.UISymbols;

/**
 * @author balimiv
 *
 */
public final class AppImages {
	private static final String			ID			= "com.practise.bnc.image-registry";
	private static final Bundle			BUNDLE		= FrameworkUtil.getBundle(AppImages.class);
	private static final ImageRegistry	REGISTRY	= new ImageRegistry();

	private AppImages() {
		// Singelton
	}

	public static void register(final IEclipseContext context) {
		context.set(ID, REGISTRY);

		REGISTRY.put(UISymbols.IMG_RELOAD, ImageDescriptor.createFromURL(BUNDLE.getEntry(UISymbols.IMG_RELOAD)));
		REGISTRY.put(UISymbols.IMG_CHECK, ImageDescriptor.createFromURL(BUNDLE.getEntry(UISymbols.IMG_CHECK)));
		REGISTRY.put(UISymbols.IMG_BULL, ImageDescriptor.createFromURL(BUNDLE.getEntry(UISymbols.IMG_BULL)));
		REGISTRY.put(UISymbols.IMG_COW, ImageDescriptor.createFromURL(BUNDLE.getEntry(UISymbols.IMG_COW)));
	}

	public static Image get(final String key) {
		return REGISTRY.get(key);
	}
}
