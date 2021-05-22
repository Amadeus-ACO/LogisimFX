package com.cburch.LogisimFX.newgui.MainFrame;

import com.cburch.LogisimFX.circuit.SubcircuitFactory;
import com.cburch.LogisimFX.file.LogisimFile;
import com.cburch.LogisimFX.newgui.ContextMenuManager;
import com.cburch.LogisimFX.proj.Project;
import com.cburch.LogisimFX.tools.AddTool;
import com.cburch.LogisimFX.tools.Library;
import com.cburch.LogisimFX.tools.Tool;
import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.comp.ComponentFactory;

import javafx.scene.control.*;
import javafx.scene.input.MouseButton;


public class ProjectTreeExplorer extends AbstractTreeExplorer {

    private Project proj;

    public ProjectTreeExplorer(Project project){

        super();

        this.proj = project;

        MultipleSelectionModel<TreeItem> selectionModel = this.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        this.setCellFactory(tree -> {

            TreeCell<Object> cell = new TreeCell<Object>() {

                @Override
                public void updateItem(Object item, boolean empty) {

                    super.updateItem(item, empty);

                    textProperty().unbind();

                    if(empty || item == null) {

                        setText(null);
                        setGraphic(null);
                        setTooltip(null);
                        setContextMenu(null);

                    } else {

                        if(item instanceof LogisimFile){

                            setText(proj.getLogisimFile().getName());
                            setGraphic(null);
                            setTooltip(null);
                            setContextMenu(ContextMenuManager.ProjectContextMenu(proj));

                        }
                        else if(item instanceof Library){

                            textProperty().bind(((Library) item).getDisplayName());
                            setGraphic(null);
                            setTooltip(null);
                            setContextMenu(ContextMenuManager.LibraryContextMenu(proj, (Library) item));

                        }
                        else if(item instanceof Tool){

                            setText(((Tool)item).getDisplayName());

                            //Tooltip tip = new Tooltip();
                            //tip.textProperty().bind(((Tool)item).getDescription());
                            //setTooltip(tip);

                            setTooltip(new Tooltip(((Tool)item).getDescription()));

                            setGraphic(((Tool) item).getIcon());

                            /*
                                                        ComponentFactory fact = ((AddTool) item).getFactory(false);

                            if (fact instanceof SubcircuitFactory) {

                                Circuit circ = ((SubcircuitFactory) fact).getSubcircuit();

                                setContextMenu(ContextMenuManager.CircuitContextMenu(proj, circ));

                                if(proj.getCurrentCircuit().equals(circ)){

                                }

                            }else{
                                setContextMenu(null);
                            }
                             */

                        }
                        else{
                            setText("you fucked up2");
                        }

                    }

                }

            };

            cell.setOnMouseClicked(event -> {

                if (!cell.isEmpty()) {

                    TreeItem<Object> treeItem = cell.getTreeItem();

                    if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2
                            && !event.isConsumed()) {

                        event.consume();

                        if(treeItem.getValue() instanceof AddTool){

                            ComponentFactory fact = ((AddTool) treeItem.getValue()).getFactory(false);
                            if (fact instanceof SubcircuitFactory) {
                                proj.setCurrentCircuit(
                                        ((SubcircuitFactory) fact).getSubcircuit()
                                );

                            }

                        }


                    }else if (event.getButton().equals(MouseButton.PRIMARY)){

                        if(treeItem.getValue() instanceof AddTool){
                            proj.setTool((Tool)treeItem.getValue());
                        }

                    }

                }

            });

            return cell ;

        });

        updateTree();

    }

    public void updateTree(){

        TreeItem<Object> root = new TreeItem<>(proj.getLogisimFile());
        this.setRoot(root);
        root.expandedProperty().set(true);

        //Circuits
        for (AddTool tool: proj.getLogisimFile().getTools()) {

            TreeItem<Object> l = new TreeItem<>(tool);
            root.getChildren().add(l);

        }

        //Libs and tools
        for (Library lib: proj.getLogisimFile().getLibraries()) {

            TreeItem<Object> l = new TreeItem<>(lib);
            root.getChildren().add(l);

            for (Tool tool: lib.getTools()) {

                TreeItem<Object> t = new TreeItem<>(tool);
                l.getChildren().add(t);

            }

        }

    }

    private void checkForHighLight(TreeCell<Object> cell, Object item){

        //if circuit tool, and is current circuit, then highlight cell

        ComponentFactory fact = ((AddTool) item).getFactory(false);

        if (fact instanceof com.cburch.logisim.circuit.SubcircuitFactory) {

            Circuit circ = ((SubcircuitFactory) fact).getSubcircuit();

            if(proj.getCurrentCircuit().equals(circ)){

            }

        }

    }

}