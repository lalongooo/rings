package com.lalongooo;

import android.content.res.Resources;

class Utils {

    /**
     * Convert the specified dimen value to pixels, used for fonts, not views.
     *
     * @param resources An instance of Android {@link Resources}
     * @param sp        A dimen value specified in sp
     * @return A float value corresponding to the specified sp converted to pixels
     */
    static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
