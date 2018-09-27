package com.tylersuehr.enginej;

/**
 * Represents an in-game object.
 *
 * An in-game object can be rendered with an OpenGL ES shader program, which is why the shader
 * type is needed, and provides callbacks to set vertex data and draw the object.
 *
 * All game objects that will be used/rendered in the game will subclass this.
 *
 * @author Tyler Suehr
 */
public abstract class AbstractGameObject<T extends AbstractShaderProgram> {
    /**
     * Called when vertex data should be sent to the OpenGL ES program.
     * @param program the program to send vertex data to
     */
    public abstract void onBindData(T program);

    /**
     * Called when this object should be rendered.
     */
    public abstract void onDraw();
}