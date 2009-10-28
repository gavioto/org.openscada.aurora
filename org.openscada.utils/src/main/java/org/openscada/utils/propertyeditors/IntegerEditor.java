package org.openscada.utils.propertyeditors;


public class IntegerEditor extends NumberEditor {
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		setValue(Integer.valueOf(text));
	}
}
