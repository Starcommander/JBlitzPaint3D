package com.starcom.app.tools;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import com.starcom.app.BlitzPaintFrame;
import com.starcom.app.Layer;
import com.starcom.app.PaintObject;
import com.starcom.app.PaintObjectOfChain;
import com.starcom.app.View3D;
import com.starcom.paint.tools.ITool;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class ShapeTool implements ITool
{
  public static View3D view;
  static int tail_len = 30;
  static int line_thick = 10;
  static double opacity = 0.8;
  Pane pane;
  Line line;
  Circle circle;
  PaintObjectOfChain lastChainObj; // pending object
  //TODO: Speichern und Laden mit Projekt:
  //https://github.com/mutantbob/jwavefrontobj/blob/master/src/main/java/com/purplefrog/jwavefrontobj/WavefrontOBJWriter.java
  
  public static Line makeLine()
  {
    Line line = new Line();
    line.setStroke(BlitzPaintFrame.color);
    line.setStrokeWidth(line_thick);
    line.setStrokeLineCap(StrokeLineCap.BUTT);
    line.setOpacity(opacity);
    return line;
  }
  
  public static Circle makeCircle()
  {
    Circle circle = new Circle(10);
    circle.setStroke(BlitzPaintFrame.color.darker());
    circle.setFill(BlitzPaintFrame.color);
    circle.setOpacity(opacity);
    return circle;
  }
  
  void makeShape()
  {
    line = makeLine();
    pane.getChildren().add(line);
    circle = makeCircle();
    pane.getChildren().add(circle);
  }
  
  @Override
  public void init(Pane pane)
  {
    this.pane = pane;
    view.getMeshViewGroup().getParent().setOnMouseClicked((ev) -> this.onDeselected());
  }

  @Override
  public void handle(EventType evType, MouseEvent event)
  {
    if (!view.isValidLayer()) { return; }
    EventHandle:
    if (evType == EventType.DRAG)
    {
      if (line==null) { break EventHandle; }
      double posX = event.getX();
      double posY = event.getY();
      if (lastChainObj == null)
      { // First ObjectOfChain, do not drag line
        line.setStartX(posX);
        line.setStartY(posY);
      }
      update(line, circle, line.getStartX(),line.getStartY(),posX,posY);
    }
    else if (evType == EventType.CLICK)
    {
      makeShape();

      double posX1 = event.getX();
      double posY1 = event.getY();
      double posX2 = event.getX();
      double posY2 = event.getY();
      if (lastChainObj!=null)
      {
        Line lastLine = (Line) lastChainObj.getNodeList().get(0);
        posX1 = lastLine.getEndX();
        posY1 = lastLine.getEndY();
      }
      update(line, circle, posX1,posY1,posX2,posY2);
    }
    else if (evType == EventType.RELEASE)
    {
      createPaintObject();
    }
  }
  
  // Finish shape and create modifieable PaintObject for this shape.
  private void createPaintObject()
  {
    PaintObjectOfChain newChainObj = createPaintObjectStatic(line, circle);
    if (lastChainObj!=null)
    {
      lastChainObj.connectWithNext(newChainObj);
    }
    lastChainObj = newChainObj;
    line = null;
    circle = null;
  }
  
  /** Parallel move, so startP and endP is moved. **/
  private static void updateChain(PaintObjectOfChain thisObj, double posX, double posY)
  {
    Line thisLine = (Line) thisObj.getNodeList().get(0);
    PaintObjectOfChain lastRotatedObj = thisObj.getPolyChain().getLastObj(thisObj, true);
    PaintObjectOfChain nextRotatedObj = thisObj.getPolyChain().getNextObj(thisObj, true);
    updateChain(lastRotatedObj, thisLine.getStartX(), thisLine.getStartY(), false);
    updateChain(nextRotatedObj, thisLine.getEndX(), thisLine.getEndY(), true);
    Pane pane = (Pane) thisLine.getParent();
    thisObj.getPolyChain().fillChain(pane);
  }

  /** Move start or end of nextObj. **/
  private static void updateChain(PaintObjectOfChain nextObj, double posX, double posY, boolean startP)
  {
    Line line = (Line)nextObj.getNodeList().get(0);
    Circle circle = (Circle)nextObj.getNodeList().get(1);
    if (startP)
    {
      double ex = line.getEndX();
      double ey = line.getEndY();
      update(line, circle, posX, posY, ex, ey);
    }
    else
    {
      double sx = line.getStartX();
      double sy = line.getStartY();
      update(line, circle, sx, sy, posX, posY);
    }
  }
  
  private static void update(Line line, Circle circle, double x1, double y1, double x2, double y2)
  {
    /* Line start end. */
    line.setStartX(x1);
    line.setStartY(y1);
    line.setEndX(x2);
    line.setEndY(y2);
    
    circle.setCenterX(line.getEndX());
    circle.setCenterY(line.getEndY());
  }

  @Override
  public void onSelected()
  {
  }

  @Override
  public void onDeselected() // Finish and close chain and fill shape.
  {
    System.out.println("ShapeTool.java: onDeselect()");
    if (lastChainObj!=null)
    {
//      makeShape();
      Line lastLine = (Line) lastChainObj.getNodeList().get(0);
      double posX1 = lastLine.getEndX();
      double posY1 = lastLine.getEndY();
      
      PaintObjectOfChain firstChainObj = lastChainObj.getPolyChain().getObjects().get(0);
      Line firstLine = (Line) firstChainObj.getNodeList().get(0);
      Circle firstCircle = (Circle) firstChainObj.getNodeList().get(1);
      double posX2 = firstLine.getStartX();
      double posY2 = firstLine.getStartY();
      
      update(firstLine, firstCircle, posX1,posY1,posX2,posY2);
      lastChainObj = null;
      fillShape(firstChainObj, pane);
    }
  }

  /** Fill shape and draw 3d shape. **/
  public static void fillShape(PaintObjectOfChain firstChainObj, Pane pane)
  {
    firstChainObj.getPolyChain().fillChain(pane);
    //TODO: Gegebenenfalls Subtract-Polygon erzeugen: Path.substract(myShape, anyHole)
    firstChainObj.getPolyChain().attachFillPoly(view);
//    Group3D layerGroup = new Group3D();
//    layerGroup.getChildren().add(p2);
//    layerGroup.move(0.0, view.getRectPositionY(), 0.0);
//    view.attachChild(layerGroup);
  }
  
  public static PaintObjectOfChain_Shape createPaintObjectStatic(Line l, Circle c)
  {
    return new PaintObjectOfChain_Shape(l, c);
  }

  static class PaintObjectOfChain_Shape extends PaintObjectOfChain
  {
    Line l;
    Circle c;
    
    public PaintObjectOfChain_Shape(Line l, Circle c)
    {
      super(l,c);
      this.l = l;
      this.c = c;
    }
    
    @Override
    public void moveGizmo(Node gizmo, double posX, double posY)
    {
System.out.println("ShapeTool: Pre moveGizmo()");
      String s_giz = gizmo.getUserData().toString();
      Line line = (Line)getNodeList().get(0);
      Circle circle = (Circle)getNodeList().get(1);
      if (s_giz.equals(GIZMO_START))
      {
System.out.println("- move start to x=" + posX + " y=" + posY);
        update(line, circle, posX, posY, circle.getCenterX(), circle.getCenterY());
        updateChain(this, posX, posY);
      }
      else if (s_giz.equals(GIZMO_END))
      {
System.out.println("- move end to x=" + posX + " y=" + posY);
        update(line, circle, line.getStartX(), line.getStartY(), posX, posY);
        updateChain(this, posX, posY);
      }
System.out.println("- End (move start/end)");
      if (s_giz.equals(GIZMO_CENTER))
      {
        double cx = line.getStartX() - line.getEndX();
        double cy = line.getStartY() - line.getEndY();
        cx = line.getEndX() + (cx/2.0);
        cy = line.getEndY() + (cy/2.0);
        double movX = posX -cx;
        double movY = posY -cy;
        double ex = circle.getCenterX() + movX;
        double ey = circle.getCenterY() + movY;
        update(line, circle, line.getStartX() + movX, line.getStartY() + movY, ex, ey);
        updateChain(this, posX, posY);
      }
    }

    @Override
    public void appendGizmos(ArrayList<Node> gizmoList)
    {
      gizmoList.add(PaintObject.createGizmoCircle(GIZMO_START));
      gizmoList.add(PaintObject.createGizmoCircle(GIZMO_END));
      gizmoList.add(PaintObject.createGizmoCircle(GIZMO_CENTER));
    }

    @Override
    public void updateGizmoPositions(ArrayList<Node> gizmoList)
    {
      Line l = (Line)getNodeList().get(0);
      double ex = l.getEndX();
      double ey = l.getEndY();
      double sx = l.getStartX();
      double sy = l.getStartY();
      double lx = (ex -sx) / 2.0;
      double ly = (ey -sy) / 2.0;
      Circle gizmo = (Circle)gizmoList.get(0);
      gizmo.setCenterX(sx);
      gizmo.setCenterY(sy);
      gizmo = (Circle)gizmoList.get(1);
      gizmo.setCenterX(ex);
      gizmo.setCenterY(ey);
      gizmo = (Circle)gizmoList.get(2);
      gizmo.setCenterX(sx + lx);
      gizmo.setCenterY(sy + ly);
      Layer.storeCurrentLayer(Layer.getCurrentLayer());
    }

    @Override
    public String saveObj()
    {
      return l.getStartX() + " " + l.getStartY() + " " + l.getEndX() + " " + l.getEndY();
    }
    
    @Override
    public void loadObj(String data)
    {
      String[] objList = data.split(" ");
      l.setStartX(Double.parseDouble(objList[0]));
      l.setStartY(Double.parseDouble(objList[1]));
      l.setEndX(Double.parseDouble(objList[2]));
      l.setEndY(Double.parseDouble(objList[3]));
      c.setCenterX(Double.parseDouble(objList[2]));
      c.setCenterY(Double.parseDouble(objList[3]));
    }
  }

}
