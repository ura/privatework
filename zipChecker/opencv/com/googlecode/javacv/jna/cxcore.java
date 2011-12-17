/*
 * Copyright (C) 2009,2010 Samuel Audet
 *
 * This file is part of JavaCV.
 *
 * JavaCV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version (subject to the "Classpath" exception
 * as provided in the LICENSE.txt file that accompanied this code).
 *
 * JavaCV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaCV.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * This file is based on information found in cxtypes.h, cxcore.h and cxerror.h
 * of OpenCV 2.0, which are covered by the following copyright notice:
 *
 *                          License Agreement
 *                For Open Source Computer Vision Library
 *
 * Copyright (C) 2000-2008, Intel Corporation, all rights reserved.
 * Copyright (C) 2009, Willow Garage Inc., all rights reserved.
 * Third party copyrights are property of their respective owners.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * Redistribution's of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *   * Redistribution's in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *   * The name of the copyright holders may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * This software is provided by the copyright holders and contributors "as is" and
 * any express or implied warranties, including, but not limited to, the implied
 * warranties of merchantability and fitness for a particular purpose are disclaimed.
 * In no event shall the Intel Corporation or contributors be liable for any direct,
 * indirect, incidental, special, exemplary, or consequential damages
 * (including, but not limited to, procurement of substitute goods or services;
 * loss of use, data, or profits; or business interruption) however caused
 * and on any theory of liability, whether in contract, strict liability,
 * or tort (including negligence or otherwise) arising in any way out of
 * the use of this software, even if advised of the possibility of such damage.
 *
 */

package com.googlecode.javacv.jna;

import com.sun.jna.Callback;
import com.sun.jna.Function;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.StringArray;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayDeque;
import java.util.HashMap;

/**
 *
 * @author Samuel Audet
 */
public class cxcore {
    // OpenCV does not always install itself in the PATH :(
    public static final String[] paths = { "/usr/local/lib/", "/usr/local/lib64/",
            "/opt/local/lib/", "/opt/local/lib64/",
            "C:/OpenCV2.1/bin/Release/", "C:/OpenCV2.1/bin/",
            "C:/OpenCV2.0/bin/Release/", "C:/OpenCV2.0/bin/",
            "C:/Program Files/OpenCV/bin/", "C:/Program Files (x86)/OpenCV/bin/" };
    public static final String[] libnames = { "cxcore", "cxcore_64", "cxcore210", "cxcore210_64",
            "cxcore200", "cxcore200_64", "cxcore110", "cxcore110_64", "cxcore100", "cxcore100_64" };
    public static final String libname = Loader.load(paths, libnames);


    public static class v10     extends cxcore  {  }
    public static class v11     extends v11or20 {  }
    public static class v20     extends v11or20 {  }
    public static class v11or20 extends cxcore  {
        public static final String libname = Loader.load(paths, libnames);

        public static final int
                CV_SORT_EVERY_ROW = 0,
                CV_SORT_EVERY_COLUMN = 1,
                CV_SORT_ASCENDING = 0,
                CV_SORT_DESCENDING = 16;
        public static native void cvSort(CvArr src, CvArr dst/*=null*/, CvArr idxmat/*=null*/, int flags/*=0*/);

        public static native void cvCompleteSymm(CvMat matrix, int LtoR/*=0*/);

        public static native void cvSolvePoly(CvMat coeffs, CvMat roots, int maxiter/*=20*/, int fig/*=100*/);


        public interface CvLoadImageFunc extends Callback {
            IplImage callback(String filename, int colorness);
        }
        public interface CvLoadImageMFunc extends Callback {
            CvMat callback(String filename, int colorness);
        }
        public interface CvSaveImageFunc extends Callback {
            int callback(String filename, CvArr image);
        }
        public interface CvShowImageFunc extends Callback {
            void callback(String windowname, CvArr image);
        }
        public static native int cvSetImageIOFunctions(CvLoadImageFunc _load_image,
                CvLoadImageMFunc _load_image_m, CvSaveImageFunc _save_image,
                CvShowImageFunc _show_image);
        public static native int cvSetImageIOFunctions(Function _load_image,
                Function _load_image_m, Function _save_image,
                Function _show_image);
    }
    public static class v21 extends v11or20 {
        public static final String libname = Loader.load(paths, libnames);

        public static native void cvRectangleR(CvArr img, CvRect.ByValue r, CvScalar.ByValue color,
                int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/);

        public static final int
                CV_CPU_NONE   =  0,
                CV_CPU_MMX    =  1,
                CV_CPU_SSE    =  2,
                CV_CPU_SSE2   =  3,
                CV_CPU_SSE3   =  4,
                CV_CPU_SSSE3  =  5,
                CV_CPU_SSE4_1 =  6,
                CV_CPU_SSE4_2 =  7,
                CV_CPU_AVX    = 10,
                CV_HARDWARE_MAX_FEATURE = 255;

        public static native int cvCheckHardwareSupport(int feature);
    }


    public static class CvPoint extends Structure {
        public static boolean autoSynch = true;
        public CvPoint() { setAutoSynch(autoSynch); }
        public CvPoint(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvPoint.class) read(); }
        public CvPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public CvPoint(CvPoint2D32f o) {
            this.x = (int)o.x;
            this.y = (int)o.y;
        }


        public static void fillArray(CvPoint[] array, int[] pts, int offset, int length) {
            for (int i = 0; i < length/2; i++) {
                array[i].set(pts[offset + i*2], pts[offset + i*2+1]);
            }
        }
        public static void fillArray(CvPoint[] array, int ... pts) {
            fillArray(array, pts, 0, pts.length);
        }
        public static void fillArray(CvPoint[] array, byte shift, double[] pts, int offset, int length) {
            int[] a = new int[length];
            for (int i = 0; i < length; i++) {
                a[i] = (int)Math.round(pts[offset + i] * (1<<shift));
            }
            fillArray(array, a, 0, length);
        }
        public static void fillArray(CvPoint[] array, byte shift, double ... pts) {
            fillArray(array, shift, pts, 0, pts.length);
        }


        public static CvPoint[] createArray(int size) {
            CvPoint p = new CvPoint(new Memory(Native.getNativeSize(ByValue.class) * size));
            return (CvPoint[])p.toArray(size);
        }
        public static CvPoint[] createArray(int[] pts, int offset, int length) {
            CvPoint[] a = CvPoint.createArray(length/2);
            fillArray(a, pts, offset, length);
            return a;
        }
        public static CvPoint[] createArray(int ... pts) {
            return createArray(pts, 0, pts.length);
        }
        public static CvPoint[] createArray(byte shift, double[] pts, int offset, int length) {
            CvPoint[] a = CvPoint.createArray(length/2);
            fillArray(a, shift, pts, offset, length);
            return a;
        }
        public static CvPoint[] createArray(byte shift, double ... pts) {
            return createArray(shift, pts, 0, pts.length);
        }

        private static final String[] fieldOrder = { "x", "y" };
        { setFieldOrder(fieldOrder); }
        public int x;
        public int y;

        public void set(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public void set(CvPoint o) {
            this.x = o.x;
            this.y = o.y;
        }
        public void set(CvPoint2D32f o, byte shift) {
            this.x = (int)Math.round(o.x * (1<<shift));
            this.y = (int)Math.round(o.y * (1<<shift));
        }
        public void set(CvPoint2D64f o, byte shift) {
            this.x = (int)Math.round(o.x * (1<<shift));
            this.y = (int)Math.round(o.y * (1<<shift));
        }

        public static class ByValue extends CvPoint implements Structure.ByValue {
            public ByValue() { }
            public ByValue(int x, int y) {
                super(x, y);
            }
            public ByValue(CvPoint o) {
                x = o.x;
                y = o.y;
            }
        }
        public ByValue byValue() {
            return this instanceof ByValue ? (ByValue)this : new ByValue(this);
        }

        public static class ByReference extends CvPoint implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvPoint p) {
                setStructure(p);
            }
            public CvPoint getStructure() {
                return new CvPoint(getValue());
            }
            public void getStructure(CvPoint p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvPoint p) {
                p.write();
                setValue(p.getPointer());
            }
            public static PointerByReference[] createArray(int size) {
                PointerByReference[] a = new PointerByReference[size];
                Pointer p = new Memory(Pointer.SIZE * size);
                for (int i = 0; i < size; i++) {
                    a[i] = new PointerByReference();
                    a[i].setPointer(p.share(Pointer.SIZE * i));
                }
                return a;
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }

        @Override public String toString() { return "(" + x + ", " + y + ")"; }

        public static final ByValue ZERO = new ByValue(0, 0);
    }

    public static CvPoint.ByValue cvPoint(int x, int y) {
        return new CvPoint.ByValue(x, y);
    }

    public static class CvPoint2D32f extends Structure {
        public static boolean autoSynch = true;
        public CvPoint2D32f() { setAutoSynch(autoSynch); }
        public CvPoint2D32f(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvPoint2D32f.class) read(); }
        public CvPoint2D32f(double x, double y) {
            this.x = (float)x;
            this.y = (float)y;
        }
        public CvPoint2D32f(CvPoint o) {
            this.x = o.x;
            this.y = o.y;
        }

        public static CvPoint2D32f[] createArray(int size) {
            CvPoint2D32f p = new CvPoint2D32f(new Memory(Native.getNativeSize(ByValue.class) * size));
            return (CvPoint2D32f[])p.toArray(size);
        }
        public static CvPoint2D32f[] createArray(double[] pts, int offset, int length) {
            CvPoint2D32f[] a = CvPoint2D32f.createArray(length/2);
            for (int i = 0; i < length/2; i++) {
                a[i].set(pts[offset + i*2], pts[offset + i*2+1]);
            }
            return a;
        }
        public static CvPoint2D32f[] createArray(double ... pts) {
            return createArray(pts, 0, pts.length);
        }

        private static final String[] fieldOrder = { "x", "y" };
        { setFieldOrder(fieldOrder); }
        public float x;
        public float y;

        public void set(double x, double y) {
            this.x = (float)x;
            this.y = (float)y;
        }
        public void set(CvPoint o) {
            this.x = o.x;
            this.y = o.y;
        }
        public void set(CvPoint2D32f o) {
            this.x = o.x;
            this.y = o.y;
        }
        public void set(CvPoint2D64f o) {
            this.x = (float)o.x;
            this.y = (float)o.y;
        }

        public static class ByValue extends CvPoint2D32f implements Structure.ByValue {
            public ByValue() { }
            public ByValue(double x, double y) {
                super(x, y);
            }
            public ByValue(CvPoint2D32f o) {
                x = o.x;
                y = o.y;
            }
        }
        public ByValue byValue() {
            return this instanceof ByValue ? (ByValue)this : new ByValue(this);
        }

        @Override public String toString() { return "(" + x + ", " + y + ")"; }
    }

    public static CvPoint2D32f.ByValue cvPoint2D32f(double x, double y) {
        return new CvPoint2D32f.ByValue(x, y);
    }

    public static class CvPoint3D32f extends Structure {
        public static boolean autoSynch = true;
        public CvPoint3D32f() { setAutoSynch(autoSynch); }
        public CvPoint3D32f(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvPoint3D32f.class) read(); }
        public CvPoint3D32f(double x, double y, double z) {
            this.x = (float)x;
            this.y = (float)y;
            this.z = (float)z;
        }
        public CvPoint3D32f(CvPoint o) {
            this.x = o.x;
            this.y = o.y;
            this.z = 0;
        }

        public static CvPoint3D32f[] createArray(int size) {
            CvPoint3D32f p = new CvPoint3D32f(new Memory(Native.getNativeSize(ByValue.class) * size));
            return (CvPoint3D32f[])p.toArray(size);
        }
        public static CvPoint3D32f[] createArray(double[] pts, int offset, int length) {
            CvPoint3D32f[] a = CvPoint3D32f.createArray(length/3);
            for (int i = 0; i < length/3; i++) {
                a[i].set(pts[offset + i*3], pts[offset + i*3+1], pts[offset + i*3+2]);
            }
            return a;
        }
        public static CvPoint3D32f[] createArray(double ... pts) {
            return createArray(pts, 0, pts.length);
        }

        private static final String[] fieldOrder = { "x", "y", "z" };
        { setFieldOrder(fieldOrder); }
        public float x;
        public float y;
        public float z;

        public void set(double x, double y, double z) {
            this.x = (float)x;
            this.y = (float)y;
            this.z = (float)z;
        }
        public void set(CvPoint o) {
            this.x = o.x;
            this.y = o.y;
            this.z = 0;
        }
        public void set(CvPoint2D32f o) {
            this.x = o.x;
            this.y = o.y;
            this.z = 0;
        }
        public void set(CvPoint2D64f o) {
            this.x = (float)o.x;
            this.y = (float)o.y;
            this.z = 0;
        }

        public static class ByValue extends CvPoint3D32f implements Structure.ByValue {
            public ByValue() { }
            public ByValue(double x, double y, double z) {
                super(x, y, z);
            }
            public ByValue(CvPoint3D32f o) {
                x = o.x;
                y = o.y;
                z = o.z;
            }
        }
        public ByValue byValue() {
            return this instanceof ByValue ? (ByValue)this : new ByValue(this);
        }

        @Override public String toString() { return "(" + x + ", " + y + ", " + z + ")"; }
    }

    public static CvPoint3D32f.ByValue cvPoint3D32f(double x, double y, double z) {
        return new CvPoint3D32f.ByValue(x, y, z);
    }

    public static class CvPoint2D64f extends Structure {
        public static boolean autoSynch = true;
        public CvPoint2D64f() { setAutoSynch(autoSynch); }
        public CvPoint2D64f(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvPoint2D64f.class) read(); }
        public CvPoint2D64f(double x, double y) {
            this.x = x;
            this.y = y;
        }
        public CvPoint2D64f(CvPoint o) {
            this.x = o.x;
            this.y = o.y;
        }

        public static CvPoint2D64f[] createArray(int size) {
            CvPoint2D64f p = new CvPoint2D64f(new Memory(Native.getNativeSize(ByValue.class) * size));
            return (CvPoint2D64f[])p.toArray(size);
        }
        public static CvPoint2D64f[] createArray(double[] pts, int offset, int length) {
            CvPoint2D64f[] a = CvPoint2D64f.createArray(length/2);
            for (int i = 0; i < length/2; i++) {
                a[i].set(pts[offset + i*2], pts[offset + i*2+1]);
            }
            return a;
        }
        public static CvPoint2D64f[] createArray(double ... pts) {
            return createArray(pts, 0, pts.length);
        }

        private static final String[] fieldOrder = { "x", "y" };
        { setFieldOrder(fieldOrder); }
        public double x;
        public double y;

        public void set(double x, double y) {
            this.x = x;
            this.y = y;
        }
        public void set(CvPoint o) {
            this.x = o.x;
            this.y = o.y;
        }
        public void set(CvPoint2D32f o) {
            this.x = o.x;
            this.y = o.y;
        }
        public void set(CvPoint2D64f o) {
            this.x = o.x;
            this.y = o.y;
        }

        public static class ByValue extends CvPoint2D64f implements Structure.ByValue {
            public ByValue() { }
            public ByValue(double x, double y) {
                super(x, y);
            }
            public ByValue(CvPoint2D64f o) {
                x = o.x;
                y = o.y;
            }
        }
        public ByValue byValue() {
            return this instanceof ByValue ? (ByValue)this : new ByValue(this);
        }

        @Override public String toString() { return "(" + (float)x + ", " + (float)y + ")"; }
    }

    public static CvPoint2D64f.ByValue cvPoint2D64f(double x, double y) {
        return new CvPoint2D64f.ByValue(x, y);
    }

    public static class CvPoint3D64f extends Structure {
        public static boolean autoSynch = true;
        public CvPoint3D64f() { setAutoSynch(autoSynch); }
        public CvPoint3D64f(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvPoint3D64f.class) read(); }
        public CvPoint3D64f(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        public CvPoint3D64f(CvPoint o) {
            this.x = o.x;
            this.y = o.y;
            this.z = 0;
        }

        public static CvPoint3D64f[] createArray(int size) {
            CvPoint3D64f p = new CvPoint3D64f(new Memory(Native.getNativeSize(ByValue.class) * size));
            return (CvPoint3D64f[])p.toArray(size);
        }
        public static CvPoint3D64f[] createArray(double[] pts, int offset, int length) {
            CvPoint3D64f[] a = CvPoint3D64f.createArray(length/3);
            for (int i = 0; i < length/3; i++) {
                a[i].set(pts[offset + i*3], pts[offset + i*3+1], pts[offset + i*3+2]);
            }
            return a;
        }
        public static CvPoint3D64f[] createArray(double ... pts) {
            return createArray(pts, 0, pts.length);
        }

        private static final String[] fieldOrder = { "x", "y", "z" };
        { setFieldOrder(fieldOrder); }
        public double x;
        public double y;
        public double z;

        public void set(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        public void set(CvPoint o) {
            this.x = o.x;
            this.y = o.y;
            this.z = 0;
        }
        public void set(CvPoint2D32f o) {
            this.x = o.x;
            this.y = o.y;
            this.z = 0;
        }
        public void set(CvPoint2D64f o) {
            this.x = o.x;
            this.y = o.y;
            this.z = 0;
        }

        public static class ByValue extends CvPoint3D64f implements Structure.ByValue {
            public ByValue() { }
            public ByValue(double x, double y, double z) {
                super(x, y, z);
            }
            public ByValue(CvPoint3D64f o) {
                x = o.x;
                y = o.y;
                z = o.z;
            }
        }
        public ByValue byValue() {
            return this instanceof ByValue ? (ByValue)this : new ByValue(this);
        }

        @Override public String toString() { return "(" + x + ", " + y + ", " + z + ")"; }
    }

    public static CvPoint3D64f.ByValue cvPoint3D64f(double x, double y, double z) {
        return new CvPoint3D64f.ByValue(x, y, z);
    }

    public static class CvSize extends Structure {
        public static boolean autoSynch = true;
        public CvSize() { setAutoSynch(autoSynch); }
        public CvSize(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvSize.class) read(); }
        public CvSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        private static final String[] fieldOrder = { "width", "height" };
        { setFieldOrder(fieldOrder); }
        public int width;
        public int height;

        public static class ByValue extends CvSize implements Structure.ByValue {
            public ByValue() { }
            public ByValue(int width, int height) {
                super(width, height);
            }
            public ByValue(CvSize o) {
                width = o.width;
                height = o.height;
            }
        }
        public ByValue byValue() {
            return this instanceof ByValue ? (ByValue)this : new ByValue(this);
        }

        @Override public String toString() { return "(" + width + ", " + height + ")"; }

        public static final ByValue ZERO = new ByValue(0, 0);
    }

    public static CvSize.ByValue cvSize(int width, int height) {
        return new CvSize.ByValue(width, height);
    }

    public static class CvSize2D32f extends Structure {
        public static boolean autoSynch = true;
        public CvSize2D32f() { setAutoSynch(autoSynch); }
        public CvSize2D32f(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvSize2D32f.class) read(); }
        public CvSize2D32f(double width, double height) {
            this.width = (float)width;
            this.height = (float)height;
        }

        private static final String[] fieldOrder = { "width", "height" };
        { setFieldOrder(fieldOrder); }
        public float width;
        public float height;

        public static class ByValue extends CvSize2D32f implements Structure.ByValue {
            public ByValue() { }
            public ByValue(double width, double height) {
                super(width, height);
            }
            public ByValue(CvSize2D32f o) {
                width = o.width;
                height = o.height;
            }
        }
        public ByValue byValue() {
            return this instanceof ByValue ? (ByValue)this : new ByValue(this);
        }

        @Override public String toString() { return "(" + width + ", " + height + ")"; }
    }

    public static CvSize2D32f.ByValue cvSize2D32f(double width, double height) {
        return new CvSize2D32f.ByValue(width, height);
    }

    public static class CvRect extends Structure {
        public static boolean autoSynch = true;
        public CvRect() { setAutoSynch(autoSynch); }
        public CvRect(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvRect.class) read(); }
        public CvRect(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        private static final String[] fieldOrder = { "x", "y", "width", "height" };
        { setFieldOrder(fieldOrder); }
        public int x;
        public int y;
        public int width;
        public int height;

        public static class ByValue extends CvRect implements Structure.ByValue {
            public ByValue() { }
            public ByValue(int x, int y, int width, int height) {
                super(x, y, width, height);
            }
            public ByValue(CvRect o) {
                x = o.x;
                y = o.y;
                width = o.width;
                height = o.height;
            }
        }
        public ByValue byValue() {
            return this instanceof ByValue ? (ByValue)this : new ByValue(this);
        }

        @Override public String toString() { return "(" + x + ", " + y + "; " +
                width + ", " + height + ")"; }
    }

    public static CvRect.ByValue cvRect(int x, int y, int width, int height) {
        return new CvRect.ByValue(x, y, width, height);
    }
    public static IplROI.ByReference cvRectToROI(CvRect rect, int coi) {
        IplROI.ByReference roi = new IplROI.ByReference();
        roi.xOffset = rect.x;
        roi.yOffset = rect.y;
        roi.width = rect.width;
        roi.height = rect.height;
        roi.coi = coi;
        return roi;
    }
    public static CvRect.ByValue cvROIToRect(IplROI roi) {
        return cvRect(roi.xOffset, roi.yOffset, roi.width, roi.height);
    }

    public static class CvBox2D extends Structure {
        public static boolean autoSynch = true;
        public CvBox2D() { setAutoSynch(autoSynch); }
        public CvBox2D(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvBox2D.class) read(); }
        public CvBox2D(CvPoint2D32f.ByValue center, CvSize2D32f.ByValue size, float angle) {
            this.center = center;
            this.size = size;
            this.angle = angle;
        }

        private static final String[] fieldOrder = { "center", "size", "angle" };
        { setFieldOrder(fieldOrder); }
        public CvPoint2D32f center = new CvPoint2D32f();
        public CvSize2D32f  size   = new CvSize2D32f();
        public float angle;

        public static class ByValue extends CvBox2D implements Structure.ByValue {
            public ByValue() { }
            public ByValue(CvBox2D o) {
                center = o.center;
                size = o.size;
                angle = o.angle;
            }
        }
        public ByValue byValue() {
            return this instanceof ByValue ? (ByValue)this : new ByValue(this);
        }

        @Override public String toString() { return center + " " + size + " " + angle; }
    }

    public static class CvScalar extends Structure {
        public static boolean autoSynch = true;
        public CvScalar() { setAutoSynch(autoSynch); }
        public CvScalar(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvScalar.class) read(); }
        public CvScalar(double val0, double val1, double val2, double val3) {
            val[0] = val0; val[1] = val1; val[2] = val2; val[3] = val3;
        }

        private static final String[] fieldOrder = { "val" };
        { setFieldOrder(fieldOrder); }
        public double[] val = new double[4];
        public double[] getVal() { return val; }

        public void scale(double s) {
            for (int i = 0; i < val.length; i++) {
                val[i] *= s;
            }
        }

        public double getRed() {
            return val[2];
        }
        public double getGreen() {
            return val[1];
        }
        public double getBlue() {
            return val[0];
        }
        public void setRed(double r) {
            val[2] = r;
        }
        public void setGreen(double g) {
            val[1] = g;
        }
        public void setBlue(double b) {
            val[0] = b;
        }

        public double getMagnitude() {
            return Math.sqrt(val[0]*val[0] + val[1]*val[1] + val[2]*val[2] + val[3]*val[3]);
        }

        public static class ByValue extends CvScalar implements Structure.ByValue {
            public ByValue() { }
            public ByValue(double val0, double val1, double val2, double val3) {
                super(val0, val1, val2, val3);
            }
            public ByValue(CvScalar o) {
                System.arraycopy(o.val, 0, val, 0, val.length);
            }
        }
        public ByValue byValue() {
            return this instanceof ByValue ? (ByValue)this : new ByValue(this);
        }

        @Override public String toString() { return "(" + (float)val[0] + ", " +
            (float)val[1] + ", " + (float)val[2] + ", " + (float)val[3] + ")"; }

        public static final ByValue
                ZERO    = new ByValue(0.0, 0.0, 0.0, 0.0),
                ONE     = new ByValue(1.0, 1.0, 1.0, 1.0),
                ONEHALF = new ByValue(0.5, 0.5, 0.5, 0.5),

                WHITE   = CV_RGB(255, 255, 255),
                GRAY    = CV_RGB(128, 128, 128),
                BLACK   = CV_RGB(  0,   0,   0),
                RED     = CV_RGB(255,   0,   0),
                GREEN   = CV_RGB(  0, 255,   0),
                BLUE    = CV_RGB(  0,   0, 255),
                CYAN    = CV_RGB(  0, 255, 255),
                MAGENTA = CV_RGB(255,   0, 255),
                YELLOW  = CV_RGB(255, 255,   0);
    }

    public static CvScalar.ByValue cvScalar(double val0, double val1, double val2, double val3) {
        return new CvScalar.ByValue(val0, val1, val2, val3);
    }
    public static CvScalar.ByValue cvRealScalar(double val0) {
        return new CvScalar.ByValue(val0, 0, 0, 0);
    }
    public static CvScalar.ByValue cvScalarAll(double val0123) {
        return new CvScalar.ByValue(val0123, val0123, val0123, val0123);
    }

    public static class CvIntScalar extends Structure {
        public static boolean autoSynch = true;
        public CvIntScalar() { setAutoSynch(autoSynch); }
        public CvIntScalar(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvIntScalar.class) read(); }
        public CvIntScalar(long val0, long val1, long val2, long val3) {
            val[0] = val0; val[1] = val1; val[2] = val2; val[3] = val3;
        }

        private static final String[] fieldOrder = { "val" };
        { setFieldOrder(fieldOrder); }
        public long[] val = new long[4];
        public long[] getVal() { return val; }

        public static class ByValue extends CvIntScalar implements Structure.ByValue {
            public ByValue() { }
            public ByValue(long val0, long val1, long val2, long val3) {
                super(val0, val1, val2, val3);
            }
            public ByValue(CvIntScalar o) {
                System.arraycopy(o.val, 0, val, 0, val.length);
            }
        }
        public ByValue byValue() {
            return this instanceof ByValue ? (ByValue)this : new ByValue(this);
        }

        @Override public String toString() { return "(" + val[0] + ", " +
                          val[1] + ", " + val[2] + ", " + val[3] + ")"; }
    }

    public static CvIntScalar.ByValue cvIntScalar(long val0, long val1, long val2, long val3) {
        return new CvIntScalar.ByValue(val0, val1, val2, val3);
    }
    public static CvIntScalar.ByValue cvRealIntScalar(long val0) {
        return new CvIntScalar.ByValue(val0, 0, 0, 0);
    }
    public static CvIntScalar.ByValue cvIntScalarAll(long val0123) {
        return new CvIntScalar.ByValue(val0123, val0123, val0123, val0123);
    }


    public static final int
            CV_TERMCRIT_ITER   = 1,
            CV_TERMCRIT_NUMBER = CV_TERMCRIT_ITER,
            CV_TERMCRIT_EPS    = 2;
    public static class CvTermCriteria extends Structure {
        public static boolean autoSynch = true;
        public CvTermCriteria() { setAutoSynch(autoSynch); }
        public CvTermCriteria(int type, int max_iter, double epsilon) {
            this.type = type;
            this.max_iter = max_iter;
            this.epsilon = (float)epsilon;
        }

        private static final String[] fieldOrder = { "type", "max_iter", "epsilon" };
        { setFieldOrder(fieldOrder); }
        public int    type;
        public int    max_iter;
        public double epsilon;

        public static class ByValue extends CvTermCriteria implements Structure.ByValue {
            public ByValue() { }
            public ByValue(int type, int max_iter, double epsilon) {
                super(type, max_iter, epsilon);
            }
            public ByValue(CvTermCriteria o) {
                this.type = o.type;
                this.max_iter = o.max_iter;
                this.epsilon = o.epsilon;
            }
        }
        public ByValue byValue() {
            return this instanceof ByValue ? (ByValue)this : new ByValue(this);
        }
    }

    public static CvTermCriteria.ByValue cvTermCriteria(int type, int max_iter, double epsilon) {
        return new CvTermCriteria.ByValue(type, max_iter, epsilon);
    }


    public static final int
            CV_CN_MAX    = 64,
            CV_CN_SHIFT  = 3,
            CV_DEPTH_MAX = (1 << CV_CN_SHIFT),

            CV_8U  = 0,
            CV_8S  = 1,
            CV_16U = 2,
            CV_16S = 3,
            CV_32S = 4,
            CV_32F = 5,
            CV_64F = 6,
            CV_USRTYPE1 = 7,

            CV_MAT_DEPTH_MASK      = (CV_DEPTH_MAX - 1);
    public static int CV_MAT_DEPTH(int flags) { return flags & CV_MAT_DEPTH_MASK; }
    public static int CV_MAKETYPE(int depth, int cn) { return CV_MAT_DEPTH(depth) + ((cn-1) << CV_CN_SHIFT); }
    public static int CV_MAKE_TYPE(int depth, int cn) { return CV_MAKETYPE(depth, cn); }
    public static final int
            CV_8UC1 = CV_MAKETYPE(CV_8U,1),
            CV_8UC2 = CV_MAKETYPE(CV_8U,2),
            CV_8UC3 = CV_MAKETYPE(CV_8U,3),
            CV_8UC4 = CV_MAKETYPE(CV_8U,4),
            //CV_8UC(n) = CvMat.CV_MAKETYPE(CV_8U,(n)),

            CV_8SC1 = CV_MAKETYPE(CV_8S,1),
            CV_8SC2 = CV_MAKETYPE(CV_8S,2),
            CV_8SC3 = CV_MAKETYPE(CV_8S,3),
            CV_8SC4 = CV_MAKETYPE(CV_8S,4),
            //CV_8SC(n) = CvMat.CV_MAKETYPE(CV_8S,(n)),

            CV_16UC1 = CV_MAKETYPE(CV_16U,1),
            CV_16UC2 = CV_MAKETYPE(CV_16U,2),
            CV_16UC3 = CV_MAKETYPE(CV_16U,3),
            CV_16UC4 = CV_MAKETYPE(CV_16U,4),
            //CV_16UC(n) = CvMat.CV_MAKETYPE(CV_16U,(n)),

            CV_16SC1 = CV_MAKETYPE(CV_16S,1),
            CV_16SC2 = CV_MAKETYPE(CV_16S,2),
            CV_16SC3 = CV_MAKETYPE(CV_16S,3),
            CV_16SC4 = CV_MAKETYPE(CV_16S,4),
            //CV_16SC(n) = CvMat.CV_MAKETYPE(CV_16S,(n)),

            CV_32SC1 = CV_MAKETYPE(CV_32S,1),
            CV_32SC2 = CV_MAKETYPE(CV_32S,2),
            CV_32SC3 = CV_MAKETYPE(CV_32S,3),
            CV_32SC4 = CV_MAKETYPE(CV_32S,4),
            //CV_32SC(n) = CvMat.CV_MAKETYPE(CV_32S,(n)),

            CV_32FC1 = CV_MAKETYPE(CV_32F,1),
            CV_32FC2 = CV_MAKETYPE(CV_32F,2),
            CV_32FC3 = CV_MAKETYPE(CV_32F,3),
            CV_32FC4 = CV_MAKETYPE(CV_32F,4),
            //CV_32FC(n) = CvMat.CV_MAKETYPE(CV_32F,(n)),

            CV_64FC1 = CV_MAKETYPE(CV_64F,1),
            CV_64FC2 = CV_MAKETYPE(CV_64F,2),
            CV_64FC3 = CV_MAKETYPE(CV_64F,3),
            CV_64FC4 = CV_MAKETYPE(CV_64F,4),
            //CV_64FC(n) = CvMat.CV_MAKETYPE(CV_64F,(n)),

            CV_AUTO_STEP = 0x7fffffff;
    public static final CvSlice CV_WHOLE_ARR = new CvSlice(0, 0x3fffffff);

    public static final int
            CV_MAT_CN_MASK         = ((CV_CN_MAX - 1) << CV_CN_SHIFT),
            CV_MAT_TYPE_MASK       = (CV_DEPTH_MAX*CV_CN_MAX - 1),
            CV_MAT_CONT_FLAG_SHIFT = 14,
            CV_MAT_CONT_FLAG       = (1 << CV_MAT_CONT_FLAG_SHIFT),
            CV_MAT_TEMP_FLAG_SHIFT = 15,
            CV_MAT_TEMP_FLAG       = (1 << CV_MAT_TEMP_FLAG_SHIFT);
    public static int CV_MAT_CN(int flags) { return ((flags & CV_MAT_CN_MASK) >> CV_CN_SHIFT) + 1; }
    public static int CV_MAT_TYPE(int flags) { return flags & CV_MAT_TYPE_MASK; }
    public static boolean CV_IS_MAT_CONT(int flags) { return (flags & CV_MAT_CONT_FLAG) != 0; }
    public static boolean CV_IS_CONT_MAT(int flags) { return CV_IS_MAT_CONT(flags); }
    public static boolean CV_IS_TEMP_MAT(int flags) { return (flags & CV_MAT_TEMP_FLAG) != 0;}

    public static final int
            CV_MAGIC_MASK    = 0xFFFF0000,
            CV_MAT_MAGIC_VAL = 0x42420000;
    public static final String CV_TYPE_NAME_MAT   = "opencv-matrix";

    public static class CvMat extends CvArr {
        public static boolean autoSynch = true;
        public CvMat() { setAutoSynch(autoSynch); fullSize = getSize(); releasable = false; }
        public CvMat(Pointer m) { super(m); setAutoSynch(autoSynch); releasable = true;
                if (autoSynch && getClass() == CvMat.class) read(); fullSize = getSize();  }
        public CvMat(int rows, int cols, int type, Pointer data, int step) {
            setAutoSynch(autoSynch);
            cvInitMatHeader(this, rows, cols, type, data, step);
            fullSize = getSize(); releasable = false;
        }
        public CvMat(int rows, int cols, int type, Pointer data) {
            this(rows, cols, type, data, cols*CV_ELEM_SIZE(type));
        }
        public CvMat(int rows, int cols, int depth, int channels, Pointer data, int step) {
            this(rows, cols, CV_MAKETYPE(depth, channels), data, step);
        }
        public CvMat(int rows, int cols, int depth, int channels, Pointer data) {
            this(rows, cols, CV_MAKETYPE(depth, channels), data);
        }

        public static CvMat create(int rows, int cols, int type) {
            CvMat m = cvCreateMat(rows, cols, type);
            if (m != null) {
                m.fullSize = m.getSize();
                m.releasable = true;
            }
            return m;
        }
        public static CvMat create(int rows, int cols, int depth, int channels) {
            return create(rows, cols, CV_MAKETYPE(depth, channels));
        }
        public static CvMat create(int rows, int cols) {
            return create(rows, cols, CV_64F, 1);
        }

        public static CvMat createHeader(int rows, int cols, int type) {
            CvMat m = cvCreateMatHeader(rows, cols, type);
            if (m != null) {
                m.fullSize = m.getSize();
                m.releasable = true;
            }
            return m;
        }
        public static CvMat createHeader(int rows, int cols, int depth, int channels) {
            return createHeader(rows, cols, CV_MAKETYPE(depth, channels));
        }
        public static CvMat createHeader(int rows, int cols) {
            return createHeader(rows, cols, CV_64F, 1);
        }

        public void release() {
            releasable = false;
            cvReleaseMat(pointerByReference());
        }
        @Override protected void finalize() {
            if (releasable) {
                release();
            }
        }
        @Override public CvMat clone() {
            CvMat m = cvCloneMat(this);
            if (m != null) {
                m.releasable = true;
            }
            return m;
        }
        private boolean releasable = false;


        private static final String[] fieldOrder = { "type", "step",
            "refcount", "hdr_refcount", "data", "rows", "cols" };
        { setFieldOrder(fieldOrder); }
        public int type;
        public int step;

        public IntByReference refcount;
        public int hdr_refcount;

        public Pointer data;

        public int rows, cols;


        public int getChannels() {
            return CV_MAT_CN(type);
        }
        public int getDepth() {
            return CV_MAT_DEPTH(type);
        }
        public int getType() {
            return CV_MAT_TYPE(type);
        }
        public void setType(int depth, int cn) {
            type = CV_MAKETYPE(depth, cn) | CV_MAT_MAGIC_VAL;
        }
        public int getElemSize() {
            switch (getDepth()) {
                case CV_8U:
                case CV_8S:  return 1;
                case CV_16U:
                case CV_16S: return 2;
                case CV_32S:
                case CV_32F: return 4;
                case CV_64F: return 8;
                default: assert(false);
            }
            return 0;
        }
        public int getLength() {
            return rows*cols;
        }
        public int getSize() {
            if (rows > 1) {
                return step*rows;
            }
            // step == 0 when height == 1...
            return cols*getElemSize()*getChannels();
        }

        public CvSize.ByValue getCvSize() { return new CvSize.ByValue(cols, rows); }

        private int fullSize = 0;
        private int getFullSize() { return fullSize > 0 ? fullSize : (fullSize = getSize()); }
        private ByteBuffer byteBuffer = null;
        private ShortBuffer shortBuffer = null;
        private IntBuffer intBuffer = null;
        private FloatBuffer floatBuffer = null;
        private DoubleBuffer doubleBuffer = null;
        public ByteBuffer getByteBuffer() {
            if (byteBuffer == null) {
                byteBuffer = data.getByteBuffer(0, getFullSize());
            }
            byteBuffer.position(0);
            return byteBuffer;
        }
        public ShortBuffer getShortBuffer() {
            if (shortBuffer == null) {
                shortBuffer = data.getByteBuffer(0, getFullSize()).asShortBuffer();
            }
            shortBuffer.position(0);
            return shortBuffer;
        }
        public IntBuffer getIntBuffer() {
            if (intBuffer == null) {
                intBuffer = data.getByteBuffer(0, getFullSize()).asIntBuffer();
            }
            intBuffer.position(0);
            return intBuffer;
        }
        public FloatBuffer getFloatBuffer() {
            if (floatBuffer == null) {
                floatBuffer = data.getByteBuffer(0, getFullSize()).asFloatBuffer();
            }
            floatBuffer.position(0);
            return floatBuffer;
        }
        public DoubleBuffer getDoubleBuffer() {
            if (doubleBuffer == null) {
                doubleBuffer = data.getByteBuffer(0, getFullSize()).asDoubleBuffer();
            }
            doubleBuffer.position(0);
            return doubleBuffer;
        }

        public double get(int i) {
            switch (getDepth()) {
                case CV_8U:  return getByteBuffer()  .get(i)&0xFF;
                case CV_8S:  return getByteBuffer()  .get(i);
                case CV_16U: return getShortBuffer() .get(i)&0xFFFF;
                case CV_16S: return getShortBuffer() .get(i);
                case CV_32S: return getIntBuffer()   .get(i);
                case CV_32F: return getFloatBuffer() .get(i);
                case CV_64F: return getDoubleBuffer().get(i);
                default: assert(false);
            }
            return Double.NaN;
        }
        public double get(int i, int j) {
            return get((i*step/getElemSize() + j)*getChannels());
        }

        public double get(int i, int j, int k) {
            return get((i*step/getElemSize() + j)*getChannels() + k);
        }
        public synchronized void get(int index, double[] vv, int offset, int length) {
            int d = getDepth();
            switch (d) {
                case CV_8U:
                case CV_8S:
                    ByteBuffer bb = getByteBuffer();
                    bb.position(index);
                    for (int i = 0; i < length; i++) {
                        if (d == CV_8U) {
                            vv[i+offset] = bb.get(i)&0xFF;
                        } else {
                            vv[i+offset] = bb.get(i);
                        }
                    }
                    break;
                case CV_16U:
                case CV_16S:
                    ShortBuffer sb = getShortBuffer();
                    sb.position(index);
                    for (int i = 0; i < length; i++) {
                        if (d == CV_16U) {
                            vv[i+offset] = sb.get()&0xFFFF;
                        } else {
                            vv[i+offset] = sb.get();
                        }
                    }
                    break;
                case CV_32S:
                    IntBuffer ib = getIntBuffer();
                    ib.position(index);
                    for (int i = 0; i < length; i++) {
                        vv[i+offset] = ib.get();
                    }
                    break;
                case CV_32F:
                    FloatBuffer fb = getFloatBuffer();
                    fb.position(index);
                    for (int i = 0; i < length; i++) {
                        vv[i+offset] = fb.get();
                    }
                    break;
                case CV_64F:
                    getDoubleBuffer().position(index);
                    getDoubleBuffer().get(vv, offset, length);
                    break;
                default: assert(false);
            }
        }
        public void get(int index, double[] vv) {
            get(index, vv, 0, vv.length);
        }
        public void get(double[] vv) {
            get(0, vv);
        }
        public double[] get() {
            int c = getDoubleBuffer().capacity();
            double[] vv = new double[c];
            get(vv);
            return vv;
        }

        public void put(int i, double v) {
            switch (getDepth()) {
                case CV_8U:
                case CV_8S:  getByteBuffer()  .put(i, (byte)(int)v);  break;
                case CV_16U:
                case CV_16S: getShortBuffer() .put(i, (short)(int)v); break;
                case CV_32S: getIntBuffer()   .put(i, (int)v);        break;
                case CV_32F: getFloatBuffer() .put(i, (float)v);      break;
                case CV_64F: getDoubleBuffer().put(i, v);             break;
                default: assert(false);
            }
        }
        public void put(int i, int j, double v) {
            put((i*step/getElemSize() + j)*getChannels(), v);
        }

        public void put(int i, int j, int k, double v) {
            put((i*step/getElemSize() + j)*getChannels() + k, v);
        }
        public synchronized void put(int index, double[] vv, int offset, int length) {
            switch (getDepth()) {
                case CV_8U:
                case CV_8S:
                    ByteBuffer bb = getByteBuffer();
                    bb.position(index);
                    for (int i = 0; i < length; i++) {
                        bb.put((byte)(int)vv[i+offset]);
                    }
                    break;
                case CV_16U:
                case CV_16S:
                    ShortBuffer sb = getShortBuffer();
                    sb.position(index);
                    for (int i = 0; i < length; i++) {
                        sb.put((short)(int)vv[i+offset]);
                    }
                    break;
                case CV_32S:
                    IntBuffer ib = getIntBuffer();
                    ib.position(index);
                    for (int i = 0; i < length; i++) {
                        ib.put((int)vv[i+offset]);
                    }
                    break;
                case CV_32F:
                    FloatBuffer fb = getFloatBuffer();
                    fb.position(index);
                    for (int i = 0; i < length; i++) {
                        fb.put((float)vv[i+offset]);
                    }
                    break;
                case CV_64F:
                    DoubleBuffer db = getDoubleBuffer();
                    db.position(index);
                    db.put(vv, offset, length);
                    break;
                default: assert(false);
            }
        }
        public void put(int index, double ... vv) {
            put(index, vv, 0, vv.length);
        }
        public void put(double ... vv) {
            put(0, vv);
        }
        public void put(CvMat mat) {
            put(0, 0, 0, mat, 0, 0, 0);
        }
        public synchronized void put(int dsti, int dstj, int dstk,
                CvMat mat, int srci, int srcj, int srck) {
            if (rows == mat.rows && cols == mat.cols && step == mat.step && type == mat.type &&
                    dsti == 0 && dstj == 0 && dstk == 0 && srci == 0 && srcj == 0 && srck == 0) {
                getByteBuffer().clear();
                mat.getByteBuffer().clear();
                getByteBuffer().put(mat.getByteBuffer());
            } else {
                int w = Math.min(rows-dsti, mat.rows-srci);
                int h = Math.min(cols-dstj, mat.cols-srcj);
                int d = Math.min(getChannels()-dstk, mat.getChannels()-srck);
                for (int i = 0; i < w; i++) {
                    for (int j = 0; j < h; j++) {
                        for (int k = 0; k < d; k++) {
                            put(i+dsti, j+dstj, k+dstk, mat.get(i+srci, j+srcj, k+srck));
                        }
                    }
                }
            }
        }


        public static CvMat take(int rows, int cols, int type) {
            synchronized (pool) {
                ArrayDeque<CvMat> deque = getDeque(rows, cols, type);
                if (deque.isEmpty()) {
                    return create(rows, cols, type);
                } else {
                    return deque.pop();
                }
            }
        }
        public static CvMat take(int rows, int cols, int depth, int channels) {
            return take(rows, cols, CV_MAKETYPE(depth, channels));
        }
        public static CvMat take(int rows, int cols) {
            return take(rows, cols, CV_64F, 1);
        }
        public void pool() {
            synchronized (pool) {
                ArrayDeque<CvMat> deque = getDeque(rows, cols, getType());
                deque.push(this);
            }
        }
        private static final HashMap<Long, ArrayDeque<CvMat>> pool =
                new HashMap<Long, ArrayDeque<CvMat>>();
        private static ArrayDeque<CvMat> getDeque(int rows, int cols, int type) {
            long key = (rows<<36) | (cols<<9) | (type);
            ArrayDeque<CvMat> deque = pool.get(key);
            if (deque == null) {
                deque = new ArrayDeque<CvMat>();
                pool.put(key, deque);
            }
            return deque;
        }


        @Override public String toString() {
            return toString(0);
        }
        public String toString(int indent) {
            StringBuilder s = new StringBuilder("[ ");
            int channels = getChannels();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    CvScalar v = cvGet2D(this, i, j);
                    if (channels > 1) {
                        s.append("(");
                    }
                    for (int k = 0; k < channels; k++) {
                        s.append((float)v.val[k]);
                        if (k < channels-1) {
                            s.append(", ");
                        }
                    }
                    if (channels > 1) {
                        s.append(")");
                    }
                    if (j < cols-1) {
                        s.append(", ");
                    }
                }
                if (i < rows-1) {
                    s.append("\n  ");
                    for (int j = 0; j < indent; j++) {
                        s.append(' ');
                    }
                }
            }
            s.append(" ]");
            return s.toString();
        }
        public static class ByReference extends CvMat implements Structure.ByReference { }

        public static class PointerByReference extends CvArr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvMat p) { setStructure(p); }
            public PointerByReference(CvMat[] a) { super(a); }

            public CvMat getStructure() {
                return new CvMat(getValue());
            }
            public void getStructure(CvMat p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvMat p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static boolean CV_IS_MAT_HDR(CvMat mat) {
        return mat != null && (mat.type & CV_MAGIC_MASK) == CV_MAT_MAGIC_VAL &&
               mat.cols > 0 && mat.rows > 0;
    }
    public static boolean CV_IS_MAT(CvMat mat) {
        return CV_IS_MAT_HDR(mat) && mat.data != null;
    }
    public static boolean CV_IS_MASK_ARR(CvMat mat) {
        return (mat.type & (CV_MAT_TYPE_MASK & ~CV_8SC1)) == 0;
    }
    public static boolean CV_ARE_TYPES_EQ(CvMat mat1, CvMat mat2) {
        return ((mat1.type ^ mat2.type) & CV_MAT_TYPE_MASK) == 0;
    }
    public static boolean CV_ARE_CNS_EQ(CvMat mat1, CvMat mat2) {
        return ((mat1.type ^ mat2.type) & CV_MAT_CN_MASK) == 0;
    }
    public static boolean CV_ARE_DEPTHS_EQ(CvMat mat1, CvMat mat2) {
        return ((mat1.type ^ mat2.type) & CV_MAT_DEPTH_MASK) == 0;
    }
    public static boolean CV_ARE_SIZES_EQ(CvMat mat1, CvMat mat2) {
        return (mat1.rows == mat2.rows && mat1.cols == mat2.cols);
    }
    public static boolean CV_IS_MAT_CONST(CvMat mat) {
        return (mat.rows|mat.cols) == 1;
    }
    public static int CV_ELEM_SIZE1(int type) {
        return (((Native.SIZE_T_SIZE<<28)|0x8442211) >> CV_MAT_DEPTH(type)*4) & 15;
    }
    public static int CV_ELEM_SIZE(int type) {
        return CV_MAT_CN(type) << ((((Native.SIZE_T_SIZE/4+1)*16384|0x3a50) >> CV_MAT_DEPTH(type)*2) & 3);
    }
    public static int cvIplDepth(int type) {
        int depth = CV_MAT_DEPTH(type);
        return CV_ELEM_SIZE1(depth)*8 | (depth == CV_8S || depth == CV_16S ||
               depth == CV_32S ? IPL_DEPTH_SIGN : 0);
    }

    public static final int CV_MATND_MAGIC_VAL    = 0x42430000;
    public static final String CV_TYPE_NAME_MATND = "opencv-nd-matrix";

    public static final int
            CV_MAX_DIM          = 32,
            CV_MAX_DIM_HEAP     = (1 << 16);

    public static class CvMatND extends CvArr {
        public static boolean autoSynch = true;
        public CvMatND() { setAutoSynch(autoSynch); releasable = false; }
        public CvMatND(Pointer m) { super(m); setAutoSynch(autoSynch); releasable = true;
                if (autoSynch && getClass() == CvMatND.class) read(); }

        public static CvMatND create(int dims, int[] sizes, int type) {
            CvMatND m = cvCreateMatND(dims, sizes, type);
            if (m != null) {
                m.releasable = true;
            }
            return m;
        }

        public void release() {
            releasable = false;
            cvReleaseMatND(pointerByReference());
        }
        @Override protected void finalize() {
            if (releasable) {
                release();
            }
        }
        @Override public CvMatND clone() {
            CvMatND m = cvCloneMatND(this);
            if (m != null) {
                m.releasable = true;
            }
            return m;
        }
        private boolean releasable = false;


        private static final String[] fieldOrder = {
            "type", "dims", "refcount", "hdr_refcount", "data", "dim" };
        { setFieldOrder(fieldOrder); }
        public int type;
        public int dims;

        public IntByReference refcount;
        public int hdr_refcount;

        public Pointer data;

        public static class Dim extends Structure {
            private static final String[] fieldOrder = { "size", "step" };
            { setFieldOrder(fieldOrder); }
            public int size;
            public int step;
        }
        public Dim[] dim = new Dim[CV_MAX_DIM];


        public static class ByReference extends CvMatND implements Structure.ByReference { }

        public static class PointerByReference extends CvMat.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvMatND p) {
                setStructure(p);
            }
            public CvMatND getStructureND() {
                return new CvMatND(getValue());
            }
            public void getStructure(CvMatND p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvMatND p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static boolean CV_IS_MATND_HDR(CvMatND mat) {
        return mat != null && (mat.type & CV_MAGIC_MASK) == CV_MATND_MAGIC_VAL;
    }
    public static boolean CV_IS_MATND(CvMatND mat) {
        return CV_IS_MATND_HDR(mat) && mat.data != null;
    }


    public static final int CV_SPARSE_MAT_MAGIC_VAL    = 0x42440000;
    public static final String CV_TYPE_NAME_SPARSE_MAT = "opencv-sparse-matrix";

    public static class CvSparseMat extends CvArr {
        public static boolean autoSynch = true;
        public CvSparseMat() { setAutoSynch(autoSynch); releasable = false; }
        public CvSparseMat(Pointer m) { super(m); setAutoSynch(autoSynch); releasable = true;
                if (autoSynch && getClass() == CvSparseMat.class) read(); }

        public static CvSparseMat create(int dims, int[] sizes, int type) {
            CvSparseMat m = cvCreateSparseMat(dims, sizes, type);
            if (m != null) {
                m.releasable = true;
            }
            return m;
        }

        public void release() {
            releasable = false;
            cvReleaseSparseMat(pointerByReference());
        }
        @Override protected void finalize() {
            if (releasable) {
                release();
            }
        }
        @Override public CvSparseMat clone() {
            CvSparseMat m = cvCloneSparseMat(this);
            if (m != null) {
                m.releasable = true;
            }
            return m;
        }
        private boolean releasable = false;


        private static final String[] fieldOrder = { "type", "dims",
            "refcount", "hdr_refcount", "heap", "hashtable", "hashsize",
            "valoffset", "idxoffset", "size"};
        { setFieldOrder(fieldOrder); }
        public int type;
        public int dims;
        public IntByReference refcount;
        public int hdr_refcount;

        public CvSet.ByReference heap;
        public PointerByReference hashtable;
        public int hashsize;
        public int valoffset;
        public int idxoffset;
        public int[] size = new int[CV_MAX_DIM];


        public static class ByReference extends CvSparseMat implements Structure.ByReference { }

        public static class PointerByReference extends CvArr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvSparseMat p) { setStructure(p); }
            public PointerByReference(CvSparseMat[] a) { super(a); }

            public CvSparseMat getStructure() {
                return new CvSparseMat(getValue());
            }
            public void getStructure(CvSparseMat p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvSparseMat p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static boolean CV_IS_SPARSE_MAT_HDR(CvSparseMat mat) {
        return mat != null && (mat.type & CV_MAGIC_MASK) == CV_SPARSE_MAT_MAGIC_VAL;
    }
    public static boolean CV_IS_SPARSE_MAT(CvSparseMat mat) {
        return CV_IS_SPARSE_MAT_HDR(mat);
    }

    public static class CvSparseNode extends Structure {
        public static boolean autoSynch = true;
        public CvSparseNode() { setAutoSynch(autoSynch); }
        public CvSparseNode(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvSparseNode.class) read(); }

        private static final String[] fieldOrder = { "hashval", "next" };
        { setFieldOrder(fieldOrder); }
        public int hashval;
        public CvSparseNode.ByReference next;

        public static class ByReference extends CvSparseNode implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); if (getClass() == ByReference.class) read(); }
        }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvSparseNode p) {
                setStructure(p);
            }
            public CvSparseNode getStructure() {
                return new CvSparseNode(getValue());
            }
            public void getStructure(CvSparseNode p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvSparseNode p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static byte CV_NODE_VAL_byte(CvSparseMat mat, CvSparseNode node) {
        return node.getPointer().getByte(mat.valoffset);
    }
    public static short CV_NODE_VAL_short(CvSparseMat mat, CvSparseNode node) {
        return node.getPointer().getShort(mat.valoffset);
    }
    public static int CV_NODE_VAL_int(CvSparseMat mat, CvSparseNode node) {
        return node.getPointer().getInt(mat.valoffset);
    }
    public static float CV_NODE_VAL_float(CvSparseMat mat, CvSparseNode node) {
        return node.getPointer().getFloat(mat.valoffset);
    }
    public static double CV_NODE_VAL_double(CvSparseMat mat, CvSparseNode node) {
        return node.getPointer().getDouble(mat.valoffset);
    }
    public static int[] CV_NODE_IDX(CvSparseMat mat, CvSparseNode node) {
        return node.getPointer().getIntArray(mat.idxoffset, mat.dims);
    }

    public static class CvSparseMatIterator extends Structure {
        public static boolean autoSynch = true;
        public CvSparseMatIterator() { setAutoSynch(autoSynch); }
        public CvSparseMatIterator(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvSparseMatIterator.class) read(); }

        private static final String[] fieldOrder = { "mat", "node", "curidx" };
        { setFieldOrder(fieldOrder); }
        public CvSparseMat.ByReference mat;
        public CvSparseNode.ByReference node;
        public int curidx;
    }


    public static final int
            IPL_DEPTH_SIGN = 0x80000000,
            IPL_DEPTH_1U   = 1,
            IPL_DEPTH_8U   = 8,
            IPL_DEPTH_16U  = 16,
            IPL_DEPTH_32F  = 32,
            IPL_DEPTH_8S   = (IPL_DEPTH_SIGN| 8),
            IPL_DEPTH_16S  = (IPL_DEPTH_SIGN|16),
            IPL_DEPTH_32S  = (IPL_DEPTH_SIGN|32),
            IPL_DEPTH_64F  = 64,

            IPL_ORIGIN_TL  = 0,
            IPL_ORIGIN_BL  = 1,

            IPL_DATA_ORDER_PIXEL = 0,
            IPL_DATA_ORDER_PLANE = 1,

            IPL_ALIGN_4BYTES  =  4,
            IPL_ALIGN_8BYTES  =  8,
            IPL_ALIGN_16BYTES = 16,
            IPL_ALIGN_32BYTES = 32,

            IPL_ALIGN_DWORD  = IPL_ALIGN_4BYTES,
            IPL_ALIGN_QWORD  = IPL_ALIGN_8BYTES,

            IPL_BORDER_CONSTANT  = 0,
            IPL_BORDER_REPLICATE = 1,
            IPL_BORDER_REFLECT   = 2,
            IPL_BORDER_WRAP      = 3;

    public static class IplImage extends CvArr {
        public static boolean autoSynch = true;
        public IplImage() { setAutoSynch(autoSynch); releasable = false; releasableHeader = false; }
        public IplImage(Pointer m) { super(m); setAutoSynch(autoSynch); releasable = true;
                if (autoSynch && getClass() == IplImage.class) read(); }
        public static IplImage create(CvSize.ByValue size, int depth, int channels) {
            IplImage i = cvCreateImage(size, depth, channels);
            if (i != null) {
                i.releasable = true;
                i.releasableHeader = false;
            }
            return i;
        }
        public static IplImage create(int width, int height, int depth, int channels) {
            return create(new CvSize.ByValue(width, height), depth, channels);
        }
        public static IplImage create(CvSize.ByValue size, int depth, int channels, int origin) {
            IplImage i = create(size, depth, channels);
            if (i != null) {
                i.origin = origin;
            }
            return i;
        }
        public static IplImage create(int width, int height, int depth, int channels, int origin) {
            IplImage i = create(width, height, depth, channels);
            if (i != null) {
                i.origin = origin;
            }
            return i;
        }

        public static IplImage createHeader(CvSize.ByValue size, int depth, int channels) {
            IplImage i = cvCreateImageHeader(size, depth, channels);
            if (i != null) {
                i.releasable = false;
                i.releasableHeader = true;
            }
            return i;
        }
        public static IplImage createHeader(int width, int height, int depth, int channels) {
            return createHeader(new CvSize.ByValue(width, height), depth, channels);
        }
        public static IplImage createHeader(CvSize.ByValue size, int depth, int channels, int origin) {
            IplImage i = createHeader(size, depth, channels);
            if (i != null) {
                i.origin = origin;
            }
            return i;
        }
        public static IplImage createHeader(int width, int height, int depth, int channels, int origin) {
            IplImage i = createHeader(width, height, depth, channels);
            if (i != null) {
                i.origin = origin;
            }
            return i;
        }

        public static IplImage createCompatible(IplImage template) {
            return createIfNotCompatible(null, template);
        }
        public static IplImage createIfNotCompatible(IplImage image, IplImage template) {
            if (image == null || image.width != template.width || image.height != template.height ||
                    image.depth != template.depth || image.nChannels != template.nChannels) {
                image = create(template.width, template.height,
                        template.depth, template.nChannels, template.origin);
            }
            image.origin = template.origin;
            return image;
        }

        public static IplImage createFrom(BufferedImage image) {
            SampleModel sm = image.getSampleModel();
            int depth = 0, numChannels = sm.getNumBands();
            switch (image.getType()) {
                case BufferedImage.TYPE_INT_RGB:
                case BufferedImage.TYPE_INT_ARGB:
                case BufferedImage.TYPE_INT_ARGB_PRE:
                case BufferedImage.TYPE_INT_BGR:
                    depth = IPL_DEPTH_8U;
                    numChannels = 4;
                    break;
            }
            if (depth == 0 || numChannels == 0) {
                switch (sm.getDataType()) {
                    case DataBuffer.TYPE_BYTE:   depth = IPL_DEPTH_8U;  break;
                    case DataBuffer.TYPE_USHORT: depth = IPL_DEPTH_16U; break;
                    case DataBuffer.TYPE_SHORT:  depth = IPL_DEPTH_16S; break;
                    case DataBuffer.TYPE_INT:    depth = IPL_DEPTH_32S; break;
                    case DataBuffer.TYPE_FLOAT:  depth = IPL_DEPTH_32F; break;
                    case DataBuffer.TYPE_DOUBLE: depth = IPL_DEPTH_64F; break;
                    default: assert (false);
                }
            }
            IplImage i = create(sm.getWidth(), sm.getHeight(), depth, numChannels);
            i.copyFrom(image, 1.0, null);
            return i;
        }

        public void release() {
            if (releasableHeader) {
                cvReleaseImageHeader(pointerByReference());
            } else if (releasable) {
                cvReleaseImage(pointerByReference());
            }
            releasable = false;
            releasableHeader = false;
        }
        @Override protected void finalize() {
            if (releasable || releasableHeader) {
                release();
            }
        }
        @Override public IplImage clone() {
            IplImage i = cvCloneImage(this);
            if (i != null) {
                i.releasable = true;
                i.releasableHeader = false;
            }
            return i;
        }
        private boolean releasable = false;
        private boolean releasableHeader = false;


        private static final String[] fieldOrder = {
            "nSize", "ID", "nChannels", "alphaChannel", "depth",
            "colorModel0", "colorModel1", "colorModel2", "colorModel3",
            "channelSeq0", "channelSeq1", "channelSeq2", "channelSeq3",
            "dataOrder", "origin", "align", "width", "height", "roi", "maskROI",
            "imageId", "tileInfo", "imageSize", "imageData", "widthStep",
            "BorderMode0",  "BorderMode1",  "BorderMode2",  "BorderMode3",
            "BorderConst0", "BorderConst1", "BorderConst2", "BorderConst3",
            "imageDataOrigin" };
        { setFieldOrder(fieldOrder); }
        public int  nSize;
        public int  ID;
        public int  nChannels;
        public int  alphaChannel;
        public int  depth;
        public byte colorModel0, colorModel1, colorModel2, colorModel3;
        public byte channelSeq0, channelSeq1, channelSeq2, channelSeq3;
        public int  dataOrder;
        public int  origin;
        public int  align;
        public int  width;
        public int  height;
        public IplROI.ByReference roi;
        public IplImage.ByReference maskROI;
        public Pointer imageId;
        public IplTileInfo tileInfo;
        public int  imageSize;
        public Pointer imageData;
        public int widthStep;
        public int BorderMode0,  BorderMode1,  BorderMode2,  BorderMode3;
        public int BorderConst0, BorderConst1, BorderConst2, BorderConst3;
        public Pointer imageDataOrigin;


        public double getMaxIntensity() {
            double maxIntensity = 0.0;
            switch (depth) {
                case IPL_DEPTH_8U:  maxIntensity = 0xFF;              break;
                case IPL_DEPTH_16U: maxIntensity = 0xFFFF;            break;
                case IPL_DEPTH_8S:  maxIntensity = Byte.MAX_VALUE;    break;
                case IPL_DEPTH_16S: maxIntensity = Short.MAX_VALUE;   break;
                case IPL_DEPTH_32S: maxIntensity = Integer.MAX_VALUE; break;
                case IPL_DEPTH_1U:
                case IPL_DEPTH_32F:
                case IPL_DEPTH_64F: maxIntensity = 1.0; break;
                default: assert(false);
            }
            return maxIntensity;
        }

        public CvSize.ByValue getCvSize() { return new CvSize.ByValue(width, height); }

        public ByteBuffer   getByteBuffer(int index)   { return imageData.getByteBuffer(index, imageSize-index); }
        public ShortBuffer  getShortBuffer(int index)  { return getByteBuffer(index*2).asShortBuffer(); }
        public IntBuffer    getIntBuffer(int index)    { return getByteBuffer(index*4).asIntBuffer(); }
        public FloatBuffer  getFloatBuffer(int index)  { return getByteBuffer(index*4).asFloatBuffer(); }
        public DoubleBuffer getDoubleBuffer(int index) { return getByteBuffer(index*8).asDoubleBuffer(); }
        public ByteBuffer   getByteBuffer()   { return getByteBuffer(0); }
        public ShortBuffer  getShortBuffer()  { return getShortBuffer(0); }
        public IntBuffer    getIntBuffer()    { return getIntBuffer(0); }
        public FloatBuffer  getFloatBuffer()  { return getFloatBuffer(0); }
        public DoubleBuffer getDoubleBuffer() { return getDoubleBuffer(0); }

        // timestamp is an extension of IplImage used by FrameGrabber
        private long timestamp;
        public long getTimestamp() {
            return timestamp;
        }
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }


        public static final byte[]
                gamma22    = new byte[256],
                gamma22inv = new byte[256];
        static {
            for (int i = 0; i < 256; i++) {
                gamma22[i]    = (byte)Math.round(Math.pow(i/255.0,   2.2)*255.0);
                gamma22inv[i] = (byte)Math.round(Math.pow(i/255.0, 1/2.2)*255.0);
            }
        }
        public static void flipCopyWithGamma(ByteBuffer srcBuf, int srcStep,
                ByteBuffer dstBuf, int dstStep, boolean signed, double gamma, boolean flip) {
            assert (srcBuf != dstBuf);
            int w = Math.min(srcStep, dstStep);
            int srcLine = srcBuf.position(), dstLine = dstBuf.position();
            while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
                if (flip) {
                    srcBuf.position(srcBuf.capacity() - srcLine - srcStep);
                } else {
                    srcBuf.position(srcLine);
                }
                dstBuf.position(dstLine);
                w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());
                if (signed) {
                    for (int x = 0; x < w; x++) {
                        int in = srcBuf.get();
                        byte out;
                        if (gamma != 1.0) {
                            out = (byte)Math.round(Math.pow((double)in/Byte.MAX_VALUE, gamma)*Byte.MAX_VALUE);
                        } else {
                            out = (byte)in;
                        }
                        dstBuf.put(out);
                    }
                } else {
                    for (int x = 0; x < w; x++) {
                        byte out;
                        int in = srcBuf.get() & 0xFF;
                        if (gamma == 2.2) {
                            out = gamma22[in];
                        } else if (gamma == 1/2.2) {
                            out = gamma22inv[in];
                        } else if (gamma != 1.0) {
                            out = (byte)Math.round(Math.pow((double)in/0xFF, gamma)*0xFF);
                        } else {
                            out = (byte)in;
                        }
                        dstBuf.put(out);
                    }
                }
                srcLine += srcStep;
                dstLine += dstStep;
            }
        }
        public static void flipCopyWithGamma(ShortBuffer srcBuf, int srcStep,
                ShortBuffer dstBuf, int dstStep, boolean signed, double gamma, boolean flip) {
            assert (srcBuf != dstBuf);
            int w = Math.min(srcStep, dstStep);
            int srcLine = srcBuf.position(), dstLine = dstBuf.position();
            while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
                if (flip) {
                    srcBuf.position(srcBuf.capacity() - srcLine - srcStep);
                } else {
                    srcBuf.position(srcLine);
                }
                dstBuf.position(dstLine);
                w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());
                if (signed) {
                    for (int x = 0; x < w; x++) {
                        int in = srcBuf.get();
                        short out;
                        if (gamma != 1.0) {
                            out = (short)Math.round(Math.pow((double)in/Short.MAX_VALUE, gamma)*Short.MAX_VALUE);
                        } else {
                            out = (short)in;
                        }
                        dstBuf.put(out);
                    }
                } else {
                    for (int x = 0; x < w; x++) {
                        int in = srcBuf.get() & 0xFFFF;
                        short out;
                        if (gamma != 1.0) {
                            out = (short)Math.round(Math.pow((double)in/0xFFFF, gamma)*0xFFFF);
                        } else {
                            out = (short)in;
                        }
                        dstBuf.put(out);
                    }
                }
                srcLine += srcStep;
                dstLine += dstStep;
            }
        }
        public static void flipCopyWithGamma(IntBuffer srcBuf, int srcStep,
                IntBuffer dstBuf, int dstStep, double gamma, boolean flip) {
            assert (srcBuf != dstBuf);
            int w = Math.min(srcStep, dstStep);
            int srcLine = srcBuf.position(), dstLine = dstBuf.position();
            while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
                if (flip) {
                    srcBuf.position(srcBuf.capacity() - srcLine - srcStep);
                } else {
                    srcBuf.position(srcLine);
                }
                dstBuf.position(dstLine);
                w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());
                for (int x = 0; x < w; x++) {
                    int in = srcBuf.get();
                    int out;
                    if (gamma != 1.0) {
                        out = (int)Math.round(Math.pow((double)in/Integer.MAX_VALUE, gamma)*Integer.MAX_VALUE);
                    } else {
                        out = in;
                    }
                    dstBuf.put(out);
                }
                srcLine += srcStep;
                dstLine += dstStep;
            }
        }
        public static void flipCopyWithGamma(FloatBuffer srcBuf, int srcStep,
                FloatBuffer dstBuf, int dstStep, double gamma, boolean flip) {
            assert (srcBuf != dstBuf);
            int w = Math.min(srcStep, dstStep);
            int srcLine = srcBuf.position(), dstLine = dstBuf.position();
            while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
                if (flip) {
                    srcBuf.position(srcBuf.capacity() - srcLine - srcStep);
                } else {
                    srcBuf.position(srcLine);
                }
                dstBuf.position(dstLine);
                w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());
                for (int x = 0; x < w; x++) {
                    float in = srcBuf.get();
                    float out;
                    if (gamma != 1.0) {
                        out = (float)Math.pow(in, gamma);
                    } else {
                        out = in;
                    }
                    dstBuf.put(out);
                }
                srcLine += srcStep;
                dstLine += dstStep;
            }
        }
        public static void flipCopyWithGamma(DoubleBuffer srcBuf, int srcStep,
                DoubleBuffer dstBuf, int dstStep, double gamma, boolean flip) {
            assert (srcBuf != dstBuf);
            int w = Math.min(srcStep, dstStep);
            int srcLine = srcBuf.position(), dstLine = dstBuf.position();
            while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
                if (flip) {
                    srcBuf.position(srcBuf.capacity() - srcLine - srcStep);
                } else {
                    srcBuf.position(srcLine);
                }
                dstBuf.position(dstLine);
                w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());
                for (int x = 0; x < w; x++) {
                    double in = srcBuf.get();
                    double out;
                    if (gamma != 1.0) {
                        out = Math.pow(in, gamma);
                    } else {
                        out = in;
                    }
                    dstBuf.put(out);
                }
                srcLine += srcStep;
                dstLine += dstStep;
            }
        }
        public void applyGamma(double gamma) {
            if (gamma == 1.0) {
                return;
            }
            switch (depth) {
                case IPL_DEPTH_8U:
                    flipCopyWithGamma(getByteBuffer(), widthStep, getByteBuffer(), widthStep, false, gamma, false);
                    break;
                case IPL_DEPTH_8S:
                    flipCopyWithGamma(getByteBuffer(), widthStep, getByteBuffer(), widthStep, true, gamma, false);
                    break;
                case IPL_DEPTH_16U:
                    flipCopyWithGamma(getShortBuffer(), widthStep/2, getShortBuffer(), widthStep/2, false, gamma, false);
                    break;
                case IPL_DEPTH_16S:
                    flipCopyWithGamma(getShortBuffer(), widthStep/2, getShortBuffer(), widthStep/2, true, gamma, false);
                    break;
                case IPL_DEPTH_32S:
                    flipCopyWithGamma(getFloatBuffer(), widthStep/4, getFloatBuffer(), widthStep/4, gamma, false);
                    break;
                case IPL_DEPTH_32F:
                    flipCopyWithGamma(getFloatBuffer(), widthStep/4, getFloatBuffer(), widthStep/4, gamma, false);
                    break;
                case IPL_DEPTH_64F:
                    flipCopyWithGamma(getDoubleBuffer(), widthStep/8, getDoubleBuffer(), widthStep/8, gamma, false);
                    break;
                default:
                    assert (false);
            }
        }


        public void copyTo(BufferedImage image) {
            copyTo(image, 1.0);
        }
        public void copyTo(BufferedImage image, double gamma) {
            Rectangle r = null;
            if (roi != null) {
                r = new Rectangle(roi.xOffset, roi.yOffset, roi.width, roi.height);
            }
            copyTo(image, gamma, r);
        }
        public void copyTo(BufferedImage image, double gamma, Rectangle roi) {
            boolean flip = origin == IPL_ORIGIN_BL; // need to add support for ROI..

            ByteBuffer in  = getByteBuffer(roi == null ? 0 : roi.y*widthStep + roi.x*nChannels);
            SampleModel sm = image.getSampleModel();
            Raster r       = image.getRaster();
            DataBuffer out = r.getDataBuffer();
            int x = -r.getSampleModelTranslateX();
            int y = -r.getSampleModelTranslateY();
            int step = sm.getWidth()*sm.getNumBands();
            int channels = sm.getNumBands();
            if (sm instanceof ComponentSampleModel) {
                step = ((ComponentSampleModel)sm).getScanlineStride();
                channels = ((ComponentSampleModel)sm).getPixelStride();
            } else if (sm instanceof SinglePixelPackedSampleModel) {
                step = ((SinglePixelPackedSampleModel)sm).getScanlineStride();
                channels = 1;
            } else if (sm instanceof MultiPixelPackedSampleModel) {
                step = ((MultiPixelPackedSampleModel)sm).getScanlineStride();
                channels = ((MultiPixelPackedSampleModel)sm).getPixelBitStride()/8; // ??
            }
            int start = y*step + x*channels;

            if (out instanceof DataBufferByte) {
                byte[] a = ((DataBufferByte)out).getData();
                flipCopyWithGamma(in, widthStep, ByteBuffer.wrap(a, start, a.length - start), step, false, gamma, flip);
            } else if (out instanceof DataBufferDouble) {
                double[] a = ((DataBufferDouble)out).getData();
                flipCopyWithGamma(in.asDoubleBuffer(), widthStep/8, DoubleBuffer.wrap(a, start, a.length - start), step, gamma, flip);
            } else if (out instanceof DataBufferFloat) {
                float[] a = ((DataBufferFloat)out).getData();
                flipCopyWithGamma(in.asFloatBuffer(), widthStep/4, FloatBuffer.wrap(a, start, a.length - start), step, gamma, flip);
            } else if (out instanceof DataBufferInt) {
                int[] a = ((DataBufferInt)out).getData();
                if (depth == IPL_DEPTH_8U || depth == IPL_DEPTH_8S) {
                    in = in.order(ByteOrder.LITTLE_ENDIAN);
                }
                flipCopyWithGamma(in.asIntBuffer(), widthStep/4, IntBuffer.wrap(a, start, a.length - start), step, gamma, flip);
            } else if (out instanceof DataBufferShort) {
                short[] a = ((DataBufferShort)out).getData();
                flipCopyWithGamma(in.asShortBuffer(), widthStep/2, ShortBuffer.wrap(a, start, a.length - start), step, true, gamma, flip);
            } else if (out instanceof DataBufferUShort) {
                short[] a = ((DataBufferUShort)out).getData();
                flipCopyWithGamma(in.asShortBuffer(), widthStep/2, ShortBuffer.wrap(a, start, a.length - start), step, false, gamma, flip);
            } else {
                assert(false);
            }
        }

        public void copyFrom(BufferedImage image) {
            copyFrom(image, 1.0);
        }
        public void copyFrom(BufferedImage image, double gamma) {
            Rectangle r = null;
            if (roi != null) {
                r = new Rectangle(roi.xOffset, roi.yOffset, roi.width, roi.height);
            }
            copyFrom(image, gamma, r);
        }
        public void copyFrom(BufferedImage image, double gamma, Rectangle roi) {
            origin = IPL_ORIGIN_TL;

            ByteBuffer out = getByteBuffer(roi == null ? 0 : roi.y*widthStep + roi.x);
            SampleModel sm = image.getSampleModel();
            Raster r       = image.getRaster();
            DataBuffer in  = r.getDataBuffer();
            int x = r.getSampleModelTranslateX();
            int y = r.getSampleModelTranslateY();
            int step = sm.getWidth()*sm.getNumBands();
            if (sm instanceof ComponentSampleModel) {
                step = ((ComponentSampleModel)sm).getScanlineStride();
            } else if (sm instanceof SinglePixelPackedSampleModel) {
                step = ((SinglePixelPackedSampleModel)sm).getScanlineStride();
            } else if (sm instanceof MultiPixelPackedSampleModel) {
                step = ((MultiPixelPackedSampleModel)sm).getScanlineStride();
            }
            int start = y*step + x;

            if (in instanceof DataBufferByte) {
                byte[] a = ((DataBufferByte)in).getData();
                flipCopyWithGamma(ByteBuffer.wrap(a, start, a.length - start), step, out, widthStep, false, gamma, false);
            } else if (in instanceof DataBufferDouble) {
                double[] a = ((DataBufferDouble)in).getData();
                flipCopyWithGamma(DoubleBuffer.wrap(a, start, a.length - start), step, out.asDoubleBuffer(), widthStep/8, gamma, false);
            } else if (in instanceof DataBufferFloat) {
                float[] a = ((DataBufferFloat)in).getData();
                flipCopyWithGamma(FloatBuffer.wrap(a, start, a.length - start), step, out.asFloatBuffer(), widthStep/4, gamma, false);
            } else if (in instanceof DataBufferInt) {
                int[] a = ((DataBufferInt)in).getData();
                if (depth == IPL_DEPTH_8U || depth == IPL_DEPTH_8S) {
                    out = out.order(ByteOrder.LITTLE_ENDIAN);
                }
                flipCopyWithGamma(IntBuffer.wrap(a, start, a.length - start), step, out.asIntBuffer(), widthStep/4, gamma, false);
            } else if (in instanceof DataBufferShort) {
                short[] a = ((DataBufferShort)in).getData();
                flipCopyWithGamma(ShortBuffer.wrap(a, start, a.length - start), step, out.asShortBuffer(), widthStep/2, true, gamma, false);
            } else if (in instanceof DataBufferUShort) {
                short[] a = ((DataBufferUShort)in).getData();
                flipCopyWithGamma(ShortBuffer.wrap(a, start, a.length - start), step, out.asShortBuffer(), widthStep/2, false, gamma, false);
            } else {
                assert(false);
            }
            if (bufferedImage == null && roi == null &&
                    image.getWidth() == width && image.getHeight() == height) {
                bufferedImage = image;
            }
        }
        // not declared as BufferedImage => Android friendly
        private Object bufferedImage = null;
        public int getBufferedImageType() {
            // precanned BufferedImage types are confusing... in practice though,
            // they all use the sRGB color model when blitting:
            //     http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5051418
            // and we should use them because they are *A LOT* faster with Java 2D.
            // workaround: do gamma correction ourselves ("gamma" parameter)
            //             since we'll never use getRGB() and setRGB(), right?
            int type = BufferedImage.TYPE_CUSTOM;
            if (nChannels == 1) {
                if (depth == IPL_DEPTH_8U || depth == IPL_DEPTH_8S) {
                    type = BufferedImage.TYPE_BYTE_GRAY;
                } else if (depth == IPL_DEPTH_16U) {
                    type = BufferedImage.TYPE_USHORT_GRAY;
                }
            } else if (nChannels == 3) {
                if (depth == IPL_DEPTH_8U || depth == IPL_DEPTH_8S) {
                    type = BufferedImage.TYPE_3BYTE_BGR;
                }
            } else if (nChannels == 4) {
                // With big endian, the alpha channel ends up at the wrong end
                // for OpenCV. We work around this in copyTo() and copyFrom() by
                // forcing little endian order to get BGRA in all cases
                if (depth == IPL_DEPTH_8U || depth == IPL_DEPTH_8S) {
                    type = BufferedImage.TYPE_INT_ARGB;
                }
            }
            return type;
        }
        public BufferedImage getBufferedImage() {
            return getBufferedImage(1.0);
        }
        public BufferedImage getBufferedImage(double gamma) {
            return getBufferedImage(gamma, null);
        }
        public BufferedImage getBufferedImage(double gamma, ColorSpace cs) {
            int type = getBufferedImageType();

            if (bufferedImage == null && type != BufferedImage.TYPE_CUSTOM && cs == null) {
                bufferedImage = new BufferedImage(width, height, type);
            }

            if (bufferedImage == null) {
                boolean alpha = false;
                int[] offsets = null;
                if (nChannels == 1) {
                    alpha = false;
                    if (cs == null) {
                        cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
                    }
                    offsets = new int[] {0};
                } else if (nChannels == 3) {
                    alpha = false;
                    if (cs == null) {
                        cs = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
                    }
                    // raster in "BGR" order like OpenCV..
                    offsets = new int[] {2, 1, 0};
                } else if (nChannels == 4) {
                    alpha = true;
                    if (cs == null) {
                        cs = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
                    }
                    // raster in "BGRA" order like OpenCV.. alpha needs to be last
                    offsets = new int[] {2, 1, 0, 3};
                }

                ColorModel cm = null;
                WritableRaster wr = null;
                if (depth == IPL_DEPTH_8U || depth == IPL_DEPTH_8S) {
                    cm = new ComponentColorModel(cs, alpha,
                            false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
                    wr = Raster.createWritableRaster(new ComponentSampleModel(
                            DataBuffer.TYPE_BYTE, width, height, nChannels, widthStep,
                            offsets), null);
                } else if (depth == IPL_DEPTH_16U) {
                    cm = new ComponentColorModel(cs, alpha,
                            false, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);
                    wr = Raster.createWritableRaster(new ComponentSampleModel(
                            DataBuffer.TYPE_USHORT, width, height, nChannels, widthStep/2,
                            offsets), null);
                } else if (depth == IPL_DEPTH_16S) {
                    cm = new ComponentColorModel(cs, alpha,
                            false, Transparency.OPAQUE, DataBuffer.TYPE_SHORT);
                    wr = Raster.createWritableRaster(new ComponentSampleModel(
                            DataBuffer.TYPE_SHORT, width, height, nChannels, widthStep/2,
                            offsets), null);
                } else if (depth == IPL_DEPTH_32S) {
                    cm = new ComponentColorModel(cs, alpha,
                            false, Transparency.OPAQUE, DataBuffer.TYPE_INT);
                    wr = Raster.createWritableRaster(new ComponentSampleModel(
                            DataBuffer.TYPE_INT, width, height, nChannels, widthStep/4,
                            offsets), null);
                } else if (depth == IPL_DEPTH_32F) {
                    cm = new ComponentColorModel(cs, alpha,
                            false, Transparency.OPAQUE, DataBuffer.TYPE_FLOAT);
                    wr = Raster.createWritableRaster(new ComponentSampleModel(
                            DataBuffer.TYPE_FLOAT, width, height, nChannels, widthStep/4,
                            offsets), null);
                } else if (depth == IPL_DEPTH_64F) {
                    cm = new ComponentColorModel(cs, alpha,
                            false, Transparency.OPAQUE, DataBuffer.TYPE_DOUBLE);
                    wr = Raster.createWritableRaster(new ComponentSampleModel(
                            DataBuffer.TYPE_DOUBLE, width, height, nChannels, widthStep/8,
                            offsets), null);
                }

                bufferedImage = new BufferedImage(cm, wr, false, null);
            }

            if (bufferedImage != null) {
                if (roi != null) {
                    copyTo(((BufferedImage)bufferedImage).getSubimage(roi.xOffset, roi.yOffset, roi.width, roi.height), gamma);
                } else {
                    copyTo((BufferedImage)bufferedImage, gamma);
                }
            }

            return (BufferedImage)bufferedImage;
        }

        public static class ByReference extends IplImage implements Structure.ByReference { }
        public static class ByValue extends IplImage implements Structure.ByValue { }

        public static class PointerByReference extends CvArr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(IplImage p) { setStructure(p); }
            public PointerByReference(IplImage[] a) { super(a); }

            public IplImage getStructure() {
                return new IplImage(getValue());
            }
            public void getStructure(IplImage p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(IplImage p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static class IplTileInfo extends PointerType { }

    public static class IplROI extends Structure {
        public static boolean autoSynch = true;
        public IplROI() { setAutoSynch(autoSynch); }
        public IplROI(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == IplROI.class) read(); }

        private static final String[] fieldOrder = {
            "coi", "xOffset", "yOffset", "width", "height" };
        { setFieldOrder(fieldOrder); }
        public int coi;
        public int xOffset;
        public int yOffset;
        public int width;
        public int height;

        public static class ByReference extends IplROI implements Structure.ByReference { }
    }

    public static final int
            IPL_IMAGE_HEADER = 1,
            IPL_IMAGE_DATA   = 2,
            IPL_IMAGE_ROI    = 4,

            IPL_BORDER_REFLECT_101   = 4,

            IPL_IMAGE_MAGIC_VAL  = Native.getNativeSize(IplImage.ByValue.class);
    public static final String CV_TYPE_NAME_IMAGE = "opencv-image";

    public static boolean CV_IS_IMAGE_HDR(IplImage img) {
        return img != null && img.nSize == img.size();
    }
    public static boolean CV_IS_IMAGE(IplImage img) {
        return CV_IS_IMAGE_HDR(img) && img.imageData != null;
    }


    public static class CvArr extends Structure implements Cloneable {
        public CvArr() { }
        public CvArr(Pointer m) { super(m); if (getClass() == CvArr.class) read(); }
        public static class ByReference extends CvArr implements Structure.ByReference {
            @Override protected void useMemory(Pointer m, int offset) {
                try {
                    super.useMemory(m, offset);
                } catch (IllegalArgumentException e) {
                    // size == 0 is normal
                }
            }
            @Override protected void allocateMemory() { /* has no memory */ }
            @Override protected void allocateMemory(int size) { /* has no memory */ }
        }
        public static class PointerByReference extends com.sun.jna.ptr.ByReference {
            public PointerByReference() { super(Pointer.SIZE); }
            public PointerByReference(Pointer value) { super(Pointer.SIZE); setValue(value); }
            public PointerByReference(CvArr[] a) {
                super(Pointer.SIZE * a.length);
                Pointer p = getPointer();
                for (int i = 0; i < a.length; i++) {
                    if (a[i] != null) {
                        a[i].write();
                        p.setPointer(Pointer.SIZE * i, a[i].getPointer());
                    } else {
                        p.setPointer(Pointer.SIZE * i, null);
                    }
                }
            }
            public static PointerByReference[] createArray(int size) {
                PointerByReference[] a = new PointerByReference[size];
                Pointer p = new Memory(Pointer.SIZE * size);
                for (int i = 0; i < size; i++) {
                    a[i] = new PointerByReference(p.share(Pointer.SIZE * i));
                }
                return a;
            }

            public void setValue(Pointer value) {
                getPointer().setPointer(0, value);
            }
            public Pointer getValue() {
                return getPointer().getPointer(0);
            }
        }
    }


    public static native IplImage cvCreateImage(CvSize.ByValue size, int depth, int channels);
    public static native IplImage cvCreateImageHeader(CvSize.ByValue size, int depth, int channels);
    public static native void cvReleaseImageHeader(IplImage.PointerByReference image);
    public static native void cvReleaseImage(IplImage.PointerByReference image);
    public static native IplImage cvInitImageHeader(IplImage image, CvSize.ByValue size, int depth,
            int channels, int origin/*=0*/, int align/*=4*/);
    public static native IplImage cvCloneImage(IplImage image);
    public static native void cvSetImageCOI(IplImage image, int coi);
    public static native int cvGetImageCOI(IplImage image);
    public static native void cvSetImageROI(IplImage image, CvRect.ByValue rect);
    public static native void cvResetImageROI(IplImage image);
    public static native CvRect.ByValue cvGetImageROI(IplImage image);

    public static native CvMat cvCreateMat(int rows, int cols, int type);
    public static native CvMat cvCreateMatHeader(int rows, int cols, int type);
    public static native void cvReleaseMat(CvMat.PointerByReference mat);
    public static native CvMat cvInitMatHeader(CvMat mat, int rows, int cols, int type,
            Pointer data/*=null*/, int step/*=CV_AUTOSTEP*/);
    public static native CvMat cvCloneMat(CvMat mat);

    public static native CvMatND cvCreateMatND(int dims, int[] sizes, int type);
    public static native CvMatND cvCreateMatNDHeader(int dims, int[] sizes, int type);
    public static native CvMatND cvInitMatNDHeader(CvMatND mat, int dims, int[] sizes,
            int type, Pointer data/*=null*/);
    public static void cvReleaseMatND(CvMatND.PointerByReference mat) {
        cvReleaseMat(mat);
    }
    public static native CvMatND cvCloneMatND(CvMatND mat);

    public static native void cvCreateData(CvArr arr);
    public static native void cvReleaseData(CvArr arr);
    public static native void cvSetData(CvArr arr, Pointer data, int step);
    public static native void cvGetRawData(CvArr arr, PointerByReference data,
            IntByReference step/*=null*/, CvSize roi_size/*=null*/);

    public static native CvMat cvGetMat(CvArr arr, CvMat header, IntByReference coi/*=null*/, int allowND/*=0*/);
    public static native IplImage cvGetImage(CvArr arr, IplImage image_header);

    public static native CvSparseMat cvCreateSparseMat(int dims, int[] sizes, int type);
    public static native void cvReleaseSparseMat(CvSparseMat.PointerByReference mat);
    public static native CvSparseMat cvCloneSparseMat(CvSparseMat mat);


    public static native CvMat cvGetSubRect(CvArr arr, CvMat submat, CvRect.ByValue rect);
    public static CvMat cvGetSubArr(CvArr arr, CvMat submat, CvRect.ByValue rect) {
        return cvGetSubRect(arr, submat, rect);
    }
    public static native CvMat cvGetRows(CvArr arr, CvMat submat, int start_row,
            int end_row/*=start_row+1*/, int delta_row/*=1*/);
    public static CvMat cvGetRow(CvArr arr, CvMat submat, int row) {
        return cvGetRows(arr, submat, row, row + 1, 1);
    }
    public static native CvMat cvGetCols(CvArr arr, CvMat submat, int start_col,
            int end_col/*=start_col+1*/);
    public static CvMat cvGetCols(CvArr arr, CvMat submat, int col) {
        return cvGetCols(arr, submat, col, col + 1);
    }
    public static native CvMat cvGetDiag(CvArr arr, CvMat submat, int diag/*=0*/);
    public static native CvSize.ByValue cvGetSize(CvArr arr);


    public static final int CV_MAX_ARR = 10,
            CV_NO_DEPTH_CHECK    = 1,
            CV_NO_CN_CHECK       = 2,
            CV_NO_SIZE_CHECK     = 4;
    public static class CvNArrayIterator extends Structure {
        public static boolean autoSynch = true;
        public CvNArrayIterator() { setAutoSynch(autoSynch); }
        public CvNArrayIterator(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvNArrayIterator.class) read(); }

        private static final String[] fieldOrder = {
            "count", "dims", "size", "ptr", "stack", "hdr" };
        { setFieldOrder(fieldOrder); }
        public int count;
        public int dims;
        public CvSize size;
        public byte[] ptr = new byte[CV_MAX_ARR];
        public int[] stack = new int[CV_MAX_DIM];
        public CvMatND.ByReference[] hdr = new CvMatND.ByReference[CV_MAX_ARR];
    }
    public static native int cvInitNArrayIterator(int count, CvArr.PointerByReference arrs,
            CvArr mask, CvMatND stubs, CvNArrayIterator array_iterator, int flags/*=0*/);
    public static int cvInitNArrayIterator(int count, CvArr[] arrs,
            CvArr mask, CvMatND stubs, CvNArrayIterator array_iterator, int flags/*=0*/) {
        return cvInitNArrayIterator(count, new CvArr.PointerByReference(arrs), mask, stubs, array_iterator, flags);
    }
    public static native int cvNextNArraySlice(CvNArrayIterator array_iterator);


    public static native CvSparseNode cvInitSparseMatIterator(CvSparseMat mat,
            CvSparseMatIterator mat_iterator);
    public static CvSparseNode cvGetNextSparseNode(CvSparseMatIterator mat_iterator) {
        if (mat_iterator.node.next != null) {
            return mat_iterator.node = mat_iterator.node.next;
        } else {
            int idx;
            Pointer[] hashtable = mat_iterator.mat.hashtable.getPointer().getPointerArray(0, mat_iterator.mat.hashsize);
            for (idx = ++mat_iterator.curidx; idx < mat_iterator.mat.hashsize; idx++) {
                if (hashtable[idx] != null) {
                    mat_iterator.curidx = idx;
                    return mat_iterator.node = new CvSparseNode.ByReference(hashtable[idx]);
                }
            }
            return null;
        }
    }
    public static native int cvGetElemType(CvArr arr);
    public static native int cvGetDims(CvArr arr, int[] sizes/*=null*/);
    public static native int cvGetDimSize(CvArr arr, int index);

    public static native Pointer cvPtr1D(CvArr arr, int idx0, IntByReference type/*=null*/);
    public static native Pointer cvPtr2D(CvArr arr, int idx0, int idx1, IntByReference type/*=null*/);
    public static native Pointer cvPtr3D(CvArr arr, int idx0, int idx1, int idx2, IntByReference type/*=null*/);
    public static native Pointer cvPtrND(CvArr arr, int[] idx, IntByReference type/*=null*/,
            int create_node/*=1*/, IntByReference precalc_hashval/*=null*/);
    public static native CvScalar.ByValue cvGet1D(CvArr arr, int idx0);
    public static native CvScalar.ByValue cvGet2D(CvArr arr, int idx0, int idx1);
    public static native CvScalar.ByValue cvGet3D(CvArr arr, int idx0, int idx1, int idx2);
    public static native CvScalar.ByValue cvGetND(CvArr arr, int[] idx);
    public static native double cvGetReal1D(CvArr arr, int idx0);
    public static native double cvGetReal2D(CvArr arr, int idx0, int idx1);
    public static native double cvGetReal3D(CvArr arr, int idx0, int idx1, int idx2);
    public static native double cvGetRealND(CvArr arr, int[] idx);
    public static native void cvSet1D(CvArr arr, int idx0, CvScalar.ByValue value);
    public static native void cvSet2D(CvArr arr, int idx0, int idx1, CvScalar.ByValue value);
    public static native void cvSet3D(CvArr arr, int idx0, int idx1, int idx2, CvScalar.ByValue value);
    public static native void cvSetND(CvArr arr, int[] idx, CvScalar.ByValue value);
    public static native void cvSetReal1D(CvArr arr, int idx0, double value);
    public static native void cvSetReal2D(CvArr arr, int idx0, int idx1, double value);
    public static native void cvSetReal3D(CvArr arr, int idx0, int idx1, int idx2, double value);
    public static native void cvSetRealND(CvArr arr, int[] idx, double value);
    public static native void cvClearND(CvArr arr, int[] idx);


    public static native void cvCopy(CvArr src, CvArr dst, CvArr mask/*=null*/);
    public static void cvCopy(CvArr src, CvArr dst) {
        cvCopy(src, dst, null);
    }
    public static native void cvSet(CvArr arr, CvScalar.ByValue value, CvArr mask/*=null*/);
    public static void cvSet(CvArr arr, CvScalar.ByValue value) {
        cvSet(arr, value, null);
    }
    public static native void cvSetZero(CvArr arr);
    public static void cvZero(CvArr arr) {
        cvSetZero(arr);
    }
    public static native void cvSetIdentity(CvArr mat, CvScalar.ByValue value/*=cvRealScalar(1)*/);
    public static void cvSetIdentity(CvArr mat, double value) {
        cvSetIdentity(mat, cvRealScalar(value));
    }
    public static void cvSetIdentity(CvArr mat) {
        cvSetIdentity(mat, 1);
    }
    public static native CvArr cvRange(CvArr mat, double start, double end);


    public static native CvMat cvReshape(CvArr arr, CvMat header, int new_cn, int new_rows/*=0*/);
    public static native CvArr cvReshapeMatND(CvArr arr, int sizeof_header, CvArr header,
            int new_cn, int new_dims, int[] new_sizes);
    public static CvArr cvReshapeND(CvArr arr, CvArr header, int new_cn, int new_dims, int[] new_sizes) {
        return cvReshapeMatND(arr, header.size(), header, new_cn, new_dims, new_sizes);
    }

    public static native void cvRepeat(CvArr src, CvArr dst);

    public static native void cvFlip(CvArr src, CvArr dst, int flip_mode/*=0*/);
    public static void cvMirror(CvArr src, CvArr dst, int flip_mode/*=0*/) {
        cvFlip(src, dst, flip_mode);
    }
    public static native void cvSplit(CvArr src, CvArr dst0, CvArr dst1, CvArr dst2, CvArr dst3);
    public static native void cvMerge(CvArr src0, CvArr src1, CvArr src2, CvArr src3, CvArr dst);
    public static native void cvMixChannels(CvArr src, int src_count, CvArr dst, int dst_count,
            int[] from_to, int pair_count);
    public static void cvMixChannels(CvArr[] src, int src_count, CvArr[] dst, int dst_count,
            int[] from_to, int pair_count) {
        for (Structure s : src) { s.write(); } for (Structure s : dst) { s.write(); }
        cvMixChannels(src[0], src_count, dst[0], dst_count, from_to, pair_count);
        for (Structure s : dst) { s.read(); } for (Structure s : dst) { s.read(); }
    }
    public static native void cvRandShuffle(CvArr mat, CvRNG rng, double iter_factor/*=1*/);


    public static native void cvLUT(CvArr src, CvArr dst, CvArr lut);
    public static native void cvConvertScale(CvArr src, CvArr dst, double scale/*=1*/, double shift/*=0*/);
    public static void cvCvtScale(CvArr src, CvArr dst, double scale/*=1*/, double shift/*=0*/) {
        cvConvertScale(src, dst, scale, shift);
    }
    public static void cvScale(CvArr src, CvArr dst, double scale/*=1*/, double shift/*=0*/) {
        cvConvertScale(src, dst, scale, shift);
    }
    public static void cvConvert(CvArr src, CvArr dst) {
        cvConvertScale(src, dst, 1, 0);
    }
    public static native void cvConvertScaleAbs(CvArr src, CvArr dst, double scale/*=1*/, double shift/*=0*/);
    public static void cvCvtScaleAbs(CvArr src, CvArr dst, double scale/*=1*/, double shift/*=0*/) {
        cvConvertScaleAbs(src, dst, scale, shift);
    }
    public static native void cvAdd(CvArr src1, CvArr src2, CvArr dst, CvArr mask/*=null*/);
    public static native void cvAddS(CvArr src, CvScalar.ByValue value, CvArr dst, CvArr mask/*=null*/);
    public static native void cvAddWeighted(CvArr src1, double alpha, CvArr src2, double beta,
            double gamma, CvArr dst);
    public static native void cvSub(CvArr src1, CvArr src2, CvArr dst, CvArr mask/*=null*/);
    public static void cvSubS(CvArr src, CvScalar.ByValue value, CvArr dst, CvArr mask/*=null*/) {
        cvAddS(src, new CvScalar.ByValue(-value.val[0], -value.val[1], -value.val[2], -value.val[3]),
               dst, mask);
    }
    public static native void cvSubRS(CvArr src, CvScalar.ByValue value, CvArr dst, CvArr mask/*=null*/);
    public static native void cvMul(CvArr src1, CvArr src2, CvArr dst, double scale/*=1*/);
    public static native void cvDiv(CvArr src1, CvArr src2, CvArr dst, double scale/*=1*/);
    public static native void cvAnd(CvArr src1, CvArr src2, CvArr dst, CvArr mask/*=null*/);
    public static native void cvAndS(CvArr src, CvIntScalar.ByValue value, CvArr dst, CvArr mask/*=null*/);
    public static native void cvOr(CvArr src1, CvArr src2, CvArr dst, CvArr mask/*=null*/);
    public static native void cvOrS(CvArr src, CvIntScalar.ByValue value, CvArr dst, CvArr mask/*=null*/);
    public static native void cvXor(CvArr src1, CvArr src2, CvArr dst, CvArr mask/*=null*/);
    public static native void cvXorS(CvArr src, CvIntScalar.ByValue value, CvArr dst, CvArr mask/*=null*/);
    public static native void cvNot(CvArr src, CvArr dst);

    public static final int
            CV_CMP_EQ  = 0,
            CV_CMP_GT  = 1,
            CV_CMP_GE  = 2,
            CV_CMP_LT  = 3,
            CV_CMP_LE  = 4,
            CV_CMP_NE  = 5;
    public static native void cvCmp(CvArr src1, CvArr src2, CvArr dst, int cmp_op);
    public static native void cvCmpS(CvArr src, double value, CvArr dst, int cmp_op);
    public static native void cvInRange(CvArr src, CvArr lower, CvArr upper, CvArr dst);
    public static native void cvInRangeS(CvArr src, CvScalar.ByValue lower, CvScalar.ByValue upper, CvArr dst);
    public static native void cvMax(CvArr src1, CvArr src2, CvArr dst);
    public static native void cvMaxS(CvArr src, double value, CvArr dst);
    public static native void cvMin(CvArr src1, CvArr src2, CvArr dst);
    public static native void cvMinS(CvArr src, double value, CvArr dst);
    public static native void cvAbsDiff(CvArr src1, CvArr src2, CvArr dst);
    public static native void cvAbsDiffS(CvArr src, CvArr dst, CvScalar.ByValue value/*=cvScalarAll(0)*/);
    public static void cvAbs(CvArr src, CvArr dst) {
        cvAbsDiffS(src, dst, cvScalarAll(0.0));
    }

    public static native int cvCountNonZero(CvArr arr);
    public static native CvScalar.ByValue cvSum(CvArr arr);
    public static native CvScalar.ByValue cvAvg(CvArr arr, CvArr mask/*=null*/);
    public static native void cvAvgSdv(CvArr arr, CvScalar mean, CvScalar std_dev, CvArr mask/*=null*/);
    public static native void cvMinMaxLoc(CvArr arr, DoubleByReference min_val, DoubleByReference max_val,
            CvPoint min_loc/*=null*/, CvPoint max_loc/*=null*/, CvArr mask/*=null*/);
    public static final int
            CV_C          = 1,
            CV_L1         = 2,
            CV_L2         = 4,
            CV_NORM_MASK  = 7,
            CV_RELATIVE   = 8,
            CV_DIFF       = 16,
            CV_MINMAX     = 32,

            CV_DIFF_C     = (CV_DIFF | CV_C),
            CV_DIFF_L1    = (CV_DIFF | CV_L1),
            CV_DIFF_L2    = (CV_DIFF | CV_L2),
            CV_RELATIVE_C = (CV_RELATIVE | CV_C),
            CV_RELATIVE_L1= (CV_RELATIVE | CV_L1),
            CV_RELATIVE_L2= (CV_RELATIVE | CV_L2);
    public static native double cvNorm(CvArr arr1, CvArr arr2/*=null*/,
            int norm_type/*=CV_L2*/, CvArr mask/*=null*/);
    public static double cvNorm(CvArr arr1, CvArr arr2/*=null*/,
            int norm_type/*=CV_L2*/) {
        return cvNorm(arr1, arr2, norm_type, null);
    }
    public static double cvNorm(CvArr arr1, CvArr arr2/*=null*/) {
        return cvNorm(arr1, arr2, CV_L2, null);
    }
    public static double cvNorm(CvArr arr1) {
        return cvNorm(arr1, null, CV_L2, null);
    }
    public static final int
            CV_REDUCE_SUM = 0,
            CV_REDUCE_AVG = 1,
            CV_REDUCE_MAX = 2,
            CV_REDUCE_MIN = 3;
    public static native void cvReduce(CvArr src, CvArr dst, int op/*=CV_REDUCE_SUM*/);


    public static native double cvDotProduct(CvArr src1, CvArr src2);
    public static native void cvNormalize(CvArr src, CvArr dst, double a/*=1*/, double b/*=0*/,
            int norm_type/*=CV_L2*/, CvArr mask/*=null*/);
    public static void cvNormalize(CvArr src, CvArr dst) {
        cvNormalize(src, dst, 1, 0, CV_L2, null);
    }
    public static native void cvCrossProduct(CvArr src1, CvArr src2, CvArr dst);
    public static native void cvScaleAdd(CvArr src1, CvScalar.ByValue scale, CvArr src2, CvArr dst);
    public static void cvAXPY(CvArr A, double real_scalar, CvArr B, CvArr C) {
        cvScaleAdd(A, cvRealScalar(real_scalar), B, C);
    }
    public static void cvMatMulAdd(CvArr src1, CvArr src2, CvArr src3, CvArr dst) {
        cvGEMM(src1, src2, 1., src3, 1., dst, 0);
    }
    public static void cvMatMul(CvArr src1, CvArr src2, CvArr dst) {
        cvMatMulAdd(src1, src2, null, dst);
    }
    public static final int
            CV_GEMM_A_T = 1,
            CV_GEMM_B_T = 2,
            CV_GEMM_C_T = 4;
    public static native void cvGEMM(CvArr srcA, CvArr srcB, double alpha,  CvArr srcC, double beta,
            CvArr dst, int tABC/*=0*/);
    public static void cvMatMulAddEx(CvArr srcA, CvArr srcB, double alpha,  CvArr srcC, double beta,
            CvArr dst, int tABC/*=0*/) {
        cvGEMM(srcA, srcB, alpha, srcC, beta, dst, tABC);
    }
    public static native void cvTransform(CvArr src, CvArr dst, CvMat transmat, CvMat shiftvec/*=null*/);
    public static void cvMatMulAddS(CvArr src, CvArr dst, CvMat transmat, CvMat shiftvec/*=null*/) {
        cvTransform(src, dst, transmat, shiftvec);
    }
    public static native void cvPerspectiveTransform(CvArr src, CvArr dst, CvMat mat);
    public static native void cvMulTransposed(CvArr src, CvArr dst, int order, CvArr delta/*=null*/,
            double scale/*=1*/);
    public static native CvScalar.ByValue cvTrace(CvArr mat);
    public static native void cvTranspose(CvArr src, CvArr dst);
    public static void cvT(CvArr src, CvArr dst) {
        cvTranspose(src, dst);
    }
    public static native double cvDet(CvArr mat);
    public static final int
            CV_LU  = 0,
            CV_SVD = 1,
            CV_SVD_SYM = 2,
            CV_CHOLESKY = 3,
            CV_QR = 4,
            CV_LSQ = 8,
            CV_NORMAL = 16;
    public static native double cvInvert(CvArr src, CvArr dst, int method/*=CV_LU*/);
    public static double cvInvert(CvArr src, CvArr dst) {
        return cvInvert(src, dst, CV_LU);
    }
    public static double cvInv(CvArr src, CvArr dst, int method/*=CV_LU*/) {
        return cvInvert(src, dst, method);
    }
    public static double cvInv(CvArr src, CvArr dst) {
        return cvInvert(src, dst, CV_LU);
    }
    public static native int cvSolve(CvArr A, CvArr B, CvArr X, int method/*=CV_LU*/);
    public static int cvSolve(CvArr A, CvArr B, CvArr X) {
        return cvSolve(A, B, X, CV_LU);
    }
    public static final int
            CV_SVD_MODIFY_A  = 1,
            CV_SVD_U_T       = 2,
            CV_SVD_V_T       = 4;
    public static native void cvSVD(CvArr A, CvArr W, CvArr U/*=null*/, CvArr V/*=null*/, int flags/*=0*/);
    public static native void cvSVBkSb(CvArr W, CvArr U, CvArr V, CvArr B, CvArr X, int flags);
    public static native void cvEigenVV(CvArr mat, CvArr evects, CvArr evals, double eps/*=0*/,
            int lowindex/*=-1*/, int highindex/*=-1*/);
    public static final int
            CV_COVAR_SCRAMBLED = 0,
            CV_COVAR_NORMAL    = 1,
            CV_COVAR_USE_AVG   = 2,
            CV_COVAR_SCALE     = 4,
            CV_COVAR_ROWS      = 8,
            CV_COVAR_COLS     = 16;
    public static native void cvCalcCovarMatrix(CvArr vects, int count, CvArr cov_mat, CvArr avg, int flags);
    public static void cvCalcCovarMatrix(CvArr[] vects, int count, CvArr cov_mat, CvArr avg, int flags) {
        for (Structure s : vects) { s.write(); }
        cvCalcCovarMatrix(vects[0], count, cov_mat, avg, flags);
        for (Structure s : vects) { s.read(); }
    }
    public static native double cvMahalanobis(CvArr vec1, CvArr vec2, CvArr mat);
    public static double cvMahalonobis(CvArr vec1, CvArr vec2, CvArr mat) {
        return cvMahalanobis(vec1, vec2, mat);
    }
    public static final int
            CV_PCA_DATA_AS_ROW = 0,
            CV_PCA_DATA_AS_COL = 1,
            CV_PCA_USE_AVG     = 2;
    public static native void cvCalcPCA(CvArr data, CvArr mean, CvArr eigenvals, CvArr eigenvects, int flags);
    public static native void cvProjectPCA(CvArr data, CvArr mean, CvArr eigenvects, CvArr result);
    public static native void cvBackProjectPCA(CvArr proj, CvArr mean, CvArr eigenvects, CvArr result);


    public static native float cvCbrt(float value);
    public static native float cvFastArctan(float y, float x);
    public static native void cvCartToPolar(CvArr x, CvArr y, CvArr magnitude,
            CvArr angle/*=null*/, int angle_in_degrees/*=0*/);
    public static native void cvPolarToCart(CvArr magnitude, CvArr angle,
            CvArr x, CvArr y, int angle_in_degrees/*=0*/);
    public static native void cvPow(CvArr src, CvArr dst, double power);
    public static native void cvExp(CvArr src, CvArr dst);
    public static native void cvLog(CvArr src, CvArr dst);
    public static native int cvSolveCubic(CvMat coeffs, CvMat roots);


    public static class CvRNG extends LongByReference {
        public CvRNG() { super(-1); }
        public CvRNG(long seed) { super(seed); }
    }
    public static CvRNG cvRNG() {
        return new CvRNG();
    }
    public static CvRNG cvRNG(long seed/*=-1*/) {
        return new CvRNG(seed);
    }
    public static int cvRandInt(CvRNG rng) {
        long temp = rng.getValue();
        temp = ((temp&0xFFFFFFFFL)*4164903690L) + ((temp >> 32)&0xFFFFFFFFL);
        rng.setValue(temp);
        return (int)temp;
    }
    public static double cvRandReal(CvRNG rng) {
        return ((long)cvRandInt(rng)&0xFFFFFFFFL)*2.3283064365386962890625e-10;
    }

    public static final int
        CV_RAND_UNI     = 0,
        CV_RAND_NORMAL  = 1;
    public static native void cvRandArr(CvRNG rng, CvArr arr, int dist_type,
            CvScalar.ByValue param1, CvScalar.ByValue param2);


    public static final int
            CV_DXT_FORWARD  = 0,
            CV_DXT_INVERSE  = 1,
            CV_DXT_SCALE    = 2,
            CV_DXT_INV_SCALE = (CV_DXT_INVERSE + CV_DXT_SCALE),
            CV_DXT_INVERSE_SCALE = CV_DXT_INV_SCALE,
            CV_DXT_ROWS     = 4,
            CV_DXT_MUL_CONJ = 8;
    public static native void cvDFT(CvArr src, CvArr dst, int flags, int nonzero_rows/*=0*/);
    public static void cvFFT(CvArr src, CvArr dst, int flags, int nonzero_rows/*=0*/) {
        cvDFT(src, dst, flags, nonzero_rows);
    }
    public static native int cvGetOptimalDFTSize(int size0);
    public static native void cvMulSpectrums(CvArr src1, CvArr src2, CvArr dst, int flags);
    public static native void cvDCT(CvArr src, CvArr dst, int flags);


    public static class CvMemBlock extends Structure {
        public static boolean autoSynch = true;
        public CvMemBlock() { setAutoSynch(autoSynch); }
        public CvMemBlock(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvMemBlock.class) read(); }

        private static final String[] fieldOrder = { "prev", "next" };
        { setFieldOrder(fieldOrder); }
        public CvMemBlock.ByReference  prev;
        public CvMemBlock.ByReference  next;

        public static class ByReference extends CvMemBlock implements Structure.ByReference { }
    }

    public static final int CV_STORAGE_MAGIC_VAL = 0x42890000;

    public static class CvMemStoragePos extends Structure {
        public static boolean autoSynch = true;
        public CvMemStoragePos() { setAutoSynch(autoSynch); }
        public CvMemStoragePos(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvMemStoragePos.class) read(); }

        private static final String[] fieldOrder = { "top", "free_space" };
        { setFieldOrder(fieldOrder); }
        public CvMemBlock.ByReference top;
        public int free_space;
    }

    public static class CvMemStorage extends Structure {
        public static boolean autoSynch = true;
        public CvMemStorage() { setAutoSynch(autoSynch); releasable = false; }
        public CvMemStorage(Pointer m) { super(m); setAutoSynch(autoSynch); releasable = true;
                if (autoSynch && getClass() == CvMemStorage.class) read(); }

        public static CvMemStorage create(int block_size) {
            CvMemStorage m = cvCreateMemStorage(block_size);
            if (m != null) {
                m.releasable = true;
            }
            return m;
        }
        public static CvMemStorage create() {
            return create(0);
        }

        public void release() {
            releasable = false;
            cvReleaseMemStorage(pointerByReference());
        }
        @Override protected void finalize() {
            if (releasable) {
                release();
            }
        }
        private boolean releasable = false;


        private static final String[] fieldOrder = {
            "signature", "bottom", "top", "parent", "block_size", "free_space" };
        { setFieldOrder(fieldOrder); }
        public int signature;
        public CvMemBlock.ByReference bottom;
        public CvMemBlock.ByReference top;
        public CvMemStorage.ByReference parent;
        public int block_size;
        public int free_space;


        public static class ByReference extends CvMemStorage implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvMemStorage p) {
                setStructure(p);
            }
            public CvMemStorage getStructure() {
                return new CvMemStorage(getValue());
            }
            public void getStructure(CvMemStorage p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvMemStorage p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static native CvMemStorage cvCreateMemStorage(int block_size/*=0*/);
    public static native CvMemStorage cvCreateChildMemStorage(CvMemStorage parent);
    public static native void cvReleaseMemStorage(CvMemStorage.PointerByReference storage);
    public static native void cvClearMemStorage(CvMemStorage storage);
    public static native Pointer cvMemStorageAlloc(CvMemStorage storage, size_t size);
    public static class CvString extends Structure {
        private static final String[] fieldOrder = { "len", "ptr" };
        { setFieldOrder(fieldOrder); }
        public int len;
        public String ptr;

        public static class ByValue extends CvString implements Structure.ByReference { }
    }
    public static native CvString.ByValue cvMemStorageAllocString(CvMemStorage storage, Pointer ptr, int len/*=-1*/);
    public static native void cvSaveMemStoragePos(CvMemStorage storage, CvMemStoragePos pos);
    public static native void cvRestoreMemStoragePos(CvMemStorage storage, CvMemStoragePos pos);


    public static final int
            CV_SEQ_MAGIC_VAL            = 0x42990000,

            CV_SET_MAGIC_VAL            = 0x42980000,

            CV_SEQ_ELTYPE_BITS          = 9,
            CV_SEQ_ELTYPE_MASK          = ((1 << CV_SEQ_ELTYPE_BITS) - 1),

            CV_SEQ_ELTYPE_POINT         = CV_32SC2,
            CV_SEQ_ELTYPE_CODE          = CV_8UC1,
            CV_SEQ_ELTYPE_GENERIC       = 0,
            CV_SEQ_ELTYPE_PTR           = CV_USRTYPE1,
            CV_SEQ_ELTYPE_PPOINT        = CV_SEQ_ELTYPE_PTR,
            CV_SEQ_ELTYPE_INDEX         = CV_32SC1,
            CV_SEQ_ELTYPE_GRAPH_EDGE    = 0,
            CV_SEQ_ELTYPE_GRAPH_VERTEX  = 0,
            CV_SEQ_ELTYPE_TRIAN_ATR     = 0,
            CV_SEQ_ELTYPE_CONNECTED_COMP= 0,
            CV_SEQ_ELTYPE_POINT3D       = CV_32FC3,

            CV_SEQ_KIND_BITS       = 3,
            CV_SEQ_KIND_MASK       = (((1 << CV_SEQ_KIND_BITS) - 1)<<CV_SEQ_ELTYPE_BITS),


            CV_SEQ_KIND_GENERIC    = (0 << CV_SEQ_ELTYPE_BITS),
            CV_SEQ_KIND_CURVE      = (1 << CV_SEQ_ELTYPE_BITS),
            CV_SEQ_KIND_BIN_TREE   = (2 << CV_SEQ_ELTYPE_BITS),


            CV_SEQ_KIND_GRAPH      = (3 << CV_SEQ_ELTYPE_BITS),
            CV_SEQ_KIND_SUBDIV2D   = (4 << CV_SEQ_ELTYPE_BITS),

            CV_SEQ_FLAG_SHIFT      = (CV_SEQ_KIND_BITS + CV_SEQ_ELTYPE_BITS),


            CV_SEQ_FLAG_CLOSED    = (1 << CV_SEQ_FLAG_SHIFT),
            CV_SEQ_FLAG_SIMPLE    = (2 << CV_SEQ_FLAG_SHIFT),
            CV_SEQ_FLAG_CONVEX    = (4 << CV_SEQ_FLAG_SHIFT),
            CV_SEQ_FLAG_HOLE      = (8 << CV_SEQ_FLAG_SHIFT),


            CV_GRAPH_FLAG_ORIENTED = (1 << CV_SEQ_FLAG_SHIFT),

            CV_GRAPH              = CV_SEQ_KIND_GRAPH,
            CV_ORIENTED_GRAPH     = (CV_SEQ_KIND_GRAPH|CV_GRAPH_FLAG_ORIENTED),


            CV_SEQ_POINT_SET      = (CV_SEQ_KIND_GENERIC| CV_SEQ_ELTYPE_POINT),
            CV_SEQ_POINT3D_SET    = (CV_SEQ_KIND_GENERIC| CV_SEQ_ELTYPE_POINT3D),
            CV_SEQ_POLYLINE       = (CV_SEQ_KIND_CURVE  | CV_SEQ_ELTYPE_POINT),
            CV_SEQ_POLYGON        = (CV_SEQ_FLAG_CLOSED | CV_SEQ_POLYLINE ),
            CV_SEQ_CONTOUR        = CV_SEQ_POLYGON,
            CV_SEQ_SIMPLE_POLYGON = (CV_SEQ_FLAG_SIMPLE | CV_SEQ_POLYGON  ),


            CV_SEQ_CHAIN          = (CV_SEQ_KIND_CURVE  | CV_SEQ_ELTYPE_CODE),
            CV_SEQ_CHAIN_CONTOUR  = (CV_SEQ_FLAG_CLOSED | CV_SEQ_CHAIN),


            CV_SEQ_POLYGON_TREE   = (CV_SEQ_KIND_BIN_TREE  | CV_SEQ_ELTYPE_TRIAN_ATR),


            CV_SEQ_CONNECTED_COMP = (CV_SEQ_KIND_GENERIC  | CV_SEQ_ELTYPE_CONNECTED_COMP),


            CV_SEQ_INDEX          = (CV_SEQ_KIND_GENERIC  | CV_SEQ_ELTYPE_INDEX);

        public static boolean CV_IS_SEQ(CvSeq seq) {
            return seq != null && (seq.flags & CV_MAGIC_MASK) == CV_SEQ_MAGIC_VAL;
        }
        public static boolean CV_IS_SET(CvSeq set) {
            return set != null && (set.flags & CV_MAGIC_MASK) == CV_SET_MAGIC_VAL;
        }

        public static int CV_SEQ_ELTYPE(CvSeq seq) { return seq.flags & CV_SEQ_ELTYPE_MASK; }
        public static int CV_SEQ_KIND(CvSeq seq) { return seq.flags & CV_SEQ_KIND_MASK; }
        public static boolean CV_IS_SEQ_INDEX(CvSeq seq) {
            return (CV_SEQ_ELTYPE(seq) == CV_SEQ_ELTYPE_INDEX) &&
                   (CV_SEQ_KIND(seq) == CV_SEQ_KIND_GENERIC);
        }
        public static boolean CV_IS_SEQ_CURVE(CvSeq seq) { return CV_SEQ_KIND(seq) == CV_SEQ_KIND_CURVE; }
        public static boolean CV_IS_SEQ_CLOSED(CvSeq seq) { return (seq.flags & CV_SEQ_FLAG_CLOSED) != 0; }
        public static boolean CV_IS_SEQ_CONVEX(CvSeq seq) { return (seq.flags & CV_SEQ_FLAG_CONVEX) != 0; }
        public static boolean CV_IS_SEQ_HOLE(CvSeq seq) { return (seq.flags & CV_SEQ_FLAG_HOLE) != 0; }
        public static boolean CV_IS_SEQ_SIMPLE(CvSeq seq) {
            return ((seq.flags & CV_SEQ_FLAG_SIMPLE) != 0) || CV_IS_SEQ_CONVEX(seq);
        }

        public static boolean CV_IS_SEQ_POINT_SET(CvSeq seq) {
            return CV_SEQ_ELTYPE(seq) == CV_32SC2 || CV_SEQ_ELTYPE(seq) == CV_32FC2;
        }
        public static boolean CV_IS_SEQ_POINT_SUBSET(CvSeq seq) {
            return CV_IS_SEQ_INDEX(seq) || CV_SEQ_ELTYPE(seq) == CV_SEQ_ELTYPE_PPOINT;
        }
        public static boolean CV_IS_SEQ_POLYLINE(CvSeq seq) {
            return CV_SEQ_KIND(seq) == CV_SEQ_KIND_CURVE && CV_IS_SEQ_POINT_SET(seq);
        }
        public static boolean CV_IS_SEQ_POLYGON(CvSeq seq) {
            return CV_IS_SEQ_POLYLINE(seq) && CV_IS_SEQ_CLOSED(seq);
        }
        public static boolean CV_IS_SEQ_CHAIN(CvSeq seq) {
            return CV_SEQ_KIND(seq) == CV_SEQ_KIND_CURVE && seq.elem_size == 1;
        }
        public static boolean CV_IS_SEQ_CONTOUR(CvSeq seq) {
            return CV_IS_SEQ_CLOSED(seq) && (CV_IS_SEQ_POLYLINE(seq) || CV_IS_SEQ_CHAIN(seq));
        }
        public static boolean CV_IS_SEQ_CHAIN_CONTOUR(CvSeq seq) {
            return CV_IS_SEQ_CHAIN(seq) && CV_IS_SEQ_CLOSED(seq);
        }
        public static boolean CV_IS_SEQ_POLYGON_TREE(CvSeq seq) {
            return CV_SEQ_ELTYPE(seq) == CV_SEQ_ELTYPE_TRIAN_ATR &&
                   CV_SEQ_KIND(seq) == CV_SEQ_KIND_BIN_TREE;
        }
        public static boolean CV_IS_GRAPH(CvSeq seq) {
            return CV_IS_SET(seq) && CV_SEQ_KIND(seq) == CV_SEQ_KIND_GRAPH;
        }
        public static boolean CV_IS_GRAPH_ORIENTED(CvSeq seq) {
            return (seq.flags & CV_GRAPH_FLAG_ORIENTED) != 0;
        }
        public static boolean CV_IS_SUBDIV2D(CvSeq seq) {
            return CV_IS_SET(seq) && CV_SEQ_KIND(seq) == CV_SEQ_KIND_SUBDIV2D;
        }

    public static class CvSeq extends CvArr {
        public static boolean autoSynch = true;
        public CvSeq() { setAutoSynch(autoSynch); }
        public CvSeq(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvSeq.class) read(); }

        public static CvSeq create(int seq_flags, int header_size, int elem_size,
                CvMemStorage storage) {
            return cvCreateSeq(seq_flags, header_size, elem_size, storage);
        }


        private static final String[] fieldOrder = { "flags", "header_size",
            "h_prev", "h_next", "v_prev", "v_next", "total", "elem_size",
            "block_max", "ptr", "delta_elems", "storage", "free_blocks", "first" };
        { setFieldOrder(fieldOrder); }
        public int flags;
        public int header_size;
        public CvSeq.ByReference h_prev;
        public CvSeq.ByReference h_next;
        public CvSeq.ByReference v_prev;
        public CvSeq.ByReference v_next;
        public int total;
        public int elem_size;
        public Pointer block_max;
        public Pointer ptr;
        public int delta_elems;
        public CvMemStorage.ByReference storage;
        public CvSeqBlock.ByReference free_blocks;
        public CvSeqBlock.ByReference first;


        public static class ByValue extends CvSeq implements Structure.ByValue { }
        public static class ByReference extends CvSeq implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvSeq p) {
                setStructure(p);
            }
            public CvSeq getStructure() {
                return new CvSeq(getValue());
            }
            public void getStructure(CvSeq p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvSeq p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static class CvSeqBlock extends Structure {
        public static boolean autoSynch = true;
        public CvSeqBlock() { setAutoSynch(autoSynch); }
        public CvSeqBlock(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvSeqBlock.class) read(); }

        private static final String[] fieldOrder = {
            "prev", "next", "start_index", "count", "data" };
        { setFieldOrder(fieldOrder); }
        public CvSeqBlock.ByReference prev;
        public CvSeqBlock.ByReference next;
        public int     start_index;
        public int     count;
        public Pointer data;

        public static class ByReference extends CvSeqBlock implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvSeqBlock p) {
                setStructure(p);
            }
            public CvSeqBlock getStructure() {
                return new CvSeqBlock(getValue());
            }
            public void getStructure(CvSeqBlock p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvSeqBlock p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static class CvSlice extends Structure {
        public static boolean autoSynch = true;
        public CvSlice() { setAutoSynch(autoSynch); }
        public CvSlice(int start, int end) {
            setAutoSynch(autoSynch);
            this.start_index = start;
            this.end_index = end;
        }

        private static final String[] fieldOrder = { "start_index", "end_index" };
        { setFieldOrder(fieldOrder); }
        public int start_index;
        public int end_index;

        public static class ByValue extends CvSlice implements Structure.ByValue {
            public ByValue() { }
            public ByValue(int start, int end) {
                super(start, end);
            }
            public ByValue(CvSlice o) {
                this.start_index = o.start_index;
                this.end_index = o.end_index;
            }
        }
        public ByValue byValue() {
            return this instanceof ByValue ? (ByValue)this : new ByValue(this);
        }
    }
    public static CvSlice.ByValue cvSlice(int start, int end) {
        return new CvSlice.ByValue(start, end);
    }
    public static final int CV_WHOLE_SEQ_END_INDEX = 0x3fffffff;
    public static final CvSlice.ByValue CV_WHOLE_SEQ = new CvSlice.ByValue(0, CV_WHOLE_SEQ_END_INDEX);
    public static native int cvSliceLength(CvSlice.ByValue slice, CvSeq seq);

    public static native CvSeq cvCreateSeq(int seq_flags, int header_size, int elem_size, CvMemStorage storage);
    public static native void cvSetSeqBlockSize(CvSeq seq, int delta_elems);
    public static native Pointer cvSeqPush(CvSeq seq, Pointer element/*=null*/);
    public static native void cvSeqPop(CvSeq seq, Pointer element/*=null*/);
    public static native char cvSeqPushFront(CvSeq seq, Pointer element/*=null*/);
    public static native void cvSeqPopFront(CvSeq seq, Pointer element/*=null*/);
    public static final int
            CV_FRONT = 1,
            CV_BACK = 0;
    public static native void cvSeqPushMulti(CvSeq seq, Pointer elements, int count, int in_front/*=0*/);
    public static native void cvSeqPopMulti(CvSeq seq, Pointer elements, int count, int in_front/*=0*/);
    public static native Pointer cvSeqInsert(CvSeq seq, int before_index, Pointer element/*=null*/);
    public static native void cvSeqRemove(CvSeq seq, int index);
    public static native void cvClearSeq(CvSeq seq);
    public static native Pointer cvGetSeqElem(CvSeq seq, int index);
    public static native int cvSeqElemIdx(CvSeq seq, Pointer element, CvSeqBlock.PointerByReference block/*=null*/);
    public static native Pointer cvCvtSeqToArray(CvSeq seq, Pointer elements, CvSlice.ByValue slice/*=CV_WHOLE_SEQ*/);
    public static native CvSeq cvMakeSeqHeaderForArray(int seq_type, int header_size, int elem_size,
            Pointer elements, int total, CvSeq seq, CvSeqBlock block);
    public static native CvSeq cvSeqSlice(CvSeq seq, CvSlice.ByValue slice,
            CvMemStorage storage/*=null*/, int copy_data/*=0*/);
    public static CvSeq cvCloneSeq(CvSeq seq, CvMemStorage storage/*=null*/) {
        return cvSeqSlice(seq, CV_WHOLE_SEQ, storage, 1);
    }
    public static native void cvSeqRemoveSlice(CvSeq seq, CvSlice.ByValue slice);
    public static native void cvSeqInsertSlice(CvSeq seq, int before_index, CvArr from_arr);
    public static native void cvSeqInvert(CvSeq seq);
    public interface CvCmpFunc extends Callback {
        int callback(Pointer a, Pointer b, Pointer userdata);
    }
    public static native void cvSeqSort(CvSeq seq, CvCmpFunc func, Pointer userdata/*=null*/);
    public static native void cvSeqSort(CvSeq seq, Function func, Pointer userdata/*=null*/);
    public static native Pointer cvSeqSearch(CvSeq seq, Pointer elem, CvCmpFunc func,
            int is_sorted, IntByReference elem_idx, Pointer userdata/*=null*/);

    public static class CvSeqWriter extends Structure {
        public static boolean autoSynch = true;
        public CvSeqWriter() { setAutoSynch(autoSynch); }
        public CvSeqWriter(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvSeqWriter.class) read(); }

        private static final String[] fieldOrder = { "header_size", "seq",
            "block", "ptr", "block_min", "block_max" };
        { setFieldOrder(fieldOrder); }
        public int            header_size;
        public CvSeq.ByReference      seq;
        public CvSeqBlock.ByReference block;
        public Pointer        ptr;
        public Pointer        block_min;
        public Pointer        block_max;
    }
    public static native void cvStartAppendToSeq(CvSeq seq, CvSeqWriter writer);
    public static native void cvStartWriteSeq(int seq_flags, int header_size, int elem_size,
            CvMemStorage storage, CvSeqWriter writer);
    public static native CvSeq cvEndWriteSeq(CvSeqWriter writer);
    public static native void cvFlushSeqWriter(CvSeqWriter writer);

    public static class CvSeqReader extends Structure {
        public static boolean autoSynch = true;
        public CvSeqReader() { setAutoSynch(autoSynch); }
        public CvSeqReader(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvSeqReader.class) read(); }

        private static final String[] fieldOrder = { "header_size", "seq",
            "block", "ptr", "block_min", "block_max", "delta_index", "prev_elem" };
        { setFieldOrder(fieldOrder); }
        public int            header_size;
        public CvSeq.ByReference      seq;
        public CvSeqBlock.ByReference block;
        public Pointer        ptr;
        public Pointer        block_min;
        public Pointer        block_max;
        public int            delta_index;
        public Pointer        prev_elem;
    }
    public static native void cvStartReadSeq(CvSeq seq, CvSeqReader reader, int reverse/*=0*/);
    public static native int cvGetSeqReaderPos(CvSeqReader reader);
    public static native void cvSetSeqReaderPos(CvSeqReader reader, int index, int is_relative);

    public static native void cvChangeSeqBlock(CvSeqReader reader, int direction);
    public static native void cvCreateSeqBlock(CvSeqWriter writer);


    public static class CvSetElem extends Structure {
        public static boolean autoSynch = true;
        public CvSetElem() { setAutoSynch(autoSynch); }
        public CvSetElem(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvSetElem.class) read(); }

        private static final String[] fieldOrder = { "flags", "next_free" };
        { setFieldOrder(fieldOrder); }
        public int flags;
        public CvSetElem.ByReference next_free;


        public static class ByReference extends CvSetElem implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); if (getClass() == ByReference.class) read(); }
        }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvSetElem p) {
                setStructure(p);
            }
            public CvSetElem getStructure() {
                return new CvSetElem(getValue());
            }
            public void getStructure(CvSetElem p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvSetElem p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static class CvSet extends CvSeq {
        public static boolean autoSynch = true;
        public CvSet() { setAutoSynch(autoSynch); }
        public CvSet(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvSet.class) read(); }

        public static CvSet create(int set_flags, int header_size, int elem_size,
                CvMemStorage storage) {
            return cvCreateSet(set_flags, header_size, elem_size, storage);
        }

        private static final String[] fieldOrder = { "free_elems", "active_count" };
        { setFieldOrder(fieldOrder); }
        public CvSetElem.ByReference free_elems;
        public int active_count;


        public static class ByValue extends CvSet implements Structure.ByValue { }
        public static class ByReference extends CvSet implements Structure.ByReference { }
    }
    public static final int
            CV_SET_ELEM_IDX_MASK  = ((1 << 26) - 1),
            CV_SET_ELEM_FREE_FLAG = (1 << (32-1));
    public static boolean CV_IS_SET_ELEM(CvSetElem e) {
        return e.flags >= 0;
    }
    public static native CvSet cvCreateSet(int set_flags, int header_size, int elem_size, CvMemStorage storage);
    public static native int cvSetAdd(CvSet set_header, CvSetElem elem/*=null*/,
            CvSetElem.PointerByReference inserted_elem/*=null*/);
    public CvSetElem.ByReference cvSetNew(CvSet set_header) {
        CvSetElem.ByReference elem = set_header.free_elems;
        if (elem != null) {
            set_header.free_elems = elem.next_free;
            elem.flags = elem.flags & CV_SET_ELEM_IDX_MASK;
            set_header.active_count++;
        } else {
            cvSetAdd(set_header, null, elem.pointerByReference());
        }
        return elem;
    }
    public void cvSetRemoveByPtr(CvSet set_header, CvSetElem.ByReference elem) {
        assert (elem.flags >= 0 /*&& (elem.flags & CV_SET_ELEM_IDX_MASK) < total*/ );
        elem.next_free = set_header.free_elems;
        elem.flags = (elem.flags & CV_SET_ELEM_IDX_MASK) | CV_SET_ELEM_FREE_FLAG;
        set_header.free_elems = elem;
        set_header.active_count--;
    }
    public static native void cvSetRemove(CvSet set_header, int index);
    public static CvSetElem cvGetSetElem(CvSet set_header, int index) {
        CvSetElem elem = new CvSetElem(cvGetSeqElem(set_header, index));
        return elem != null && CV_IS_SET_ELEM(elem) ? elem : null;
    }
    public static native void cvClearSet(CvSet set_header);


    public static class CvGraphEdge extends Structure {
        public static boolean autoSynch = true;
        public CvGraphEdge() { setAutoSynch(autoSynch); }
        public CvGraphEdge(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvGraphEdge.class) read(); }

        private static final String[] fieldOrder = { "flags", "weight", "next", "vtx" };
        { setFieldOrder(fieldOrder); }
        public int   flags;
        public float weight;
        public CvGraphEdge.ByReference[] next = new CvGraphEdge.ByReference[2];
        public CvGraphVtx .ByReference[] vtx  = new CvGraphVtx .ByReference[2];


        public static class ByReference extends CvGraphEdge implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); if (getClass() == ByReference.class) read(); }
        }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvGraphEdge p) {
                setStructure(p);
            }
            public CvGraphEdge getStructure() {
                return new CvGraphEdge(getValue());
            }
            public void getStructure(CvGraphEdge p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvGraphEdge p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static class CvGraphVtx extends Structure {
        public static boolean autoSynch = true;
        public CvGraphVtx() { setAutoSynch(autoSynch); }
        public CvGraphVtx(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvGraphVtx.class) read(); }

        private static final String[] fieldOrder = { "flags", "first" };
        { setFieldOrder(fieldOrder); }
        public int flags;
        public CvGraphEdge.ByReference first;


        public static class ByReference extends CvGraphVtx implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); if (getClass() == ByReference.class) read(); }
        }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvGraphVtx p) {
                setStructure(p);
            }
            public CvGraphVtx getStructure() {
                return new CvGraphVtx(getValue());
            }
            public void getStructure(CvGraphVtx p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvGraphVtx p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static class CvGraph extends CvSet {
        public static boolean autoSynch = true;
        public CvGraph() { setAutoSynch(autoSynch); }
        public CvGraph(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvGraph.class) read(); }

        public static CvGraph create(int graph_flags, int header_size, int vtx_size,
                int edge_size, CvMemStorage storage) {
            return cvCreateGraph(graph_flags, header_size, vtx_size, edge_size, storage);
        }

        public CvSet.ByReference edges;

        public static class ByReference extends CvGraph implements Structure.ByReference { }
    }

    public static final String CV_TYPE_NAME_GRAPH = "opencv-graph";

    public static native CvGraph cvCreateGraph(int graph_flags, int header_size,
            int vtx_size, int edge_size, CvMemStorage storage);
    public static native int cvGraphAddVtx(CvGraph graph, CvGraphVtx vtx/*=null*/,
            CvGraphVtx.PointerByReference inserted_vtx/*=null*/);
    public static native int cvGraphRemoveVtx(CvGraph graph, int index);
    public static native int cvGraphRemoveVtxByPtr(CvGraph graph, CvGraphVtx vtx);
    public static native int cvGraphAddEdge(CvGraph graph, int start_idx, int end_idx,
            CvGraphEdge edge/*=null*/, CvGraphEdge.PointerByReference inserted_edge/*=null*/);
    public static native int cvGraphAddEdgeByPtr(CvGraph graph, CvGraphVtx start_vtx, CvGraphVtx end_vtx,
        CvGraphEdge edge/*=null*/, CvGraphEdge.PointerByReference inserted_edge/*=null*/);
    public static native void cvGraphRemoveEdge(CvGraph graph, int start_idx, int end_idx);
    public static native void cvGraphRemoveEdgeByPtr(CvGraph graph, CvGraphVtx start_vtx, CvGraphVtx end_vtx);
    public static native CvGraphEdge cvFindGraphEdge(CvGraph graph, int start_idx, int end_idx);
    public static CvGraphEdge cvGraphFindEdge(CvGraph graph, int start_idx, int end_idx) {
        return cvFindGraphEdge(graph, start_idx, end_idx);
    }
    public static native CvGraphEdge cvFindGraphEdgeByPtr(CvGraph graph, CvGraphVtx start_vtx, CvGraphVtx end_vtx);
    public static CvGraphEdge cvGraphFindEdgeByPtr(CvGraph graph, CvGraphVtx start_vtx, CvGraphVtx end_vtx) {
        return cvFindGraphEdgeByPtr(graph, start_vtx, end_vtx);
    }
    public static native int  cvGraphVtxDegree(CvGraph graph, int vtx_idx);
    public static native int  cvGraphVtxDegreeByPtr(CvGraph graph, CvGraphVtx vtx);
    public static native void cvClearGraph(CvGraph graph);
    public static native CvGraph cvCloneGraph(CvGraph graph, CvMemStorage storage);

    public static CvGraphVtx cvGetGraphVtx(CvGraph graph, int idx) { return new CvGraphVtx(cvGetSetElem(graph, idx).getPointer()); }
    public static int cvGraphVtxIdx(CvGraph graph, CvGraphVtx vtx) { return vtx.flags & CV_SET_ELEM_IDX_MASK; }
    public static int cvGraphEdgeIdx(CvGraph graph, CvGraphEdge edge) { return edge.flags & CV_SET_ELEM_IDX_MASK; }
    public static int cvGraphGetVtxCount(CvGraph graph) { return graph.active_count; }
    public static int cvGraphGetEdgeCount(CvGraph graph) { return graph.edges.active_count; }

    public static final int
            CV_GRAPH_VERTEX       = 1,
            CV_GRAPH_TREE_EDGE    = 2,
            CV_GRAPH_BACK_EDGE    = 4,
            CV_GRAPH_FORWARD_EDGE = 8,
            CV_GRAPH_CROSS_EDGE   = 16,
            CV_GRAPH_ANY_EDGE     = 30,
            CV_GRAPH_NEW_TREE     = 32,
            CV_GRAPH_BACKTRACKING = 64,
            CV_GRAPH_OVER         = -1,

            CV_GRAPH_ALL_ITEMS    = -1,

            CV_GRAPH_ITEM_VISITED_FLAG = (1 << 30),

            CV_GRAPH_SEARCH_TREE_NODE_FLAG  = (1 << 29),
            CV_GRAPH_FORWARD_EDGE_FLAG      = (1 << 28);
    public static boolean CV_IS_GRAPH_EDGE_VISITED(CvGraphVtx vtx) {
        return (vtx.flags & CV_GRAPH_ITEM_VISITED_FLAG) != 0;
    }
    public static boolean CV_IS_GRAPH_VERTEX_VISITED(CvGraphEdge edge) {
        return (edge.flags & CV_GRAPH_ITEM_VISITED_FLAG) != 0;
    }

    public static class CvGraphScanner extends Structure  {
        public static boolean autoSynch = true;
        public CvGraphScanner() { setAutoSynch(autoSynch); releasable = false; }
        public CvGraphScanner(Pointer m) { super(m); setAutoSynch(autoSynch); releasable = true;
                if (autoSynch && getClass() == CvGraphScanner.class) read(); }

        public static CvGraphScanner create(CvGraph graph,
                CvGraphVtx vtx/*=null*/, int mask/*=CV_GRAPH_ALL_ITEMS*/) {
            CvGraphScanner g = cvCreateGraphScanner(graph, vtx, mask);
            if (g != null) {
                g.releasable = true;
            }
            return g;
        }
        public void release() {
            releasable = false;
            cvReleaseGraphScanner(pointerByReference());
        }
        @Override protected void finalize() {
            if (releasable) {
                release();
            }
        }
        private boolean releasable = false;

        private static final String[] fieldOrder = { "vtx", "dst", "edge",
            "graph", "stack", "index", "mask" };
        { setFieldOrder(fieldOrder); }
        public CvGraphVtx .ByReference vtx;
        public CvGraphVtx .ByReference dst;
        public CvGraphEdge.ByReference edge;

        public CvGraph.ByReference graph;
        public CvSeq  .ByReference stack;
        public int                 index;
        public int                 mask;

        public static class ByReference extends CvGraphScanner implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvGraphScanner p) {
                setStructure(p);
            }
            public CvGraphScanner getStructure() {
                return new CvGraphScanner(getValue());
            }
            public void getStructure(CvGraphScanner p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvGraphScanner p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static native CvGraphScanner cvCreateGraphScanner(CvGraph graph, CvGraphVtx vtx/*=null*/,
            int mask/*=CV_GRAPH_ALL_ITEMS*/);
    public static native int cvNextGraphItem(CvGraphScanner scanner);
    public static native void cvReleaseGraphScanner(CvGraphScanner.PointerByReference scanner);


    public static class CvTreeNodeIterator extends Structure  {
        public static boolean autoSynch = true;
        public CvTreeNodeIterator() { setAutoSynch(autoSynch); }
        public CvTreeNodeIterator(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvTreeNodeIterator.class) read(); }

        private static final String[] fieldOrder = { "node", "level", "max_level" };
        { setFieldOrder(fieldOrder); }
        public Pointer node;
        public int level;
        public int max_level;
    }
    public static native void cvInitTreeNodeIterator(CvTreeNodeIterator tree_iterator, Pointer first, int max_level);
    public static native Pointer cvNextTreeNode(CvTreeNodeIterator tree_iterator);
    public static native Pointer cvPrevTreeNode(CvTreeNodeIterator tree_iterator);
    public static native CvSeq cvTreeToNodeSeq(Pointer first, int header_size, CvMemStorage storage);
    public static native void cvInsertNodeIntoTree(Pointer node, Pointer parent, Pointer frame);
    public static native void cvRemoveNodeFromTree(Pointer node, Pointer frame);


    public static CvScalar.ByValue CV_RGB(double r, double g, double b) {
        return new CvScalar.ByValue(b, g, r, 0);
    }
    public static final int
            CV_FILLED = -1,
            CV_AA     = 16;
    public static native void cvLine(CvArr img, CvPoint.ByValue pt1, CvPoint.ByValue pt2,
            CvScalar.ByValue color, int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/);
    public static void cvDrawLine(CvArr img, CvPoint.ByValue pt1, CvPoint.ByValue pt2,
            CvScalar.ByValue color, int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/) {
        cvLine(img, pt1, pt2, color, thickness, line_type, shift);
    }
    public static native void cvRectangle(CvArr img, CvPoint.ByValue pt1, CvPoint.ByValue pt2,
            CvScalar.ByValue color, int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/);
    public static void cvDrawRect(CvArr img, CvPoint.ByValue pt1, CvPoint.ByValue pt2,
            CvScalar.ByValue color, int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/) {
        cvRectangle(img, pt1, pt2, color, thickness, line_type, shift);
    }
    public static native void cvCircle(CvArr img, CvPoint.ByValue center, int radius,
            CvScalar.ByValue color, int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/);
    public static void cvDrawCircle(CvArr img, CvPoint.ByValue center, int radius,
            CvScalar.ByValue color, int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/) {
        cvCircle(img, center, radius, color, thickness, line_type, shift);
    }
    public static native void cvEllipse(CvArr img, CvPoint.ByValue center, CvSize.ByValue axes, double angle,
            double start_angle, double end_angle, CvScalar.ByValue color,
            int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/);
    public static void cvDrawEllipse(CvArr img, CvPoint.ByValue center, CvSize.ByValue axes, double angle,
            double start_angle, double end_angle, CvScalar.ByValue color,
            int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/) {
        cvEllipse(img, center, axes, angle, start_angle, end_angle, color,
                thickness, line_type, shift);
    }
    public static void cvEllipseBox(CvArr img, CvBox2D.ByValue box, CvScalar.ByValue color,
            int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/) {
        CvSize.ByValue axes = new CvSize.ByValue((int)Math.round(box.size.width*0.5), (int)Math.round(box.size.height*0.5));
        cvEllipse(img, new CvPoint(box.center).byValue(), axes, box.angle,
                   0, 360, color, thickness, line_type, shift);
    }
    public static native void cvFillPoly(CvArr img, CvPoint.PointerByReference pts, int[] npts, int contours,
            CvScalar.ByValue color, int line_type/*=8*/, int shift/*=0*/);
    public static void cvFillPoly(CvArr img, CvPoint.PointerByReference[] pts, int[] npts, int contours,
            CvScalar.ByValue color, int line_type/*=8*/, int shift/*=0*/) {
        cvFillPoly(img, pts[0], npts, contours, color, line_type, shift);
    }
    public static native void cvFillConvexPoly(CvArr img, CvPoint pts, int npts,
            CvScalar.ByValue color, int line_type/*=8*/, int shift/*=0*/);
    public static void cvFillConvexPoly(CvArr img, CvPoint[] pts, int npts,
            CvScalar.ByValue color, int line_type/*=8*/, int shift/*=0*/) {
        for (Structure s : pts) { s.write(); }
        cvFillConvexPoly(img, pts[0], npts, color, line_type, shift);
    }
    public static native void cvPolyLine(CvArr img, CvPoint.PointerByReference pts, int[] npts,
            int contours, int is_closed, CvScalar.ByValue color,
            int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/);
    public static void cvPolyLine(CvArr img, CvPoint.PointerByReference[] pts, int[] npts,
            int contours, int is_closed, CvScalar.ByValue color,
            int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/) {
        cvPolyLine(img, pts[0], npts, contours, is_closed, color, thickness, line_type, shift);
    }
    public static void cvDrawPolyLine(CvArr img, CvPoint.PointerByReference pts, int[] npts,
            int contours, int is_closed, CvScalar.ByValue color,
            int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/) {
        cvPolyLine(img, pts, npts, contours, is_closed, color, thickness, line_type, shift);
    }
    public static void cvDrawPolyLine(CvArr img, CvPoint.PointerByReference[] pts, int[] npts,
            int contours, int is_closed, CvScalar.ByValue color,
            int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/) {
        cvPolyLine(img, pts[0], npts, contours, is_closed, color, thickness, line_type, shift);
    }

    public static native CvScalar.ByValue cvColorToScalar(double packed_color, int arrtype);

    public static final int
            CV_FONT_HERSHEY_SIMPLEX        = 0,
            CV_FONT_HERSHEY_PLAIN          = 1,
            CV_FONT_HERSHEY_DUPLEX         = 2,
            CV_FONT_HERSHEY_COMPLEX        = 3 ,
            CV_FONT_HERSHEY_TRIPLEX        = 4,
            CV_FONT_HERSHEY_COMPLEX_SMALL  = 5,
            CV_FONT_HERSHEY_SCRIPT_SIMPLEX = 6,
            CV_FONT_HERSHEY_SCRIPT_COMPLEX = 7,

            CV_FONT_ITALIC                 = 16,

            CV_FONT_VECTOR0   = CV_FONT_HERSHEY_SIMPLEX;
    public static class CvFont extends Structure {
        public static boolean autoSynch = true;
        public CvFont() { setAutoSynch(autoSynch); }
        public CvFont(int font_face, double hscale, double vscale,
            double shear, int thickness, int line_type) {
            setAutoSynch(autoSynch);
            cvInitFont(this, font_face, hscale, vscale, shear, thickness, line_type);
        }
        public CvFont(int font_face, double scale, int thickness) {
            setAutoSynch(autoSynch);
            cvInitFont(this, font_face, scale, scale, 0, thickness, CV_AA);
        }

        private static final String[] fieldOrder = { "font_face", "ascii", "greek",
            "cyrillic", "hscale", "vscale", "shear", "thickness", "dx", "line_type" };
        { setFieldOrder(fieldOrder); }
        public int             font_face;
        public IntByReference  ascii;
        public IntByReference  greek;
        public IntByReference  cyrillic;
        public float           hscale, vscale;
        public float           shear;
        public int             thickness;
        public float           dx;
        public int             line_type;
    }
    public static native void cvInitFont(CvFont font, int font_face, double hscale, double vscale,
            double shear/*=0*/, int thickness/*=1*/, int line_type/*=8*/);
    public static native void cvPutText(CvArr img, String text, CvPoint.ByValue org,
            CvFont font, CvScalar.ByValue color);
    public static native void cvGetTextSize(String text_string, CvFont font,
            CvSize text_size, IntByReference baseline);


    public static native void cvDrawContours(CvArr img, CvSeq contour, CvScalar.ByValue external_color,
            CvScalar.ByValue hole_color, int max_level, int thickness/*=1*/,
            int line_type/*=8*/, CvPoint.ByValue offset/*=cvPoint(0,0)*/);
    public static void cvDrawContours(CvArr img, CvSeq contour, CvScalar.ByValue external_color,
            CvScalar.ByValue hole_color, int max_level, int thickness/*=1*/, int line_type/*=8*/) {
        cvDrawContours(img, contour, external_color, hole_color, max_level, thickness, line_type, CvPoint.ZERO);
    }

    public static class CvLineIterator extends Structure {
        public static boolean autoSynch = true;
        public CvLineIterator() { setAutoSynch(autoSynch); }
        public CvLineIterator(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvLineIterator.class) read(); }

        private static final String[] fieldOrder = { "ptr", "err",
            "plus_delta", "minus_delta", "plus_step", "minus_step"};
        { setFieldOrder(fieldOrder); }
        public Pointer ptr;

        public int err;
        public int plus_delta;
        public int minus_delta;
        public int plus_step;
        public int minus_step;
    }
    public static native int cvInitLineIterator(CvArr image, CvPoint.ByValue pt1, CvPoint.ByValue pt2,
            CvLineIterator line_iterator, int connectivity/*=8*/, int left_to_right/*=0*/);
    public static void CV_NEXT_LINE_POINT(CvLineIterator line_iterator) {
        int _line_iterator_mask = line_iterator.err < 0 ? -1 : 0;
        line_iterator.err += line_iterator.minus_delta + (line_iterator.plus_delta & _line_iterator_mask);
        Pointer p = line_iterator.getPointer();
        long n = 0;
        switch (Pointer.SIZE) {
            case 1: n = p.getByte (0); break;
            case 2: n = p.getShort(0); break;
            case 4: n = p.getInt  (0); break;
            case 8: n = p.getLong (0); break;
            default: assert(false);
        }
        //line_iterator.ptr += line_iterator.minus_step + (line_iterator.plus_step & _line_iterator_mask);
                          n += line_iterator.minus_step + (line_iterator.plus_step & _line_iterator_mask);
        switch (Pointer.SIZE) {
            case 1: p.setByte (0, (byte) n); break;
            case 2: p.setShort(0, (short)n); break;
            case 4: p.setInt  (0, (int)  n); break;
            case 8: p.setLong (0, (long) n); break;
            default: assert(false);
        }
    }
    public static native int cvClipLine(CvSize.ByValue img_size, CvPoint pt1, CvPoint pt2);
    public static native int cvEllipse2Poly(CvPoint.ByValue center, CvSize.ByValue axes,
            int angle, int arc_start, int arc_end, CvPoint pts, int delta);
    public static int cvEllipse2Poly(CvPoint.ByValue center, CvSize.ByValue axes,
            int angle, int arc_start, int arc_end, CvPoint[] pts, int delta) {
        for (Structure s : pts) { s.write(); }
        return cvEllipse2Poly(center, axes, angle, arc_start, arc_end, pts[0], delta);
    }


    public static class CvFileStorage extends PointerType {
        public CvFileStorage() { releasable = false; }
        public CvFileStorage(Pointer p) { super(p); releasable = true; }

        public static CvFileStorage open(String filename, CvMemStorage memstorage, int flags) {
            CvFileStorage f = cvOpenFileStorage(filename, memstorage, flags);
            if (f != null) {
                f.releasable = true;
            }
            return f;
        }

        public void release() {
            releasable = false;
            cvReleaseFileStorage(pointerByReference());
        }
        @Override protected void finalize() {
            if (releasable) {
                release();
            }
        }
        private boolean releasable = false;

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvFileStorage p) {
                setStructure(p);
            }
            public CvFileStorage getStructure() {
                return new CvFileStorage(getValue());
            }
            public void getStructure(CvFileStorage p) {
                p.setPointer(getValue());
            }
            public void setStructure(CvFileStorage p) {
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static final int
            CV_STORAGE_READ         = 0,
            CV_STORAGE_WRITE        = 1,
            CV_STORAGE_WRITE_TEXT   = CV_STORAGE_WRITE,
            CV_STORAGE_WRITE_BINARY = CV_STORAGE_WRITE,
            CV_STORAGE_APPEND       = 2;

    public static class CvAttrList extends Structure {
        public static boolean autoSynch = true;
        public CvAttrList() { setAutoSynch(autoSynch); }
        public CvAttrList(String[] attr, CvAttrList next) {
            setAutoSynch(autoSynch);
            this.attr = new StringArray(attr);
            this.next = new ByReference(next);
        }

        private static final String[] fieldOrder = { "attr", "next" };
        { setFieldOrder(fieldOrder); }
        public Pointer /* StringArray */ attr = null;
        public CvAttrList.ByReference next = null;

        public static class ByValue extends CvAttrList implements Structure.ByValue {
            public ByValue() { }
            public ByValue(String[] attr, CvAttrList next) {
                super(attr, next);
            }
            public ByValue(CvAttrList o) {
                this.attr = o.attr;
                this.next = o.next;
            }
        }
        public ByValue byValue() {
            return this instanceof ByValue ? (ByValue)this : new ByValue(this);
        }

        public static class ByReference extends CvAttrList implements Structure.ByReference {
            public ByReference() { }
            public ByReference(CvAttrList o) {
                this.attr = o.attr;
                this.next = o.next;
            }
        }
    }

    public static CvAttrList.ByValue cvAttrList(String[] attr, CvAttrList next) {
        return new CvAttrList.ByValue(attr, next);
    }
    public static CvAttrList.ByValue cvAttrList() {
        return new CvAttrList.ByValue();
    }

    public static final int
            CV_NODE_NONE       = 0,
            CV_NODE_INT        = 1,
            CV_NODE_INTEGER    = CV_NODE_INT,
            CV_NODE_REAL       = 2,
            CV_NODE_FLOAT      = CV_NODE_REAL,
            CV_NODE_STR        = 3,
            CV_NODE_STRING     = CV_NODE_STR,
            CV_NODE_REF        = 4,
            CV_NODE_SEQ        = 5,
            CV_NODE_MAP        = 6,
            CV_NODE_TYPE_MASK  = 7,

            CV_NODE_FLOW       = 8,
            CV_NODE_USER       = 16,
            CV_NODE_EMPTY      = 32,
            CV_NODE_NAMED      = 64,

            CV_NODE_SEQ_SIMPLE = 256;
    public static int     CV_NODE_TYPE(int flags)          { return flags & CV_NODE_TYPE_MASK; }
    public static boolean CV_NODE_IS_INT(int flags)        { return CV_NODE_TYPE(flags) == CV_NODE_INT; }
    public static boolean CV_NODE_IS_REAL(int flags)       { return CV_NODE_TYPE(flags) == CV_NODE_REAL; }
    public static boolean CV_NODE_IS_STRING(int flags)     { return CV_NODE_TYPE(flags) == CV_NODE_STRING; }
    public static boolean CV_NODE_IS_SEQ(int flags)        { return CV_NODE_TYPE(flags) == CV_NODE_SEQ; }
    public static boolean CV_NODE_IS_MAP(int flags)        { return CV_NODE_TYPE(flags) == CV_NODE_MAP; }
    public static boolean CV_NODE_IS_COLLECTION(int flags) { return CV_NODE_TYPE(flags) >= CV_NODE_SEQ; }
    public static boolean CV_NODE_IS_FLOW(int flags)       { return (flags & CV_NODE_FLOW) != 0; }
    public static boolean CV_NODE_IS_EMPTY(int flags)      { return (flags & CV_NODE_EMPTY) != 0; }
    public static boolean CV_NODE_IS_USER(int flags)       { return (flags & CV_NODE_USER) != 0; }
    public static boolean CV_NODE_HAS_NAME(int flags)      { return (flags & CV_NODE_NAMED) != 0; }
    public static boolean CV_NODE_SEQ_IS_SIMPLE(CvSeq seq) { return (seq.flags & CV_NODE_SEQ_SIMPLE) != 0; }

    public static class CvStringHashNode extends Structure {
        public static boolean autoSynch = true;
        public CvStringHashNode() { setAutoSynch(autoSynch); }
        public CvStringHashNode(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvStringHashNode.class) read(); }

        private static final String[] fieldOrder = { "hashval", "str", "next" };
        { setFieldOrder(fieldOrder); }
        public int hashval;
        public CvString str;
        public CvStringHashNode.ByReference next;

        public static class ByReference extends CvStringHashNode implements Structure.ByReference { }
    }

    public static class CvFileNodeHash extends PointerType { }

    public static class CvFileNode extends Structure {
        public static boolean autoSynch = true;
        public CvFileNode() { setAutoSynch(autoSynch); }
        public CvFileNode(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvFileNode.class) read(); }

        private static final String[] fieldOrder = { "tag", "info", "data" };
        { setFieldOrder(fieldOrder); }
        public int tag;
        public CvTypeInfo.ByReference info;

        public static class Data extends Union {
            public double f;
            public int i;
            public CvString str;
            public CvSeq.ByReference seq;
            public CvFileNodeHash map;
        }
        public Data data = new Data();
    }

    public static native String cvAttrValue(CvAttrList attr, String attr_name);
    public static native CvFileStorage cvOpenFileStorage(String filename, CvMemStorage memstorage, int flags);
    public static native void cvReleaseFileStorage(CvFileStorage.PointerByReference fs);

    public static native void cvStartWriteStruct(CvFileStorage fs, String name, int struct_flags,
            String type_name/*=null*/, CvAttrList.ByValue attributes/*=cvArrtList()*/);
    public static native void cvEndWriteStruct(CvFileStorage fs);
    public static native void cvWriteInt(CvFileStorage fs, String name, int value);
    public static native void cvWriteReal(CvFileStorage fs, String name, double value);
    public static native void cvWriteString(CvFileStorage fs, String name, String str, int quote/*=0*/);
    public static native void cvWriteComment(CvFileStorage fs, String comment, int eol_comment);
    public static native void cvStartNextStream(CvFileStorage fs);
    public static native void cvWrite(CvFileStorage fs, String name, Structure ptr, CvAttrList.ByValue attributes/*=cvArrtList()*/);
    public static native void cvWriteRawData(CvFileStorage fs, Pointer src, int len, String dt);
    public static native void cvWriteFileNode(CvFileStorage fs, String new_node_name, CvFileNode node, int embed);

    public static native CvFileNode cvGetRootFileNode(CvFileStorage fs, int stream_index/*=0*/);
    public static native CvFileNode cvGetFileNodeByName(CvFileStorage fs, CvFileNode map, String name);
    public static native CvStringHashNode cvGetHashedKey(CvFileStorage fs, String name,
            int len/*=-1*/, int create_missing/*=0*/);
    public static native CvFileNode cvGetFileNode(CvFileStorage fs, CvFileNode map,
            CvStringHashNode key, int create_missing/*=0*/);
    public static native String cvGetFileNodeName(CvFileNode node);
    public static int cvReadInt(CvFileNode node, int default_value) {
        return node == null ? default_value :
            CV_NODE_IS_INT(node.tag) ? node.data.i :
            CV_NODE_IS_REAL(node.tag) ? (int)Math.round(node.data.f) : 0x7fffffff;
    }
    public static int cvReadInt(CvFileNode node) {
        return cvReadInt(node, 0);
    }
    public static int cvReadIntByName(CvFileStorage fs, CvFileNode map, String name, int default_value) {
        return cvReadInt(cvGetFileNodeByName(fs, map, name), default_value);
    }
    public static int cvReadIntByName(CvFileStorage fs, CvFileNode map, String name) {
        return cvReadIntByName(fs, map, name, 0);
    }
    public static double cvReadReal(CvFileNode node, double default_value) {
        return node == null ? default_value :
            CV_NODE_IS_INT(node.tag) ? (double)node.data.i :
            CV_NODE_IS_REAL(node.tag) ? node.data.f : 1e300;
    }
    public static double cvReadReal(CvFileNode node) {
        return cvReadReal(node, 0);
    }
    public static double cvReadRealByName(CvFileStorage fs, CvFileNode map, String name, double default_value) {
        return cvReadReal(cvGetFileNodeByName(fs, map, name), default_value);
    }
    public static double cvReadRealByName(CvFileStorage fs, CvFileNode map, String name) {
        return cvReadRealByName(fs, map, name, 0);
    }
    public static String cvReadString(CvFileNode node, String default_value) {
        if (node == null) {
            return default_value;
        } else if (CV_NODE_IS_STRING(node.tag)) {
            node.data.setType(CvString.class);
            node.data.read();
            return node.data.str.ptr;
        } else {
            return null;
        }
    }
    public static String cvReadString(CvFileNode node) {
        return cvReadString(node, null);
    }
    public static String cvReadStringByName(CvFileStorage fs, CvFileNode map, String name, String default_value) {
        return cvReadString(cvGetFileNodeByName(fs, map, name), default_value);
    }
    public static String cvReadStringByName(CvFileStorage fs, CvFileNode map, String name) {
        return cvReadStringByName(fs, map, name, null);
    }
    public static native Pointer cvRead(CvFileStorage fs, CvFileNode node, CvAttrList attributes/*=null*/);
    public static Pointer cvReadByName(CvFileStorage fs, CvFileNode map, String name, CvAttrList attributes) {
        CvFileNode n = cvGetFileNodeByName(fs, map, name);
        n.setAutoWrite(false); // without this, it fails ... why exactly??
        return cvRead(fs, n, attributes);
    }
    public static native void cvReadRawData(CvFileStorage fs, CvFileNode src, Pointer dst, String dt);
    public static native void cvStartReadRawData(CvFileStorage fs, CvFileNode src, CvSeqReader reader);
    public static native void cvReadRawDataSlice(CvFileStorage fs, CvSeqReader reader,
            int count, Pointer dst, String dt);


    public interface CvIsInstanceFunc extends Callback {
        int callback(Pointer struct_ptr);
    }
    public interface CvReleaseFunc extends Callback {
        int callback(PointerByReference struct_dblptr);
    }
    public interface CvReadFunc extends Callback {
        int callback(CvFileStorage storage, CvFileNode node);
    }
    public interface CvWriteFunc extends Callback {
        int callback(CvFileStorage storage, String name,
                Pointer struct_ptr, CvAttrList.ByValue attributes);
    }
    public interface CvCloneFunc extends Callback {
        int callback(Pointer struct_ptr);
    }
    public static class CvTypeInfo extends Structure {
        public static boolean autoSynch = true;
        public CvTypeInfo() { setAutoSynch(autoSynch); }
        public CvTypeInfo(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvTypeInfo.class) read(); }

        private static final String[] fieldOrder = { "flags", "header_size", "prev",
            "next", "type_name", "is_instance", "release", "read", "write", "close"};
        { setFieldOrder(fieldOrder); }
        public int flags;
        public int header_size = size();
        public CvTypeInfo.ByReference prev;
        public CvTypeInfo.ByReference next;
        public String type_name;

        public CvIsInstanceFunc is_instance;
        public CvReleaseFunc release;
        public CvReadFunc read;
        public CvWriteFunc write;
        public CvCloneFunc clone;

        public static class ByReference extends CvTypeInfo implements Structure.ByReference { }
    }
    public static native void cvRegisterType(CvTypeInfo info);
    public static native void cvUnregisterType(String type_name);
    public static native CvTypeInfo cvFirstType();
    public static native CvTypeInfo cvFindType(String type_name);
    public static native CvTypeInfo cvTypeOf(Pointer struct_ptr);

    public static native void cvRelease(PointerByReference struct_ptr);
    public static native Pointer cvClone(Pointer struct_ptr);

    public static native void cvSave(String filename, Pointer struct_ptr,
            String name/*=null*/, String comment/*=null*/,
            CvAttrList.ByValue attributes/*=cvAttrList()*/);
    public static void cvSave(String filename, Pointer struct_ptr) {
        cvSave(filename, struct_ptr, null, null, null);
    }
    public static native Pointer cvLoad(String filename, CvMemStorage memstorage/*=null*/,
            String name/*=null*/, StringByReference real_name/*=null*/);
    public static Pointer cvLoad(String filename) {
        return cvLoad(filename, null, null, null);
    }


    public static final int
            CV_CHECK_RANGE  = 1,
            CV_CHECK_QUIET  = 2;
    public static native int cvCheckArr(CvArr arr, int flags/*=0*/, double min_val/*=0*/, double max_val/*=0*/);
    public static int cvCheckArray(CvArr arr, int flags/*=0*/, double min_val/*=0*/, double max_val/*=0*/) {
        return cvCheckArr(arr, flags, min_val, max_val);
    }
    public static native int cvKMeans2(CvArr samples, int cluster_count, CvArr labels,
            CvTermCriteria.ByValue termcrit, int attempts/*=1*/, LongByReference /* CvRNG* */ rng/*=null*/,
            int flags/*=0*/, CvArr _centers/*=0*/, DoubleByReference compactness/*=null*/);
    public static native int cvSeqPartition(CvSeq seq, CvMemStorage storage, CvSeq.PointerByReference labels,
            CvCmpFunc is_equal, Pointer userdata);
    public static native int cvSeqPartition(CvSeq seq, CvMemStorage storage, CvSeq.PointerByReference labels,
            Function is_equal, Pointer userdata);


    public static final int
            CV_StsOk                  =  0,
            CV_StsBackTrace           = -1,
            CV_StsError               = -2,
            CV_StsInternal            = -3,
            CV_StsNoMem               = -4,
            CV_StsBadArg              = -5,
            CV_StsBadFunc             = -6,
            CV_StsNoConv              = -7,
            CV_StsAutoTrace           = -8,

            CV_HeaderIsNull           = -9,
            CV_BadImageSize           = -10,
            CV_BadOffset              = -11,
            CV_BadDataPtr             = -12,
            CV_BadStep                = -13,
            CV_BadModelOrChSeq        = -14,
            CV_BadNumChannels         = -15,
            CV_BadNumChannel1U        = -16,
            CV_BadDepth               = -17,
            CV_BadAlphaChannel        = -18,
            CV_BadOrder               = -19,
            CV_BadOrigin              = -20,
            CV_BadAlign               = -21,
            CV_BadCallBack            = -22,
            CV_BadTileSize            = -23,
            CV_BadCOI                 = -24,
            CV_BadROISize             = -25,

            CV_MaskIsTiled            = -26,

            CV_StsNullPtr               = -27,
            CV_StsVecLengthErr          = -28,
            CV_StsFilterStructContentErr= -29,
            CV_StsKernelStructContentErr= -30,
            CV_StsFilterOffsetErr       = -31,

            CV_StsBadSize               = -201,
            CV_StsDivByZero             = -202,
            CV_StsInplaceNotSupported   = -203,
            CV_StsObjectNotFound        = -204,
            CV_StsUnmatchedFormats      = -205,
            CV_StsBadFlag               = -206,
            CV_StsBadPoint              = -207,
            CV_StsBadMask               = -208,
            CV_StsUnmatchedSizes        = -209,
            CV_StsUnsupportedFormat     = -210,
            CV_StsOutOfRange            = -211,
            CV_StsParseError            = -212,
            CV_StsNotImplemented        = -213,
            CV_StsBadMemBlock           = -214,
            CV_StsAssert                = -215;
    public static native int cvGetErrStatus();
    public static native void cvSetErrStatus(int status);
    public static final int
            CV_ErrModeLeaf    = 0,
            CV_ErrModeParent  = 1,
            CV_ErrModeSilent  = 2;
    public static native int cvGetErrMode();
    public static native int cvSetErrMode(int mode);
    public static native void cvError(int status, String func_name, String err_msg, String file_name, int line);
    public static native String cvErrorStr(int status);
    public static native int cvGetErrInfo(StringByReference errcode_desc,
            StringByReference description, StringByReference filename, IntByReference line);
    public static native int cvErrorFromIppStatus(int ipp_status);

    public interface CvErrorCallback extends Callback {
        int callback(int status, String func_name,
                String err_msg, String file_name, int line, Pointer userdata);
    }
    public static native CvErrorCallback cvRedirectError(CvErrorCallback error_handler,
            Pointer userdata, PointerByReference prev_userdata);
    public static native CvErrorCallback cvRedirectError(Function error_handler,
            Pointer userdata, PointerByReference prev_userdata);

    public static native int cvNulDevReport(int status, String func_name, String err_msg,
            String file_name, int line, Pointer userdata);
    public static native int cvStdErrReport(int status, String func_name, String err_msg,
            String file_name, int line, Pointer userdata);
    public static native int cvGuiBoxReport(int status, String func_name, String err_msg,
            String file_name, int line, Pointer userdata);


    public static native Pointer cvAlloc(size_t size);
    public static native void cvFree_(Pointer ptr);
    public static native long cvGetTickCount();
    public static native double cvGetTickFrequency();


    public static class CvPluginFuncInfo extends Structure {
        public static boolean autoSynch = true;
        public CvPluginFuncInfo() { setAutoSynch(autoSynch); }
        public CvPluginFuncInfo(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvPluginFuncInfo.class) read(); }

        private static final String[] fieldOrder = { "func_addr",
            "default_func_addr", "func_names", "search_modules", "loaded_from" };
        { setFieldOrder(fieldOrder); }
        PointerByReference func_addr;
        Pointer default_func_addr;
        String func_names;
        int search_modules;
        int loaded_from;

        public static class ByReference extends CvPluginFuncInfo implements Structure.ByReference { }
    }

    public static class CvModuleInfo extends Structure {
        public static boolean autoSynch = true;
        public CvModuleInfo() { setAutoSynch(autoSynch); }
        public CvModuleInfo(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvModuleInfo.class) read(); }

        private static final String[] fieldOrder = { "next", "name", "version", "func_tab" };
        { setFieldOrder(fieldOrder); }
        CvModuleInfo.ByReference next;
        String name;
        String version;
        CvPluginFuncInfo.ByReference func_tab;

        public static class ByReference extends CvModuleInfo implements Structure.ByReference { }
    }

    public static native int cvRegisterModule(CvModuleInfo module_info);
    public static native void cvGetModuleInfo(String module_name,
            StringByReference version, StringArray loaded_addon_plugins);
    public static native int cvUseOptimized(int on_off);

    public interface CvAllocFunc extends Callback {
        Pointer callback(size_t size, Pointer userdata);
    }
    public interface CvFreeFunc extends Callback {
        int callback(Pointer pptr, Pointer userdata);
    }
    public static native void cvSetMemoryManager(CvAllocFunc alloc_func/*=null*/,
            CvFreeFunc free_func/*=null*/, Pointer userdata/*=null*/);
    public static native void cvSetMemoryManager(Function alloc_func/*=null*/,
            Function free_func/*=null*/, Pointer userdata/*=null*/);


//    typedef IplImage* (CV_STDCALL* Cv_iplCreateImageHeader)
//                                (int,int,int,char*,char*,int,int,int,int,int,
//                                IplROI*,IplImage*,void*,IplTileInfo*);
//    typedef void (CV_STDCALL* Cv_iplAllocateImageData)(IplImage*,int,int);
//    typedef void (CV_STDCALL* Cv_iplDeallocate)(IplImage*,int);
//    typedef IplROI* (CV_STDCALL* Cv_iplCreateROI)(int,int,int,int,int);
//    typedef IplImage* (CV_STDCALL* Cv_iplCloneImage)(const IplImage*);
    public static native void cvSetIPLAllocators(
            Function /*Cv_iplCreateImageHeader*/ create_header,
            Function /*Cv_iplAllocateImageData*/ allocate_data,
            Function /*Cv_iplDeallocate*/ deallocate,
            Function /*Cv_iplCreateROI*/ create_roi,
            Function /*Cv_iplCloneImage*/ clone_image);

    public static native int cvGetNumThreads();
    public static native void cvSetNumThreads(int threads/*=0*/);
    public static native int cvGetThreadNum();
}
