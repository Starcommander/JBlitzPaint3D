package com.starcom.app;

import com.starcom.app.tools.EditTool3D;
import java.util.ArrayList;
import java.util.HashMap;

import com.starcom.paint.Frame;
import com.starcom.paint.tools.ITool;
import com.starcom.paint.tools.ITool.EventType;
import com.starcom.app.tools.ShapeTool;
import com.starcom.debug.LoggingSystem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class BlitzPaintFrame
{
  enum MenuType {Load, Save, Settings};
  enum ToolBarType {Canvas, Paint};
  public static int SIZE_X = 800;
  public static int SIZE_Y = 600;
  public static String SHAPE_TOOL = "ShapeTool";
  public static String ID_FILE_TOOL = "fileTool";
  public static Color color = Color.RED;
  @FXML private Pane pane;
  @FXML private ScrollPane scrollPane;
  @FXML private Button shapeTool;
  @FXML private Group meshViewGroup;
  private Node lastSelectedToolButton;
  ArrayList<CheckMenuItem> toolBarList;
  HashMap<String,ITool> tools = new HashMap<>();
  boolean isDrag = false;
  ITool currentTool;

  @FXML public void initialize()
  {
//    toolbar_paint.managedProperty().bind(toolbar_paint.visibleProperty());
//    toolbar_canvas.managedProperty().bind(toolbar_canvas.visibleProperty());
//    toolbar_canvas.setVisible(false);
    
//    pane.setOnMouseMoved((ev) -> currentTool.handle(EventType.MOVE, ev)); // Not used yet!
    pane.setOnMousePressed((ev) -> currentTool.handle(EventType.CLICK, ev));
    pane.setOnMouseReleased((ev) -> currentTool.handle(EventType.RELEASE, ev));
    pane.setOnMouseDragged((ev) -> currentTool.handle(EventType.DRAG, ev));
    scrollPane.setOnKeyPressed((ev) -> onKey(ev));
    pane.setStyle("-fx-border-color: black");
//    meshView.setRotationAxis(Rotate.X_AXIS);
//    meshView.setRotate(10.0);
//    meshView.setTranslateZ(60.0);
    
    View3D view = new View3D(meshViewGroup, SIZE_X, 600, SIZE_Y, pane);
    
    ShapeTool.view = view;
  }

  public void onShowPre()
  {
    Frame.openEmptyPix(pane, SIZE_X, SIZE_Y);
    selectTool(SHAPE_TOOL);
    lastSelectedToolButton = shapeTool;
    changeButtonActive(shapeTool, true);
    Frame.clipChildren(pane);
  }
  
  public void onShowPost()
  {
//    if (BlitzPaint.fullShot!=null)
//    {
//      selectTool(CROP_TOOL);
//      lastSelectedToolButton = cropTool;
//      changeButtonActive(cropTool, true);
//    }
  }
  
  private void onKey(KeyEvent ev)
  {
    if (ev.getCode() == KeyCode.DELETE)
    {
      PaintObject.clearFocusObject(pane);
    }
  }
  
  @FXML void selectTool(ActionEvent event)
  {
    Object source = event.getSource();
    if (source instanceof Node)
    {
      PaintObject.clearGizmos(pane);
      Node sourceN = (Node) source;
      if (sourceN.getId().equals(ID_FILE_TOOL))
      { //TODO: Save to File
        System.out.println("Save to File!");
      }
      else
      {
        changeButtonActive(lastSelectedToolButton, false);
        lastSelectedToolButton = sourceN;
        changeButtonActive(lastSelectedToolButton, true);
        String id = sourceN.getId();
        id = id.substring(0,1).toUpperCase() + id.substring(1);
        selectTool(id);
        LoggingSystem.info(BlitzPaintFrame.class, "Tool selected: " + sourceN.getId());
      }
    }
    else
    {
      throw new IllegalStateException("Selected from unknown source: " + source);
    }
  }

  void changeButtonActive(Node button, boolean b_active)
  {
    if (b_active)
    {
      button.setStyle("-fx-background-color: yellowgreen");
    }
    else
    {
      button.setStyle(null);
    }
  }
  
  private void selectTool(String id)
  {
    System.out.println("Selected tool: " + id);
    ITool lastTool = currentTool;
    currentTool = tools.get(id);
    if (lastTool != null) { lastTool.onDeselected(); }
    if (currentTool != null)
    {
      currentTool.onSelected();
      return;
    }
    String toolClass = EditTool3D.class.getPackage().getName() + "." + id;
    try
    {
      currentTool = (ITool) Class.forName(toolClass).getDeclaredConstructor().newInstance();
    }
    catch (Exception e) { throw new IllegalArgumentException(e); }
    currentTool.init(pane);
    currentTool.onSelected();
    tools.put(id, currentTool);
  }
}
