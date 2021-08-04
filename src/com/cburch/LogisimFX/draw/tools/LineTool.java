/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.draw.tools;

import com.cburch.LogisimFX.IconsManager;
import com.cburch.LogisimFX.draw.actions.ModelAddAction;
import com.cburch.LogisimFX.draw.canvas.AppearanceCanvas;
import com.cburch.LogisimFX.draw.canvas.Canvas;
import com.cburch.LogisimFX.draw.model.CanvasModel;
import com.cburch.LogisimFX.draw.model.CanvasObject;
import com.cburch.LogisimFX.draw.shapes.DrawAttr;
import com.cburch.LogisimFX.draw.shapes.LineUtil;
import com.cburch.LogisimFX.draw.shapes.Poly;
import com.cburch.LogisimFX.data.Attribute;
import com.cburch.LogisimFX.data.Location;
import com.cburch.LogisimFX.util.UnmodifiableList;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import java.util.List;

public class LineTool extends AbstractTool {

	private DrawingAttributeSet attrs;
	private boolean active;
	private Location mouseStart;
	private Location mouseEnd;
	private int lastMouseX;
	private int lastMouseY;
	
	public LineTool(DrawingAttributeSet attrs) {
		this.attrs = attrs;
		active = false;
	}
	
	@Override
	public ImageView getIcon() {
		return IconsManager.getIcon("drawline.gif");
	}

	@Override
	public Cursor getCursor(AppearanceCanvas canvas) { return Cursor.CROSSHAIR);
	}
	
	@Override
	public List<Attribute<?>> getAttributes() {
		return DrawAttr.ATTRS_STROKE;
	}
	
	@Override
	public void toolDeselected(AppearanceCanvas canvas) {
		active = false;
		repaintArea(canvas);
	}
	
	@Override
	public void mousePressed(AppearanceCanvas canvas, AppearanceCanvas.CME e) {
		int x = e.getX();
		int y = e.getY();
		int mods = e.getModifiersEx();
		if ((mods & InputEvent.CTRL_DOWN_MASK) != 0) {
			x = canvas.snapX(x);
			y = canvas.snapY(y);
		}
		Location loc = Location.create(x, y);
		mouseStart = loc;
		mouseEnd = loc;
		lastMouseX = loc.getX();
		lastMouseY = loc.getY();
		active = canvas.getModel() != null;
		repaintArea(canvas);
	}
	
	@Override
	public void mouseDragged(AppearanceCanvas canvas, AppearanceCanvas.CME e) {
		updateMouse(canvas, e.getX(), e.getY(), e.getModifiersEx());
	}
	
	@Override
	public void mouseReleased(AppearanceCanvas canvas, AppearanceCanvas.CME e) {
		if (active) {
			updateMouse(canvas, e.getX(), e.getY(), e.getModifiersEx());
			Location start = mouseStart;
			Location end = mouseEnd;
			CanvasObject add = null;
			if (!start.equals(end)) {
				active = false;
				CanvasModel model = canvas.getModel();
				Location[] ends = { start, end };
				List<Location> locs = UnmodifiableList.create(ends);
				add = attrs.applyTo(new Poly(false, locs));
				add.setValue(DrawAttr.PAINT_TYPE, DrawAttr.PAINT_STROKE);
				canvas.doAction(new ModelAddAction(model, add));
				repaintArea(canvas);
			}
			canvas.toolGestureComplete(this, add);
		}
	}
	
	@Override
	public void keyPressed(AppearanceCanvas canvas, KeyEvent e) {
		int code = e.getKeyCode();
		if (active && (code == KeyEvent.VK_SHIFT || code == KeyEvent.VK_CONTROL)) {
			updateMouse(canvas, lastMouseX, lastMouseY, e.getModifiersEx());
		}
	}
	
	@Override
	public void keyReleased(AppearanceCanvas canvas, KeyEvent e) {
		keyPressed(canvas, e);
	}
	
	private void updateMouse(AppearanceCanvas canvas, int mx, int my, int mods) {
		if (active) {
			boolean shift = (mods & MouseEvent.SHIFT_DOWN_MASK) != 0;
			Location newEnd;
			if (shift) {
				newEnd = LineUtil.snapTo8Cardinals(mouseStart, mx, my);
			} else {
				newEnd = Location.create(mx, my);
			}
			
			if ((mods & InputEvent.CTRL_DOWN_MASK) != 0) {
				int x = newEnd.getX();
				int y = newEnd.getY();
				x = canvas.snapX(x);
				y = canvas.snapY(y);
				newEnd = Location.create(x, y);
			}
			
			if (!newEnd.equals(mouseEnd)) {
				mouseEnd = newEnd;
				repaintArea(canvas);
			}
		}
		lastMouseX = mx;
		lastMouseY = my;
	}

	private void repaintArea(AppearanceCanvas canvas) {
		canvas.repaint();
	}
	
	@Override
	public void draw(AppearanceCanvas canvas) {
		if (active) {
			Location start = mouseStart;
			Location end = mouseEnd;
			g.setColor(Color.GRAY);
			g.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
		}
	}
	
	static Location snapTo4Cardinals(Location from, int mx, int my) {
		int px = from.getX();
		int py = from.getY();
		if (mx != px && my != py) {
			if (Math.abs(my - py) < Math.abs(mx - px)) {
				return Location.create(mx, py);
			} else {
				return Location.create(px, my);
			}
		}
		return Location.create(mx, my); // should never happen
	}
}
