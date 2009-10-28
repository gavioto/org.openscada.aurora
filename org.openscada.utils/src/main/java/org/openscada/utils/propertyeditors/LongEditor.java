package org.openscada.utils.propertyeditors;


public class LongEditor extends NumberEditor {
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		setValue(Long.valueOf(text));
	}
}
