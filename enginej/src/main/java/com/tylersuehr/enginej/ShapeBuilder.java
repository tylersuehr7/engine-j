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

import android.opengl.GLES20;

import com.tylersuehr.enginej.geometry.Circle;
import com.tylersuehr.enginej.geometry.Cylinder;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder for building game objects using simple shapes to form complex shapes.
 * @author Tyler Suehr
 */
public class ShapeBuilder {
    private static final int FLOATS_PER_VERTEX = 3;
    private final List<ShapeDrawCommand> mDrawList = new ArrayList<>();
    private final float[] mVertexData;
    private int mOffset = 0;


    /** Constructs with the number of vertices needed for the shade. */
    private ShapeBuilder(int sizeInVertices) {
        mVertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

    // TODO - add builder methods to generate non-standalone complex geometric shapes

    /**
     * Creates a circle that can be rendered with OpenGL ES. Uses a triangle fan.
     *
     * @param circle the circle dimensions to create
     * @param numPoints the number of points around the circle
     * @return shape data that can be rendered by OpenGL ES
     */
    public static ShapeData createCircle(Circle circle, int numPoints) {
        final List<ShapeDrawCommand> drawList = new ArrayList<>();
        int offset = 0;

        // Figure out how many vertices we need to represent this circle... equal to
        // one circle with num points around it.
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);
        final float[] vertexData = new float[numVertices * FLOATS_PER_VERTEX];

        // Center point of fan
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;

        // Fan around center point. <= is used because we want to generate the point at
        // the starting angle twice to complete the fan.
        for (int i = 0; i <= numPoints; i++) {
            final float angleInRadians = ((float)i / (float)numPoints) * ((float)Math.PI * 2f);
            vertexData[offset++] = circle.center.x + circle.radius * (float)Math.cos(angleInRadians);
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] = circle.center.z + circle.radius * (float)Math.sin(angleInRadians);
        }

        drawList.add(new ShapeDrawCommand() {
            @Override
            public void draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });

        return new ShapeData(vertexData, drawList);
    }

    /**
     * Creates a cylinder that can be rendered with OpenGL ES. Uses a triangle fan.
     *
     * @param cylinder the cylinder dimensions to create
     * @param numPoints the number of points around the cylinder
     * @return shape data that can be rendered by OpenGL ES
     */
    public static ShapeData createCylinder(Cylinder cylinder, int numPoints) {
        final List<ShapeDrawCommand> drawList = new ArrayList<>();
        int offset = 0;

        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
        final float[] vertexData = new float[numVertices * FLOATS_PER_VERTEX];

        final float yStart = cylinder.center.y - (cylinder.height / 2f);
        final float yEnd = cylinder.center.y + (cylinder.height / 2f);

        for (int i = 0; i <= numPoints; i++) {
            final float angleInRadians = ((float)i / (float)numPoints) * ((float)Math.PI * 2f);
            final float xPos = cylinder.center.x + cylinder.radius * (float)Math.cos(angleInRadians);
            final float zPos = cylinder.center.z + cylinder.radius * (float)Math.sin(angleInRadians);

            vertexData[offset++] = xPos;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPos;

            vertexData[offset++] = xPos;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPos;
        }

        drawList.add(new ShapeDrawCommand() {
            @Override
            public void draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });

        return new ShapeData(vertexData, drawList);
    }

    public static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1);
    }

    public static int sizeOfOpenCylinderInVertices(int numPoints) {
        return (numPoints + 1) * 2;
    }


    /**
     * Structure holding the drawing information for a complex shape.
     */
    public static class ShapeData {
        public final float[] vertexData;
        public final List<ShapeDrawCommand> drawList;

        ShapeData(float[] vertexData, List<ShapeDrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }

    /**
     * Defines a drawing command for a shape.
     */
    public interface ShapeDrawCommand {
        void draw();
    }
}