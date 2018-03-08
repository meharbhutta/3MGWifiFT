package com.bhutta.mmmgwifift;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ShareActivity extends AppCompatActivity {

    private ServerSocket httpServer;
    private int port;
    private String address;
    private ImageView imageView;
    private TextView textView, ss, pas;
    private final int RequestCOde = 121;
    private boolean stopServer = false, wifiInd = true, hardw = true;
    private Socket connected;
    private final int NOTIFICATION_ID = 121;
    private String oldSsid, oldPass;
    private Context mContext;
    private static ArrayList<Uri> shareDataUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        mContext = this;
        checkAndRequestPermissions();
        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_SEND.equals(action)) {
            shareDataUri = new ArrayList<>();
            shareDataUri.add((Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM));
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
            shareDataUri = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        }
        textView = (TextView) findViewById(R.id.serveraccess);
        imageView = ((ImageView)findViewById(R.id.appImageView));
        ss = (TextView) findViewById(R.id.hot_ssid);
        pas = (TextView) findViewById(R.id.hot_pass);
        wifiInd = true;
        hardw = true;
        if (shareDataUri == null || shareDataUri.isEmpty()) {
            textView.setText(R.string.no_data_available_for_share);
            return;
        }
        final WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Share Method");
            builder.setMessage("How do you want to share?");
            builder.setCancelable(false);
            builder.setNegativeButton("Wifi", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    ss.setVisibility(View.GONE);
                    pas.setVisibility(View.GONE);
                    wifiIndicator();
                    address = getIpAddress();
                    switchON();
                }
            });
            builder.setPositiveButton("Hotspot", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    wifiIndicator();
                    wifiManager.setWifiEnabled(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (turnOnHotspot(wifiManager)) {
                                address = getIpAddress();
                                switchON();
                            } else {
                                ShareActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        wifiInd = false;
                                        hardw = false;
                                        imageView.setImageResource(R.drawable.wifi_indicator);
                                        Toast.makeText(mContext, "Hardware not supported!", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(mContext, "Use wifi instead!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    }).start();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    wifiIndicator();
                    if (turnOnHotspot(wifiManager)) {
                        address = getIpAddress();
                        switchON();
                    } else {
                        ShareActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                wifiInd = false;
                                hardw = false;
                                imageView.setImageResource(R.drawable.wifi_indicator);
                                Toast.makeText(mContext, "Hardware not supported!", Toast.LENGTH_SHORT).show();
                                Toast.makeText(mContext, "Use wifi instead!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();
        }

    }

    private boolean turnOnHotspot(WifiManager wifiManager) {
        WifiAPControl apControl = WifiAPControl.getApControl(wifiManager);
        if (apControl != null) {
            WifiConfiguration oldConfiguration = apControl.getWifiApConfiguration();
            oldSsid = oldConfiguration.SSID;
            oldPass = oldConfiguration.preSharedKey;
            ShareActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ss.setVisibility(View.VISIBLE);
                    ss.setText("SSID: " + oldSsid);
                    pas.setVisibility(View.VISIBLE);
                    pas.setText("Pass: " + oldPass);
                }
            });
            apControl.setWifiApEnabled( oldConfiguration, true);
            int i = 0;
            while (true){
                if (apControl.isWifiApEnabled()) i++;
                if (i == 6000) break;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        cancleNotification();
        wifiInd = false;
        hardw = false;
        stopServer = true;
        if (connected != null) {
            try {
                connected.close();
                connected = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (httpServer != null) {
            try {
                httpServer.close();
                httpServer = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            AboutDialog about = new AboutDialog(this);
            about.show();
        }
        return true;
    }

    private void wifiIndicator(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final boolean[] check = {true};
                    do {
                        ShareActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (hardw) imageView.setImageResource(R.drawable.wifi_indicator1);
                            }
                        });
                        Thread.sleep(200);
                        ShareActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (hardw) imageView.setImageResource(R.drawable.wifi_indicator2);
                            }
                        });
                        Thread.sleep(200);
                        ShareActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (hardw) imageView.setImageResource(R.drawable.wifi_indicator3);
                            }
                        });
                        Thread.sleep(200);
                        ShareActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (hardw) imageView.setImageResource(R.drawable.wifi_indicator4);
                                check[0] = wifiInd;
                            }
                        });
                        Thread.sleep(200);
                    } while(check[0]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void switchON(){
        if (address.trim().contains(getString(R.string.something_wrong)+"! ")) {
            wifiInd = false;
            hardw = false;
            imageView.setImageResource(R.drawable.wifi_indicator);
            textView.setText(getString(R.string.share_again));
            Toast.makeText(getBaseContext(), getString(R.string.something_wrong)+"!", Toast.LENGTH_SHORT).show();
        } else if (address.trim().contains(getString(R.string.no_hardware))) {
            wifiInd = false;
            hardw = false;
            imageView.setImageResource(R.drawable.wifi_indicator);
            textView.setText(getString(R.string.no_hardware));
            Toast.makeText(getBaseContext(), getString(R.string.no_hardware)+"!", Toast.LENGTH_SHORT).show();
        } else if (address.trim().isEmpty()) {
            wifiInd = false;
            hardw = false;
            imageView.setImageResource(R.drawable.wifi_indicator);
            textView.setText(getString(R.string.not_connected_to_network));
            Toast.makeText(getBaseContext(), getString(R.string.not_connected_to_network)+"!", Toast.LENGTH_SHORT).show();
        } else {
            stopServer = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        makeNotification(mContext);
                        port = getFreePorts(8080, 9000, 1)[0];
                        httpServer = new ServerSocket(port);
                        ShareActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                wifiInd = false;
                                textView.setText("http://" + address.trim() + ":" + port);
                            }
                        });
                        while (!stopServer) {
                            connected = httpServer.accept();
                            (new HTTPPOSTServer(connected, address, port, mContext)).start();
                        }
                    } catch (IOException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private String getIpAddress() {
        String ip = "";
        try {
            NetworkInterface networkInterface = NetworkInterface.getByName("wlan0");
            if (networkInterface != null) {
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress();
                    }
                }
            } else {
                ip += getString(R.string.no_hardware);
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += getString(R.string.something_wrong) + "! " + e.toString();
        }
        return ip;
    }

    private static boolean isPortFree (int port){
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(port);
            socket.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private static int[] getFreePorts (int rangeMin, int rangeMax, int count) {
        int currPortCount = 0;
        int port[] = new int [count];
        for (int currPort = rangeMin; currPortCount < count && currPort <= rangeMax; ++currPort) {
            if (isPortFree(currPort))
                port[currPortCount++] = currPort;
        }
        if (currPortCount < count)
            throw new IllegalStateException ("Could not find " + count + " free ports to allocate within range " +
                    rangeMin + "-" + rangeMax + ".");
        return port;
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            List<String> listPermissionsNeeded = new ArrayList<>();
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permission);
                }
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), RequestCOde);
            }
        }
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            permission = Settings.System.canWrite(mContext);
        } else {
            permission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (!permission){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                ((Activity) mContext).startActivityForResult(intent, RequestCOde);
            } else {
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_SETTINGS}, RequestCOde);
            }
        }
    }

    private void saveImages(){
        save(R.drawable.favicon);
        save(R.drawable.pa_folder);
        save(R.drawable.folder);
        save(R.drawable.music_icon);
        save(R.drawable.video_icon);
        save(R.drawable.unknown_file_icon);
        save(R.drawable.document_icon);
        save(R.drawable.compressed_icon);
    }

    private void save(int drawable){
        try {
            String filename;
            if (drawable == R.drawable.favicon) filename =  "favicon.ico";
            else if (drawable == R.drawable.pa_folder) filename = "pa_folder.png";
            else if (drawable == R.drawable.folder) filename = "folder.png";
            else if (drawable == R.drawable.music_icon) filename = "music.png";
            else if (drawable == R.drawable.video_icon) filename = "video.png";
            else if (drawable == R.drawable.unknown_file_icon) filename = "unknown_file.png";
            else if (drawable == R.drawable.compressed_icon) filename = "compressed.png";
            else filename = "document_file.png";
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.bhutta.mmmgwifift/" + filename);
            if (file.exists()) file.delete();
            File dir = new File(file.getParent());
            if (!dir.exists()) dir.mkdirs();
            file.createNewFile();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawable);
            bitmap.compress(Bitmap.CompressFormat.PNG,0,new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeNotification(Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).
                setSmallIcon(R.drawable.ic_stat_name).
                setContentTitle(getString(R.string.app_name)).
                setContentText(getString(R.string.file_transfer_is_running));
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void cancleNotification(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public static ArrayList<Uri> getShareDataUri(){
        return shareDataUri;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case RequestCOde:
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, getString(R.string.please_grant), Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    saveImages();
                }
        }
    }

}
