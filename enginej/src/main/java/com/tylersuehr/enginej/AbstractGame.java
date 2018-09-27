package com.tylersuehr.enginej;

import android.content.Context;

/**
 * Defines a game that's able to be rendered with OpenGL ES.
 *
 * Basically, this is a wrapper for the {@link android.opengl.GLSurfaceView.Renderer} interface.
 * Please note that this was design to ONLY support OpenGL ES 2.0.
 *
 * All games must subclass this class.
 *
 * @author Tyler Suehr
 */
public abstract class AbstractGame {
    /**
     * Called when the game has been created.
     *
     * This can be used to setup and initialize all the game assets to be used like
     * various textures, sounds, and data.
     *
     * @param c {@link Context}
     */
    public abstract void onGameCreated(Context c);

    /**
     * Called when the game surface has been changed.
     *
     * This can be used to set the OpenGL ES viewport and setup any projections that
     * you may want to use to support 2D and 3D graphics.
     *
     * @param width the changed width of the surface
     * @param height the changed height of the surface
     */
    public abstract void onGameSurfaceChanged(int width, int height);

    /**
     * Called when a single frame in the game should be rendered.
     */
    public abstract void onGameDrawFrame();
}