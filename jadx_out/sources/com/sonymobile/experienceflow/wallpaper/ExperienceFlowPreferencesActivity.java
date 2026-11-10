package com.sonymobile.experienceflow.wallpaper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.sonymobile.experienceflow2.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public class ExperienceFlowPreferencesActivity extends PreferenceActivity {
    public static final String ENVENT_ACTION = "Select Color";
    public static final String ENVENT_CATEGORY = "Experience Flow Setting Color";
    public static final String ENVENT_VALUE = "1";
    private static final String LICENSE_FILE = "LICENSE.html";
    public static final String LOG_TAG = "ExperienceFlowPreferencesActivity";
    private static final int MENU_ID_LICENSE = 0;
    private static final String MIME_TYPE = "text/html";
    public static final String PREFS_KEY_COLOR = "preferences_color";
    public static final String SHARED_PREFS_NAME = "ExperienceFlowPrefs";
    private static final String UTF8_ENCODING = "utf-8";
    private static int sDirectionalityLeftToRightField;
    private static Method sSetDirectionalityMethod;
    private AlertDialog mLicenseDialog;
    private LoadLicenseTask mLoadLicenseTask;
    private SharedPreferences mPreferences;
    private ProgressDialog mProgressDialog;

    static {
        try {
            sDirectionalityLeftToRightField = ViewGroup.class.getDeclaredField("DIRECTIONALITY_LEFT_TO_RIGHT").getInt(null);
            sSetDirectionalityMethod = ViewGroup.class.getDeclaredMethod("setDirectionality", Integer.TYPE);
        } catch (IllegalAccessException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (NoSuchFieldException e2) {
            Log.w(LOG_TAG, e2.getMessage());
        } catch (NoSuchMethodException e3) {
            Log.w(LOG_TAG, e3.getMessage());
        } catch (SecurityException e4) {
            Log.w(LOG_TAG, e4.getMessage());
        }
    }

    static final void setDirectionalityLeftToRight(ViewGroup vg) {
        if (sSetDirectionalityMethod != null) {
            try {
                sSetDirectionalityMethod.invoke(vg, Integer.valueOf(sDirectionalityLeftToRightField));
            } catch (IllegalAccessException e) {
                Log.e(LOG_TAG, "Failed to invoke setDirectionality: " + e.getMessage());
            } catch (InvocationTargetException e2) {
                Log.e(LOG_TAG, "Failed to invoke setDirectionality: " + e2.getMessage());
            }
        }
    }

    @Override // android.preference.PreferenceActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        this.mPreferences = getSharedPreferences(SHARED_PREFS_NAME, MENU_ID_LICENSE);
        setListAdapter(new ArrayAdapter<ColorInfo>(this, R.layout.settings_item, ColorPicker.getSemcColors(getApplicationContext())) { // from class: com.sonymobile.experienceflow.wallpaper.ExperienceFlowPreferencesActivity.1
            @Override // android.widget.ArrayAdapter, android.widget.Adapter
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    LayoutInflater vi = (LayoutInflater) ExperienceFlowPreferencesActivity.this.getSystemService("layout_inflater");
                    view = vi.inflate(R.layout.settings_item, (ViewGroup) null);
                    ExperienceFlowPreferencesActivity.setDirectionalityLeftToRight((ViewGroup) view);
                }
                ColorInfo color = getItem(position);
                ImageView imageView = (ImageView) view.findViewById(R.id.itemIcon);
                if (imageView != null) {
                    imageView.setImageDrawable(ExperienceFlowPreferencesActivity.this.getResources().getDrawable(color.thumbnailID));
                }
                TextView textView = (TextView) view.findViewById(R.id.itemText);
                if (textView != null) {
                    textView.setText(ExperienceFlowPreferencesActivity.this.getText(color.nameID));
                }
                return view;
            }
        });
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.sonymobile.experienceflow.wallpaper.ExperienceFlowPreferencesActivity.2
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ColorInfo color = (ColorInfo) parent.getItemAtPosition(position);
                if (color != null) {
                    ExperienceFlowPreferencesActivity.this.mPreferences.edit().putInt(ExperienceFlowPreferencesActivity.PREFS_KEY_COLOR, color.index).apply();
                }
                ExperienceFlowPreferencesActivity.this.finish();
            }
        });
    }

    @Override // android.preference.PreferenceActivity, android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ID_LICENSE /* 0 */:
                if (this.mLoadLicenseTask == null) {
                    this.mLoadLicenseTask = new LoadLicenseTask();
                    CharSequence title = getText(R.string.license_dialog_title);
                    CharSequence msg = getText(R.string.license_loading);
                    this.mProgressDialog = ProgressDialog.show(this, title, msg, true, false);
                    this.mProgressDialog.setProgressStyle(MENU_ID_LICENSE);
                    this.mLoadLicenseTask.execute((Void) null);
                    break;
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(MENU_ID_LICENSE, MENU_ID_LICENSE, MENU_ID_LICENSE, R.string.license_dialog_title).setShowAsAction(4);
        return true;
    }

    @Override // android.preference.PreferenceActivity, android.app.ListActivity, android.app.Activity
    protected void onDestroy() {
        if (this.mLoadLicenseTask != null && this.mLoadLicenseTask.getStatus() != AsyncTask.Status.FINISHED) {
            this.mLoadLicenseTask.cancel(true);
        }
        if (this.mProgressDialog != null) {
            if (this.mProgressDialog.isShowing()) {
                this.mProgressDialog.dismiss();
            }
            this.mProgressDialog = null;
        }
        if (this.mLicenseDialog != null) {
            if (this.mLicenseDialog.isShowing()) {
                this.mLicenseDialog.dismiss();
            }
            this.mLicenseDialog = null;
        }
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showLicenseDialog(String license) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.license_dialog_title);
        WebView webView = new WebView(this);
        builder.setView(webView);
        this.mLicenseDialog = builder.create();
        this.mLicenseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.sonymobile.experienceflow.wallpaper.ExperienceFlowPreferencesActivity.3
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialog) {
                ExperienceFlowPreferencesActivity.this.mLoadLicenseTask = null;
            }
        });
        webView.setWebViewClient(new WebViewClient() { // from class: com.sonymobile.experienceflow.wallpaper.ExperienceFlowPreferencesActivity.4
            @Override // android.webkit.WebViewClient
            public void onPageFinished(WebView view, String url) {
                if (ExperienceFlowPreferencesActivity.this.mProgressDialog != null) {
                    if (ExperienceFlowPreferencesActivity.this.mProgressDialog.isShowing()) {
                        ExperienceFlowPreferencesActivity.this.mProgressDialog.dismiss();
                    }
                    ExperienceFlowPreferencesActivity.this.mProgressDialog = null;
                    if (ExperienceFlowPreferencesActivity.this.mLicenseDialog != null) {
                        ExperienceFlowPreferencesActivity.this.mLicenseDialog.show();
                    }
                }
            }
        });
        webView.loadDataWithBaseURL(null, license, MIME_TYPE, UTF8_ENCODING, null);
    }

    /* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
    private class LoadLicenseTask extends AsyncTask<Void, Void, String> {
        private LoadLicenseTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public String doInBackground(Void... params) {
            BufferedReader reader;
            BufferedReader reader2 = null;
            StringBuilder builder = new StringBuilder();
            try {
                try {
                    reader = new BufferedReader(new InputStreamReader(ExperienceFlowPreferencesActivity.this.getAssets().open(ExperienceFlowPreferencesActivity.LICENSE_FILE), ExperienceFlowPreferencesActivity.UTF8_ENCODING));
                } catch (IOException e) {
                    e = e;
                }
            } catch (Throwable th) {
                th = th;
            }
            try {
                char[] data = new char[1024];
                while (true) {
                    int read = reader.read(data);
                    if (read <= 0) {
                        break;
                    }
                    builder.append(data, ExperienceFlowPreferencesActivity.MENU_ID_LICENSE, read);
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e2) {
                        Log.w(ExperienceFlowPreferencesActivity.LOG_TAG, e2);
                    }
                }
            } catch (IOException e3) {
                e = e3;
                reader2 = reader;
                Log.w(ExperienceFlowPreferencesActivity.LOG_TAG, e);
                if (reader2 != null) {
                    try {
                        reader2.close();
                    } catch (IOException e4) {
                        Log.w(ExperienceFlowPreferencesActivity.LOG_TAG, e4);
                    }
                }
                return builder.toString();
            } catch (Throwable th2) {
                th = th2;
                reader2 = reader;
                if (reader2 != null) {
                    try {
                        reader2.close();
                    } catch (IOException e5) {
                        Log.w(ExperienceFlowPreferencesActivity.LOG_TAG, e5);
                    }
                }
                throw th;
            }
            return builder.toString();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(String result) {
            ExperienceFlowPreferencesActivity.this.showLicenseDialog(result);
        }
    }
}
