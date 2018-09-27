package com.tylersuehr.enginej;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Implementation of {@link GLSurfaceView.Renderer} which invokes the appropriate wrapping methods
 * on a {@link AbstractGame} to be rendered.
 *
 * @author Tyler Suehr
 */
public class AbstractGameRenderer implements GLSurfaceView.Renderer {
    /* Stores reference to the Android context */
    private final Context mContext;
    /* Stores reference to the game being rendered */
    private final AbstractGame mGame;


    public AbstractGameRenderer(final Context c, final AbstractGame game) {
        mContext = c;
        mGame = game;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mGame.onGameCreated(mContext);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mGame.onGameSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mGame.onGameDrawFrame();
    }
}