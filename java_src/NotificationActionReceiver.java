
package org.hooman.yarhooshmand_ai_v9;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getStringExtra("action");
            String rid = intent.getStringExtra("reminder_id");
            Log.i("NotifAction", "Action="+action+" rid="+rid);
            // Map the action and call into the Python service if required
        } catch (Exception e) {
            Log.e("NotifAction", "Error", e);
        }
    }
}
