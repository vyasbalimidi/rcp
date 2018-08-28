package com.balimidi.bnc.part.provider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.balimidi.bnc.constant.UISymbols;
import com.balimidi.bnc.model.Trail;
import com.balimidi.bnc.registry.AppImages;

/**
 * @author balimiv
 *
 */
public final class GameLabelProvider extends LabelProvider implements ITableLabelProvider {
	private final int numOfColumns;

	public GameLabelProvider(final int numOfColumns) {
		this.numOfColumns = numOfColumns;
	}

	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		final Trail trail = (Trail) element;

		if (columnIndex == 0) {
			if (trail.getBulls() > 0) {
				return AppImages.get(UISymbols.IMG_BULL);
			}
			if (trail.getCows() > 0) {
				return AppImages.get(UISymbols.IMG_COW);
			}
		}

		return null;
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		final Trail trail = (Trail) element;
		Integer cell = null;

		if (columnIndex == 0) {
			cell = trail.getAttempt();
		} else if (columnIndex <= numOfColumns - 3) {
			cell = trail.getDigits(columnIndex);
		} else if (columnIndex == numOfColumns - 2) {
			cell = trail.getBulls();
		} else if (columnIndex == numOfColumns - 1) {
			cell = trail.getCows();
		}

		return cell == null ? null : cell.toString();
	}
}
