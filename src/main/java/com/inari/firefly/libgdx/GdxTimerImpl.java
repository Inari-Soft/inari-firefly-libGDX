package com.inari.firefly.libgdx;

import com.badlogic.gdx.utils.TimeUtils;
import com.inari.firefly.system.external.FFTimer;

public final class GdxTimerImpl extends FFTimer {

    @Override
    public final void tick() {
        if ( lastUpdateTime == 0 ) {
            lastUpdateTime = TimeUtils.millis();
        } else {
            long currentTime = TimeUtils.millis();
            time += timeElapsed;
            timeElapsed = currentTime - lastUpdateTime;
            lastUpdateTime = currentTime;
        }
    }

}
