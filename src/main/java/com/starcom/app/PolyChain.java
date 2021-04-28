package com.starcom.app;

import java.util.ArrayList;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.TriangleMesh;
import com.starcom.math.Point2i;
import com.starcom.app.PaintObject.PaintObjType;
import com.starcom.app.PaintObjectOfChain;
import com.starcom.app.mesh3d.MeshCreator;
import com.starcom.app.mesh3d.PolygonToTriangle;
import com.starcom.app.tools.ShapeTool;

/** This class is connects the PaintObjects with the 2d-mesh fillPolygon plus the 3d-mesh extrudedPolyMesh **/
public class PolyChain
{
  long debugID = com.starcom.Run.getId();
  PolyChain extPolyUpper;
  PolyChain extPolyLower;
  TriangleMesh extPolyMesh;
  Polygon fillPolygon = new Polygon();
  TriangleMesh fillPolygon3D = null;
  private Boolean isClock;
  private ArrayList<PaintObjectOfChain> objs = new ArrayList<PaintObjectOfChain>();
  
  /** Init exclusive in PaintObjectOfChain! **/
  protected PolyChain() {}
  
  public boolean isClockwise()
  {
    if (isClock==null)
    {
      ArrayList<Point2i> pList = new ArrayList<Point2i>();
      for (PaintObjectOfChain curObj : objs)
      {
        Line line = (Line)curObj.getNodeList().get(0);
        Point2i p = new Point2i();
        p.x = (int) line.getStartX();
        p.y = (int) line.getStartY();
        pList.add(p);
      }
      isClock = PolygonToTriangle.isClockwise(pList);
    }
    return isClock;
  }

  public void setExtrudedPoly(PolyChain extPoly, boolean upper)
  {
    if (upper)
    {
      extPolyUpper = extPoly;
      extPoly.extPolyLower = this;
    }
    else
    {
      extPolyLower = extPoly;
      extPoly.extPolyUpper = this;
    }
  }
  
  public PolyChain getExtrudedPoly(boolean upper)
  {
    if (upper)
    {
      return extPolyUpper;
    }
    else
    {
      return extPolyLower;
    }
  }
  
  /** Delete this from Extruded Chains, if set. Clear mesh. **/
  private void clearThisChain()
  {
    if (extPolyLower!=null) { extPolyLower.extPolyUpper = extPolyUpper; }
    if (extPolyUpper!=null) { extPolyUpper.extPolyLower = extPolyLower; }
  }
  
  private void clearExtrudedPolyMesh()
  {
    if (extPolyMesh!=null)
    {
      System.out.println(this.getClass().getName() + ": Entfernt: ExtrudedMesh=" +  extPolyMesh);
      ShapeTool.view.detachChild(extPolyMesh);
      extPolyMesh = null;
    }
  }
  
  /** Find the next object of chain, null on end, except first on rotate. **/
  public PaintObjectOfChain getNextObj(PaintObjectOfChain curObj, boolean rotate)
  {
    int index = objs.indexOf(curObj);
    if (objs.size() > index+1) { return objs.get(index+1); }
    if (rotate) { return objs.get(0); }
    return null;
  }
  
  /** Find the previous object of chain, null on start, except last on rotate. **/
  public PaintObjectOfChain getLastObj(PaintObjectOfChain curObj, boolean rotate)
  {
    int index = objs.indexOf(curObj);
    if (index!=0) { return objs.get(index-1); }
    if (rotate) { return objs.get(objs.size()-1); }
    return null;
  }
  
  /** Clone the poly for 3D-View that will update automatically. **/
  public void attachFillPoly(View3D view3d)
  {
    if (fillPolygon3D == null)
    {
      fillPolygon3D = view3d.attachChild(fillPolygon, view3d.getRectPositionY());
    }
    else
    {
      view3d.updateTriangleMesh(fillPolygon3D, fillPolygon, view3d.getRectPositionY(), true);
    }
  }
  
  /** Fill shape (or modify shape) and create PaintObject (on first call). **/
  public void fillChain(Pane pane)
  {
    fillPolygon.getPoints().clear();
    for(PaintObjectOfChain nextObj : getObjects())
    {
      Line line = (Line)nextObj.getNodeList().get(0);
      fillPolygon.getPoints().addAll(line.getStartX(), line.getStartY());
    }
    if (!pane.getChildren().contains(fillPolygon))
    {
      Color fillCol = BlitzPaintFrame.color.interpolate(Color.WHITE, 0.8);
      fillPolygon.setFill(fillCol);
      pane.getChildren().add(fillPolygon);
      fillPolygon.toBack();
      PaintObjectOfChain.createDummyPaintObject(fillPolygon, PaintObjType.FillObj);
    }
  }
  
  public boolean hasExtrudedPolyMesh() { return (extPolyMesh!=null); }
  
  private void recreateExtrudedPolyMeshUpperOnly()
  {
    float cur_y = (float)Layer.findLayer(this.getObjects().get(0)).y_3d;
    clearExtrudedPolyMesh();
    PolyChain upperChain = getExtrudedPoly(true);
    if (upperChain!=null)
    {
      float upper_y = (float)Layer.findLayer(upperChain.getObjects().get(0)).y_3d;
      extPolyMesh = MeshCreator.createExtrudeMesh(this, upperChain, cur_y, upper_y);
      ShapeTool.view.attachChild(extPolyMesh);
    }
  }
  
  public void recreateExtrudedPolyMesh()
  {
    recreateExtrudedPolyMeshUpperOnly();
    PolyChain lowerChain = getExtrudedPoly(false);
    if (lowerChain != null)
    {
      lowerChain.recreateExtrudedPolyMeshUpperOnly();
    }
  }

  public static PolyChain loadObj(String data)
  {
    String objs[] = data.split(PaintObject.DATA_END_OF_PAINTOBJ);
    PaintObjectOfChain lastObj = null;
    for (String objString : objs)
    {
      PaintObjectOfChain newObj = ShapeTool.createPaintObjectStatic(ShapeTool.makeLine(), ShapeTool.makeCircle());
      newObj.loadObj(objString);
      if (lastObj!=null) { lastObj.connectWithNext(newObj); }
      lastObj = newObj;
    }
    return lastObj.getPolyChain();
  }

  public String saveObj()
  {
    StringBuilder sb = new StringBuilder();
    for (int i=0; i<getObjects().size(); i++)
    {
      sb.append(getObjects().get(i).saveObj());
      sb.append(PaintObject.DATA_END_OF_PAINTOBJ);
    }
    return sb.toString();
  }

  public ArrayList<PaintObjectOfChain> getObjects() { return objs; }
}
