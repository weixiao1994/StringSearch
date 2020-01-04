package per.wei.stringsearch;

import java.util.EventObject;

public class ProgressValueEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5705533933369395380L;
	private int value;

	public ProgressValueEvent(Object source, int value) {
	        super(source);
	        this.value = value;
	    }

	public void setValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}
