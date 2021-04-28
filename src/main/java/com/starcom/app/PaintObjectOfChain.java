package com.starcom.app;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import com.starcom.app.tools.ShapeTool;
import com.starcom.debug.LoggingSystem;

/** This class is a 2d-PaintObject, that is inherent to a chain of PaintObjects **/
public abstract class PaintObjectOfChain extends PaintObject
{
  /** Increment Counter when next obj from chain removed or extruded mesh was extended.**/
  public int trimeshIndexCounter = 1;
  private PolyChain polyChain;
  static int line_thick = 5;

  /** Polygon with last and next Object of chain. Used from ShapeTool. **/
  public PaintObjectOfChain(Node... nodes)
  {
    super(nodes);
  }
  
  private PaintObjectOfChain() {} // For loadObj() saveObj()
  
  public PolyChain getPolyChain()
  {
    if (polyChain==null)
    {
      polyChain = new PolyChain();
      polyChain.getObjects().add(this);
    }
    return polyChain;
  }
  
  public void setPolyChain(PolyChain polyChain)
  {
    this.polyChain = polyChain;
  }
  
  public void connectWithNext(PaintObjectOfChain newNextObj)
  {
    getPolyChain().getObjects().add(newNextObj);
    newNextObj.setPolyChain(getPolyChain());
  }
  

  public static void createShadowPaintObj(PolyChain polyChain, Pane pane, Color color)
  {
    ArrayList<Line> lines = new ArrayList<Line>();
    for (PaintObjectOfChain curObj : polyChain.getObjects())
    {
      Line curLine = (Line) curObj.getNodeList().get(0);
      Line line = new Line();
      line.setStartX(curLine.getStartX());
      line.setStartY(curLine.getStartY());
      line.setEndX(curLine.getEndX());
      line.setEndY(curLine.getEndY());
      line.setStroke(color);
      line.setStrokeWidth(line_thick);
      lines.add(line);
      pane.getChildren().add(line);
    }
    
    PaintObject shadowObject = new PaintObject(lines.toArray(new Line[lines.size()]))
    {
      @Override public String saveObj() { return null; }
      @Override public void loadObj(String data) {}
      @Override public void updateGizmoPositions(ArrayList<Node> gizmoList)
      {
System.out.println("PaintObjectOfChain: Brute Force!!!");
        //ShapeTool.view.detachChild(extPolyMesh);
        //String data = PolyChain.this.saveObj();
        //PolyChain cloneChain = PolyChain.loadObj(data);
        //extrudeMesh(cloneChain);
      }
      @Override public void moveGizmo(Node gizmo, double posX, double posY)
      {
System.out.println("PaintObjectOfChain: Empty2");
      }
      
      @Override public void appendGizmos(ArrayList<Node> gizmoList)
      { // Extrude mesh in Y.
        LoggingSystem.info(PaintObjectOfChain.class, "Make real!");
        String data = polyChain.saveObj();
        PolyChain cloneChain = PolyChain.loadObj(data);
        Layer.getCurrentLayer().getPaintObjects().addAll(cloneChain.getObjects());
Layer.storeCurrentLayer(Layer.getCurrentLayer());
float clone_y = (float)Layer.getCurrentLayer().y_3d;
float poly_y = (float)Layer.findLayer(polyChain.getObjects().get(0)).y_3d;
if (poly_y > clone_y)
{
cloneChain.extPolyLower = polyChain;
polyChain.extPolyUpper = cloneChain;
}
else
{
cloneChain.extPolyUpper = polyChain;
polyChain.extPolyLower = cloneChain;
}
        for (PaintObjectOfChain curObj : cloneChain.getObjects())
        { // Add poly to paint-area
          pane.getChildren().add(curObj.getNodeList().get(0));
          pane.getChildren().add(curObj.getNodeList().get(1));
        }
        for (Node curObj : getNodeList())
        { // Remove Shadow-Object from paint-area
          pane.getChildren().remove(curObj);
        }
        Layer.getCurrentLayer().getPaintObjects().remove(this);
        ShapeTool.fillShape(cloneChain.getObjects().get(0), pane);
//        float clone_y = (float)Layer.getCurrentLayer().y_3d;
//        float poly_y = (float)Layer.findLayer(PolyChain.this.getObjects().get(0)).y_3d;
        
        
//Layer.getCurrentLayer().getPaintObjects().addAll(cloneChain.getObjects());
polyChain.recreateExtrudedPolyMesh();
//        
//        TriangleMesh curPolyMesh = MeshCreator.createExtrudeMesh(PolyChain.this, cloneChain, poly_y, clone_y);
//        ShapeTool.view.attachChild(curPolyMesh);
//        if (poly_y > clone_y)
//        {
//          extPolyMesh = curPolyMesh;
//System.out.println("A_DEBUG: " + getObjects().get(0).type);
//        }
//        else
//        {
//          cloneChain.extPolyMesh = curPolyMesh;
//System.out.println("B_DEBUG: " + cloneChain.getObjects().get(0).type);
//        }

Layer.printStatistics();
      }
      
    };
    shadowObject.type = PaintObject.PaintObjType.ShadowObj;
  }
  
  /** Creates a DummyPaintObject, that ensures to clear the polygon on 3d-Layer-MoveY **/
  public static PaintObject createDummyPaintObject(Polygon fillPolygon, PaintObjType type)
  {
    PaintObject p = new PaintObject(fillPolygon)
    {
      @Override public void updateGizmoPositions(ArrayList<Node> gizmoList)
      {
  
  System.out.println("PaintObjectOfChain: Brute Force2!!!");
      }
      @Override public void moveGizmo(Node gizmo, double posX, double posY)
      {
  System.out.println("PaintObjectOfChain: Empty1");
      }
      @Override public void appendGizmos(ArrayList<Node> gizmoList) {}
      @Override public String saveObj() { return null; }
      @Override public void loadObj(String data) {}
    };
    p.type = type;
    return p;
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  

}
