package com.cburch.LogisimFX.newgui;

import com.cburch.LogisimFX.circuit.CircuitMutation;
import com.cburch.LogisimFX.circuit.CircuitState;
import com.cburch.LogisimFX.comp.Component;
import com.cburch.LogisimFX.instance.Instance;
import com.cburch.LogisimFX.localization.LC_null;
import com.cburch.LogisimFX.localization.LC_std;
import com.cburch.LogisimFX.localization.LC_tools;
import com.cburch.LogisimFX.localization.Localizer;
import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.file.LogisimFileActions;
import com.cburch.LogisimFX.newgui.MainFrame.*;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.std.memory.Mem;
import com.cburch.LogisimFX.std.memory.RomAttributes;
import com.cburch.LogisimFX.tools.Library;
import com.cburch.LogisimFX.file.LoadedLibrary;
import com.cburch.LogisimFX.file.Loader;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class ContextMenuManager {

    private static Localizer lc = LC_null.getInstance();

    public static ContextMenu ProjectContextMenu(Project proj){

        lc.changeBundle("menu");

        ContextMenu contextMenu = new ContextMenu();

        MenuItem AddCircuit = new MenuItem();
        AddCircuit.textProperty().bind(lc.createStringBinding("projectAddCircuitItem"));
        AddCircuit.setOnAction(event -> {

            String circuitName = DialogManager.CreateInputDialog(proj.getLogisimFile());

            if (circuitName != null) {
                Circuit circuit = new Circuit(circuitName);
                proj.doAction(LogisimFileActions.addCircuit(circuit));
                proj.setCurrentCircuit(circuit);
            }

        });

        Menu UploadLibrary = new Menu();
        UploadLibrary.textProperty().bind(lc.createStringBinding("projectLoadLibraryItem"));

        MenuItem BuiltInLib = new MenuItem();
        BuiltInLib.textProperty().bind(lc.createStringBinding("projectLoadBuiltinItem"));
        BuiltInLib.setOnAction(event -> ProjectLibraryActions.doLoadBuiltinLibrary(proj));

        MenuItem LogisimLib = new MenuItem();
        LogisimLib.textProperty().bind(lc.createStringBinding("projectLoadLogisimItem"));
        LogisimLib.setOnAction(event -> ProjectLibraryActions.doLoadLogisimLibrary(proj));

        MenuItem JARLib = new MenuItem();
        JARLib.textProperty().bind(lc.createStringBinding("projectLoadJarItem"));
        JARLib.setOnAction(event -> ProjectLibraryActions.doLoadJarLibrary(proj));

        UploadLibrary.getItems().addAll(
                BuiltInLib,
                LogisimLib,
                JARLib
        );

        contextMenu.getItems().addAll(
                AddCircuit,
                UploadLibrary
        );

        return contextMenu;

    }

    public static ContextMenu LibraryContextMenu(Project proj,Library lib){

        ContextMenu contextMenu = new ContextMenu();

        MenuItem UnloadLibrary = new MenuItem();
        UnloadLibrary.textProperty().bind(lc.createStringBinding("projectUnloadLibraryItem"));
        UnloadLibrary.setOnAction(event -> ProjectLibraryActions.doUnloadLibrary(proj, lib));

        MenuItem ReloadLibrary = new MenuItem();
        ReloadLibrary.textProperty().bind(lc.createStringBinding("projectUnloadLibraryItem"));
        ReloadLibrary.setDisable(!lib.isDirty());
        ReloadLibrary.setOnAction(event -> {
            Loader loader = proj.getLogisimFile().getLoader();
            loader.reload((LoadedLibrary) lib);
        });

        contextMenu.getItems().addAll(
                UnloadLibrary,
                ReloadLibrary
        );

        return contextMenu;

    }

    public static ContextMenu CircuitContextMenu(Project proj, Circuit circ){

        lc.changeBundle("menu");

        ContextMenu contextMenu = new ContextMenu();

        MenuItem EditCircuit = new MenuItem();
        EditCircuit.textProperty().bind(lc.createStringBinding("projectEditCircuitLayoutItem"));
        EditCircuit.setOnAction(event -> proj.getFrameController().setEditView(circ));

        MenuItem EditAppearance = new MenuItem();
        EditAppearance.textProperty().bind(lc.createStringBinding("projectEditCircuitAppearanceItem"));
        EditAppearance.setOnAction(event -> proj.getFrameController().setAppearanceView(circ));

        MenuItem AnalyzeCircuit = new MenuItem();
        AnalyzeCircuit.textProperty().bind(lc.createStringBinding("projectAnalyzeCircuitItem"));
        AnalyzeCircuit.setOnAction(event -> FrameManager.CreateCircuitAnalysisFrame(proj));

        MenuItem GetCircuitStatistics = new MenuItem();
        GetCircuitStatistics.textProperty().bind(lc.createStringBinding("projectGetCircuitStatisticsItem"));
        GetCircuitStatistics.setOnAction(event -> FrameManager.CreateCircuitStatisticFrame(proj, circ));

        MenuItem SetAsMain = new MenuItem();
        SetAsMain.disableProperty().bind(proj.getLogisimFile().isMain);
        SetAsMain.textProperty().bind(lc.createStringBinding("projectSetAsMainItem"));
        SetAsMain.setOnAction(event -> proj.getLogisimFile().setMainCircuit(circ));

        MenuItem RemoveCirc = new MenuItem();
        RemoveCirc.disableProperty().bind(proj.getLogisimFile().obsPos.isEqualTo("first&last"));
        RemoveCirc.textProperty().bind(lc.createStringBinding("projectRemoveCircuitItem"));
        RemoveCirc.setOnAction(event -> {
            ProjectCircuitActions.doRemoveCircuit(proj,circ);
            proj.getFrameController().manual_Explorer_Update();
        });


        contextMenu.getItems().addAll(
                EditCircuit,
                EditAppearance,
                AnalyzeCircuit,
                GetCircuitStatistics,
                SetAsMain,
                RemoveCirc
        );

        return contextMenu;

    }

    public static ContextMenu ComponentDefaultContextMenu(Project project, Circuit circuit, Component component){

        Project proj = project;
        Circuit circ = circuit;
        Component comp = component;
        boolean canChange = proj.getLogisimFile().contains(circ);

        ContextMenu contextMenu = new ContextMenu();

        MenuItem del = new MenuItem("Menu Item");
        del.textProperty().bind(LC_tools.getInstance().createStringBinding("compDeleteItem"));
        del.setDisable(!canChange);
        del.setOnAction(event -> {

            Circuit c = proj.getCurrentCircuit();
            CircuitMutation xn = new CircuitMutation(c);
            xn.remove(comp);
            proj.doAction(xn.toAction(LC_tools.getInstance().createStringBinding("removeComponentAction",
                    comp.getFactory().getDisplayGetter())));

        });

        MenuItem attrs = new MenuItem("Menu Item");
        attrs.textProperty().bind(LC_tools.getInstance().createStringBinding("compShowAttrItem"));
        attrs.setOnAction(event -> {
            proj.getFrameController().setAttributeTable(circ, comp);
        });

        contextMenu.getItems().addAll(del,attrs);

        return contextMenu;

    }

    public static ContextMenu SelectionContextMenu(CustomCanvas c){

        Project proj = c.getProject();
        Selection sel = c.getSelection();

        ContextMenu contextMenu = new ContextMenu();

        MenuItem del = new MenuItem();
        del.textProperty().bind(LC_tools.getInstance().createStringBinding("selDeleteItem"));
        del.setDisable(!proj.getLogisimFile().contains(proj.getCurrentCircuit()));
        del.setOnAction(event -> proj.doAction(SelectionActions.clear(sel)));

        MenuItem cut = new MenuItem();
        cut.textProperty().bind(LC_tools.getInstance().createStringBinding("selCutItem"));
        cut.setDisable(!proj.getLogisimFile().contains(proj.getCurrentCircuit()));
        cut.setOnAction(event -> proj.doAction(SelectionActions.cut(sel)));

        MenuItem copy = new MenuItem();
        copy.textProperty().bind(LC_tools.getInstance().createStringBinding("selCopyItem"));
        copy.setOnAction(event -> proj.doAction(SelectionActions.copy(sel)));

        contextMenu.getItems().addAll(del,cut,copy);

        return contextMenu;

    }

    public static ContextMenu CircuitComponentContextMenu(){

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Menu Item");

        contextMenu.getItems().addAll(menuItem);

        return contextMenu;

    }

    public static ContextMenu MemoryComponentContextMenu(Mem fact, Instance inst){

        Mem factory = fact;
        Instance instance = inst;
        Project proj = ;
        CircuitState circState = proj.getCircuitState();
        boolean enabled = circState != null;
        boolean canChange = proj.getLogisimFile().contains(proj.getCurrentCircuit());

        Object attrs = instance.getAttributeSet();
        if (attrs instanceof RomAttributes) {
            ((RomAttributes) attrs).setProject(proj);
        }

        ContextMenu contextMenu = new ContextMenu();

        MenuItem del = new MenuItem();
        del.textProperty().bind(LC_tools.getInstance().createStringBinding("compDeleteItem"));
        del.setDisable(!canChange);
        del.setOnAction(event -> {

            Circuit c = proj.getCurrentCircuit();
            CircuitMutation xn = new CircuitMutation(c);
            xn.remove(comp);
            proj.doAction(xn.toAction(LC_tools.getInstance().createStringBinding("removeComponentAction",
                    comp.getFactory().getDisplayGetter())));

        });

        MenuItem attrs = new MenuItem();
        attrs.textProperty().bind(LC_tools.getInstance().createStringBinding("compShowAttrItem"));
        attrs.setOnAction(event -> {
            proj.getFrameController().setAttributeTable(circ, comp);
        });

        MenuItem edit = new MenuItem();
        edit.setDisable(!enabled);
        edit.textProperty().bind(LC_std.getInstance().createStringBinding("ramEditMenuItem"));
        edit.setOnAction(event -> {

            MemState s = factory.getState(instance, circState);
            if (s == null) return;
            HexFrame frame = factory.getHexFrame(proj, instance, circState);
            frame.setVisible(true);
            frame.toFront();

        });

        MenuItem clear = new MenuItem();
        clear.setDisable(!enabled);
        clear.textProperty().bind(LC_std.getInstance().createStringBinding("ramClearMenuItem"));
        clear.setOnAction(event -> {

            MemState s = factory.getState(instance, circState);
            boolean isAllZero = s.getContents().isClear();
            if (isAllZero) return;

            int choice = JOptionPane.showConfirmDialog(frame,
                    Strings.get("ramConfirmClearMsg"),
                    Strings.get("ramConfirmClearTitle"),
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                s.getContents().clear();
            }

        });

        MenuItem load = new MenuItem();
        load.setDisable(!enabled);
        load.textProperty().bind(LC_std.getInstance().createStringBinding("ramLoadMenuItem"));
        load.setOnAction(event -> {

            JFileChooser chooser = proj.createChooser();
            File oldSelected = factory.getCurrentImage(instance);
            if (oldSelected != null) chooser.setSelectedFile(oldSelected);
            chooser.setDialogTitle(Strings.get("ramLoadDialogTitle"));
            int choice = chooser.showOpenDialog(frame);
            if (choice == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                try {
                    factory.loadImage(circState.getInstanceState(instance), f);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, e.getMessage(),
                            Strings.get("ramLoadErrorTitle"), JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        MenuItem save = new MenuItem();
        save.setDisable(!enabled);
        save.textProperty().bind(LC_std.getInstance().createStringBinding("ramSaveMenuItem"));
        save.setOnAction(event -> {

            MemState s = factory.getState(instance, circState);

            JFileChooser chooser = proj.createChooser();
            File oldSelected = factory.getCurrentImage(instance);
            if (oldSelected != null) chooser.setSelectedFile(oldSelected);
            chooser.setDialogTitle(Strings.get("ramSaveDialogTitle"));
            int choice = chooser.showSaveDialog(frame);
            if (choice == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                try {
                    HexFile.save(f, s.getContents());
                    factory.setCurrentImage(instance, f);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, e.getMessage(),
                            Strings.get("ramSaveErrorTitle"), JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        contextMenu.getItems().addAll(
                del,
                attrs,
                edit,
                clear,
                load,
                save
        );

        return contextMenu;

    }

}
