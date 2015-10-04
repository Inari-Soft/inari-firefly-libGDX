package com.inari.firefly.libgdx;

import com.badlogic.gdx.utils.TimeUtils;
import com.inari.firefly.system.FFTimer;

public class GDXTimerImpl extends FFTimer {

    @Override
    protected void tick() {
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
