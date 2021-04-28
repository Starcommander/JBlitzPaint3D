package com.starcom.app;

import java.util.ArrayList;

import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import com.starcom.debug.LoggingSystem;
import com.starcom.math.Point2i;
import com.starcom.app.PolyChain;
import com.starcom.app.mesh3d.Group3D;
import com.starcom.app.mesh3d.PolygonToTriangle;
import com.starcom.app.mesh3d.PolygonToTriangle.Triangle;
import com.starcom.paint.tools.ITool.EventType;

public class View3D
{
  private Group rootNode = new Group();
  private Group3D meshNodeYrotate = new Group3D();
  private Group3D meshNode = new Group3D();
  Rectangle rect = new Rectangle();
  private Group3D rectNode = new Group3D();
  private boolean moveRect = false;
  private Group meshViewGroup;
  private Pane panel;
  Point2i dragPos;

  public View3D(Group meshViewGroup, double width, double height, double z_depth, Pane panel)
  {
    this.meshViewGroup = meshViewGroup;
    this.panel = panel;
    createScene(meshViewGroup, width, height, z_depth);
    meshViewGroup.getParent().setOnMouseMoved((ev) -> handle(EventType.MOVE, ev));
    meshViewGroup.getParent().setOnMouseReleased((ev) -> handle(EventType.CLICK, ev));
    meshViewGroup.getParent().setOnMouseDragged((ev) -> handle(EventType.DRAG , ev));
    AmbientLight ambient = new AmbientLight();
    ambient.setColor(Color.rgb(150, 150, 150,1));
    rootNode.getChildren().add(ambient);
    PointLight point = new PointLight();
    point.setColor(Color.rgb(255, 255, 255,1));
    point.setTranslateX(4000.0);
    point.setTranslateY(4000.0);
//    point.setTranslateZ(-2000.0);
//    point.setRotate(45.0);
    //point.setLayoutX(400);
    //point.setLayoutY(100);
    point.setTranslateZ(-1100);
    point.getScope().add(rootNode);
    rootNode.getChildren().add(point);
    meshNodeYrotate.getChildren().add(meshNode);
    rootNode.getChildren().add(meshNodeYrotate);
  }
  
  public double getRectPositionY() { return rectNode.getTranslateY(); }
  public boolean isValidLayer() { return !moveRect; }

  private void createScene(Group meshViewGroup, double width, double height, double z_depth)
  {
    javafx.scene.SubScene subs = new javafx.scene.SubScene(rootNode,width,height,true,SceneAntialiasing.BALANCED);
    subs.setFill(Color.BLACK);
    meshViewGroup.getChildren().add(subs);
    PerspectiveCamera cam = new PerspectiveCamera(false);
    subs.setCamera(cam);
    rect.setWidth(width);
    rect.setHeight(z_depth);
    rect.setX(-width/2.0);
    rect.setY(-z_depth/2.0);
    cam.setTranslateX(rect.getX());
    cam.setTranslateZ(rect.getY());
    rect.setFill(Color.TURQUOISE);
    rectNode.getChildren().add(rect);
    attachChild(rectNode);
  }
  /** Adds the Group into this View, and manages custom Transform. **/
  private void attachChild(Group3D group)
  {
    group.rotate(90.0, 0.0, 0.0);
//    group.move(0, 0, 300);
    meshNode.getChildren().add(group);
  }
  
  /** Adds the Polygon into this View, and converts to trimesh first. **/
  public TriangleMesh attachChild(Polygon poly, double posY)
  {
    TriangleMesh tri = new TriangleMesh();
    updateTriangleMesh(tri, poly, posY, true);
    attachChild(tri);
    return tri;
  }
  
  public static void updateTriangleMesh(TriangleMesh tri, Polygon poly, double posY, boolean doubleSided)
  {
    ArrayList<Point2i> points = new ArrayList<Point2i>();
    for (int i=0; i< poly.getPoints().size(); i+=2)
    {
      Point2i p = new Point2i();
      p.x = poly.getPoints().get(i).intValue();
      p.y = poly.getPoints().get(i+1).intValue();
      points.add(p);
    }
    ArrayList<Triangle> triList = new ArrayList<Triangle>();
    PolygonToTriangle.polygonToTriangles(points, triList);
    tri.getTexCoords().clear();
    tri.getPoints().clear();
    tri.getTexCoords().addAll(0,0);
    int tci = 0; // Texture coordinate index
    for (Point2i curP : points)
    {
      tri.getPoints().addAll((float)curP.x, (float)posY, (float)curP.y);
    }
    for (Triangle curTri : triList)
    {
      int i1 = points.indexOf(curTri.getPoint(0));
      int i2 = points.indexOf(curTri.getPoint(1));
      int i3 = points.indexOf(curTri.getPoint(2));
      tri.getFaces().addAll(i1, tci, i2, tci, i3, tci);
      if (doubleSided) { tri.getFaces().addAll(i3, tci, i2, tci, i1, tci); }
    }
  }
  
  /** Adds the TriangleMesh into this View, and manages custom Transform. **/
  public Group3D attachChild(TriangleMesh tri)
  {
    MeshView meshView = new MeshView(tri);
    javafx.scene.paint.PhongMaterial m = new javafx.scene.paint.PhongMaterial(Color.YELLOW);
    meshView.setMaterial(m);
    meshView.setDrawMode(DrawMode.FILL);
    Group3D group = new Group3D();
    group.getChildren().add(meshView);
    group.setTranslateX(rect.getX());
    group.setTranslateZ(rect.getY());
    meshNode.getChildren().add(group);
    return group;
  }
  

  public void detachChild(TriangleMesh tri)
  {
    Node toRemove = findTriNode(tri);
    if (toRemove!=null)
    { 
      meshNode.getChildren().remove(toRemove);
System.out.println("NEO_DEBUG: Really removed!");
    }
  }
  
  private Node findTriNode(TriangleMesh tri)
  {
    for (Node curGroup : meshNode.getChildren())
    {
      if (curGroup instanceof Group3D)
      {
        for (Node meshView : ((Group3D) curGroup).getChildren())
        {
          if (meshView instanceof MeshView)
          {
            if (((MeshView)meshView).getMesh() == tri)
            {
              return curGroup;
            }
          }
        }
      }
    }
    return null;
  }
  
  private void handle(EventType type, MouseEvent ev)
  {
    double posX = meshViewGroup.getBoundsInParent().getMinX();
    double posY = meshViewGroup.getBoundsInParent().getMinY();
    if (type != EventType.DRAG)
    {
      if (ev.getX() < posX) { return; }
      if (ev.getY() < posY) { return; }
    }
    posX = ev.getX() - posX;
    posY = ev.getY() - posY;
    if (type == EventType.MOVE)
    {
      if (moveRect)
      {
        Layer findLayer = Layer.findLayer(posY);
        if (findLayer != null)
        {
          rectNode.setTranslateY(findLayer.y_3d);
          rect.setFill(Color.GREEN);
        }
        else
        {
          rectNode.setTranslateY(posY);
          rect.setFill(Color.TURQUOISE);
        }
      }
    }
    else if (type == EventType.DRAG)
    {
      if (dragPos == null)
      {
        dragPos = new Point2i((int)posX, (int)posY);
      }
      else
      {
        //rectNode.setVisible(false);
        moveRect = false; // TODO: Dieser Aufruf wirkt nicht!
        meshNodeYrotate.rotate(0.3 * (posY - dragPos.y), 0.0, 0.0);
        meshNode.rotate(0.0, -0.3 * (posX - dragPos.x), 0.0);
        dragPos.x = (int)posX;
        dragPos.y = (int)posY;
      }
    }
    else if (dragPos != null) // Click
    {
      dragPos = null;
    }
    else // Click
    {
      meshNode.setRotation(0.0, 0.0, 0.0);
      meshNodeYrotate.setRotation(0.0, 0.0, 0.0);
      moveRect = !moveRect;
      if (!moveRect)
      {
        Layer findLayer = Layer.findLayer(posY);
        if (findLayer != null)
        {
          Layer upperLayer = Layer.findLayer(findLayer.y_3d, true);
          Layer lowerLayer = Layer.findLayer(findLayer.y_3d, false);
          LoggingSystem.info(View3D.class, "Existing Layer --> " + posY);
          PaintObject.fromLayer(findLayer, panel);
          createShadowObjects(upperLayer, true);
          createShadowObjects(lowerLayer, false);
        }
        else
        {
          Layer upperLayer = Layer.findLayer(posY, true);
          Layer lowerLayer = Layer.findLayer(posY, false);
          LoggingSystem.info(View3D.class, "New empty Layer --> " + posY);
          Layer.newLayer(posY);
          createShadowObjects(upperLayer, true);
          createShadowObjects(lowerLayer, false);
        }
      }
      else
      {
        PaintObject.storeCurrentLayer(panel);
      }
    }
    ev.consume();
  }

  private void createShadowObjects(Layer layer, boolean upper)
  {
    if (layer==null) { return; }
    Color color = Color.PINK;
    if (upper) { color = Color.LIGHTGRAY; }
    ArrayList<PolyChain> polyChainList = layer.createPolyChainList();
    ArrayList<PolyChain> compareChainList = Layer.getCurrentLayer().createPolyChainList();
    for (PolyChain polyChain : polyChainList)
    {
      boolean doShadow = true;
      for (PolyChain compareC : compareChainList)
      {
        if (compareC.extPolyLower == polyChain) { doShadow = false; break; }
        if (compareC.extPolyUpper == polyChain) { doShadow = false; break; }
      }
      if (doShadow)
      {
        PaintObjectOfChain.createShadowPaintObj(polyChain, panel, color);
      }
    }
  }
}
