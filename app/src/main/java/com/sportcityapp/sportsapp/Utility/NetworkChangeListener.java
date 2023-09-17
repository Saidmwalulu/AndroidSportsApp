package com.sportcityapp.sportsapp.Utility;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.sportcityapp.sportsapp.R;

public class NetworkChangeListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Common.isConnectedToInternet(context)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View layout_dialog = LayoutInflater.from(context).inflate(R.layout.layout_check_internet, null);
            builder.setView(layout_dialog);

            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);

            dialog.getWindow().setGravity(Gravity.CENTER);

            Button btnRetry = layout_dialog.findViewById(R.id.btnNoInternet);
            btnRetry.setOnClickListener(v -> {
                dialog.dismiss();
                onReceive(context, intent);
            });
        }
    }
}
