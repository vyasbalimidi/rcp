package com.balimidi.bnc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balimiv
 *
 */
public final class Trail {
	private final List<Integer>	digits;

	private int					attempt;
	private int					bulls;
	private int					cows;

	public Trail() {
		digits = new ArrayList<>();
	}

	public void addDigit(final Integer digit) {
		digits.add(digit);
	}

	public int getAttempt() {
		return attempt;
	}

	public void setAttempt(final int attempt) {
		this.attempt = attempt;
	}

	public int getBulls() {
		return bulls;
	}

	public void setBulls(final int bulls) {
		this.bulls = bulls;
	}

	public int getCows() {
		return cows;
	}

	public void setCows(final int cows) {
		this.cows = cows;
	}

	public Integer getDigits(final int pos) {
		return digits.get(pos - 1);
	}
}
