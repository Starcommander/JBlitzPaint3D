package com.starcom.app.tools;

import com.starcom.app.PaintObject;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import com.starcom.app.PaintObjectOfChain;
import com.starcom.app.PolyChain;
import com.starcom.paint.AbstractPaintObject;
import com.starcom.paint.Frame;
import com.starcom.paint.tools.EditTool;
import java.util.ArrayList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class EditTool3D extends EditTool
{
  public EditTool3D()
  {
    EditTool.setPostUpdateHook((Object o1, Object o2) ->
    {
      EventType evType = (EventType)o1;
      MouseEvent event = (MouseEvent)o2;
      boolean secondMB = event.getButton() == MouseButton.SECONDARY;
      if (evType == EventType.RELEASE && getCurGizmo()!=null)
      {
        postHandleUpdate();
      }
      if (secondMB && evType == EventType.CLICK && getCurPaintObj() != null)
      {
        setMultiGizmoActive();
      }
      return null;
    });
  }

  private void postHandleUpdate()
  {
//        System.out.println("EditTool: Updating! " + curObj);
        if (getCurPaintObj() instanceof PaintObjectOfChain)
        {
          PaintObjectOfChain curP = (PaintObjectOfChain) getCurPaintObj();
          PolyChain curChain = curP.getPolyChain();
          curChain.recreateExtrudedPolyMesh();
          curChain.attachFillPoly(ShapeTool.view);
//          curChain.fillChain(pane); // Refresh XY-Poly?
          
//          
//          float cur_y = (float)Layer.findLayer(curP).y_3d;
//          curChain.clearThisChain(false, true);
//          PolyChain upperChain = curChain.getExtrudedPoly(true);
//          float upper_y = (float)Layer.findLayer(upperChain.getObjects().get(0)).y_3d;
//          
//      //TODO: Hier gegebenenfalls ExtrudeMesh, wenn vorhanden.
//          curChain.se MeshCreator.createExtrudeMesh(curChain, upperChain, cur_y, upper_y);
//      ShapeTool.view.attachChild(curPolyMesh);
////      Layer.printStatistics();
        }
  }

  /** Select whole chain for move/rotate/remove **/
  private void setMultiGizmoActive()
  {
    var p = getCurPaintObj();
    if (p==null) { return; }
    if (! (p instanceof PaintObjectOfChain)) { return; }
//    getCurPaintObj().setGizmoActive(pane, false);
    AbstractPaintObject.clearGizmos(getPane());
    
    var multiGizmoPO = createMultiPaintObject((PaintObjectOfChain)p);
    multiGizmoPO.setGizmoActive(getPane(), true);
    
    
//    if (multiGizmoPO != null && !active)
//    {
//      multiGizmoPO.setGizmoActive(getPane(), active);
//      multiGizmoPO = null;
//    }
//    else if (multiGizmoPO == null && active)
//    {
//      multiGizmoPO = createMultiPaintObject((PaintObjectOfChain)p);
//      multiGizmoPO.setGizmoActive(getPane(), active);
//    }
// TODO: curObj wurde bereits aktiviert: Gizmo deaktivieren, gesamte chain aktivieren.
//    if (active)
//    {
////      if (b_gizmoActive) { return; }
//      if (Layer.getCurrentLayer().getGizmoList().size()!=0) { clearGizmos(panel); }
//      appendGizmos(getLayer().getGizmoList());
//      updateGizmoPositions(getLayer().getGizmoList());
//      for (Node gizmo : getLayer().getGizmoList())
//      {
//        panel.getChildren().add(gizmo);
//      }
//    }
//    else
//    {
////      if (!b_gizmoActive) { return; }
//      for (Node gizmo : getLayer().getGizmoList())
//      {
//        panel.getChildren().remove(gizmo);
//      }
//      getLayer().getGizmoList().clear();
//    }
//    b_gizmoActive = active;
  }
  
  private AbstractPaintObject createMultiPaintObject(final PaintObjectOfChain p)
  {
    ImageView mv_image = Frame.createImageView(new Image("com/starcom/app/icons/hand_cursor.png"));
    ImageView rot_image = Frame.createImageView(new Image("com/starcom/app/icons/edit_undo.png"));
    ImageView scale_image = Frame.createImageView(new Image("com/starcom/app/icons/go_up.png"));
    mv_image.setFitWidth(40);
    mv_image.setFitHeight(40);
    rot_image.setFitWidth(40);
    rot_image.setFitHeight(40);
    scale_image.setFitWidth(40);
    scale_image.setFitHeight(40);
 
    return new PaintObject()
    {
      @Override
      public void moveGizmo(Node node, double x, double y)
      {
        int type = paintBoard.getGizmoList().indexOf(node); // 0=mv 1=rot 2=scale
        if (type==0)
        { // move
          double mx = ((ImageView)node).getX();
          double my = ((ImageView)node).getY();
          var nextObj = p;
          while ((nextObj = p.getPolyChain().getNextObj(nextObj,true)) != nextObj)
          {
            doMove(nextObj, mx, my);
          }
          doMove(nextObj, mx, my);
        }
        else if (type==1)
        { // rotate
          
        }
        else if (type==2)
        { // scale
          
        }
      }
      
      private void doMove(PaintObjectOfChain o, double mx, double my)
      {
        System.out.println("MultiPaintObject.doMove()");
      }

      @Override
      public void appendGizmos(ArrayList<Node> gizmoList)
      {
        gizmoList.add(mv_image);
        gizmoList.add(rot_image);
        gizmoList.add(scale_image);
      }

      @Override
      public void updateGizmoPositions(ArrayList<Node> gizmoList)
      {
        PaintObjectOfChain poc = (PaintObjectOfChain)getCurPaintObj();
        if (poc==null) { return; }
        var center = poc.getPolyChain().calculateCenter();
        for (int i=0; i<gizmoList.size(); i++)
        {
          var gizmo = (ImageView)gizmoList.get(i);
          gizmo.setX(center.x + (40*i) - 40);
          gizmo.setY(center.y);
        }
      }

      @Override
      public String saveObj()
      {
        return "";
      }

      @Override
      public void loadObj(String string)
      {
      }
      
    };
  }
}
