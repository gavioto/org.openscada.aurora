package org.openscada.utils.propertyeditors;


public class ByteEditor extends NumberEditor {
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		setValue(Byte.valueOf(text));
	}
}
