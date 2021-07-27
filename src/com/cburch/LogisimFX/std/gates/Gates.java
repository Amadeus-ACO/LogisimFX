/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.LogisimFX.std.gates;

import com.cburch.LogisimFX.std.LC;
import com.cburch.LogisimFX.tools.AddTool;
import com.cburch.LogisimFX.tools.Library;
import com.cburch.LogisimFX.tools.Tool;
import javafx.beans.binding.StringBinding;

import java.util.Arrays;
import java.util.List;

public class Gates extends Library {

	private List<Tool> tools = null;

	public Gates() {
		tools = Arrays.asList(new Tool[] {
			new AddTool(NotGate.FACTORY),
			new AddTool(Buffer.FACTORY),
			new AddTool(AndGate.FACTORY),
			new AddTool(OrGate.FACTORY),
			new AddTool(NandGate.FACTORY),
			new AddTool(NorGate.FACTORY),
			new AddTool(XorGate.FACTORY),
			new AddTool(XnorGate.FACTORY),
			new AddTool(OddParityGate.FACTORY),
			new AddTool(EvenParityGate.FACTORY),
			new AddTool(ControlledBuffer.FACTORY_BUFFER),
			new AddTool(ControlledBuffer.FACTORY_INVERTER),
		});
	}

	@Override
	public String getName() { return "Gates"; }

	@Override
	public StringBinding getDisplayName() { return LC.createStringBinding("gatesLibrary"); }

	@Override
	public List<Tool> getTools() {
		return tools;
	}

}
