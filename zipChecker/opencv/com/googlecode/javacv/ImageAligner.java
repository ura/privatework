/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.javacv;

import com.googlecode.javacv.ImageTransformer.Parameters;

import static com.googlecode.javacv.jna.cxcore.*;

/**
 *
 * @author Samuel Audet
 */
public interface ImageAligner {

    public static class Settings extends BaseSettings implements Cloneable {
        public Settings() { }
        public Settings(Settings s) {
            pyramidLevels = s.pyramidLevels;
            gammaTgamma   = s.gammaTgamma;
            tikhonovAlpha = s.tikhonovAlpha;
            constrained   = s.constrained;
        }

        int pyramidLevels    = 5;
        CvMat gammaTgamma    = null;
        double tikhonovAlpha = 0;
        boolean constrained  = false;

        public int getPyramidLevels() {
            return pyramidLevels;
        }
        public void setPyramidLevels(int pyramidLevels) {
            this.pyramidLevels = pyramidLevels;
        }

        public CvMat getGammaTgamma() {
            return gammaTgamma;
        }
        public void setGammaTgamma(CvMat gammaTgamma) {
            this.gammaTgamma = gammaTgamma;
        }

        public double getTikhonovAlpha() {
            return tikhonovAlpha;
        }
        public void setTikhonovAlpha(double tikhonovAlpha) {
            this.tikhonovAlpha = tikhonovAlpha;
        }

//        public boolean isConstrained() {
//            return constrained;
//        }
//        public void setConstrained(boolean constrained) {
//            this.constrained = constrained;
//        }

        @Override public Settings clone() {
            return new Settings(this);
        }
    }
    Settings getSettings();
    void setSettings(Settings settings);

    IplImage getTemplateImage();
    void setTemplateImage(IplImage template0, double[] roiPts);

    IplImage getTargetImage();
    void setTargetImage(IplImage target0);

    int getPyramidLevel();
    void setPyramidLevel(int pyramidLevel);

    Parameters getParameters();
    void setParameters(Parameters parameters);

    IplImage getTransformedImage();
    IplImage getResidualImage();
    IplImage getRoiMaskImage();
    double getRMSE();
    CvRect getROI();

    boolean iterate(double[] delta);
}
