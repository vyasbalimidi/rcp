package com.balimidi.bnc.parts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.balimidi.bnc.constant.UISymbols;
import com.balimidi.bnc.model.Trail;
import com.balimidi.bnc.part.provider.GameLabelProvider;
import com.balimidi.bnc.registry.AppImages;

/**
 * @author balimiv
 *
 */
public final class GamePart {
	private static final FontRegistry	REGISTRY;
	private static final Font			BOLD;

	private static final String[]		ITEMS;
	private static final int			COMBO_WIDTH;

	static {
		REGISTRY = new FontRegistry();
		BOLD = REGISTRY.getBold("Arial");

		ITEMS = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		COMBO_WIDTH = 25;
	}

	private final List<Button>	buttons;

	private final List<String>	columns;
	private final List<Integer>	secretNumber;
	private final List<Combo>	combos;
	private final List<Trail>	trails;

	private int					digits;
	private int					trailCounter;

	private Composite			container;
	private Button				checkButton;
	private Label				validationLabel;
	private GridData			validationGD;
	private TableViewer			viewer;

	public GamePart() {
		buttons = new ArrayList<>();
		columns = new ArrayList<>();
		secretNumber = new ArrayList<>();
		combos = new ArrayList<>();
		trails = new ArrayList<>();

		digits = 0;
		trailCounter = 1;
	}

	@PostConstruct
	public void createPartControl(final IEclipseContext context, final Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		AppImages.register(context);
		createDigitSelectionArea(parent);
	}

	private void createDigitSelectionArea(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false));

		final Label label = new Label(composite, SWT.NONE);
		label.setText("Select number of digits to guess:");

		for (int index = 3; index < 6; index++) {
			final Button button = new Button(composite, SWT.RADIO);

			buttons.add(button);
			button.setText(String.valueOf(index));
			button.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					final Button button = (Button) event.widget;

					if (button.getSelection()) {
						recreateUI(parent, button);
					}
				}
			});
		}
	}

	private void recreateUI(final Composite parent, final Button button) {
		digits = Integer.parseInt(button.getText());

		if (container != null) {
			container.dispose();
		}

		combos.clear();
		trails.clear();

		createContainer(parent);
		fillContainer();
		newGame();

		parent.layout();
	}

	private void createContainer(final Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	private void fillContainer() {
		// ------------- INPUT -------------
		final Composite buttonComposite = new Composite(container, SWT.NONE);
		final GridLayout layout = new GridLayout(digits + 2, false);
		layout.horizontalSpacing = 15;
		layout.marginTop = COMBO_WIDTH;
		layout.marginBottom = COMBO_WIDTH;

		buttonComposite.setLayout(layout);
		buttonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false));

		// Reload button
		final Button reloadButton = new Button(buttonComposite, SWT.PUSH);
		reloadButton.setImage(AppImages.get(UISymbols.IMG_RELOAD));
		reloadButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent exp) {
				reloadClicked();
			}
		});

		// Digits combo
		int comboIntialValue = 0;
		for (int i = 0; i < digits; i++) {
			final Combo combo = new Combo(buttonComposite, SWT.BORDER | SWT.READ_ONLY);

			combo.setLayoutData(new GridData(COMBO_WIDTH, SWT.DEFAULT));
			combo.setItems(ITEMS);
			combo.setText(String.valueOf(comboIntialValue++));

			combos.add(combo);
		}

		// Check button
		checkButton = new Button(buttonComposite, SWT.PUSH);
		checkButton.setImage(AppImages.get(UISymbols.IMG_CHECK));
		checkButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent exp) {
				checkClicked();
			}
		});

		// ------------- VALIDATION -------------
		validationLabel = new Label(container, SWT.NONE);
		validationGD = new GridData(SWT.CENTER, SWT.FILL, true, false);

		validationLabel.setText("Duplicates are not allowed!");
		validationLabel.setLayoutData(validationGD);
		validationLabel.setFont(BOLD);
		validationLabel.setForeground(UISymbols.COLOR_DARK_RED);
		showValidationLabel(false);

		// ------------- TABLE -------------
		viewer = new TableViewer(container, SWT.V_SCROLL | SWT.BORDER);
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		identifyTableColumns();
		createTableColumns(table);

		viewer.setLabelProvider(new GameLabelProvider(columns.size()));
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setComparator(new ViewerComparator() {

			@Override
			public int compare(final Viewer viewer, final Object e1, final Object e2) {
				final Trail t1 = (Trail) e1;
				final Trail t2 = (Trail) e2;

				return t2.getAttempt() - t1.getAttempt();
			}
		});
	}

	private void showValidationLabel(final boolean show) {
		validationGD.exclude = !show;
		validationLabel.setVisible(show);
		validationLabel.getParent().layout();
	}

	private void reloadClicked() {
		if (!trails.isEmpty() && trails.get(trails.size() - 1).getBulls() != digits) {
			final Display display = Display.getDefault();
			final Shell shell = display.getActiveShell();

			if (MessageDialog.openConfirm(shell, "Confirm", "Your current game will be lost. Are you sure?")) {
				newGame();
			}
		} else {
			newGame();
		}
	}

	private void identifyTableColumns() {
		// Clear old columns
		columns.clear();

		// Create new ones with digits
		columns.add("Trail");
		for (int index = 0; index < digits; index++) {
			columns.add("Digit - " + (index + 1));
		}
		columns.add("Bulls");
		columns.add("Cows");
	}

	private void createTableColumns(final Table table) {
		for (final String colName : columns) {
			final TableColumn column = new TableColumn(table, SWT.NONE);
			column.setResizable(true);
			column.setMoveable(true);

			if ("Bulls".equals(colName)) {
				column.setImage(AppImages.get(UISymbols.IMG_BULL));
				column.setWidth(30);
			} else if ("Cows".equals(colName)) {
				column.setImage(AppImages.get(UISymbols.IMG_COW));
				column.setWidth(30);
			} else {
				column.setText(colName);
				column.setWidth(60);
			}
		}
	}

	private void newGame() {
		checkButton.setEnabled(true);
		generateNewSecretNumber();

		trails.clear();
		trailCounter = 1;
		viewer.setInput(trails.toArray());

		enableRadio(true);
	}

	private void enableRadio(final boolean enable) {
		for (final Button button : buttons) {
			button.setEnabled(enable);
		}
	}

	private void generateNewSecretNumber() {
		secretNumber.clear();
		final Random random = new Random();

		while (secretNumber.size() != digits) {
			final int digit = random.nextInt(10);

			if (!secretNumber.contains(digit)) {
				secretNumber.add(digit);
			}
		}

		validationLabel.setToolTipText("Secret number is: " + secretNumber.toString());
	}

	private void checkClicked() {
		final Trail trail = new Trail();
		final Set<String> valid = new HashSet<>();
		int bulls = 0;
		int cows = 0;

		for (int index = 0; index < digits; index++) {
			final Combo combo = combos.get(index);
			final Integer secretDigit = secretNumber.get(index);

			final String text = combo.getText();
			final int digit = Integer.parseInt(text);

			if (secretDigit == digit) {
				bulls++;
			} else if (secretNumber.contains(digit)) {
				cows++;
			}

			valid.add(text);
			trail.addDigit(digit);
		}

		final boolean isValid = valid.size() == digits;
		showValidationLabel(!isValid);

		if (isValid) {
			trail.setAttempt(trailCounter++);
			trail.setBulls(bulls);
			trail.setCows(cows);

			trails.add(trail);
			viewer.setInput(trails.toArray());
		}

		if (bulls == digits) {
			final Display display = Display.getDefault();
			final Shell shell = display.getActiveShell();

			checkButton.setEnabled(false);
			MessageDialog.openInformation(shell, "Congratulation !!!", "You found the number");
		}

		enableRadio(bulls == digits);
	}
}
