package com.example.lamina;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class WebAppInterface {
    Activity _activity;
    private CameraManager camera;
    private String torchId;

    PermissionDB permissionDB;
    WebInterfaceHelper webInterfaceHelper;

    public WebAppInterface(Activity _activity){
        this._activity = _activity;
        permissionDB = new PermissionDB(_activity);
        webInterfaceHelper = (WebInterfaceHelper)_activity;
        camera = (CameraManager) _activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            torchId = camera.getCameraIdList()[0];
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addHostToPermissionDB(String host){
        String permissions = permissionDB.getHostPermission(host);
        if(permissions.length() < 3){
            permissionDB.addHostPermission(host);
        }
    }

    public String capitalize(String str){
        if(str == null) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @JavascriptInterface
    public String calc(String code){
        if(isPermitted("calc").equals("0")){
            return  "-1";
        }
        String numData[] = code.split(":")[0].split(",");
        String trigger = code.split(":")[1];

        int nArray[] = new int[numData.length];
        for(int i=0;i<numData.length;i++){
            nArray[i] = Integer.parseInt(numData[i]);
        }

        String iterateType = trigger.split(",")[0];
        String operator = trigger.split(",")[1].split(" ")[0];
        int operand = Integer.parseInt(trigger.split(",")[1].split(" ")[1]);
        if(iterateType.equals("EACH")){
            for(int i=0;i<nArray.length;i++){
                if(operator.equals("POW")) {
                    nArray[i] = (int)Math.pow(nArray[i], operand);
                } else if(operator.equals("ADD")) {
                    nArray[i] = nArray[i] + operand;
                } else if(operator.equals("SUB")) {
                    nArray[i] = nArray[i] - operand;
                } else if(operator.equals("MUL")) {
                    nArray[i] = nArray[i] * operand;
                } else if(operator.equals("DIV")) {
                    nArray[i] = (int)(nArray[i] / operand);
                }  else if(operator.equals("MOD")) {
                    nArray[i] = nArray[i] % operand;
                }
            }
        }

        StringBuffer buffer = new StringBuffer("");
        for(int i=0;i<nArray.length;i++){
            if(i == nArray.length-1){
                buffer.append(nArray[i]);
            } else {
                buffer.append(nArray[i] + ",");
            }
        }
        return buffer.toString();
    }

    @JavascriptInterface
    public String isPermitted(String type){
        if(webInterfaceHelper.isBuiltInFrameVisible()) {
            return  "1";
        }
        String host = webInterfaceHelper.getCurrentUrl().toString();
        addHostToPermissionDB(host);
        String data = permissionDB.getHostPermission(host,type);
        return data;
    }

    @JavascriptInterface
    public void requestPermission(String type){
        AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
        builder.setTitle("Permission Request: "+capitalize(type));
        builder.setMessage(webInterfaceHelper.getCurrentUrl()+" wants to access "+type+".");
        builder.setCancelable(false);
        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                permissionDB.updateHostPermission(webInterfaceHelper.getCurrentUrl(),type,1);
                webInterfaceHelper.executePermissionChangeEvent();
            }
        });
        builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                permissionDB.updateHostPermission(webInterfaceHelper.getCurrentUrl(),type,0);
                webInterfaceHelper.executePermissionChangeEvent();
            }
        });
        builder.show();
    }

    @JavascriptInterface
    public int showToast(String text,int time){
        if(isPermitted("uikit").equals("0")){
            return  -1;
        }
        Toast.makeText(_activity,text,time).show();
        return 0;
    }

    @JavascriptInterface
    public int nativeAlert(String title, String message){
        if(isPermitted("uikit").equals("0")){
            return  -1;
        }
        _activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder =  new AlertDialog.Builder(_activity);
                builder.setCancelable(true);
                builder.setTitle(title);
                builder.setMessage(message);
                builder.show();
            }
        });
        return 0;
    }

    @JavascriptInterface
    public String showNotification(String title,String content){
        if(isPermitted("notification").equals("0")){
            return  "-1";
        }
        _activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(_activity.getApplicationContext(),MainActivity.class);
                String CHANNEL_ID="MYCHANNEL";
                NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"name", NotificationManager.IMPORTANCE_LOW);
                PendingIntent pendingIntent=PendingIntent.getActivity(_activity.getApplicationContext(),1,intent,0);
                Notification notification=new Notification.Builder(_activity.getApplicationContext(),CHANNEL_ID)
                        .setContentText(title)
                        .setContentTitle(content)
                        .setContentIntent(pendingIntent)
                        .setChannelId(CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.sym_action_chat)
                        .build();

                NotificationManager notificationManager=(NotificationManager) _activity.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(notificationChannel);
                notificationManager.notify(1,notification);
            }
        });
        return "0";
    }

    @JavascriptInterface
    public int getBrightness(){
        if(isPermitted("brightness").equals("0")){
            return  -1;
        }
        return Settings.System.getInt(_activity.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,0);
    }

    @JavascriptInterface
    public String setBrightness(int value){
        if(isPermitted("brightness").equals("0")){
            return  "-1";
        }
        try {
            Settings.System.putInt(_activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, value);
        } catch(Exception e){
            return e.getMessage();
        }
        return "done";
    }

    @JavascriptInterface
    public int vibrate(int time){
        if(isPermitted("vibrate").equals("0")){
            return  -1;
        }
        Vibrator vibrator = (Vibrator) _activity.getSystemService(Context.VIBRATOR_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            vibrator.vibrate(VibrationEffect.createOneShot(time,VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(time);
        }
        return  0;
    }

    @JavascriptInterface
    public int turnOnTorch(){
        if(isPermitted("torch").equals("0")){
            return  -1;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                camera.setTorchMode(torchId,true);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return 1;
    }

    @JavascriptInterface
    public int turnOffTorch(){
        if(isPermitted("torch").equals("0")){
            return  -1;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                camera.setTorchMode(torchId,false);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}