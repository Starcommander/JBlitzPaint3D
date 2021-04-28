package com.starcom.app.mesh3d;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import com.starcom.math.Point2i;

/** Source from user: hisui
 *  https://gist.github.com/hisui/5737683 **/

public class PolygonToTriangle
{
  private static double polygonArea(java.util.List<Point2i> points)
  {
    double area = 0;
    for (int i = 0; i < points.size(); ++i) {
      area += exteriorProduct(
                    points.get(i),
                    points.get((i + 1) % points.size()));
    }
    return area * 0.5;
  }

  private static double exteriorProduct(Point2i p0, Point2i p1) {
    return  p0.x * p1.y - p1.x * p0.y;
  }

  private static boolean isEmptyTriangle(Point2i a, Point2i b, Point2i c, LinkedTri list, boolean clockwise) {
    LinkedTri linkedTri = list;
    do {
      final Point2i p = linkedTri.b;
      if (p != a && p != b && p != c) {
        if (isClockwise(b, a, p) != clockwise &&
           isClockwise(a, c, p) != clockwise &&
           isClockwise(c, b, p) != clockwise) {
          return false;
        }
        }
    } while ((linkedTri = linkedTri.next) != list);
    return true;
  }

  public static boolean isClockwise(Point2i a, Point2i b, Point2i c) {
    return 0 <= (b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y);
  }

  public static boolean isClockwise(java.util.List<Point2i> points) {
    return 0 <= polygonArea(points);
  }

  public static class Triangle {
    Point2i a;
    Point2i b;
    Point2i c;
    
    private Triangle() {}

    Triangle(Point2i a, Point2i b, Point2i c) {
      this.a = a;
      this.b = b;
      this.c = c;
    }
    
    public Point2i getPoint(int index)
    {
      if (index == 0) { return a; }
      if (index == 1) { return b; }
      if (index == 2) { return c; }
      return null;
    }
  }

  public static void polygonToTriangles(List<Point2i> points, List<Triangle> triangles) {
          final boolean clockwise = isClockwise(points);
          final TreeSet<LinkedTri> set = new TreeSet<>();

          LinkedTri list = new LinkedTri();
//          {
             LinkedTri preTri = list;
              for (int i = 0; i < points.size(); ++i) {
                  int preNum = (i == 0 ? points.size(): i) - 1;
                  int nexNum = (i + 1) % points.size();
  
                  LinkedTri nexTri = new LinkedTri();
                  preTri.next = nexTri;
                  nexTri.prev = preTri;
  
                  nexTri.a = points.get(preNum);
                  nexTri.b = points.get(i);
                  nexTri.c = points.get(nexNum);
  
                  if(nexTri.isClockwise() == clockwise) {
                      set.add(nexTri);
}
else
{
System.out.println("Skip a tree: " + clockwise);

                  }
System.out.println("Nums: " + preNum + "," + i + "," + nexNum);
System.out.println("WasStep: " + i + " with point " + nexTri.b.x + "," + nexTri.b.y);
                  preTri = nexTri;
              }
              LinkedTri tmpLink = list.next;
              list = tmpLink;
              preTri.next = tmpLink;
//              list = prev.next = list.next;
              list.prev = preTri;
//          }
  
  outer:
    while (!set.isEmpty()) {
      Iterator<LinkedTri> i = set.iterator();
      boolean found = false;
      while (i.hasNext()) {
        LinkedTri linkedTri = i.next();
        Point2i a = linkedTri.a;
        Point2i b = linkedTri.b;
        Point2i c = linkedTri.c;
        if (isEmptyTriangle(a, b, c, linkedTri, clockwise)) {
          found = true;
          i.remove();
          LinkedTri prev = linkedTri.prev;
          LinkedTri next = linkedTri.next;
          set.remove(prev);
          set.remove(next);
          triangles.add(new Triangle(a, b, c));
          if (next.next == prev /*prev == next*/) {
            break outer;
          }
          prev.c = c; prev.distance = -1;
          next.a = a; next.distance = -1;
          prev.next = next;
          next.prev = prev;
          if (prev.isClockwise() == clockwise) set.add(prev);
          if (next.isClockwise() == clockwise) set.add(next);
          break;
        }
      }
      if (!found) {
        throw new IllegalStateException("found == false");
      }
    }
  }

  static class LinkedTri extends Triangle implements Comparable<LinkedTri> {
    
    LinkedTri prev;
    LinkedTri next;
    int distance = -1;
    
    @Override
    public int compareTo(LinkedTri that) {
      if (this == that) {
        return 0;
      }
      int delta = getDistance() - that.getDistance();
      return delta != 0 ? delta: hashCode() - that.hashCode(); // <(^_^;)
    }

    int getDistance() {
      if (distance == -1) {
        distance = (int) distanceSq(a.x,a.y,c.x,c.y);
      }
      return distance;
    }
    
    public double distanceSq(int px1, int py1, int px2, int py2) {
      double px = px1 - px2;
      double py = py1 - py2;
      return (px * px + py * py);
    }
    
    boolean isClockwise() { return PolygonToTriangle.isClockwise(a, b, c); }
  }
  
}
