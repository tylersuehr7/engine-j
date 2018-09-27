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
import android.opengl.GLES20;
import android.support.annotation.RawRes;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A utility used by the engine internals containing various helper methods and constants.
 * @author Tyler Suehr
 */
public final class EngineUtils {
    /* Constants for data types sizes in bytes. */
    public static final int BYTES_PER_DOUBLE = 8;
    public static final int BYTES_PER_FLOAT  = 4;
    public static final int BYTES_PER_SHORT  = 2;

    private static final String TAG = "ENGINE";


    private EngineUtils() {}

    /**
     * Creates a perspective projection matrix.
     *
     * {@link android.opengl.Matrix#frustumM(float[], int, float, float, float, float, float, float)}
     * has a bug that affects some types of projections and perspectiveM(...) was only introduced
     * in Android Ice Cream Sandwich and above.
     *
     * We can simply target Ice Cream Sandwich and above, which is now perfectly fine as we're so
     * much more advanced than the writing time of the excerpt from which this code was taken and
     * we can even target Lollipop and above while still supporting much more than the majority of
     * the market, or we can build our own implementation. At the time of writing, it was more
     * effective to build your own implementation and that is what's demonstrated here.
     *
     * @param m the output matrix (results stored here)
     * @param yFovInDegrees the field of view in degrees on y-axis
     * @param aspect the device aspect ratio
     * @param n the near perspective value
     * @param f the far perspective value
     */
    public static void perspectiveM(float[] m, float yFovInDegrees, float aspect, float n, float f) {
        // Calculate the focal length (distance between the focal point and the small side of
        // the frustum), which will be based on the field of vision across the y-axis.
        final float angleInRadians = (float)(yFovInDegrees * Math.PI / 180.0);
        final float a = (float)(1.0 / Math.tan(angleInRadians / 2.0));

        // Write out the matrix values in column-major order (must be 4x4 matrix)
        m[0] = a / aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;

        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;

        m[8] = 0f;
        m[9] = 0f;
        m[10] = -((f + n) / (f - n));
        m[11] = -1f;

        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2f * f * n) / (f - n));
        m[15] = 0f;
    }

    /**
     * Fully builds a new OpenGL ES program object with accompanying shaders.
     *
     * @param vertexShaderCode the source code of the vertex shader object
     * @param fragmentShaderCode the source code of the fragment shader object
     * @return the pointer to the OpenGL ES program object or 0 if failed
     */
    public static int buildProgram(String vertexShaderCode, String fragmentShaderCode) {
        final int vertexShaderPtr = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        final int fragShaderPtr = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        final int programPtr = linkProgram(vertexShaderPtr, fragShaderPtr);
        validateProgram(programPtr);
        return programPtr;
    }

    /**
     * Validates an OpenGL ES program object.
     *
     * @param programPtr the pointer to the OpenGL ES program object
     * @return true if valid, otherwise false
     */
    public static boolean validateProgram(int programPtr) {
        GLES20.glValidateProgram(programPtr);

        final int[] validStatus = new int[1];
        GLES20.glGetProgramiv(programPtr, GLES20.GL_VALIDATE_STATUS, validStatus, 0);
        Log.d(TAG, "Program validation status: " + GLES20.glGetProgramInfoLog(programPtr));

        return validStatus[0] != 0;
    }

    /**
     * Creates a new OpenGL ES program and links the shader objects to it.
     *
     * @param vertexShaderPtr the pointer to the OpenGL ES vertex shader object
     * @param fragmentShaderPtr the pointer to the OpenGL ES fragment shader object
     * @return the pointer to the OpenGL ES program object or 0 if failed
     */
    public static int linkProgram(int vertexShaderPtr, int fragmentShaderPtr) {
        // Create a new OpenGL ES program object
        final int programPtr = GLES20.glCreateProgram();
        if (programPtr == 0) {
            Log.wtf(TAG, "Could not create new OpenGL ES program object!");
            return 0;
        }

        // Attach the shaders and link program
        GLES20.glAttachShader(programPtr, vertexShaderPtr);
        GLES20.glAttachShader(programPtr, fragmentShaderPtr);
        GLES20.glLinkProgram(programPtr);

        // Determine if program link was successful
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programPtr, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            Log.wtf(TAG, "Program link failed: " + GLES20.glGetProgramInfoLog(programPtr));
            return 0;
        }

        return programPtr;
    }

    /**
     * Compiles the source code for an OpenGL ES shader program.
     *
     * @param type {@link GLES20#GL_VERTEX_SHADER} or {@link GLES20#GL_FRAGMENT_SHADER}
     * @param sourceCode the source code of the shader program
     * @return the pointer to the OpenGL ES shader object or 0 if failed
     */
    public static int compileShader(int type, String sourceCode) {
        // Create a new OpenGL ES shader object
        final int shaderPtr = GLES20.glCreateShader(type);
        if (shaderPtr == 0) {
            Log.wtf(TAG, "Could not create new OpenGL ES shader object!");
            return 0;
        }

        // Attach the source code to the shader and compile it
        GLES20.glShaderSource(shaderPtr, sourceCode);
        GLES20.glCompileShader(shaderPtr);

        // Determine if shader compilation was successful
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shaderPtr, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            Log.wtf(TAG, "Shader compilation failed: " + GLES20.glGetShaderInfoLog(shaderPtr));
            return 0;
        }

        return shaderPtr;
    }

    /**
     * Reads a shader program's source code as a string from an Android resource.
     *
     * @param c {@link Context}
     * @param sourceCodeResId the resource id of the shader program source code
     * @return the shader program source code
     */
    public static String readSourceCode(Context c, @RawRes int sourceCodeResId) {
        final Resources res = c.getResources();
        try (final BufferedReader in = new BufferedReader(
                new InputStreamReader(res.openRawResource(sourceCodeResId)))) {
            final StringBuilder sb = new StringBuilder();
            for (String temp; ((temp = in.readLine()) != null);)
                sb.append(temp);
            return sb.toString();
        } catch (IOException ex) {
            throw new Error("Could not read source code!", ex);
        }
    }
}