package com.tylersuehr.enginej;

import android.content.Context;
import android.opengl.GLES20;
import android.support.annotation.RawRes;

/**
 * Represents an OpenGL ES shader program.
 *
 * Any shaders written in GLSL (OpenGL's shading language) should be wrapped with this object-
 * oriented version. This object will be used to tell OpenGL ES which shaders to use.
 *
 * Subclass this object for each conjunction of vertex and fragment shader programs made.
 *
 * @author Tyler Suehr
 */
public abstract class AbstractShaderProgram {
    /* Stores pointer to the OpenGL ES program this represents */
    protected final int mCurrentProgram;


    /** Constructs with both vertex and fragment shaders. */
    public AbstractShaderProgram(Context c, @RawRes int vertexShaderResId, @RawRes int fragShaderResId) {
        mCurrentProgram = EngineUtils.buildProgram(
                EngineUtils.readSourceCode(c, vertexShaderResId),
                EngineUtils.readSourceCode(c, fragShaderResId)
        );
    }

    /**
     * Marks this program as the current OpenGL ES shader program.
     */
    public final void useProgram() {
        GLES20.glUseProgram(mCurrentProgram);
    }
}