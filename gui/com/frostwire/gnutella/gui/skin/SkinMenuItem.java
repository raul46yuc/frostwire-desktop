package com.frostwire.gnutella.gui.skin;

import javax.swing.Action;
import javax.swing.JMenuItem;

public class SkinMenuItem extends JMenuItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4442214505668973505L;

	public SkinMenuItem(String text) {
		super(text);
    }
	
	public SkinMenuItem(Action a) {
		super(a);
	}
}