/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.draw.model;

import com.cburch.LogisimFX.data.Attribute;
import com.cburch.LogisimFX.data.AttributeSet;
import com.cburch.LogisimFX.data.Bounds;
import com.cburch.LogisimFX.data.Location;
import com.cburch.LogisimFX.newgui.MainFrame.Canvas.Graphics;

import java.util.List;

public interface CanvasObject {

	CanvasObject clone();
	String getDisplayName();
	AttributeSet getAttributeSet();
	<V> V getValue(Attribute<V> attr);
	Bounds getBounds();
	boolean matches(CanvasObject other);
	int matchesHashCode();
	boolean contains(Location loc, boolean assumeFilled);
	boolean overlaps(CanvasObject other);
	List<Handle> getHandles(HandleGesture gesture);
	boolean canRemove();
	boolean canMoveHandle(Handle handle);
	Handle canInsertHandle(Location desired);
	Handle canDeleteHandle(Location desired);
	void paint(Graphics g, HandleGesture gesture);
	
	Handle moveHandle(HandleGesture gesture);
	void insertHandle(Handle desired, Handle previous);
	Handle deleteHandle(Handle handle);
	void translate(int dx, int dy);
	<V> void setValue(Attribute<V> attr, V value);

}
