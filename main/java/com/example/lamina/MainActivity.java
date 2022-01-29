package com.example.lamina;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraManager;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

interface WebInterfaceHelper {
    void closeBuiltInPage(String url);
    String getCurrentUrl();
    boolean isBuiltInFrameVisible();
    void executePermissionChangeEvent();
}
interface ToolBoxInterfaceHelper {
    void loadUrlFromToolBox(String url);
}
interface SettingInterfaceHelper {
    void setDefaultSearchEngine(String url);
    String getDefaultSearchEngine();
}

class HistoryInterface {
    WebInterfaceHelper _activity;
    HistoryDB historyDB;

    HistoryInterface(Activity _activity){
        this._activity = (WebInterfaceHelper) _activity;
        historyDB = new HistoryDB(_activity);
    }

    @JavascriptInterface
    public String getHistory(){
        return historyDB.getHistory();
    }

    @JavascriptInterface
    public void clearHistory(String id){
        historyDB.clearHistory(id);
    }

    @JavascriptInterface
    public void clearHistory(){
        historyDB.clearHistory("");
    }

    @JavascriptInterface
    public void loadPage(String url){
        _activity.closeBuiltInPage(url);
    }

    @JavascriptInterface
    public  void closeHistoryPage(){
        _activity.closeBuiltInPage("");
    }
}

class ToolBoxInterface {
    ToolBoxInterfaceHelper _activity;
    public ToolBoxInterface(Activity _activity){
        this._activity = (ToolBoxInterfaceHelper) _activity;
    }

    @JavascriptInterface
    public void loadUrl(String url){
        _activity.loadUrlFromToolBox(url);
    }
}

class SettingInterface {
    SettingInterfaceHelper _activity;
    WebInterfaceHelper _activity1;
    SettingDB settingDB;
    public SettingInterface(Activity _activity){
        this._activity = (SettingInterfaceHelper) _activity;
        this._activity1 = (WebInterfaceHelper) _activity;
        settingDB = new SettingDB(_activity);
    }

    @JavascriptInterface
    public void setDefaultSearchEngine(String url){
        _activity.setDefaultSearchEngine(url);
    }

    @JavascriptInterface
    public String getDefaultSearchEngine(){
        return _activity.getDefaultSearchEngine();
    }

    @JavascriptInterface
    public void loadPage(String url){
        _activity1.closeBuiltInPage(url);
    }

    @JavascriptInterface
    public void setDarkTheme(String value){
        settingDB.setDarkThemeSetting(Integer.parseInt(value));
    }

    @JavascriptInterface
    public  String  isDarkTheme(){
        return settingDB.isDarkTheme();
    }
}

class PermissionInterface {
    PermissionDB permissionDB;
    WebInterfaceHelper _activity;
    public  PermissionInterface(Activity _activity){
        permissionDB = new PermissionDB(_activity);
        this._activity = (WebInterfaceHelper)_activity;
    }

    @JavascriptInterface
    public  String getPermission(){
        return permissionDB.getHostPermission(_activity.getCurrentUrl());
    }

    @JavascriptInterface
    public void setPermission(String type, int value){
        permissionDB.updateHostPermission(_activity.getCurrentUrl(), type, value);
    }

    @JavascriptInterface
    public void loadPage(String url){
        _activity.closeBuiltInPage(url);
    }

}

public class MainActivity extends AppCompatActivity implements WebInterfaceHelper, ToolBoxInterfaceHelper, SettingInterfaceHelper {

    public WebView frame, builtInFrame, visibleFrame, toolBoxFrame;

    public String currentUrl = "";
    StringBuffer defaultSearchEngine;

    HistoryDB historyDB;
    SettingDB settingDB;
    PermissionDB permissionDB;

    boolean isDarkTheme = true;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        historyDB = new HistoryDB(this);
        settingDB = new SettingDB(this);
        permissionDB = new PermissionDB(this);

        handleDefaults();

        toolBoxFrame = findViewById(R.id.toolBoxFrame);
        frame = findViewById(R.id.frame);
        builtInFrame = findViewById(R.id.builtInUrlFrame);

        visibleFrame = frame;

        toolBoxFrame.clearCache(true);
        toolBoxFrame.setWebViewClient(new WebViewClient());
        toolBoxFrame.getSettings().setJavaScriptEnabled(true);
        toolBoxFrame.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        toolBoxFrame.getSettings().setLoadsImagesAutomatically(true);
        toolBoxFrame.getSettings().setLoadWithOverviewMode(true);
        toolBoxFrame.setWebChromeClient(new WebChromeClient());
        toolBoxFrame.addJavascriptInterface(new ToolBoxInterface(this),"_lamina");
        toolBoxFrame.loadUrl("file:///android_asset/toolbox.html");

        builtInFrame.clearCache(true);
        builtInFrame.setWebViewClient(new WebViewClient());
        builtInFrame.getSettings().setJavaScriptEnabled(true);
        builtInFrame.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        builtInFrame.getSettings().setLoadsImagesAutomatically(true);
        builtInFrame.getSettings().setLoadWithOverviewMode(true);
        builtInFrame.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress == 100){
                    if(settingDB.isDarkTheme().equals("1")) {
                        builtInFrame.loadUrl("javascript:(()=>{document.body.classList.add(\"dark-theme\")})();");
                    }
                }
            }
        });

        frame.clearCache(true);
        frame.setWebViewClient(new MyBrowser(this));
        frame.getSettings().setJavaScriptEnabled(true);
        frame.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        frame.getSettings().setLoadsImagesAutomatically(true);
        frame.getSettings().setLoadWithOverviewMode(true);
        frame.getSettings().setDomStorageEnabled(true);
        frame.getSettings().setDatabaseEnabled(true);
        frame.addJavascriptInterface(new WebAppInterface(this),"lamina");
        frame.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress){
                String url = view.getUrl().toString();
                if(!url.startsWith("file:///")) {
                    if (progress == 100) {
                        if (!currentUrl.equals(url)) {
                            settingDB.setLastVisitedUrl(url);
                            historyDB.addToHistory(url, view.getTitle());
                            currentUrl = url;
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    toolBoxFrame.loadUrl("javascript:setUrl(\"" + url + "\")");
                                    toolBoxFrame.loadUrl("javascript:setLoadingProgress(\"" + progress + "\")");
                                }
                            });
                        }
                    } else {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toolBoxFrame.loadUrl("javascript:setLoadingProgress(\"" + progress + "\")");
                            }
                        });
                    }
                }
                super.onProgressChanged(view, progress);
            }
        });
        if(currentUrl.length() == 0) {
            frame.loadUrl("https://" + defaultSearchEngine + ".com/");
        } else {
            frame.loadUrl(currentUrl);
            currentUrl = "";
        }
    }

    protected  void handleDefaults(){
        String defaultSearchEngineFromDB = settingDB.getDefaultSearchEngine();
        String lastVisitedUrlFromDB = settingDB.getLastVisitedUrl();

        if(defaultSearchEngineFromDB.length() == 0){
            settingDB.setDefaultSearchEngine("google");
        }
        defaultSearchEngine = new StringBuffer(settingDB.getDefaultSearchEngine());

        if(lastVisitedUrlFromDB.length() != 0){
            currentUrl = lastVisitedUrlFromDB;
        }

        if(settingDB.isDarkTheme().equals("0")){
            isDarkTheme = false;
        } else {
            isDarkTheme = true;
        }
    }

    public void showBuiltInUrls(String url){
        String file = "";
        if(url.equals("lam://history")){
            file = "history.html";
            builtInFrame.addJavascriptInterface(new HistoryInterface(this),"_history");
        } else if (url.equals("lam://setting")){
            file = "setting.html";
            builtInFrame.addJavascriptInterface(new SettingInterface(this),"_setting");
        }  else if (url.equals("lam://permission")){
            file = "permission.html";
            builtInFrame.clearHistory();
            builtInFrame.addJavascriptInterface(new PermissionInterface(this),"_permission");
        } else if (url.equals("lam://tictactoe")){
            file = "game-tictactoe.html";
            builtInFrame.addJavascriptInterface(new WebAppInterface(this),"lamina");
        }

        builtInFrame.loadUrl("file:///android_asset/"+file);

        toolBoxFrame.setVisibility(View.GONE);
        frame.setVisibility(View.GONE);
        builtInFrame.setVisibility(View.VISIBLE);
        visibleFrame = builtInFrame;
    }

    @Override
    public void onBackPressed(){
        if(visibleFrame == frame) {
            if (frame.canGoBack()) {
                frame.goBack();
            } else {
                settingDB.setLastVisitedUrl("");
                super.onBackPressed();
            }
        } else {
            if((!builtInFrame.getUrl().equals("lam://permission")) && builtInFrame.canGoBack()){
                builtInFrame.goBack();
                if(!builtInFrame.canGoBack()){
                    onBackPressed();
                }
            } else {
                builtInFrame.setVisibility(View.GONE);
                frame.setVisibility(View.VISIBLE);
                toolBoxFrame.setVisibility(View.VISIBLE);
                visibleFrame = frame;
                builtInFrame.clearHistory();
                builtInFrame.clearCache(true);
            }
        }
    }

    @Override
    public void closeBuiltInPage(String url) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadUrlFromToolBox(url);
            }
        });
    }

    @Override
    public void loadUrlFromToolBox(String url) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(url.startsWith("lam://")) {
                    showBuiltInUrls(url);
                }else{
                    if(url.length() > 0) {
                        if(url.startsWith("search://")){
                            String searchText = url.substring(9,url.length());
                            if(defaultSearchEngine.toString().equals("google")) {
                                frame.loadUrl("https://" + defaultSearchEngine.toString() + ".com/search?q=" + searchText);
                            } else {
                                frame.loadUrl("https://" + defaultSearchEngine.toString() + ".com?q=" + searchText);
                            }
                        } else {
                            frame.loadUrl(url);
                        }
                    }
                    if(visibleFrame != frame) {
                        builtInFrame.setVisibility(View.GONE);
                        frame.setVisibility(View.VISIBLE);
                        toolBoxFrame.setVisibility(View.VISIBLE);
                        visibleFrame = frame;
                        builtInFrame.clearHistory();
                        builtInFrame.clearCache(true);
                    }
                }
            }
        });
    }

    @Override
    public void setDefaultSearchEngine(String name) {
        if(name.length() == 0){
            settingDB.setDefaultSearchEngine("google");
        } else {
            settingDB.setDefaultSearchEngine(name);
        }
        defaultSearchEngine = new StringBuffer(name);
    }

    @Override
    public String getDefaultSearchEngine() {
        return defaultSearchEngine.toString();
    }

    @Override
    public String getCurrentUrl() {
        try {
            return new URL(currentUrl.toString()).getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public boolean isBuiltInFrameVisible() {
        return visibleFrame == builtInFrame;
    }

    @Override
    public void executePermissionChangeEvent() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                frame.loadUrl("javascript:(()=>{" +
                        "alert('opened');alert(typeof window.lamina.permission_change_event);" +
                        "let event = new CustomEvent('lam-permission-change', { 'detail': null });\n" +
                        "document.dispatchEvent(event);" +
                        "}" +
                        ")();");
            }
        });
    }

    class MyBrowser extends WebViewClient {
        Activity activity;
        PermissionDB permissionDB;
        WebInterfaceHelper webInterfaceHelper;

        MyBrowser(Activity activity){
            super();
            webInterfaceHelper = (WebInterfaceHelper)activity;
            permissionDB = MainActivity.this.permissionDB;
            this.activity = activity;
        }

        public  void addHostToPermissionDB(String url){
            String host = null;
            try {
                host = new URL(url).getHost();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String permissions = permissionDB.getHostPermission(host);
            if(permissions.length() < 3){
                permissionDB.addHostPermission(host);
            }
        }

        public void onPageFinished(WebView view, String url){
            super.onPageFinished(view, url);
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toolBoxFrame.loadUrl("javascript:setLoadingProgress(\""+ 100 +"\")");
                }
            });
            addHostToPermissionDB(url);
        }

        public void onReceivedError(WebView view, int errCode, String description, String failingUrl){
            view.loadUrl("file:///android_asset/error.html?error="+description);
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toolBoxFrame.loadUrl("javascript:setUrl(\""+ failingUrl +"\")");
                    toolBoxFrame.loadUrl("javascript:setLoadingProgress(\""+ 100 +"\")");
                }
            });
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toolBoxFrame.loadUrl("javascript:setSsl(\"false\")");
                    toolBoxFrame.loadUrl("javascript:setLoadingProgress(\""+ 100 +"\")");
                }
            });
            handler.proceed();

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            addHostToPermissionDB(url);
            view.addJavascriptInterface(new WebAppInterface(activity),"lamina");
            view.loadUrl(url);
            return true;
        }
    }
}

