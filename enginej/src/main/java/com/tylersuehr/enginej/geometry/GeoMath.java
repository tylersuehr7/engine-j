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
 * A utility providing mathematics between various geometric shapes.
 * @author Tyler Suehr
 */
public final class GeoMath {
    private GeoMath() {}

    /**
     * Calculates a vector between two points, returning that vector.
     *
     * @param from the point from which the vector starts
     * @param to the point at which the vector ends
     * @return the vector between the points
     */
    public static Vector vectorBetween(Point from, Point to) {
        return new Vector(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z
        );
    }

    /**
     * Calculates the distance between a point and ray.
     *
     * @param point the point
     * @param ray the ray
     * @return distance between the point and the ray
     */
    public static float distanceBetween(Point point, Ray ray) {
        final Vector p1ToPoint = vectorBetween(ray.point, point);
        final Vector p2ToPoint = vectorBetween(ray.point.translate(ray.vector), point);

        // The length of the cross product gives the area of an imaginary parallelogram having
        // the two vectors as sides. A parallelogram can be thought of as consisting of two
        // triangles, so this is the same as twice the area of the triangle defined by the two
        // vectors. See http://en.wikipedia.org/wiki/Cross_product#Geometric_meaning
        final float areaOfTriangleTimeTwo = p1ToPoint.crossProduct(p2ToPoint).length();
        final float lengthOfBase = ray.vector.length();

        // The area of a triangle is also equal to (base * height) / 2. In other words, the
        // height is equal to (area * 2) / base. The height of this triangle is the distance
        // from the point to the ray.
        return areaOfTriangleTimeTwo / lengthOfBase;
    }

    /**
     * Determines the intersection between a sphere and a ray; used for collisions.
     *
     * To perform intersection we need to follow these steps:
     * (1) Figure out the distance between the sphere and the ray. Do this by first defining
     *     two points on the ray: the initial point and the end point, found by adding the ray's
     *     vector to the initial point. We then create an imaginary triangle between these points
     *     and the center of the sphere, and then we get the distance by calculating the height of
     *     that triangle.
     *
     * (2) We then compare that distance to the sphere's radius. If that distance is smaller than
     *     the radius, then the ray intersects the sphere.
     *
     * @param sphere the sphere to check intersection on
     * @param ray the ray to check intersection on
     * @return true if intersection between sphere and ray
     */
    public static boolean intersects(Sphere sphere, Ray ray) {
        return distanceBetween(sphere.center, ray) < sphere.radius;
    }

    /**
     * Calculates the point of intersection between a ray and a plane.
     *
     * To calculate the intersection point between a ray and a plane, we need to figure out
     * how much we need to scale the ray's vector until it touches the plane exactly; this
     * is the 'scaleFactor'. We can then translate the ray's point by this scaled vector to
     * find the intersection point.
     *
     * To calculate the scaling factor, we first create a vector between the ray's starting
     * point and a point on the plane. Calculate the dot product between that vector and the
     * plane's normal. The dot product between two vectors is directly related to (though
     * usually not equivalent to) the cosine between those two vectors.
     *
     * See: http://en.wikipedia.org/wiki/Line-plane_intersection
     *
     * @param ray the ray to check intersection point
     * @param plane the plane to check intersection point
     * @return a point of intersection between the provided ray and plane
     */
    public static Point intersectionPoint(Ray ray, Plane plane) {
        final Vector rayToPlaneVector = vectorBetween(ray.point, plane.point);
        final float scaleFactor = rayToPlaneVector.dotProduct(plane.normal) / ray.vector.dotProduct(plane.normal);
        return ray.point.translate(ray.vector.scale(scaleFactor)); // the intersection point
    }
}