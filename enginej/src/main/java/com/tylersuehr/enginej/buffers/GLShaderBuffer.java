package com.tylersuehr.enginej.buffers;

/**
 * Defines a buffer used to store memory for use with an OpenGL ES vertex shader.
 * @author Tyler Suehr
 */
public interface GLShaderBuffer {
    /**
     * Associates vertex data with an attribute variable in an OpenGL ES shader.
     *
     * @param dataOffset the start to insert data into the buffer
     * @param attrLoc the pointer to the attribute variable
     * @param compCount the number of components in a vertex
     * @param stride the stride of a vertex in bytes
     */
    void setVertexAttr(int dataOffset, int attrLoc, int compCount, int stride);
}