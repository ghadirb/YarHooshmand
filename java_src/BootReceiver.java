
package org.hooman.yarhooshmand_ai_v9;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            try {
                Intent svc = new Intent();
                svc.setClassName("org.hooman.yarhooshmand_ai_v9",
                        "org.hooman.yarhooshmand_ai_v9.ServiceRemindersvc");
                context.startForegroundService(svc);
            } catch (Exception e) {
                Log.e("BootReceiver", "Failed to start service after boot", e);
            }
        }
    }
}
