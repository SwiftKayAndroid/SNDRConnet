package com.swiftkaydevelopment.testing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.preference.PreferenceManager;

/**
 * Created by Kevin Haines on 9/22/15.
 */
public class ApkSecurityManager {

    String expectedSignature = "Signature@bbec24ed";    //apk signature to test against to ensure app hasnt been recompiled

    Context context;
    SharedPreferences prefs;

    public ApkSecurityManager(Context context) {

        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

    }
    public void verifyApk(){
        if(getSignature().equals(expectedSignature)){

        }else{
            new AlertDialog.Builder(context)
                    .setTitle("Security Warning!")
                    .setMessage("The integrity of this app has been lost. Please uninstall and reinstall from Play Store")
                    .setPositiveButton("I understand", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    })
                    .show();

        }
    }

//get signature for apk
    private String getSignature(){
        StringBuilder sb = new StringBuilder("");
        String s = "";

        try {

            Signature[] sigs = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
            for (Signature sig : sigs) {
                String[] temparray = sig.toString().trim().split(".pm.");
                String temp = temparray[1];
                sb.append(temp);
                System.out.println("Signature: " + temp);

            }
        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        s = sb.toString();
        return s;
    }


}
