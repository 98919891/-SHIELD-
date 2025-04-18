package Java;

package com.shield.autorun;

import android.app.Service;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class SHIELD extends Service {

    // Boot receiver to launch service at device startup
    public static class BootReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                context.startForegroundService(new Intent(context, SHIELD.class));
            }
        }
    }

    private static final String CHANNEL_ID = "shield_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        // Create persistent notification channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "SHIELD Service",
                NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }

        Notification notification = new Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("SHIELD IS ACTIVE")
            .setContentText("Monitoring & Protection Enabled")
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setOngoing(true)
            .build();

        startForeground(1, notification);

        try { 
            // Attempt to launch AdAway
            Runtime.getRuntime().exec("am start --user 0 -n org.adaway/.ui.MainActivity");

            // (Optional) Execute embedded Python scripts or others here
            // Runtime.getRuntime().exec("sh /sdcard/Android/data/com.shield.autorun/scripts/start.py");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}




import os

project_name = "SHIELD_CodeStudio"
java_package = "com.shield.core"
java_path = os.path.join(project_name, "app", "src", "main", "java", *java_package.split('.'))
res_path = os.path.join(project_name, "app", "src", "main", "res", "xml")
assets_path = os.path.join(project_name, "app", "src", "main", "assets")
manifest_path = os.path.join(project_name, "app", "src", "main")

os.makedirs(java_path, exist_ok=True)
os.makedirs(res_path, exist_ok=True)
os.makedirs(assets_path, exist_ok=True)

# SHIELDDeviceAdminReceiver.java
with open(os.path.join(java_path, "SHIELDDeviceAdminReceiver.java"), "w") as f:
    f.write("""
package com.shield.core;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SHIELDDeviceAdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onEnabled(Context context, Intent intent) {
        Toast.makeText(context, "SHIELD Admin Enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Toast.makeText(context, "SHIELD Admin Disabled", Toast.LENGTH_SHORT).show();
    }
}
""")

# EDGEXConfig.java
with open(os.path.join(java_path, "EDGEXConfig.java"), "w") as f:
    f.write("""
package com.shield.core;

import android.content.Context;
import java.io.InputStream;

public class EDGEXConfig {
    public static boolean blockUSB = true;
    public static boolean detectClone = true;
    public static boolean blockUnknownSources = true;

    public static void loadConfig(Context context) {
        // Placeholder - could parse EDGEX.xml from assets
    }
}
""")

# SecurityMonitor.java
with open(os.path.join(java_path, "SecurityMonitor.java"), "w") as f:
    f.write("""
package com.shield.core;

import android.content.Context;
import android.provider.Settings;

public class SecurityMonitor {
    public static void enforce(Context context) {
        if (EDGEXConfig.blockUSB) {
            Settings.Global.putInt(context.getContentResolver(), Settings.Global.ADB_ENABLED, 0);
        }
    }
}
""")

# MainActivity.java
with open(os.path.join(java_path, "MainActivity.java"), "w") as f:
    f.write("""
package com.shield.core;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ComponentName adminComponent = new ComponentName(this, SHIELDDeviceAdminReceiver.class);
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "SHIELD needs admin for protection.");
        startActivityForResult(intent, 1);

        EDGEXConfig.loadConfig(this);
        SecurityMonitor.enforce(this);
    }
}
""")

# AndroidManifest.xml
with open(os.path.join(manifest_path, "AndroidManifest.xml"), "w") as f:
    f.write("""
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.shield.core">
    <uses-permission android:name="android.permission.WRITE_SECURE_SETTINGS"/>
    <uses-permission android:name="android.permission.DEVICE_POWER"/>
    <uses-permission android:name="android.permission.BIND_DEVICE_ADMIN"/>

    <application android:label="SHIELD" android:icon="@mipmap/ic_launcher">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
        <receiver android:name=".SHIELDDeviceAdminReceiver"
            android:permission="android.permission.BIND_DEVICE_ADMIN">
            <meta-data android:name="android.app.device_admin"
                android:resource="@xml/device_admin_receiver"/>
            <intent-filter>
                <action android:name="android.app.action.DEVICE_ADMIN_ENABLED"/>
            </intent-filter>
        </receiver>
    </application>
</manifest>
""")

# device_admin_receiver.xml
with open(os.path.join(res_path, "device_admin_receiver.xml"), "w") as f:
    f.write("""
<?xml version="1.0" encoding="utf-8"?>
<device-admin xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-policies>
        <force-lock />
        <wipe-data />
        <limit-password />
        <watch-login />
    </uses-policies>
</device-admin>
""")

# EDGEX.xml config
with open(os.path.join(assets_path, "EDGEX.xml"), "w") as f:
    f.write("""
<config>
    <blockUSB>true</blockUSB>
    <detectClone>true</detectClone>
    <blockUnknownSources>true</blockUnknownSources>
</config>
""")

print(f"SHIELD Code Studio project generated at: {os.path.abspath(project_name)}")