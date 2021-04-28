package com.starcom.app.tools;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import com.starcom.app.PaintObjectOfChain;
import com.starcom.app.PolyChain;
import com.starcom.paint.tools.EditTool;

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
        setMultiGizmoActive(true);
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
  private void setMultiGizmoActive(boolean active)
  {
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

}
