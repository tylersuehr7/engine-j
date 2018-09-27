package com.tylersuehr.enginej.buffers;

import android.opengl.GLES20;

import com.tylersuehr.enginej.EngineUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Stores native floating-point memory using typical CPU memory allocation.
 * @author Tyler Suehr
 */
public class CPUVertexBuffer implements GLShaderBuffer {
    private final FloatBuffer mVertexBuffer;


    public CPUVertexBuffer(final float[] vertexData) {
        mVertexBuffer = ByteBuffer
                .allocateDirect(vertexData.length * EngineUtils.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        mVertexBuffer.position(0);
    }

    @Override
    public void setVertexAttr(int dataOffset, int attrLoc, int compCount, int stride) {
        mVertexBuffer.position(dataOffset);
        GLES20.glVertexAttribPointer(attrLoc, compCount, GLES20.GL_FLOAT, false, stride, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(attrLoc);
        mVertexBuffer.position(0);
    }

    public void updateVertexData(float[] vertexData, int start, int count) {
        mVertexBuffer.position(start);
        mVertexBuffer.put(start, count);
        mVertexBuffer.position(0);
    }

    public FloatBuffer getVertexBuffer() {
        return mVertexBuffer;
    }
}