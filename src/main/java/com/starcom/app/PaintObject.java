package com.starcom.app;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import com.starcom.paint.AbstractPaintObject;
import com.starcom.paint.IPaintBoard;

public abstract class PaintObject extends AbstractPaintObject
{
  public static String DATA_END_OF_PAINTOBJ = "\n=== NEXT P_OBJ ==\n";
  public enum PaintObjType {PaintObj, ShadowObj, FillObj};
  
  public PaintObjType type = PaintObjType.PaintObj;
  boolean b_paintObjActive = true;
  
  static
  {
    AbstractPaintObject.paintBoard = new IPaintBoard()
    {
      @Override
      public ArrayList<AbstractPaintObject> getPaintObjects()
      {
        return Layer.getCurrentLayer().getPaintObjects();
      }

      @Override
      public ArrayList<Node> getGizmoList()
      {
        return Layer.getCurrentLayer().getGizmoList();
      }
    };
  }
  
  public PaintObject(Node ...nodes)
  {
    super(nodes);
  }
  
  /** Set this PaintObject active on panel. **/
  public void setPaintObjectActive(Pane panel, boolean active)
  {
    if (active)
    {
      if (b_paintObjActive) { return; }

      for (Node poly : getNodeList())
      {
        panel.getChildren().add(poly);
        if (type==PaintObjType.FillObj) { poly.toBack(); }
      }
    }
    else
    {
      if (!b_paintObjActive) { return; }
      for (Node poly : getNodeList())
      {
        panel.getChildren().remove(poly);
      }
    }
    b_paintObjActive = active;
  }

  /** Loads the layer, and shows the PaintObjects. **/
  public static void fromLayer(Layer layer, Pane panel)
  {
    Layer.setCurrentLayer(layer);
    for (AbstractPaintObject obj : layer.getPaintObjects())
    {
      ((PaintObject)obj).setPaintObjectActive(panel, true);
    }
  }

  /** Stores the layer to layerList when objects are present, and clears panel. **/
  public static void storeCurrentLayer(Pane panel)
  {
    var clearList = new ArrayList<PaintObject>();
    for (AbstractPaintObject aobj : Layer.getCurrentLayer().getPaintObjects())
    {
      PaintObject obj = (PaintObject)aobj;
      obj.setGizmoActive(panel, false);
      obj.setPaintObjectActive(panel, false);
      if (obj.type == PaintObjType.ShadowObj) { clearList.add(obj); }
    }
    Layer.getCurrentLayer().getPaintObjects().removeAll(clearList);
    Layer.storeCurrentLayer(Layer.getCurrentLayer());
  }

}
