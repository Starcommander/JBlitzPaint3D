package com.starcom.app.mesh3d;

/*
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Source from Oracle: package moleculesampleapp; Classname: Xform.java
 */

import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public class Group3D extends Group {

    public enum RotateOrder { XYZ, XZY, YXZ, YZX, ZXY, ZYX }

    public Translate t  = new Translate(); 
    public Translate p  = new Translate(); 
    public Translate ip = new Translate(); 
    public Rotate rx = new Rotate();
    { rx.setAxis(Rotate.X_AXIS); }
    public Rotate ry = new Rotate();
    { ry.setAxis(Rotate.Y_AXIS); }
    public Rotate rz = new Rotate();
    { rz.setAxis(Rotate.Z_AXIS); }
    public Scale s = new Scale();

    public Group3D() { 
        super(); 
        getTransforms().addAll(t, rz, ry, rx, s); 
    }

    public Group3D(RotateOrder rotateOrder) { 
        super(); 
        // choose the order of rotations based on the rotateOrder
        switch (rotateOrder) {
        case XYZ:
            getTransforms().addAll(t, p, rz, ry, rx, s, ip); 
            break;
        case XZY:
            getTransforms().addAll(t, p, ry, rz, rx, s, ip); 
            break;
        case YXZ:
            getTransforms().addAll(t, p, rz, rx, ry, s, ip); 
            break;
        case YZX:
            getTransforms().addAll(t, p, rx, rz, ry, s, ip);  // For Camera
            break;
        case ZXY:
            getTransforms().addAll(t, p, ry, rx, rz, s, ip); 
            break;
        case ZYX:
            getTransforms().addAll(t, p, rx, ry, rz, s, ip); 
            break;
        }
    }

    /** Set new Position **/
    public void setTranslation(double x, double y, double z)
    {
      t.setX(x);
      t.setY(y);
      t.setZ(z);
    }
    
    /** Move from current position. **/
    public void move(double x, double y, double z)
    {
      t.setX(t.getX() + x);
      t.setY(t.getY() + y);
      t.setZ(t.getZ() + z);
    }

    /** Rotate angles. **/
    public void setRotation(double x, double y, double z)
    {
      rx.setAngle(x);
      ry.setAngle(y);
      rz.setAngle(z);
    }

    /** Rotate angles. **/
    public void rotate(double x, double y, double z)
    {
      rx.setAngle(rx.getAngle() + x);
      ry.setAngle(ry.getAngle() + y);
      rz.setAngle(rz.getAngle() + z);
    }
    
    public void setScale(double scaleFactor)
    {
      s.setX(scaleFactor);
      s.setY(scaleFactor);
      s.setZ(scaleFactor);
    }

    public void setScale(double x, double y, double z)
    {
      s.setX(x);
      s.setY(y);
      s.setZ(z);
    }

//    public void setPivot(double x, double y, double z)
//    {
//      p.setX(x);
//      p.setY(y);
//      p.setZ(z);
//      ip.setX(-x);
//      ip.setY(-y);
//      ip.setZ(-z);
//    }
}
