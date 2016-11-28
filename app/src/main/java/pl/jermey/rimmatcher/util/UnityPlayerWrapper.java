package pl.jermey.rimmatcher.util;

import android.content.ContextWrapper;

import com.unity3d.player.UnityPlayer;

/**
 * Created by Jermey on 28.11.2016.
 */

public class UnityPlayerWrapper extends UnityPlayer {

    public UnityPlayerWrapper(final ContextWrapper contextWrapper) {
        super(contextWrapper);
    }

    @Override
    protected void kill() {
        //Do nothing
    }

}
