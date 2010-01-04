package org.openscada.utils.toggle.internal;

import java.io.Serializable;

public class ToggleInfo implements Serializable {
	
    private static final long serialVersionUID = -5949481833360048424L;

    private final int interval;
	
	private boolean on = false;
	
	public ToggleInfo(int interval) {
		this.interval = interval;
	}
	
	public int getInterval() {
		return interval;
	}

	public boolean toggle() {
		this.on = !this.on;
		return !on;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + interval;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ToggleInfo other = (ToggleInfo) obj;
		if (interval != other.interval)
			return false;
		return true;
	}
}
