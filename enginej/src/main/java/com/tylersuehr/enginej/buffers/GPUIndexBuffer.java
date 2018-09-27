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

package com.tylersuehr.enginej.buffers;

import android.opengl.GLES20;

import com.tylersuehr.enginej.EngineUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * Stores pointer to the GPU buffer that holds the index data.
 * @author Tyler Suehr
 */
public class GPUIndexBuffer extends GPUShaderBuffer {
    private final int mBufferId;


    public GPUIndexBuffer(final short[] indexData) {
        // Create the GPU buffer
        final int[] buffers = new int[1];
        GLES20.glGenBuffers(1, buffers, 0);
        if (buffers[0] == 0) {
            throw new RuntimeException("Could not create new OpenGL ES buffer!");
        }
        mBufferId = buffers[0];

        // Send short VM memory into native memory on CPU
        final ShortBuffer indexBuffer = ByteBuffer
                .allocateDirect(indexData.length * EngineUtils.BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(indexData);
        indexBuffer.position(0);

        // Send native memory from CPU to the GPU buffer
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mBufferId);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,
                indexBuffer.capacity() * EngineUtils.BYTES_PER_SHORT,
                indexBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public void setVertexAttr(int dataOffset, int attrLoc, int compCount, int stride) {
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mBufferId);
        GLES20.glVertexAttribPointer(attrLoc, compCount, GLES20.GL_UNSIGNED_SHORT, false, stride, dataOffset);
        GLES20.glEnableVertexAttribArray(attrLoc);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public int getBufferId() {
        return mBufferId;
    }
}