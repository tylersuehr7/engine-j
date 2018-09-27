/*
 * MIT License
 *
 * Copyright (c) Tyler Suehr 2018
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tylersuehr.enginej;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.support.annotation.DrawableRes;
import android.util.Log;

/**
 * A utility for helping with common texture methods and computations.
 * @author Tyler Suehr
 */
public final class TextureUtils {
    private static final String TAG = "TEXTURE";


    private TextureUtils() {}

    public static Bitmap loadLargeBitmap(Context c, int sampleSizePow2, @DrawableRes int drawRes) {
        final BitmapFactory.Options ops = new BitmapFactory.Options();
        ops.inScaled = false;
        ops.inSampleSize = sampleSizePow2;

        final Bitmap bmp = BitmapFactory.decodeResource(c.getResources(), drawRes, ops);
        if (bmp == null) {
            throw new RuntimeException("Bitmap resource could not be decoded!");
        }

        return bmp;
    }

    public static Bitmap loadBitmap(Context c, @DrawableRes int drawRes) {
        final BitmapFactory.Options ops = new BitmapFactory.Options();
        ops.inScaled = false;

        final Bitmap bmp = BitmapFactory.decodeResource(c.getResources(), drawRes, ops);
        if (bmp == null) {
            throw new RuntimeException("Bitmap resource could not be decoded!");
        }

        return bmp;
    }

    /**
     * Creates an OpenGL ES cube map texture object, transfers all Android Bitmap data provided
     * into the GPU texture cube buffer, and returns the pointer to the texture object.
     *
     * @param c {@link Context}
     * @param textureResIds the resource ids of the cube textures to be loaded
     * @return the pointer to the OpenGL ES texture object or 0 if failed
     */
    public static int loadCubeMap(Context c, int[] textureResIds) {
        // Create a new OpenGL ES texture object
        final int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        if (textures[0] == 0) {
            Log.wtf(TAG, "Could not generate a new texture object!");
            return 0;
        }

        // OpenGL ES doesn't understand typical image encoding like PNG or JPG, so we
        // need to use Android APIs to get the raw image data itself.
        final BitmapFactory.Options ops = new BitmapFactory.Options();
        ops.inScaled = false;

        final Resources res = c.getResources();
        final Bitmap[] cubeBitmaps = new Bitmap[6];
        for (int i = 0; i < cubeBitmaps.length; i++) {
            cubeBitmaps[i] = BitmapFactory.decodeResource(res, textureResIds[i], ops);
            if (cubeBitmaps[i] == null) {
                Log.wtf(TAG, "Bitmap resource could not be decoded!");
                GLES20.glDeleteTextures(1, textures, 0);
                return 0;
            }
        }

        // Apply texture calls to the above created texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textures[0]);

        // Set the texture filtering for both minification and magnification as bilinear
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // Associate each image with appropriate face of cube
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmaps[0], 0);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmaps[1], 0);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmaps[2], 0);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmaps[3], 0);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmaps[4], 0);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmaps[5], 0);

        // Unbind the texture and recycle the bitmaps
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, 0);
        for (Bitmap bmp : cubeBitmaps) {
            bmp.recycle();
        }

        return textures[0];
    }

    /**
     * Creates a new OpenGL ES texture object, transfers Android Bitmap data into the GPU texture
     * buffer, and returns the pointer to the texture object.
     *
     * @param c {@link Context}
     * @param textureResId the resource id of the texture to be loaded
     * @param wrapSV true if texture should not be repeated
     * @return the pointer to the OpenGL ES texture object or 0 if failed
     */
    public static int loadTexture(Context c, @DrawableRes int textureResId, boolean wrapSV) {
        // Create a new OpenGL ES texture object
        final int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        if (textures[0] == 0) {
            Log.wtf(TAG, "Could not create new OpenGL ES texture object!");
            return 0;
        }

        // OpenGL ES doesn't understand typical image encoding like PNG or JPG, so we
        // need to use Android APIs to get the raw image data itself.
        final BitmapFactory.Options ops = new BitmapFactory.Options();
        ops.inScaled = false;

        final Bitmap bmp = BitmapFactory.decodeResource(c.getResources(), textureResId, ops);
        if (bmp == null) {
            Log.wtf(TAG, "Bitmap resource could not be decoded!");
            GLES20.glDeleteTextures(1, textures, 0);
            return 0;
        }

        // Apply texture calls to the object just created
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

        // Set the texture filtering for minification to trilinear filtering and
        // magnification to bilinear filtering.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        if (wrapSV) {
            // Prevents duplicating texture
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }

        // Load the bitmap data into OpenGL ES and recycle the bitmap
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        bmp.recycle();

        // Tell OpenGL ES to generate all of the necessary levels
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        // Unbind for the texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return textures[0];
    }
}