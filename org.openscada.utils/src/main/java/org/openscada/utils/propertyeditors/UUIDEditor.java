package org.openscada.utils.propertyeditors;


import java.beans.PropertyEditorSupport;
import java.util.UUID;

public class UUIDEditor extends PropertyEditorSupport {
	@Override
	public String getAsText() {
		return getValue().toString();
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		setValue(UUID.fromString(text));
	}
}
