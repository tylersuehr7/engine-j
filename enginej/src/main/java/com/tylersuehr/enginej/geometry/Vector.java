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

package com.tylersuehr.enginej.geometry;

/**
 * Represents the standard definition of a 3D vector.
 * @author Tyler Suehr
 */
public class Vector {
    public float x;
    public float y;
    public float z;

    public Vector(float x, float y) {
        this(x, y, 0);
    }

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Calculates the length of a vector using Pythagoras's theorem.
     * @return the length of the vector
     */
    public float length() {
        return (float)Math.sqrt((x * x) + (y * y) + (z * z));
    }

    /**
     * Calculates the cross product of this and another vector, setting the
     * results to this vector's value.
     *
     * @return this vector with results
     */
    public Vector crossProduct(final Vector other) {
        this.x = (y * other.z) - (z * other.y);
        this.y = (z * other.x) - (x * other.z);
        this.z = (x * other.y) - (y * other.x);
        return this;
    }

    /**
     * Calculates the dot product of this and another vector.
     *
     * @param other the other vector to calculate dot product from
     * @return this vector with results
     */
    public float dotProduct(final Vector other) {
        return (x * other.x) + (y * other.y) + (z * other.z);
    }

    /**
     * Sets this vector's values to the value of the provided point.
     *
     * @param point the point to set values from
     * @return this vector with results
     */
    public Vector set(final Point point) {
        this.x = point.x;
        this.y = point.y;
        this.z = point.z;
        return this;
    }

    /**
     * Scales this vector using the provided scale.
     *
     * @param scale the scale factor to scale by
     * @return this vector with results
     */
    public Vector scale(float scale) {
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
        return this;
    }
}