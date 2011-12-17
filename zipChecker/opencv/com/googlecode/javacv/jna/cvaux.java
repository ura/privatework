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
 * This file is based on information found in cvaux.h of
 * OpenCV 2.0, which is covered by the following copyright notice:
 *
 *                        Intel License Agreement
 *                For Open Source Computer Vision Library
 *
 * Copyright (C) 2000, Intel Corporation, all rights reserved.
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
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import static com.googlecode.javacv.jna.cxcore.*;
import static com.googlecode.javacv.jna.cv.*;

/**
 *
 * @author Samuel Audet
 */
public class cvaux {
    // OpenCV does not always install itself in the PATH :(
    public static final String[] paths = cxcore.paths;
    public static final String[] libnames = { "cvaux", "cvaux_64", "cvaux210", "cvaux210_64",
            "cvaux200", "cvaux200_64", "cvaux110", "cvaux110_64", "cvaux100", "cvaux100_64" };
    public static final String libname = Loader.load(paths, libnames);


    public static class v10 extends cvaux {
        public interface CvUpdateBGStatModel extends Callback {
            int callback(IplImage curr_frame, CvBGStatModel bg_model);
        }
        public static int cvUpdateBGStatModel(IplImage current_frame, CvBGStatModel bg_model) {
            if (bg_model != null && bg_model.update != null) {
                return bg_model.update.v10.callback(current_frame, bg_model);
            } else {
                return 0;
            }
        }
    }
    public static class v11 extends v10 { }
    public static class v20 extends v10 { }
    public static class v21 extends cvaux {
        public static final String libname = Loader.load(paths, libnames);

        public static native void cvCalcImageHomography(float[] line,
                CvPoint3D32f center, float[] intrinsic, float[] homography);
        public static native void cvCalcImageHomography(FloatBuffer line,
                CvPoint3D32f center, FloatBuffer intrinsic, FloatBuffer homography);

        public static native void cvCalcPGH(CvSeq contour, CvHistogram hist);
        public static final int CV_DOMINANT_IPAN = 1;
        public static native CvSeq cvFindDominantPoints(CvSeq contour,
                CvMemStorage storage, int method/*=CV_DOMINANT_IPAN*/,
                double parameter1/*=0*/, double parameter2/*=0*/,
                double parameter3/*=0*/, double parameter4/*=0*/);

        public interface CvUpdateBGStatModel extends Callback {
            int callback(IplImage curr_frame, CvBGStatModel bg_model, double learningRate);
        }
        public static int cvUpdateBGStatModel(IplImage current_frame, CvBGStatModel bg_model, double learningRate/*=-1*/) {
            if (bg_model != null && bg_model.update != null) {
                return bg_model.update.v21.callback(current_frame, bg_model, learningRate);
            } else {
                return 0;
            }
        }

        public static native CvConDensation cvCreateConDensation(int dynam_params, int measure_params, int sample_count);
        public static native void cvReleaseConDensation(CvConDensation.PointerByReference condens);
        public static native void cvConDensUpdateByTime(CvConDensation condens);
        public static native void cvConDensInitSampleSet(CvConDensation condens, CvMat lower_bound, CvMat upper_bound);
    }


    public static native CvSeq cvSegmentImage(CvArr srcarr, CvArr dstarr,
            double canny_threshold, double ffill_threshold, CvMemStorage storage);


    public interface CvCallback extends Callback {
        int callback(int index, Pointer buffer, Pointer user_data);
    }

    public static final int 
            CV_EIGOBJ_NO_CALLBACK     = 0,
            CV_EIGOBJ_INPUT_CALLBACK  = 1,
            CV_EIGOBJ_OUTPUT_CALLBACK = 2,
            CV_EIGOBJ_BOTH_CALLBACK   = 3;

    public static native void cvCalcCovarMatrixEx(int nObjects, Pointer input, int ioFlags,
            int ioBufSize, Pointer buffer, Pointer userData, IplImage avg, float[] covarMatrix);
    public static native void cvCalcEigenObjects(int nObjects, Pointer input, Pointer output,
            int ioFlags, int ioBufSize, Pointer userData, CvTermCriteria calcLimit, IplImage avg, float[] eigVals);
    public static native double cvCalcDecompCoeff(IplImage obj, IplImage eigObj, IplImage avg);
    public static native void cvEigenDecomposite(IplImage obj, int nEigObjs, Pointer eigInput,
            int ioFlags, Pointer userData, IplImage avg, float[] coeffs);
    public static native void cvEigenProjection(Pointer eigInput, int nEigObjs, int ioFlags,
            Pointer userData, float[] coeffs, IplImage avg, IplImage proj);

    public static native void cvCalcCovarMatrixEx(int nObjects, Pointer input, int ioFlags,
            int ioBufSize, Pointer buffer, Pointer userData, IplImage avg, FloatBuffer covarMatrix);
    public static native void cvCalcEigenObjects(int nObjects, Pointer input, Pointer output,
            int ioFlags, int ioBufSize, Pointer userData, CvTermCriteria calcLimit, IplImage avg, FloatBuffer eigVals);
    public static native void cvEigenDecomposite(IplImage obj, int nEigObjs, Pointer eigInput,
            int ioFlags, Pointer userData, IplImage avg, FloatBuffer coeffs);
    public static native void cvEigenProjection(Pointer eigInput, int nEigObjs, int ioFlags,
            Pointer userData, FloatBuffer coeffs, IplImage avg, IplImage proj);


    public static class CvImgObsInfo extends Structure {
        public static boolean autoSynch = true;
        public CvImgObsInfo() { setAutoSynch(autoSynch); releasable = false; }
        public CvImgObsInfo(Pointer m) { super(m); setAutoSynch(autoSynch); releasable = true;
                if (autoSynch && getClass() == CvImgObsInfo.class) read(); }

        public static CvImgObsInfo create(CvSize.ByValue numObs, int obsSize) {
            CvImgObsInfo c = cvCreateObsInfo(numObs, obsSize);
            if (c != null) {
                c.releasable = true;
            }
            return c;
        }
        public void release() {
            releasable = false;
            cvReleaseObsInfo(pointerByReference());
        }
        @Override protected void finalize() {
            if (releasable) {
                release();
            }
        }
        private boolean releasable = false;

        private static final String[] fieldOrder = {
            "obs_x", "obs_y", "obs_size", "obs", "state", "mix" };
        { setFieldOrder(fieldOrder); }
        public int obs_x;
        public int obs_y;
        public int obs_size;
        public FloatByReference obs;

        public IntByReference state;
        public IntByReference mix;

        public static class PointerByReference extends com.sun.jna.ptr.ByReference {
            public PointerByReference() { super(Pointer.SIZE); }
            public PointerByReference(CvImgObsInfo p) { super(Pointer.SIZE); setStructure(p); }
            public void setValue(Pointer value) {
                getPointer().setPointer(0, value);
            }
            public Pointer getValue() {
                return getPointer().getPointer(0);
            }

            public CvImgObsInfo getStructure() {
                return new CvImgObsInfo(getValue());
            }
            public void getStructure(CvImgObsInfo p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvImgObsInfo p) {
                p.write();
                setValue(p.getPointer());
            }

            public PointerByReference(CvImgObsInfo[] a) {
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
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static class Cv1DObsInfo extends CvImgObsInfo { };

    public static class CvEHMMState extends Structure {
        public static boolean autoSynch = true;
        public CvEHMMState() { setAutoSynch(autoSynch); }
        public CvEHMMState(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvEHMMState.class) read(); }

        private static final String[] fieldOrder = {
            "num_mix", "mu", "inv_var", "log_var_val", "weight" };
        { setFieldOrder(fieldOrder); }
        public int num_mix;
        public FloatByReference mu;
        public FloatByReference inv_var;
        public FloatByReference log_var_val;
        public FloatByReference weight;

        public static class ByReference extends CvEHMMState implements Structure.ByReference { }
    }

    public static class CvEHMM extends Structure {
        public static boolean autoSynch = true;
        public CvEHMM() { setAutoSynch(autoSynch); releasable = false; }
        public CvEHMM(Pointer m) { super(m); setAutoSynch(autoSynch); releasable = true;
                if (autoSynch && getClass() == CvEHMM.class) read(); }

        public static CvEHMM create(int[] stateNumber, int[] numMix, int obsSize) {
            CvEHMM c = cvCreate2DHMM(stateNumber, numMix, obsSize);
            if (c != null) {
                c.releasable = true;
            }
            return c;
        }
        public void release() {
            releasable = false;
            cvRelease2DHMM(pointerByReference());
        }
        @Override protected void finalize() {
            if (releasable) {
                release();
            }
        }
        private boolean releasable = false;

        private static final String[] fieldOrder = {
            "level", "num_states", "transP", "obsProb", "u" };
        { setFieldOrder(fieldOrder); }
        public int level;
        public int num_states;
        public FloatByReference transP;
        public FloatPointerByReference obsProb;

        public static class U extends Union {
            public CvEHMMState.ByReference state;
            public CvEHMM.ByReference ehmm;
        }
        public U u;

        public static class ByReference extends CvEHMM implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvEHMM p) {
                setStructure(p);
            }
            public CvEHMM getStructure() {
                return new CvEHMM(getValue());
            }
            public void getStructure(CvEHMM p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvEHMM p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

//    public static native int icvCreate1DHMM(CvEHMM.PointerByReference this_hmm,
//            int state_number, int[] num_mix, int obs_size);
//    public static native int icvRelease1DHMM(CvEHMM.PointerByReference phmm);
//    public static native int icvUniform1DSegm(Cv1DObsInfo obs_info, CvEHMM hmm);
//    public static native int icvInit1DMixSegm(Cv1DObsInfo.PointerByReference obs_info_array,
//            int num_img, CvEHMM hmm);
//    public static native int icvEstimate1DHMMStateParams(CvImgObsInfo.PointerByReference obs_info_array,
//            int num_img, CvEHMM hmm);
//    public static native int icvEstimate1DObsProb(CvImgObsInfo obs_info, CvEHMM hmm);
//    public static native int icvEstimate1DTransProb(Cv1DObsInfo.PointerByReference obs_info_array,
//            int num_seq, CvEHMM hmm);
//    public static native float icvViterbi(Cv1DObsInfo obs_info, CvEHMM hmm);
//    public static native int icv1DMixSegmL2(CvImgObsInfo.PointerByReference obs_info_array, int num_img, CvEHMM hmm);

    public static native CvEHMM cvCreate2DHMM(int[] stateNumber, int[] numMix, int obsSize);
    public static native void cvRelease2DHMM(CvEHMM.PointerByReference  hmm);

    public static void CV_COUNT_OBS(CvSize roi, CvSize win, CvSize delta, CvSize numObs) {
        numObs.width  = (roi.width  - win.width  + delta.width)/delta.width;
        numObs.height = (roi.height - win.height + delta.height)/delta.height;
    }

    public static native CvImgObsInfo cvCreateObsInfo(CvSize.ByValue numObs, int obsSize);
    public static native void cvReleaseObsInfo(CvImgObsInfo.PointerByReference obs_info);
    public static native void cvImgToObs_DCT(CvArr arr, float[] obs, CvSize.ByValue dctSize,
            CvSize.ByValue obsSize, CvSize.ByValue delta);
    public static native void cvImgToObs_DCT(CvArr arr, FloatBuffer obs, CvSize.ByValue dctSize,
            CvSize.ByValue obsSize, CvSize.ByValue delta);
    public static native void cvUniformImgSegm(CvImgObsInfo obs_info, CvEHMM ehmm);
    public static native void cvInitMixSegm(CvImgObsInfo.PointerByReference obs_info_array,
            int num_img, CvEHMM hmm);
    public static native void cvEstimateHMMStateParams(CvImgObsInfo.PointerByReference obs_info_array,
            int num_img, CvEHMM hmm);
    public static native void cvEstimateTransProb(CvImgObsInfo.PointerByReference obs_info_array,
            int num_img, CvEHMM hmm);
    public static native void cvEstimateObsProb(CvImgObsInfo obs_info, CvEHMM hmm);
    public static native float cvEViterbi(CvImgObsInfo obs_info, CvEHMM hmm);
    public static native void cvMixSegmL2(CvImgObsInfo.PointerByReference obs_info_array,
            int num_img, CvEHMM hmm);


    public static native void cvCreateHandMask(CvSeq hand_points, IplImage img_mask, CvRect roi);
    public static native void cvFindHandRegion(CvPoint3D32f points, int count, CvSeq indexs,
            float[] line, CvSize2D32f.ByValue size, int flag, CvPoint3D32f center,
            CvMemStorage storage, CvSeq.PointerByReference numbers);
    public static void cvFindHandRegion(CvPoint3D32f[] points, int count, CvSeq indexs,
            float[] line, CvSize2D32f.ByValue size, int flag, CvPoint3D32f center,
            CvMemStorage storage, CvSeq.PointerByReference numbers) {
        for (CvPoint3D32f p : points) { p.write(); }
        cvFindHandRegion(points[0], count, indexs, line, size, flag, center, storage, numbers);
    }
    public static native void cvFindHandRegionA(CvPoint3D32f points, int count, CvSeq indexs,
            float[] line, CvSize2D32f.ByValue size, int jc, CvPoint3D32f center,
            CvMemStorage storage, CvSeq.PointerByReference numbers);
    public static void cvFindHandRegionA(CvPoint3D32f[] points, int count, CvSeq indexs,
            float[] line, CvSize2D32f.ByValue size, int jc, CvPoint3D32f center,
            CvMemStorage storage, CvSeq.PointerByReference numbers) {
        for (CvPoint3D32f p : points) { p.write(); }
        cvFindHandRegionA(points[0], count, indexs, line, size, jc, center, storage, numbers);
    }

    public static native void cvFindHandRegion(CvPoint3D32f points, int count, CvSeq indexs,
            FloatBuffer line, CvSize2D32f.ByValue size, int flag, CvPoint3D32f center,
            CvMemStorage storage, CvSeq.PointerByReference numbers);
    public static void cvFindHandRegion(CvPoint3D32f[] points, int count, CvSeq indexs,
            FloatBuffer line, CvSize2D32f.ByValue size, int flag, CvPoint3D32f center,
            CvMemStorage storage, CvSeq.PointerByReference numbers) {
        for (CvPoint3D32f p : points) { p.write(); }
        cvFindHandRegion(points[0], count, indexs, line, size, flag, center, storage, numbers);
    }
    public static native void cvFindHandRegionA(CvPoint3D32f points, int count, CvSeq indexs,
            FloatBuffer line, CvSize2D32f.ByValue size, int jc, CvPoint3D32f center,
            CvMemStorage storage, CvSeq.PointerByReference numbers);
    public static void cvFindHandRegionA(CvPoint3D32f[] points, int count, CvSeq indexs,
            FloatBuffer line, CvSize2D32f.ByValue size, int jc, CvPoint3D32f center,
            CvMemStorage storage, CvSeq.PointerByReference numbers) {
        for (CvPoint3D32f p : points) { p.write(); }
        cvFindHandRegionA(points[0], count, indexs, line, size, jc, center, storage, numbers);
    }


    public static native void icvDrawMosaic(CvSubdiv2D subdiv, IplImage src, IplImage dst);
    public static native int icvSubdiv2DCheck(CvSubdiv2D subdiv);
    public static double icvSqDist2D32f(CvPoint2D32f pt1, CvPoint2D32f pt2) {
        double dx = pt1.x - pt2.x;
        double dy = pt1.y - pt2.y;

        return dx*dx + dy*dy;
    }


    public static int CV_CURRENT_INT(CvSeqReader reader) { return reader.ptr.getInt(0); }
    public static int CV_PREV_INT(CvSeqReader reader) { return reader.prev_elem.getInt(0); }

    public static class CvGraphWeightedVtx extends CvGraphVtx {
        private static final String[] fieldOrder = { "weight" };
        { setFieldOrder(fieldOrder); }
        public float weight;
    }

    public static class CvGraphWeightedEdge extends CvGraphEdge { }

    //typedef enum CvGraphWeightType
    public static final int
            CV_NOT_WEIGHTED = 0,
            CV_WEIGHTED_VTX = 1,
            CV_WEIGHTED_EDGE = 2,
            CV_WEIGHTED_ALL = 3;


    public static class CvCliqueFinder extends Structure {
        public static boolean autoSynch = true;
        public CvCliqueFinder() { setAutoSynch(autoSynch); }
        public CvCliqueFinder(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvCliqueFinder.class) read(); }

        private static final String[] fieldOrder = { "graph", "adj_matr", "N",
            "k", "current_comp", "All", "ne", "ce", "fixp", "nod", "s",
            "status", "best_score", "weighted", "weighted_edges", "best_weight",
            "edge_weights", "vertex_weights", "cur_weight", "cand_weight" };
        { setFieldOrder(fieldOrder); }
        public CvGraph.ByReference graph;
        public PointerByReference /* int** */ adj_matr;
        public int N;

        public int k;
        public IntByReference current_comp;
        public PointerByReference /* int** */ All;

        public IntByReference ne;
        public IntByReference ce;
        public IntByReference fixp; //node with minimal disconnections
        public IntByReference nod;
        public IntByReference s; //for selected candidate
        public int status;
        public int best_score;
        public int weighted;
        public int weighted_edges;
        public float best_weight;
        public FloatByReference edge_weights;
        public FloatByReference vertex_weights;
        public FloatByReference cur_weight;
        public FloatByReference cand_weight;
    }

    public static final int 
            CLIQUE_TIME_OFF = 2,
            CLIQUE_FOUND = 1,
            CLIQUE_END   = 0;

//    public static native void cvStartFindCliques(CvGraph graph, CvCliqueFinder finder, int reverse,
//            int weighted/*=0*/,  int weighted_edges/*=0*/);
//    public static native int cvFindNextMaximalClique(CvCliqueFinder finder, IntByReference clock_rest/*=null*/);
//    public static native void cvEndFindCliques(CvCliqueFinder finder);
//    public static native void cvBronKerbosch(CvGraph graph);

//    public static native float cvSubgraphWeight(CvGraph graph, CvSeq subgraph,
//            int /* CvGraphWeightType */ weight_type /*=CV_NOT_WEIGHTED*/,
//            float[] weight_vtx/*=null*/, float[] weight_edge/*=null*/);

//    public static native  CvSeq cvFindCliqueEx(CvGraph graph, CvMemStorage storage,
//            int is_complementary/*=0*/, int /* CvGraphWeightType */ weight_type/*=CV_NOT_WEIGHTED*/,
//            float[] weight_vtx/*=null*/, float[] weight_edge/*=null*/,
//            CvSeq start_clique/*=null*/, CvSeq subgraph_of_ban/*=null*/,
//            float[] clique_weight_ptr/*=null*/, int num_generations/*=3*/, int quality/*=2*/);


    public static final int
        CV_UNDEF_SC_PARAM         = 12345,

        CV_IDP_BIRCHFIELD_PARAM1  = 25,
        CV_IDP_BIRCHFIELD_PARAM2  = 5,
        CV_IDP_BIRCHFIELD_PARAM3  = 12,
        CV_IDP_BIRCHFIELD_PARAM4  = 15,
        CV_IDP_BIRCHFIELD_PARAM5  = 25,

        CV_DISPARITY_BIRCHFIELD  = 0;

    public static native void cvFindStereoCorrespondence(CvArr leftImage, CvArr rightImage,
            int mode, CvArr dispImage, int maxDisparity, double param1/*=CV_UNDEF_SC_PARAM*/,
            double param2/*=CV_UNDEF_SC_PARAM*/,         double param3/*=CV_UNDEF_SC_PARAM*/,
            double param4/*=CV_UNDEF_SC_PARAM*/,         double param5/*=CV_UNDEF_SC_PARAM*/);


    public static class CvStereoLineCoeff extends Structure {
        public static boolean autoSynch = true;
        public CvStereoLineCoeff() { setAutoSynch(autoSynch); }
        public CvStereoLineCoeff(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvStereoLineCoeff.class) read(); }

        private static final String[] fieldOrder = {
            "Xcoef", "XcoefA", "XcoefB", "XcoefAB",
            "Ycoef", "YcoefA", "YcoefB", "YcoefAB",
            "Zcoef", "ZcoefA", "ZcoefB", "ZcoefAB" };
        { setFieldOrder(fieldOrder); }
        public double Xcoef;
        public double XcoefA;
        public double XcoefB;
        public double XcoefAB;

        public double Ycoef;
        public double YcoefA;
        public double YcoefB;
        public double YcoefAB;

        public double Zcoef;
        public double ZcoefA;
        public double ZcoefB;
        public double ZcoefAB;

        public static class ByReference extends CvStereoLineCoeff implements Structure.ByReference { }
    }

    public static class CvCamera extends Structure {
        public static boolean autoSynch = true;
        public CvCamera() { setAutoSynch(autoSynch); }
        public CvCamera(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvCamera.class) read(); }

        private static final String[] fieldOrder = {
            "imgSize", "matrix", "distortion", "rotMatr", "transVect" };
        { setFieldOrder(fieldOrder); }
        public float[] imgSize    = new float[2];
        public float[] matrix     = new float[9];
        public float[] distortion = new float[4];
        public float[] rotMatr    = new float[9];
        public float[] transVect  = new float[3];

        public static class ByReference extends CvCamera implements Structure.ByReference { }
    }

    public static class CvStereoCamera extends Structure {
        public static boolean autoSynch = true;
        public CvStereoCamera() { setAutoSynch(autoSynch); }
        public CvStereoCamera(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvStereoCamera.class) read(); }

        private static final String[] fieldOrder = { "camera", "fundMatr",
            "epipole", "quad", "coeffs", "border", "warpSize", "lineCoeffs",
            "needSwapCameras", "rotMatrix", "transVector" };
        { setFieldOrder(fieldOrder); }
        public CvCamera.ByReference[] camera = new CvCamera.ByReference[2];
        public float[] fundMatr = new float[9];

        public CvPoint3D32f[] epipole = new CvPoint3D32f[2];
        public CvPoint2D32f[] quad = new CvPoint2D32f[2*4];
        public double[] coeffs = new double [2*3*3];
        public CvPoint2D32f[] border = new CvPoint2D32f[2*4];
        public CvSize warpSize;
        public CvStereoLineCoeff.ByReference lineCoeffs;
        public int needSwapCameras;
        public float[] rotMatrix   = new float[9];
        public float[] transVector = new float[3];
    }

    public static class CvContourOrientation extends Structure {
        public static boolean autoSynch = true;
        public CvContourOrientation() { setAutoSynch(autoSynch); }
        public CvContourOrientation(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvContourOrientation.class) read(); }

        private static final String[] fieldOrder = { "egvals", "egvects",
            "max", "min", "imax", "imin" };
        { setFieldOrder(fieldOrder); }
        public float[] egvals  = new float[2];
        public float[] egvects = new float[4];

        public float max, min;
        public int imax, imin;
    }

    public static final int 
            CV_CAMERA_TO_WARP = 1,
            CV_WARP_TO_CAMERA = 2;

    public static native int icvConvertWarpCoordinates(double[] coeffs/*[3][3]*/,
            CvPoint2D32f cameraPoint, CvPoint2D32f warpPoint, int direction);
    public static native int icvConvertWarpCoordinates(DoubleBuffer coeffs/*[3][3]*/,
            CvPoint2D32f cameraPoint, CvPoint2D32f warpPoint, int direction);
    public static native int icvGetSymPoint3D(CvPoint3D64f.ByValue pointCorner,
            CvPoint3D64f.ByValue point1, CvPoint3D64f.ByValue point2, CvPoint3D64f pointSym2);
    public static native void icvGetPieceLength3D(CvPoint3D64f.ByValue point1,
            CvPoint3D64f.ByValue point2, DoubleByReference dist);
    public static native int icvCompute3DPoint(double alpha, double betta,
            CvStereoLineCoeff coeffs, CvPoint3D64f point);
    public static native int icvCreateConvertMatrVect(double[] rotMatr1,
            double[] transVect1,  double[] rotMatr2,  double[] transVect2,
            double[] convRotMatr, double[] convTransVect);
    public static native int icvConvertPointSystem(CvPoint3D64f.ByValue M2,
            CvPoint3D64f M1, double[] rotMatr, double[] transVect);
    public static native int icvCreateConvertMatrVect(DoubleBuffer rotMatr1,
            DoubleBuffer transVect1,  DoubleBuffer rotMatr2,  DoubleBuffer transVect2,
            DoubleBuffer convRotMatr, DoubleBuffer convTransVect);
    public static native int icvConvertPointSystem(CvPoint3D64f.ByValue M2,
            CvPoint3D64f M1, DoubleBuffer rotMatr, DoubleBuffer transVect);
    public static native int icvComputeCoeffForStereo(CvStereoCamera stereoCamera);

    public static native int icvGetCrossPieceVector(CvPoint2D32f.ByValue p1_start,
            CvPoint2D32f.ByValue p1_end, CvPoint2D32f.ByValue v2_start,
            CvPoint2D32f.ByValue v2_end, CvPoint2D32f cross);
    public static native int icvGetCrossLineDirect(CvPoint2D32f.ByValue p1,
            CvPoint2D32f.ByValue p2, float a, float b, float c, CvPoint2D32f cross);
    public static native float icvDefinePointPosition(CvPoint2D32f.ByValue point1,
            CvPoint2D32f.ByValue point2, CvPoint2D32f.ByValue point);
    public static native int icvStereoCalibration(int numImages, int[] nums, CvSize.ByValue imageSize, 
            CvPoint2D32f imagePoints1, CvPoint2D32f imagePoints2,
            CvPoint3D32f objectPoints, CvStereoCamera stereoparams);
    public static int icvStereoCalibration(int numImages, int[] nums, CvSize.ByValue imageSize,
            CvPoint2D32f[] imagePoints1, CvPoint2D32f[] imagePoints2,
            CvPoint3D32f[] objectPoints, CvStereoCamera stereoparams) {
        for (CvPoint2D32f p : imagePoints1) { p.write(); }
        for (CvPoint2D32f p : imagePoints2) { p.write(); }
        for (CvPoint3D32f p : objectPoints) { p.write(); }
        return  icvStereoCalibration(numImages, nums, imageSize,
            imagePoints1[0], imagePoints2[0], objectPoints[0], stereoparams);
    }

    public static native int icvComputeRestStereoParams(CvStereoCamera stereoparams);

    public static native void cvComputePerspectiveMap(double[] coeffs/*[3][3]*/,
            CvArr rectMapX, CvArr rectMapY);
    public static native void cvComputePerspectiveMap(DoubleBuffer coeffs/*[3][3]*/,
            CvArr rectMapX, CvArr rectMapY);

    public static native int icvComCoeffForLine(CvPoint2D64f.ByValue point1,
            CvPoint2D64f.ByValue point2, CvPoint2D64f.ByValue point3, CvPoint2D64f.ByValue point4,
            double[] camMatr1, double[] rotMatr1, double[] transVect1, double[] camMatr2,
            double[] rotMatr2, double[] transVect2, CvStereoLineCoeff coeffs, IntByReference needSwapCameras);
    public static native int icvComCoeffForLine(CvPoint2D64f.ByValue point1,
            CvPoint2D64f.ByValue point2, CvPoint2D64f.ByValue point3, CvPoint2D64f.ByValue point4,
            DoubleBuffer camMatr1, DoubleBuffer rotMatr1, DoubleBuffer transVect1, DoubleBuffer camMatr2,
            DoubleBuffer rotMatr2, DoubleBuffer transVect2, CvStereoLineCoeff coeffs, IntByReference needSwapCameras);
    public static native int icvGetDirectionForPoint(CvPoint2D64f.ByValue point,
            double[] camMatr, CvPoint3D64f direct);
    public static native int icvGetDirectionForPoint(CvPoint2D64f.ByValue point,
            DoubleBuffer camMatr, CvPoint3D64f direct);
    public static native int icvGetCrossLines(CvPoint3D64f.ByValue point11, CvPoint3D64f.ByValue point12,
            CvPoint3D64f.ByValue point21, CvPoint3D64f.ByValue point22, CvPoint3D64f midPoint);
    public static native int icvComputeStereoLineCoeffs(CvPoint3D64f.ByValue pointA, CvPoint3D64f.ByValue pointB,
            CvPoint3D64f.ByValue pointCam1, double gamma, CvStereoLineCoeff coeffs);
//    public static native int icvComputeFundMatrEpipoles(double[] camMatr1, double[] rotMatr1,
//            double[] transVect1, double[] camMatr2, double[] rotMatr2, double[] transVect2,
//            CvPoint2D64f epipole1, CvPoint2D64f epipole2, double[] fundMatr);
    public static native int icvGetAngleLine(CvPoint2D64f.ByValue startPoint,
            CvSize.ByValue imageSize,  CvPoint2D64f point1, CvPoint2D64f point2);

    public static native void icvGetCoefForPiece(CvPoint2D64f.ByValue p_start, CvPoint2D64f.ByValue p_end,
            DoubleByReference a, DoubleByReference b, DoubleByReference c, IntByReference result);
//    public static native void icvGetCommonArea(CvSize.ByValue imageSize, CvPoint2D64f.ByValue epipole1,
//            CvPoint2D64f.ByValue epipole2, double[] fundMatr,  double[] coeff11, double[] coeff12,
//            double[] coeff21, double[] coeff22, IntByReference result);

    public static native void icvComputeeInfiniteProject1(double[] rotMatr, double[] camMatr1,
            double[] camMatr2, CvPoint2D32f.ByValue point1, CvPoint2D32f point2);
    public static native void icvComputeeInfiniteProject1(DoubleBuffer rotMatr, DoubleBuffer camMatr1,
            DoubleBuffer camMatr2, CvPoint2D32f.ByValue point1, CvPoint2D32f point2);
    public static native void icvComputeeInfiniteProject2(double[] rotMatr, double[] camMatr1,
            double[] camMatr2, CvPoint2D32f point1, CvPoint2D32f.ByValue point2);
    public static native void icvComputeeInfiniteProject2(DoubleBuffer rotMatr, DoubleBuffer camMatr1,
            DoubleBuffer camMatr2, CvPoint2D32f point1, CvPoint2D32f.ByValue point2);

    public static native void icvGetCrossDirectDirect(double[] direct1, double[] direct2,
            CvPoint2D64f cross, IntByReference result);
    public static native void icvGetCrossDirectDirect(DoubleBuffer direct1, DoubleBuffer direct2,
            CvPoint2D64f cross, IntByReference result);
    public static native void icvGetCrossPieceDirect(CvPoint2D64f.ByValue p_start,
            CvPoint2D64f.ByValue p_end, double a, double b, double c,
            CvPoint2D64f cross, IntByReference result);
    public static native void icvGetCrossPiecePiece(CvPoint2D64f.ByValue p1_start,
            CvPoint2D64f.ByValue p1_end, CvPoint2D64f.ByValue p2_start,CvPoint2D64f.ByValue p2_end,
            CvPoint2D64f cross, IntByReference result);

    public static native void icvGetPieceLength(CvPoint2D64f.ByValue point1,
            CvPoint2D64f.ByValue point2, DoubleByReference dist);
    public static native void icvGetCrossRectDirect(CvSize.ByValue imageSize,
            double a, double b, double c, CvPoint2D64f start, CvPoint2D64f end,
            IntByReference result);
    public static native void icvProjectPointToImage(CvPoint3D64f.ByValue point,
            double[] camMatr, double[] rotMatr, double[] transVect, CvPoint2D64f projPoint);
    public static native void icvProjectPointToImage(CvPoint3D64f.ByValue point,
            DoubleBuffer camMatr, DoubleBuffer rotMatr, DoubleBuffer transVect, CvPoint2D64f projPoint);
    public static native void icvGetQuadsTransform(CvSize.ByValue imageSize,
            double[] camMatr1, double[] rotMatr1, double[] transVect1,
            double[] camMatr2, double[] rotMatr2, double[] transVect2,
            CvSize   warpSize, double[] quad1/*[4][2]*/, double[] quad2/*[4][2]*/,
            double[] fundMatr, CvPoint3D64f epipole1, CvPoint3D64f epipole2);
    public static native void icvGetQuadsTransform(CvSize.ByValue imageSize,
            DoubleBuffer camMatr1, DoubleBuffer rotMatr1, DoubleBuffer transVect1,
            DoubleBuffer camMatr2, DoubleBuffer rotMatr2, DoubleBuffer transVect2,
            CvSize   warpSize, DoubleBuffer quad1/*[4][2]*/, DoubleBuffer quad2/*[4][2]*/,
            DoubleBuffer fundMatr, CvPoint3D64f epipole1, CvPoint3D64f epipole2);

    public static native void icvGetQuadsTransformStruct(CvStereoCamera stereoCamera);
    public static native void icvComputeStereoParamsForCameras(CvStereoCamera stereoCamera);

    public static native void icvGetCutPiece(double[] areaLineCoef1, double[] areaLineCoef2,
            CvPoint2D64f.ByValue epipole, CvSize.ByValue imageSize,
            CvPoint2D64f point11, CvPoint2D64f point12,
            CvPoint2D64f point21, CvPoint2D64f point22, IntByReference result);
    public static native void icvGetCutPiece(DoubleBuffer areaLineCoef1, DoubleBuffer areaLineCoef2,
            CvPoint2D64f.ByValue epipole, CvSize.ByValue imageSize,
            CvPoint2D64f point11, CvPoint2D64f point12,
            CvPoint2D64f point21, CvPoint2D64f point22, IntByReference result);
    public static native void icvGetMiddleAnglePoint(CvPoint2D64f.ByValue basePoint,
            CvPoint2D64f.ByValue point1, CvPoint2D64f.ByValue point2, CvPoint2D64f midPoint);
    public static native void icvGetNormalDirect(double[] direct,
            CvPoint2D64f.ByValue point, double[] normDirect);
    public static native void icvGetNormalDirect(DoubleBuffer direct,
            CvPoint2D64f.ByValue point, DoubleBuffer normDirect);
    public static native double icvGetVect(CvPoint2D64f.ByValue basePoint,
            CvPoint2D64f.ByValue point1, CvPoint2D64f.ByValue point2);
    public static native void icvProjectPointToDirect(CvPoint2D64f.ByValue point,
            double[] lineCoeff, CvPoint2D64f projectPoint);
    public static native void icvProjectPointToDirect(CvPoint2D64f.ByValue point,
            DoubleBuffer lineCoeff, CvPoint2D64f projectPoint);
    public static native void icvGetDistanceFromPointToDirect(CvPoint2D64f.ByValue point,
            double[] lineCoef, DoubleByReference dist);
    public static native void icvGetDistanceFromPointToDirect(CvPoint2D64f.ByValue point,
            DoubleBuffer lineCoef, DoubleByReference dist);

    public static native IplImage icvCreateIsometricImage(IplImage src, IplImage dst,
            int desired_depth, int desired_num_channels);

    public static native void cvDeInterlace(CvArr frame, CvArr fieldEven, CvArr fieldOdd);

//    public static native int icvSelectBestRt(int numImages, int[] numPoints, CvSize.ByValue imageSize,
//            CvPoint2D32f imagePoints1, CvPoint2D32f imagePoints2, CvPoint3D32f objectPoints,
//            float[] cameraMatrix1, float[] distortion1, float[] rotMatrs1, float[] transVects1,
//            float[] cameraMatrix2, float[] distortion2, float[] rotMatrs2, float[] transVects2,
//            float[] bestRotMatr,   float[] bestTransVect);
//    public static int icvSelectBestRt(int numImages, int[] numPoints, CvSize.ByValue imageSize,
//            CvPoint2D32f[] imagePoints1, CvPoint2D32f[] imagePoints2, CvPoint3D32f[] objectPoints,
//            float[] cameraMatrix1, float[] distortion1, float[] rotMatrs1, float[] transVects1,
//            float[] cameraMatrix2, float[] distortion2, float[] rotMatrs2, float[] transVects2,
//            float[] bestRotMatr,   float[] bestTransVect) {
//        for (CvPoint2D32f p : imagePoints1) { p.write(); }
//        for (CvPoint2D32f p : imagePoints2) { p.write(); }
//        for (CvPoint3D32f p : objectPoints) { p.write(); }
//        return icvSelectBestRt(numImages, numPoints, imageSize, imagePoints1[0], imagePoints2[0], objectPoints[0],
//            cameraMatrix1, distortion1, rotMatrs1, transVects1, cameraMatrix2, distortion2, rotMatrs2, transVects2,
//            bestRotMatr,   bestTransVect);
//    }


//    public static native CvSeq cvCalcContoursCorrespondence(CvSeq contour1,
//            CvSeq contour2, CvMemStorage storage);
//    public static native CvSeq cvMorphContours(CvSeq contour1, CvSeq contour2,
//            CvSeq corr, double alpha, CvMemStorage storage);


    public static final int
            CV_GLCM_OPTIMIZATION_NONE                  = -2,
            CV_GLCM_OPTIMIZATION_LUT                   = -1,
            CV_GLCM_OPTIMIZATION_HISTOGRAM             = 0,

            CV_GLCMDESC_OPTIMIZATION_ALLOWDOUBLENEST   = 10,
            CV_GLCMDESC_OPTIMIZATION_ALLOWTRIPLENEST   = 11,
            CV_GLCMDESC_OPTIMIZATION_HISTOGRAM         = 4,

            CV_GLCMDESC_ENTROPY                        = 0,
            CV_GLCMDESC_ENERGY                         = 1,
            CV_GLCMDESC_HOMOGENITY                     = 2,
            CV_GLCMDESC_CONTRAST                       = 3,
            CV_GLCMDESC_CLUSTERTENDENCY                = 4,
            CV_GLCMDESC_CLUSTERSHADE                   = 5,
            CV_GLCMDESC_CORRELATION                    = 6,
            CV_GLCMDESC_CORRELATIONINFO1               = 7,
            CV_GLCMDESC_CORRELATIONINFO2               = 8,
            CV_GLCMDESC_MAXIMUMPROBABILITY             = 9,

            CV_GLCM_ALL                                = 0,
            CV_GLCM_GLCM                               = 1,
            CV_GLCM_DESC                               = 2;

    public static class CvGLCM extends PointerType {
        public CvGLCM() { releasable = false; }
        public CvGLCM(Pointer p) { super(p); releasable = true; }

        public static CvGLCM create(IplImage srcImage, int stepMagnitude) {
            return create(srcImage, stepMagnitude, null, 0, CV_GLCM_OPTIMIZATION_NONE);
        }
        public static CvGLCM create(IplImage srcImage, int stepMagnitude,
                int[] stepDirections/*=null*/, int numStepDirections/*=0*/,
                int optimizationType/*=CV_GLCM_OPTIMIZATION_NONE*/) {
            CvGLCM p = cvCreateGLCM(srcImage, stepMagnitude, stepDirections,
                    numStepDirections, optimizationType);
            if (p != null) {
                p.releasable = true;
            }
            return p;
        }

        public void release() {
            releasable = false;
            cvReleaseGLCM(pointerByReference(), CV_GLCM_ALL);
        }
        @Override protected void finalize() {
            if (releasable) {
                release();
            }
        }
        private boolean releasable = false;

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvGLCM p) {
                setStructure(p);
            }
            public CvGLCM getStructure() {
                return new CvGLCM(getValue());
            }
            public void getStructure(CvGLCM p) {
                p.setPointer(getValue());
            }
            public void setStructure(CvGLCM p) {
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static native CvGLCM cvCreateGLCM(IplImage srcImage, int stepMagnitude,
            int[] stepDirections/*=null*/, int numStepDirections/*=0*/,
            int optimizationType/*=CV_GLCM_OPTIMIZATION_NONE*/);
    public static native void cvReleaseGLCM(CvGLCM.PointerByReference GLCM, int flag/*=CV_GLCM_ALL*/);
    public static native void cvCreateGLCMDescriptors(CvGLCM destGLCM,
            int descriptorOptimizationType/*=CV_GLCMDESC_OPTIMIZATION_ALLOWDOUBLENEST*/);
    public static native double cvGetGLCMDescriptor(CvGLCM GLCM, int step, int descriptor);
    public static native void cvGetGLCMDescriptorStatistics(CvGLCM GLCM, int descriptor,
            DoubleByReference average, DoubleByReference standardDeviation);
    public static native IplImage cvCreateGLCMImage(CvGLCM GLCM, int step);


    public static class CvFaceTracker extends PointerType {
        public CvFaceTracker() { releasable = false; }
        public CvFaceTracker(Pointer p) { super(p); releasable = true; }

        public static CvFaceTracker create(CvFaceTracker pFaceTracking,
                IplImage imgGray, CvRect[] pRects, int nRects) {
            CvFaceTracker p = cvInitFaceTracker(new CvFaceTracker(), imgGray, pRects, nRects);
            if (p != null) {
                p.releasable = true;
            }
            return p;
        }

        public void release() {
            releasable = false;
            cvReleaseFaceTracker(pointerByReference());
        }
        @Override protected void finalize() {
            if (releasable) {
                release();
            }
        }
        private boolean releasable = false;

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvFaceTracker p) {
                setStructure(p);
            }
            public CvFaceTracker getStructure() {
                return new CvFaceTracker(getValue());
            }
            public void getStructure(CvFaceTracker p) {
                p.setPointer(getValue());
            }
            public void setStructure(CvFaceTracker p) {
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static final int 
            CV_NUM_FACE_ELEMENTS   = 3,
    // enum CV_FACE_ELEMENTS
            CV_FACE_MOUTH = 0,
            CV_FACE_LEFT_EYE = 1,
            CV_FACE_RIGHT_EYE = 2;

    public static native CvFaceTracker cvInitFaceTracker(CvFaceTracker pFaceTracking, 
            IplImage imgGray, CvRect pRects, int nRects);
    public static CvFaceTracker cvInitFaceTracker(CvFaceTracker pFaceTracking,
            IplImage imgGray, CvRect[] pRects, int nRects) {
        for (CvRect r : pRects) { r.write(); }
        return cvInitFaceTracker(pFaceTracking, imgGray, pRects[0], nRects);
    }
    public static native int cvTrackFace(CvFaceTracker pFaceTracker, IplImage imgGray,
            CvRect pRects, int nRects, CvPoint ptRotate, DoubleByReference dbAngleRotate);
    public static int cvTrackFace(CvFaceTracker pFaceTracker, IplImage imgGray,
            CvRect[] pRects, int nRects, CvPoint ptRotate, DoubleByReference dbAngleRotate) {
        int i = cvTrackFace(pFaceTracker, imgGray, pRects[0], nRects, ptRotate, dbAngleRotate);
        for (CvRect r : pRects) { r.read(); }
        return i;
    }
    public static native void cvReleaseFaceTracker(CvFaceTracker.PointerByReference ppFaceTracker);


    public static class CvFace extends Structure {
        public static boolean autoSynch = true;
        public CvFace() { setAutoSynch(autoSynch); }
        public CvFace(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvFace.class) read(); }

        private static final String[] fieldOrder = { "MouthRect", "LeftEyeRect", "RightEyeRect" };
        { setFieldOrder(fieldOrder); }
        public CvRect MouthRect;
        public CvRect LeftEyeRect;
        public CvRect RightEyeRect;
    }

//    public static native CvSeq cvFindFace(IplImage Image, CvMemStorage storage);
//    public static native CvSeq cvPostBoostingFindFace(IplImage Image, CvMemStorage storage);


    //typedef unsigned char CvBool;

    public static class Cv3dTracker2dTrackedObject extends Structure {
        public static boolean autoSynch = true;
        public Cv3dTracker2dTrackedObject() { setAutoSynch(autoSynch); }
        public Cv3dTracker2dTrackedObject(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == Cv3dTracker2dTrackedObject.class) read(); }

        private static final String[] fieldOrder = { "id", "p" };
        { setFieldOrder(fieldOrder); }
        public int id;
        public CvPoint2D32f p;
    }
    public static Cv3dTracker2dTrackedObject cv3dTracker2dTrackedObject(int id, CvPoint2D32f p) {
        Cv3dTracker2dTrackedObject r = new Cv3dTracker2dTrackedObject();
        r.id = id;
        r.p = p;
        return r;
    }

    public static class Cv3dTrackerTrackedObject extends Structure {
        public static boolean autoSynch = true;
        public Cv3dTrackerTrackedObject() { setAutoSynch(autoSynch); }
        public Cv3dTrackerTrackedObject(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == Cv3dTrackerTrackedObject.class) read(); }

        private static final String[] fieldOrder = { "id", "p" };
        { setFieldOrder(fieldOrder); }
        public int id;
        public CvPoint3D32f p;
    }
    public static Cv3dTrackerTrackedObject cv3dTrackerTrackedObject(int id, CvPoint3D32f p) {
        Cv3dTrackerTrackedObject r = new Cv3dTrackerTrackedObject();
        r.id = id;
        r.p = p;
        return r;
    }

    public static class Cv3dTrackerCameraInfo extends Structure {
        public static boolean autoSynch = true;
        public Cv3dTrackerCameraInfo() { setAutoSynch(autoSynch); }
        public Cv3dTrackerCameraInfo(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == Cv3dTrackerCameraInfo.class) read(); }

        private static final String[] fieldOrder = { "valid", "mat", "principal_point" };
        { setFieldOrder(fieldOrder); }
        public byte /*CvBool*/ valid;
        public float[] mat = new float[4*4];
        public CvPoint2D32f principal_point;
    }

    public static class Cv3dTrackerCameraIntrinsics extends Structure {
        public static boolean autoSynch = true;
        public Cv3dTrackerCameraIntrinsics() { setAutoSynch(autoSynch); }
        public Cv3dTrackerCameraIntrinsics(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == Cv3dTrackerCameraIntrinsics.class) read(); }

        private static final String[] fieldOrder = { "principal_point", "focal_length", "distortion" };
        { setFieldOrder(fieldOrder); }
        public CvPoint2D32f principal_point;
        public float[] focal_length = new float[2];
        public float[] distortion   = new float[4];
    }

    public static native byte /*CvBool*/ cv3dTrackerCalibrateCameras(int num_cameras,
            Cv3dTrackerCameraIntrinsics camera_intrinsics, CvSize.ByValue etalon_size,
            float square_size, IplImage.PointerByReference samples, Cv3dTrackerCameraInfo camera_info);
    public static byte /*CvBool*/ cv3dTrackerCalibrateCameras(int num_cameras,
            Cv3dTrackerCameraIntrinsics[] camera_intrinsics, CvSize.ByValue etalon_size,
            float square_size, IplImage[] samples, Cv3dTrackerCameraInfo[] camera_info) {
        for (Cv3dTrackerCameraIntrinsics c : camera_intrinsics) { c.write(); }
        byte b = cv3dTrackerCalibrateCameras(num_cameras, camera_intrinsics[0], etalon_size,
                square_size, new IplImage.PointerByReference(samples), camera_info[0]);
        for (Cv3dTrackerCameraInfo c : camera_info) { c.read(); }
        return b;
    }

    public static native int cv3dTrackerLocateObjects(int num_cameras, int num_objects,
            Cv3dTrackerCameraInfo camera_info, Cv3dTracker2dTrackedObject tracking_info,
            Cv3dTrackerTrackedObject tracked_objects);
    public static int cv3dTrackerLocateObjects(int num_cameras, int num_objects,
            Cv3dTrackerCameraInfo[] camera_info, Cv3dTracker2dTrackedObject[] tracking_info,
            Cv3dTrackerTrackedObject[] tracked_objects) {
        for (Cv3dTrackerCameraInfo c : camera_info) { c.write(); }
        for (Cv3dTracker2dTrackedObject o : tracking_info) { o.write(); }
        int i = cv3dTrackerLocateObjects(num_cameras, num_objects, camera_info[0],
                tracking_info[0], tracked_objects[0]);
        for (Cv3dTrackerTrackedObject o : tracked_objects) { o.read(); }
        return i;
    }
    

    //typedef enum CvLeeParameters
    public static final int
            CV_LEE_INT = 0,
            CV_LEE_FLOAT = 1,
            CV_LEE_DOUBLE = 2,
            CV_LEE_AUTO = -1,
            CV_LEE_ERODE = 0,
            CV_LEE_ZOOM = 1,
            CV_LEE_NON = 2;

    public static CvVoronoiSite2D.ByReference CV_NEXT_VORONOISITE2D(CvVoronoiSite2D SITE) {
        return SITE.edge[0].site[(SITE.edge[0].site[0] == SITE) ? 1 : 0];
    }
    public static CvVoronoiSite2D.ByReference CV_PREV_VORONOISITE2D(CvVoronoiSite2D SITE) {
        return SITE.edge[1].site[(SITE.edge[1].site[0] == SITE) ? 1 : 0];
    }
    public static CvVoronoiEdge2D.ByReference CV_FIRST_VORONOIEDGE2D(CvVoronoiSite2D SITE) {
        return SITE.edge[0];
    }
    public static CvVoronoiEdge2D.ByReference CV_LAST_VORONOIEDGE2D(CvVoronoiSite2D SITE) {
        return SITE.edge[1];
    }
    public static CvVoronoiEdge2D.ByReference CV_NEXT_VORONOIEDGE2D( CvVoronoiEdge2D EDGE, CvVoronoiSite2D SITE) {
        return EDGE.next[(EDGE.site[0] != SITE) ? 1 : 0];
    }
    public static CvVoronoiEdge2D.ByReference CV_PREV_VORONOIEDGE2D(CvVoronoiEdge2D EDGE, CvVoronoiSite2D SITE) {
        return EDGE.next[2 + ((EDGE.site[0] != SITE) ? 1 : 0)];
    }
    public static CvVoronoiNode2D.ByReference CV_VORONOIEDGE2D_BEGINNODE(CvVoronoiEdge2D EDGE, CvVoronoiSite2D SITE) {
        return EDGE.node[(EDGE.site[0] != SITE) ? 1 : 0];
    }
    public static CvVoronoiNode2D.ByReference CV_VORONOIEDGE2D_ENDNODE(CvVoronoiEdge2D EDGE, CvVoronoiSite2D SITE) {
        return EDGE.node[(EDGE.site[0] == SITE) ? 1 : 0];
    }
    public static CvVoronoiSite2D.ByReference CV_TWIN_VORONOISITE2D(CvVoronoiSite2D SITE, CvVoronoiEdge2D EDGE) {
        return EDGE.site[(EDGE.site[0] == SITE) ? 1 : 0];
    }

    public static class CvVoronoiSite2D extends Structure {
        public static boolean autoSynch = true;
        public CvVoronoiSite2D() { setAutoSynch(autoSynch); }
        public CvVoronoiSite2D(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvVoronoiSite2D.class) read(); }

        private static final String[] fieldOrder = { "node", "edge", "next" };
        { setFieldOrder(fieldOrder); }
        public CvVoronoiNode2D.ByReference[] node = new CvVoronoiNode2D.ByReference[2];
        public CvVoronoiEdge2D.ByReference[] edge = new CvVoronoiEdge2D.ByReference[2];

        public CvVoronoiSite2D.ByReference[] next = new CvVoronoiSite2D.ByReference[2];

        public static class ByReference extends CvVoronoiSite2D implements Structure.ByReference { }
    }

    public static class CvVoronoiEdge2D extends Structure {
        public static boolean autoSynch = true;
        public CvVoronoiEdge2D() { setAutoSynch(autoSynch); }
        public CvVoronoiEdge2D(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvVoronoiEdge2D.class) read(); }

        private static final String[] fieldOrder = { "node", "site", "next" };
        { setFieldOrder(fieldOrder); }
        public CvVoronoiNode2D.ByReference[] node = new CvVoronoiNode2D.ByReference[2];
        public CvVoronoiSite2D.ByReference[] site = new CvVoronoiSite2D.ByReference[2];
        public CvVoronoiEdge2D.ByReference[] next = new CvVoronoiEdge2D.ByReference[4];

        public static class ByReference extends CvVoronoiEdge2D implements Structure.ByReference { }
    }

    public static class CvVoronoiNode2D extends CvSetElem {
        public static boolean autoSynch = true;
        public CvVoronoiNode2D() { setAutoSynch(autoSynch); }
        public CvVoronoiNode2D(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvVoronoiNode2D.class) read(); }

        private static final String[] fieldOrder = { "pt", "radius" };
        { setFieldOrder(fieldOrder); }
        public CvPoint2D32f pt;
        public float radius;

        public static class ByReference extends CvVoronoiNode2D implements Structure.ByReference { }
    }

    public static class CvVoronoiDiagram2D extends CvGraph {
        public static boolean autoSynch = true;
        public CvVoronoiDiagram2D() { setAutoSynch(autoSynch); }
        public CvVoronoiDiagram2D(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvVoronoiDiagram2D.class) read(); }

        private static final String[] fieldOrder = { "sites" };
        { setFieldOrder(fieldOrder); }
        CvSet.ByReference sites;
    }

    public static native int cvVoronoiDiagramFromContour(CvSeq ContourSeq,
            CvVoronoiDiagram2D.PointerByReference VoronoiDiagram,
            CvMemStorage VoronoiStorage, int /*CvLeeParameters*/ contour_type/*=CV_LEE_INT*/,
            int contour_orientation/*=-1*/, int attempt_number/*=10*/);
    public static native int cvVoronoiDiagramFromImage(IplImage pImage,
            CvSeq.PointerByReference ContourSeq, CvVoronoiDiagram2D.PointerByReference VoronoiDiagram,
            CvMemStorage VoronoiStorage, int /*CvLeeParameters*/ regularization_method/*=CV_LEE_NON*/,
            float approx_precision/*=CV_LEE_AUTO*/);
    public static native void cvReleaseVoronoiStorage(CvVoronoiDiagram2D VoronoiDiagram,
            CvMemStorage.PointerByReference pVoronoiStorage);


    public static class CvLCMEdge extends CvGraphEdge {
        public static boolean autoSynch = true;
        public CvLCMEdge() { setAutoSynch(autoSynch); }
        public CvLCMEdge(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvLCMEdge.class) read(); }

        private static final String[] fieldOrder = { "chain", "width", "index1", "index2" };
        { setFieldOrder(fieldOrder); }
        public CvSeq.ByReference chain;
        public float width;
        public int index1;
        public int index2;
    }

    public static class CvLCMNode extends CvGraphVtx {
        public static boolean autoSynch = true;
        public CvLCMNode() { setAutoSynch(autoSynch); }
        public CvLCMNode(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvLCMNode.class) read(); }

        private static final String[] fieldOrder = { "contour" };
        { setFieldOrder(fieldOrder); }
        public CvContour.ByReference contour;
    }

    public static native CvGraph cvLinearContorModelFromVoronoiDiagram(
            CvVoronoiDiagram2D VoronoiDiagram, float maxWidth);
    public static native int cvReleaseLinearContorModelStorage(CvGraph.PointerByReference Graph);


    public static native void cvInitPerspectiveTransform(CvSize.ByValue size,
            CvPoint2D32f vertex/*[4]*/, double[] matrix/*[3][3]*/, CvArr rectMap);
    public static void cvInitPerspectiveTransform(CvSize.ByValue size,
            CvPoint2D32f[] vertex/*[4]*/, double[] matrix/*[3][3]*/, CvArr rectMap) {
        for (CvPoint2D32f v : vertex) { v.write(); }
        cvInitPerspectiveTransform(size, vertex[0], matrix, rectMap);
    }
    public static native void cvInitPerspectiveTransform(CvSize.ByValue size,
            CvPoint2D32f vertex/*[4]*/, DoubleBuffer matrix/*[3][3]*/, CvArr rectMap);
    public static void cvInitPerspectiveTransform(CvSize.ByValue size,
            CvPoint2D32f[] vertex/*[4]*/, DoubleBuffer matrix/*[3][3]*/, CvArr rectMap) {
        for (CvPoint2D32f v : vertex) { v.write(); }
        cvInitPerspectiveTransform(size, vertex[0], matrix, rectMap);
    }

//    public static native void cvInitStereoRectification(CvStereoCamera params,
//            CvArr rectMap1, CvArr rectMap2, int do_undistortion);


    public static native void cvMakeScanlines(float[] matrix/*[3][3]*/, CvSize.ByValue img_size,
            int[] scanlines1, int[] scanlines2, int[] lengths1, int[] lengths2, int[] line_count);
    public static native void cvMakeScanlines(FloatBuffer matrix/*[3][3]*/, CvSize.ByValue img_size,
            int[] scanlines1, int[] scanlines2, int[] lengths1, int[] lengths2, int[] line_count);
    public static native void cvPreWarpImage(int line_count, IplImage img, Pointer dst,
            int[] dst_nums, int[] scanlines);
    public static native void cvFindRuns(int line_count, Pointer prewarp1, Pointer prewarp2,
            int[] line_lengths1, int[] line_lengths2, int[] runs1, int[] runs2, int[] num_runs1, int[] num_runs2);
    public static native void cvDynamicCorrespondMulti(int  line_count, int[] first, int[] first_runs,
            int[] second, int[] second_runs, int[] first_corr, int[] second_corr);
    public static native void cvMakeAlphaScanlines(int[] scanlines1, int[] scanlines2, int[] scanlinesA,
            int[] lengths, int line_count, float alpha);
    public static native void cvMorphEpilinesMulti(int line_count, Pointer first_pix, int[] first_num,
            Pointer second_pix, int[] second_num, Pointer dst_pix, int[] dst_num, float alpha,
            int[] first, int[] first_runs, int[] second, int[] second_runs, int[] first_corr, int[] second_corr);
    public static native void cvPostWarpImage(int line_count, Pointer src,
            int[] src_nums, IplImage img, int[] scanlines);

    public static native void cvDeleteMoire(IplImage img);


    public static final int 
            CV_BG_MODEL_FGD        = 0,
            CV_BG_MODEL_MOG        = 1,
            CV_BG_MODEL_FGD_SIMPLE = 2;

    public interface CvReleaseBGStatModel extends Callback {
        void callback(CvBGStatModel.PointerByReference bg_model);
    }

    public static class CvBGStatModel extends Structure {
        public static boolean autoSynch = true;
        public CvBGStatModel() { setAutoSynch(autoSynch); releasable = false; }
        public CvBGStatModel(Pointer m) { super(m); setAutoSynch(autoSynch); releasable = true;
                if (autoSynch && getClass() == CvBGStatModel.class) read(); }

        public static CvBGStatModel create(IplImage first_frame, CvFGDStatModelParams parameters) {
            CvBGStatModel c = cvCreateFGDStatModel(first_frame, parameters);
            if (c != null) {
                c.releasable = true;
            }
            return c;
        }
        public static CvBGStatModel create(IplImage first_frame, CvGaussBGStatModelParams parameters) {
            CvBGStatModel c = cvCreateGaussianBGModel(first_frame, parameters);
            if (c != null) {
                c.releasable = true;
            }
            return c;
        }
        public void release() {
            releasable = false;
            cvReleaseBGStatModel(pointerByReference());
        }
        @Override protected void finalize() {
            if (releasable) {
                release();
            }
        }
        private boolean releasable = false;

        private static final String[] fieldOrder = { "type", "release", "update",
            "background", "foreground", "layers", "layer_count", "storage", "foreground_regions" };
        { setFieldOrder(fieldOrder); }
        public int                         type;
        public CvReleaseBGStatModel        release;
        public static class CvUpdateBGStatModel extends Union {
            public v10.CvUpdateBGStatModel v10;
            public v21.CvUpdateBGStatModel v21;
        }
        public CvUpdateBGStatModel         update;
        public IplImage.ByReference        background;
        public IplImage.ByReference        foreground;
        public IplImage.PointerByReference layers;
        public int                         layer_count;
        public CvMemStorage.ByReference    storage;
        public CvSeq.ByReference           foreground_regions;

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvBGStatModel p) {
                setStructure(p);
            }
            public CvBGStatModel getStructure() {
                return new CvBGStatModel(getValue());
            }
            public void getStructure(CvBGStatModel p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvBGStatModel p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static void cvReleaseBGStatModel(CvBGStatModel.PointerByReference bg_model) {
        if (bg_model != null && bg_model.getStructure() != null && bg_model.getStructure().release != null)
            bg_model.getStructure().release.callback(bg_model);
    }
    public static native void cvRefineForegroundMaskBySegm(CvSeq segments, CvBGStatModel bg_model);
    public static native int cvChangeDetection(IplImage prev_frame, IplImage curr_frame, IplImage change_mask);

    public static final int
            CV_BGFG_FGD_LC             = 128,
            CV_BGFG_FGD_N1C            = 15,
            CV_BGFG_FGD_N2C            = 25,

            CV_BGFG_FGD_LCC            = 64,
            CV_BGFG_FGD_N1CC           = 25,
            CV_BGFG_FGD_N2CC           = 40;

    public static final float
            CV_BGFG_FGD_ALPHA_1        = 0.1f,
            CV_BGFG_FGD_ALPHA_2        = 0.005f,
            CV_BGFG_FGD_ALPHA_3        = 0.1f,
            CV_BGFG_FGD_DELTA          = 2,
            CV_BGFG_FGD_T              = 0.9f,
            CV_BGFG_FGD_MINAREA        = 15.f,
            CV_BGFG_FGD_BG_UPDATE_TRESH= 0.5f;

    public static class CvFGDStatModelParams extends Structure {
        public static boolean autoSynch = true;
        public CvFGDStatModelParams() { setAutoSynch(autoSynch); }
        public CvFGDStatModelParams(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvFGDStatModelParams.class) read(); }

        private static final String[] fieldOrder = { "Lc", "N1c", "N2c",
            "Lcc", "N1cc", "N2cc", "is_obj_without_holes", "perform_morphing",
            "alpha1", "alpha2", "alpha3", "delta", "T", "minArea"};
        { setFieldOrder(fieldOrder); }
        public int    Lc;
        public int    N1c;
        public int    N2c;

        public int    Lcc;
        public int    N1cc;
        public int    N2cc;

        public int    is_obj_without_holes;
        public int    perform_morphing;

        public float  alpha1;
        public float  alpha2;
        public float  alpha3;

        public float  delta;
        public float  T;
        public float  minArea;
    }

    public static class CvBGPixelCStatTable extends Structure {
        public static boolean autoSynch = true;
        public CvBGPixelCStatTable() { setAutoSynch(autoSynch); }
        public CvBGPixelCStatTable(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvBGPixelCStatTable.class) read(); }

        private static final String[] fieldOrder = { "Pv", "Pvb", "v" };
        { setFieldOrder(fieldOrder); }
        public float          Pv, Pvb;
        public byte[]         v = new byte[3];

        public static class ByReference extends CvBGPixelCStatTable implements Structure.ByReference { }
    }

    public static class CvBGPixelCCStatTable extends Structure {
        public static boolean autoSynch = true;
        public CvBGPixelCCStatTable() { setAutoSynch(autoSynch); }
        public CvBGPixelCCStatTable(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvBGPixelCCStatTable.class) read(); }

        private static final String[] fieldOrder = { "Pv", "Pvb", "v" };
        { setFieldOrder(fieldOrder); }
        public float          Pv, Pvb;
        public byte[]         v = new byte[6];

        public static class ByReference extends CvBGPixelCCStatTable implements Structure.ByReference { }
    }

    public static class CvBGPixelStat extends Structure {
        public static boolean autoSynch = true;
        public CvBGPixelStat() { setAutoSynch(autoSynch); }
        public CvBGPixelStat(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvBGPixelStat.class) read(); }

        private static final String[] fieldOrder = { "Pbc", "Pbcc",
            "ctable", "cctable", "is_trained_st_model", "is_trained_dyn_model"};
        { setFieldOrder(fieldOrder); }
        public float                 Pbc;
        public float                 Pbcc;
        public CvBGPixelCStatTable.ByReference  ctable;
        public CvBGPixelCCStatTable.ByReference cctable;
        public byte                  is_trained_st_model;
        public byte                  is_trained_dyn_model;

        public static class ByReference extends CvBGPixelStat implements Structure.ByReference { }
    }

    public static class CvFGDStatModel extends CvBGStatModel {
        public static boolean autoSynch = true;
        public CvFGDStatModel() { setAutoSynch(autoSynch); }
        public CvFGDStatModel(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvFGDStatModel.class) read(); }

        private static final String[] fieldOrder = {
            "pixel_stat", "Ftd", "Fbd", "prev_frame", "params" };
        { setFieldOrder(fieldOrder); }
        CvBGPixelStat.ByReference         pixel_stat;
        IplImage.ByReference              Ftd;
        IplImage.ByReference              Fbd;
        IplImage.ByReference              prev_frame;
        CvFGDStatModelParams              params;
    }

    public static native CvBGStatModel cvCreateFGDStatModel(IplImage first_frame,
            CvFGDStatModelParams parameters/*=null*/);


    public static final int
            CV_BGFG_MOG_MAX_NGAUSSIANS = 500,

            CV_BGFG_MOG_WINDOW_SIZE             = 200,
            CV_BGFG_MOG_NGAUSSIANS              = 5,

            CV_BGFG_MOG_NCOLORS                 = 3;

    public static final double
            CV_BGFG_MOG_BACKGROUND_THRESHOLD    = 0.7,
            CV_BGFG_MOG_STD_THRESHOLD           = 2.5,
            CV_BGFG_MOG_WEIGHT_INIT             = 0.05,
            CV_BGFG_MOG_SIGMA_INIT              = 30,
            CV_BGFG_MOG_MINAREA                 = 15.f;

    public static class CvGaussBGStatModelParams extends Structure {
        public static boolean autoSynch = true;
        public CvGaussBGStatModelParams() { setAutoSynch(autoSynch); }
        public CvGaussBGStatModelParams(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvGaussBGStatModelParams.class) read(); }

        private static final String[] fieldOrder = { "win_size", "n_gauss",
            "bg_threshold", "std_threshold", "minArea", "weight_init", "variance_init" };
        { setFieldOrder(fieldOrder); }
        public int     win_size;
        public int     n_gauss;
        public double  bg_threshold, std_threshold, minArea;
        public double  weight_init, variance_init;
    }

    public static class CvGaussBGValues extends Structure {
        public static boolean autoSynch = true;
        public CvGaussBGValues() { setAutoSynch(autoSynch); }
        public CvGaussBGValues(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvGaussBGValues.class) read(); }

        private static final String[] fieldOrder = {
            "match_sum", "weight", "variance", "mean" };
        { setFieldOrder(fieldOrder); }
        public int      match_sum;
        public double   weight;
        public double[] variance = new double[CV_BGFG_MOG_NCOLORS];
        public double[] mean     = new double[CV_BGFG_MOG_NCOLORS];

        public static class ByReference extends CvGaussBGValues implements Structure.ByReference { }
    }

    public static class CvGaussBGPoint extends Structure {
        public static boolean autoSynch = true;
        public CvGaussBGPoint() { setAutoSynch(autoSynch); }
        public CvGaussBGPoint(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvGaussBGPoint.class) read(); }

        public CvGaussBGValues.ByReference g_values;

        public static class ByReference extends CvGaussBGPoint implements Structure.ByReference { }
    }

    public static class CvGaussBGModel extends CvBGStatModel {
        public static boolean autoSynch = true;
        public CvGaussBGModel() { setAutoSynch(autoSynch); }
        public CvGaussBGModel(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvGaussBGModel.class) read(); }

        private static final String[] fieldOrder = {
            "params", "g_point", "countFrames" };
        { setFieldOrder(fieldOrder); }
        public CvGaussBGStatModelParams   params;
        public CvGaussBGPoint.ByReference g_point;
        public int                        countFrames;
    }

    public static native CvBGStatModel cvCreateGaussianBGModel(IplImage first_frame,
            CvGaussBGStatModelParams parameters/*=null*/);


    public static class CvBGCodeBookElem extends Structure {
        public static boolean autoSynch = true;
        public CvBGCodeBookElem() { setAutoSynch(autoSynch); }
        public CvBGCodeBookElem(Pointer m) { super(m); setAutoSynch(autoSynch); 
                if (autoSynch && getClass() == CvBGCodeBookElem.class) read(); }

        private static final String[] fieldOrder = { "next", "tLastUpdate", "stale",
            "boxMin", "boxMax", "learnMin", "learnMax" };
        { setFieldOrder(fieldOrder); }
        public CvBGCodeBookElem.ByReference next;
        public int tLastUpdate;
        public int stale;
        public byte[] boxMin    = new byte[3];
        public byte[] boxMax    = new byte[3];
        public byte[] learnMin  = new byte[3];
        public byte[] learnMax  = new byte[3];
    
        public static class ByReference extends CvBGCodeBookElem implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvBGCodeBookElem p) {
                setStructure(p);
            }
            public CvBGCodeBookElem getStructure() {
                return new CvBGCodeBookElem(getValue());
            }
            public void getStructure(CvBGCodeBookElem p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvBGCodeBookElem p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static class CvBGCodeBookModel extends Structure {
        public static boolean autoSynch = true;
        public CvBGCodeBookModel() { setAutoSynch(autoSynch); releasable = false; }
        public CvBGCodeBookModel(Pointer m) { super(m); setAutoSynch(autoSynch); releasable = true;
                if (autoSynch && getClass() == CvBGCodeBookModel.class) read(); }

        public static CvBGCodeBookModel create() {
            CvBGCodeBookModel c = cvCreateBGCodeBookModel();
            if (c != null) {
                c.releasable = true;
            }
            return c;
        }
        public void release() {
            releasable = false;
            cvReleaseBGCodeBookModel(pointerByReference());
        }
        @Override protected void finalize() {
            if (releasable) {
                release();
            }
        }
        private boolean releasable = false;

        private static final String[] fieldOrder = { "size", "t", "cbBounds",
            "modMin", "modMax", "cbmap", "storage", "freeList" };
        { setFieldOrder(fieldOrder); }
        public CvSize size;
        public int t;
        public byte[] cbBounds = new byte[3];
        public byte[] modMin   = new byte[3];
        public byte[] modMax   = new byte[3];
        CvBGCodeBookElem.PointerByReference cbmap;
        CvMemStorage.ByReference storage;
        CvBGCodeBookElem.ByReference freeList;

        public static class ByReference extends CvBGCodeBookElem implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvBGCodeBookModel p) {
                setStructure(p);
            }
            public CvBGCodeBookModel getStructure() {
                return new CvBGCodeBookModel(getValue());
            }
            public void getStructure(CvBGCodeBookModel p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvBGCodeBookModel p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static native CvBGCodeBookModel cvCreateBGCodeBookModel();
    public static native void cvReleaseBGCodeBookModel(CvBGCodeBookModel.PointerByReference model);
    public static native void cvBGCodeBookUpdate(CvBGCodeBookModel model, CvArr image,
            CvRect.ByValue roi/*=cvRect(0,0,0,0)*/, CvArr mask/*=null*/);
    public static native int cvBGCodeBookDiff(CvBGCodeBookModel model, CvArr image,
            CvArr fgmask, CvRect.ByValue roi/*=cvRect(0,0,0,0)*/);
    public static native void cvBGCodeBookClearStale(CvBGCodeBookModel model, int staleThresh,
            CvRect.ByValue roi/*=cvRect(0,0,0,0)*/, CvArr mask/*=null*/);
    public static native CvSeq cvSegmentFGMask(CvArr fgmask, int poly1Hull0/*=1*/, float perimScale/*=4.f*/,
            CvMemStorage storage/*=null*/, CvPoint.ByValue offset/*=cvPoint(0,0)*/);
}
