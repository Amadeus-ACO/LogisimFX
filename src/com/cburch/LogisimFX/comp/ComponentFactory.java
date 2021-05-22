/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.comp;

import java.awt.Color;

import com.cburch.LogisimFX.data.*;
import com.cburch.LogisimFX.util.StringGetter;
import com.cburch.LogisimFX.LogisimVersion;

import javafx.beans.binding.StringBinding;
import javafx.scene.image.ImageView;

/**
 * Represents a category of components that appear in a circuit. This class
 * and <code>Component</code> share the same sort of relationship as the
 * relation between <em>classes</em> and <em>instances</em> in Java. Normally,
 * there is only one ComponentFactory created for any particular category.
 */
public interface ComponentFactory extends AttributeDefaultProvider {

	Object SHOULD_SNAP = new Object();
	Object TOOL_TIP = new Object();
	Object FACING_ATTRIBUTE_KEY = new Object();
	
	String getName();
	StringBinding getDisplayName();
	ImageView getIcon();
	StringBinding getDisplayGetter();
	Component createComponent(Location loc, AttributeSet attrs);
	Bounds getOffsetBounds(AttributeSet attrs);
	AttributeSet createAttributeSet();
	boolean isAllDefaultValues(AttributeSet attrs, LogisimVersion ver);
	Object getDefaultAttributeValue(Attribute<?> attr, LogisimVersion ver);
	void drawGhost(ComponentDrawContext context, Color color,
                          int x, int y, AttributeSet attrs);
	void paintIcon(ComponentDrawContext context,
                          int x, int y, AttributeSet attrs);
	/**
	 * Retrieves special-purpose features for this factory. This technique
	 * allows for future Logisim versions to add new features
	 * for components without requiring changes to existing components.
	 * It also removes the necessity for the Component API to directly
	 * declare methods for each individual feature.
	 * In most cases, the <code>key</code> is a <code>Class</code> object
	 * corresponding to an interface, and the method should return an
	 * implementation of that interface if it supports the feature.
	 * 
	 * As of this writing, possible values for <code>key</code> include:
	 * <code>TOOL_TIP</code> (return a <code>String</code>) and
	 * <code>SHOULD_SNAP</code> (return a <code>Boolean</code>).
	 * 
	 * @param key  an object representing a feature.
	 * @return an object representing information about how the component
	 *    supports the feature, or <code>null</code> if it does not support
	 *    the feature.
	 */
	 Object getFeature(Object key, AttributeSet attrs);
}
