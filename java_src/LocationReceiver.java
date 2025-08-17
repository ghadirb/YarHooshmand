
package org.hooman.yarhooshmand_ai_v9;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("LocationReceiver", "Location action received");
        // Placeholder: you can start a service to check geofences here
    }
}
