/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.gates;

import java.util.Map;

import com.cburch.LogisimFX.IconsManager;
import com.cburch.LogisimFX.analyze.model.Expression;
import com.cburch.LogisimFX.analyze.model.Expressions;
import com.cburch.LogisimFX.comp.TextField;
import com.cburch.LogisimFX.data.*;
import com.cburch.LogisimFX.instance.*;
import com.cburch.LogisimFX.newgui.MainFrame.Graphics;
import com.cburch.LogisimFX.std.LC;
import com.cburch.LogisimFX.tools.key.BitWidthConfigurator;
import com.cburch.LogisimFX.util.GraphicsUtil;
import com.cburch.LogisimFX.circuit.ExpressionComputer;
import com.cburch.LogisimFX.prefs.AppPreferences;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

class NotGate extends InstanceFactory {

	public static final AttributeOption SIZE_NARROW
		= new AttributeOption(Integer.valueOf(20),
			LC.createStringBinding("gateSizeNarrowOpt"));
	public static final AttributeOption SIZE_WIDE
		= new AttributeOption(Integer.valueOf(30),
			LC.createStringBinding("gateSizeWideOpt"));
	public static final Attribute<AttributeOption> ATTR_SIZE
		= Attributes.forOption("size", LC.createStringBinding("gateSizeAttr"),
			new AttributeOption[] { SIZE_NARROW, SIZE_WIDE });

	private static final String RECT_LABEL = "1";
	private static final ImageView toolIcon = IconsManager.getIcon("notGate.gif");
	private static final ImageView toolIconRect = IconsManager.getIcon("notGateRect.gif");
	private static final ImageView toolIconDin = IconsManager.getIcon("dinNotGate.gif");

	public static InstanceFactory FACTORY = new NotGate();

	private NotGate() {

		super("NOT Gate", LC.createStringBinding("notGateComponent"));
		setAttributes(new Attribute[] {
				StdAttr.FACING, StdAttr.WIDTH, ATTR_SIZE,
				GateAttributes.ATTR_OUTPUT,
				StdAttr.LABEL, StdAttr.LABEL_FONT,
			}, new Object[] {
				Direction.EAST, BitWidth.ONE, SIZE_WIDE,
				GateAttributes.OUTPUT_01,
				"", StdAttr.DEFAULT_LABEL_FONT,
			});
		setFacingAttribute(StdAttr.FACING);
		setKeyConfigurator(new BitWidthConfigurator(StdAttr.WIDTH));

	}

	@Override
	public Bounds getOffsetBounds(AttributeSet attrs) {

		Object value = attrs.getValue(ATTR_SIZE);
		if (value == SIZE_NARROW) {
			Direction facing = attrs.getValue(StdAttr.FACING);
			if (facing == Direction.SOUTH) return Bounds.create(-9, -20, 18, 20);
			if (facing == Direction.NORTH) return Bounds.create(-9,   0, 18, 20);
			if (facing == Direction.WEST) return Bounds.create(0, -9, 20, 18);
			return Bounds.create(-20, -9, 20, 18);
		} else {
			Direction facing = attrs.getValue(StdAttr.FACING);
			if (facing == Direction.SOUTH) return Bounds.create(-9, -30, 18, 30);
			if (facing == Direction.NORTH) return Bounds.create(-9,   0, 18, 30);
			if (facing == Direction.WEST) return Bounds.create(0, -9, 30, 18);
			return Bounds.create(-30, -9, 30, 18);
		}

	}

	@Override
	public void propagate(InstanceState state) {

		Value in = state.getPort(1);
		Value out = in.not();
		out = Buffer.repair(state, out);
		state.setPort(0, out, GateAttributes.DELAY);

	}

	//
	// methods for instances
	//
	@Override
	protected void configureNewInstance(Instance instance) {

		configurePorts(instance);
		instance.addAttributeListener();
		String gateShape = AppPreferences.GATE_SHAPE.get();
		configureLabel(instance, gateShape.equals(AppPreferences.SHAPE_RECTANGULAR), null);

	}

	@Override
	protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {

		if (attr == ATTR_SIZE || attr == StdAttr.FACING) {
			instance.recomputeBounds();
			configurePorts(instance);
			String gateShape = AppPreferences.GATE_SHAPE.get();
			configureLabel(instance, gateShape.equals(AppPreferences.SHAPE_RECTANGULAR), null);
		}

	}

	private void configurePorts(Instance instance) {

		Object size = instance.getAttributeValue(ATTR_SIZE);
		Direction facing = instance.getAttributeValue(StdAttr.FACING);
		int dx = size == SIZE_NARROW ? -20 : -30;

		Port[] ports = new Port[2];
		ports[0] = new Port(0, 0, Port.OUTPUT, StdAttr.WIDTH);
		Location out = Location.create(0, 0).translate(facing, dx);
		ports[1] = new Port(out.getX(), out.getY(), Port.INPUT, StdAttr.WIDTH);
		instance.setPorts(ports);

	}

	@Override
	protected Object getInstanceFeature(final Instance instance, Object key) {

		if (key == ExpressionComputer.class) {
			return new ExpressionComputer() {
				public void computeExpression(Map<Location, Expression> expressionMap) {
					Expression e = expressionMap.get(instance.getPortLocation(1));
					if (e != null) {
						expressionMap.put(instance.getPortLocation(0), Expressions.not(e));
					}
				}
			};
		}

		return super.getInstanceFeature(instance, key);

	}

	//
	// painting methods
	//

	@Override
	public ImageView getIcon() {
		return toolIcon;
	}

	@Override
	public void paintIcon(InstancePainter painter) {
		Graphics g = painter.getGraphics();
		g.setColor(Color.black);
		if (painter.getGateShape() == AppPreferences.SHAPE_RECTANGULAR) {
			if (toolIconRect != null) {
				toolIconRect.paintIcon(painter.getDestination(), g, 2, 2);
			} else {
				g.drawRect(0, 2, 16, 16);
				GraphicsUtil.drawCenteredText(g, RECT_LABEL, 8, 8);
				g.drawOval(16, 8, 4, 4);
			}
		} else if (painter.getGateShape() == AppPreferences.SHAPE_DIN40700) {
			if (toolIconDin != null) {
				toolIconDin.paintIcon(painter.getDestination(), g, 2, 2);
			} else {
				g.drawRect(0, 2, 16, 16);
				GraphicsUtil.drawCenteredText(g, RECT_LABEL, 8, 8);
				g.drawOval(16, 8, 4, 4);
			}
		} else {
			if (toolIcon != null) {
				toolIcon.paintIcon(painter.getDestination(), g, 2, 2);
			} else {
				int[] xp = new int[4];
				int[] yp = new int[4];
				xp[0] = 15; yp[0] = 10;
				xp[1] =  1; yp[1] =  3;
				xp[2] =  1; yp[2] = 17;
				xp[3] = 15; yp[3] = 10;
				g.drawPolyline(xp, yp, 4);
				g.drawOval(15, 8, 4, 4);
			}
		}
	}

	@Override
	public void paintGhost(InstancePainter painter) {
		paintBase(painter);
	}

	@Override
	public void paintInstance(InstancePainter painter) {

		painter.getGraphics().setColor(Color.BLACK);
		paintBase(painter);
		painter.drawPorts();
		painter.drawLabel();

		painter.getGraphics().toDefault();

	}

	private void paintBase(InstancePainter painter) {

		Graphics g = painter.getGraphics();
		Direction facing = painter.getAttributeValue(StdAttr.FACING);
		Location loc = painter.getLocation();
		int x = loc.getX();
		int y = loc.getY();
		g.c.translate(x, y);
		double rotate = 0.0;
		if (facing != null && facing != Direction.EAST) {
			rotate = -facing.toRadians();
			g.c.rotate(rotate);
		}

		Object shape = painter.getGateShape();
		if (shape == AppPreferences.SHAPE_RECTANGULAR) {
			paintRectangularBase(g, painter);
		} else if (shape == AppPreferences.SHAPE_DIN40700) {
			int width = painter.getAttributeValue(ATTR_SIZE) == SIZE_NARROW ? 20 : 30;
			PainterDin.paintAnd(painter, width, 18, true);
		} else {
			PainterShaped.paintNot(painter);
		}
		
		if (rotate != 0.0) {
			g.c.rotate(-rotate);
		}
		g.c.translate(-x, -y);

	}

	private void paintRectangularBase(Graphics g, InstancePainter painter) {

		g.setLineWidth(2);
		if (painter.getAttributeValue(ATTR_SIZE) == SIZE_NARROW) {
			g.c.strokeRect(-20, -9, 14, 18);
			GraphicsUtil.drawCenteredText(g, RECT_LABEL, -13, 0);
			g.c.strokeOval(-6, -3, 6, 6);
		} else {
			g.c.strokeRect(-30, -9, 20, 18);
			GraphicsUtil.drawCenteredText(g, RECT_LABEL, -20, 0);
			g.c.strokeOval(-10, -5, 9, 9);
		}
		g.setLineWidth(1);

	}
	
	static void configureLabel(Instance instance, boolean isRectangular,
                               Location control) {

		Object facing = instance.getAttributeValue(StdAttr.FACING);
		Bounds bds = instance.getBounds();
		int x;
		int y;
		int halign;
		if (facing == Direction.NORTH || facing == Direction.SOUTH) {
			x = bds.getX() + bds.getWidth() / 2 + 2;
			y = bds.getY() - 2;
			halign = TextField.H_LEFT;
		} else { // west or east
			y = isRectangular ? bds.getY() - 2 : bds.getY();
			if (control != null && control.getY() == bds.getY()) {
				// the control line will get in the way
				x = control.getX() + 2;
				halign = TextField.H_LEFT;
			} else {
				x = bds.getX() + bds.getWidth() / 2;
				halign = TextField.H_CENTER;
			}
		}
		instance.setTextField(StdAttr.LABEL, StdAttr.LABEL_FONT, x, y,
				halign, TextField.V_BASELINE);

	}

}
