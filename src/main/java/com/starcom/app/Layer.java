package com.starcom.app;

import java.util.ArrayList;

import javafx.scene.Node;
import com.starcom.app.PolyChain;
import com.starcom.paint.AbstractPaintObject;
import com.starcom.debug.LoggingSystem;

/** A 3DLayer for Y-Height. **/
public class Layer
{
  private static ArrayList<Layer> layerList = new ArrayList<Layer>();
  private static double tolerance = 5.0;
  private static Layer layer = new Layer();

  /** Y height-position of Layer on 3d objects **/
  public double y_3d = 0.0;

  ArrayList<AbstractPaintObject> paintObjects = new ArrayList<AbstractPaintObject>();
  ArrayList<Node> gizmoList = new ArrayList<Node>();

  public Layer() {}

  public ArrayList<AbstractPaintObject> getPaintObjects() { return paintObjects; }
  public ArrayList<Node> getGizmoList() { return gizmoList; }

  public static ArrayList<Layer> getLayerList() { return layerList; }

  /** Find the Layer near to height, if any. **/
  public static Layer findLayer(double height)
  {
    for (Layer layer : layerList)
    {
      if (layer.y_3d < height-tolerance) { continue; }
      if (layer.y_3d > height+tolerance) { continue; }
      return layer;
    }
    return null;
  }

  /** Find the Layer of polyChain, if any. **/
  public static Layer findLayer(PaintObjectOfChain paintChain)
  {
    for (Layer layer : layerList)
    {
      if (layer.getPaintObjects().contains(paintChain)) { return layer; }
    }
    return null;
  }

  /** Find the upper or lower Layer, if any. **/
  public static Layer findLayer(double height, boolean upper)
  {
    Layer nearestLayer = null;
    for (Layer layer : layerList)
    {
      if (upper)
      {
        if (layer.y_3d >= height) { continue; }
        if (nearestLayer!=null && layer.y_3d<nearestLayer.y_3d) { continue; }
        nearestLayer = layer;
      }
      else
      {
        if (layer.y_3d <= height) { continue; }
        if (nearestLayer!=null && layer.y_3d>nearestLayer.y_3d) { continue; }
        nearestLayer = layer;
      }
    }
    return nearestLayer;
  }

  /** Create a new layer on position y_3d **/
  public static void newLayer(double y_3d)
  {
    Layer layer = new Layer();
    layer.y_3d = y_3d;
    Layer.setCurrentLayer(layer);
  }

  /** Stores the layer to layerList when objects are present.
   *  @return True if layer was stored. **/
  public static boolean storeCurrentLayer(Layer layer)
  {
    if (layer.gizmoList.size()==0 && layer.paintObjects.size()==0) { return false; }
    if (!layerList.contains(layer))
    {
      layerList.add(layer);
      LoggingSystem.info(Layer.class, "Layer stored at " + layer.y_3d);
    }
    return true;
  }

  public ArrayList<PolyChain> createPolyChainList()
  {
    ArrayList<PolyChain> polyChainList = new ArrayList<PolyChain>();
    for (int i=0; i<paintObjects.size(); i++)
    {
      if (!(paintObjects.get(i) instanceof PaintObjectOfChain)) { continue; }
      PaintObjectOfChain curObj = (PaintObjectOfChain) paintObjects.get(i);
      if (polyChainList.contains(curObj.getPolyChain())) { continue; }
      polyChainList.add(curObj.getPolyChain());
    }
    return polyChainList;
  }

  public static void setCurrentLayer(Layer setLayer)
  {
    if (setLayer==null)
    {
      throw new IllegalStateException("Layer must not be null!");
    }
    layer = setLayer;
  }

  public static Layer getCurrentLayer()
  {
    return layer;
  }

  public static void printStatistics()
  {
    for (Layer curL : Layer.getLayerList())
    {
      System.out.println(Layer.class.getName() + ": ->Layer");
      for (PolyChain curC : curL.createPolyChainList())
      {
        String type = curC.getObjects().get(0).type.toString();
        int size = curC.getObjects().size();
      System.out.println(Layer.class.getName() + ": ---->PolyChain: " + size + "/" + type + "/" + curC.hasExtrudedPolyMesh() + "/" + curC.debugID);
      }
    }
  }
}
