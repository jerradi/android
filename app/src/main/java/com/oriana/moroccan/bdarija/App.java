package com.oriana.moroccan.bdarija;

/**
 * Created by moroccan on 7/3/2017.
 */

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(mailTo = "hanksone420+fttberror@gmail.com", formKey = "", mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}
