package com.starcom.app.mesh3d;

import javafx.scene.shape.Line;
import javafx.scene.shape.TriangleMesh;
import com.starcom.app.PaintObjectOfChain;
import com.starcom.app.PolyChain;
//import com.starcom.app.tools.ShapeTool;

public class MeshCreator
{
  public static TriangleMesh createExtrudeMesh(PolyChain polyChain, PolyChain cloneChain, float poly_y, float clone_y)
  {
    boolean doExchange = poly_y > clone_y;
    if (!polyChain.isClockwise()) { doExchange = !doExchange; }
    if (doExchange)
    { // Exchange polyChain with cloneChain
      PolyChain curChain = polyChain;
      polyChain = cloneChain;
      cloneChain = curChain;
      float cur_y = poly_y;
      poly_y = clone_y;
      clone_y = cur_y;
    }
    TriangleMesh extPolyMesh = new TriangleMesh();
//    PaintObjectOfChain polyObj = polyChain.getObjects().get(0);
//    PaintObjectOfChain cloneObj = cloneChain.getObjects().get(0);
    /* Add Texture coordinates */
    extPolyMesh.getTexCoords().addAll(0,0);
    /* Add points! */
    for (PaintObjectOfChain curObj : polyChain.getObjects())
    {
      float px = (float) ((Line)curObj.getNodeList().get(0)).getStartX();
      float pz = (float) ((Line)curObj.getNodeList().get(0)).getStartY();
      extPolyMesh.getPoints().addAll(px, poly_y, pz);
    }
    for (PaintObjectOfChain curObj : cloneChain.getObjects())
    {
      float px = (float) ((Line)curObj.getNodeList().get(0)).getStartX();
      float pz = (float) ((Line)curObj.getNodeList().get(0)).getStartY();
      extPolyMesh.getPoints().addAll(px, clone_y, pz);
    }
    /* Add indexes */
    int polyIndex = -1;
    int cloneIndex = -1;
    int polyCounter = 0;
    int cloneCounter = 0;
    int tci = 0; // Texture coordinate index.
    int c0 = polyChain.getObjects().size();
    while(true)
    {
      if (polyCounter==0)
      {
        polyIndex++;
        if (polyChain.getObjects().size() <= polyIndex) { break; }
        polyCounter = polyChain.getObjects().get(polyIndex).trimeshIndexCounter;
      }
      if (cloneCounter==0)
      {
        cloneIndex++;
        if (cloneChain.getObjects().size() <= cloneIndex) { break; }
        cloneCounter = cloneChain.getObjects().get(cloneIndex).trimeshIndexCounter;
      }
      if (polyCounter==1)
      {
        int lastIndex = polyIndex-1;
        if (lastIndex == -1) { lastIndex =  polyChain.getObjects().size() - 1; }
        extPolyMesh.getFaces().addAll(polyIndex, tci, lastIndex, tci, c0 + cloneIndex, tci);
//        ShapeTool.debugPrint("Add indexes: " + polyIndex + " " + (c0 + cloneIndex) + " " + lastIndex);
      }
      if (cloneCounter==1)
      {
        int lastIndex = cloneIndex-1;
        if (lastIndex == -1) { lastIndex =  cloneChain.getObjects().size() - 1; }
        int lastPolyIndex = polyIndex-1;
        if (lastPolyIndex == -1) { lastPolyIndex =  polyChain.getObjects().size() - 1; }
        extPolyMesh.getFaces().addAll(c0 + cloneIndex, tci, lastPolyIndex, tci, c0 + lastIndex, tci);
//        ShapeTool.debugPrint("Add indexes: " + (c0+cloneIndex) + " " + (c0+lastIndex) + " " + lastPolyIndex);
      }
      polyCounter--;
      cloneCounter--;

//      if (polyCounter==1 && polyChain.getObjects().size() > (polyIndex+1))
//      {
//        int thirdIndex = polyIndex+1;
//        extPolyMesh.getFaces().addAll(polyIndex, tci, thirdIndex, tci, cloneIndex);
//      }
//      
//      PaintObjectOfChain nextPolyObj = polyChain.getNextObj(polyObj, false);
//      PaintObjectOfChain nextCloneObj = cloneChain.getNextObj(cloneObj, false);
//      if (nextPolyObj == null && nextCloneObj == null)
//      {
//        doContinue = false;
//        nextPolyObj = polyChain.getNextObj(polyObj, true);
//        nextCloneObj = cloneChain.getNextObj(cloneObj, true);
//      }
//      double px = ((Line)polyObj.getNodeList().get(0)).getStartX();
//      double pz = ((Line)polyObj.getNodeList().get(0)).getStartY();
//      extPolyMesh.getPoints().addAll(double[px, poly_y, pz]);
//
//      polyObj = nextPolyObj;
//      cloneObj = nextCloneObj;
    }
//    ShapeTool.debugPrint("Max index value: " + ((extPolyMesh.getPoints().size()/3.0f)-1.0f));
    return extPolyMesh;
  }
}
