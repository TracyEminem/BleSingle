package com.example.tracyeminem.blesingle;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by wells on 2018/11/21.
 */
public class TestService extends Service {

    int i =0;
    SharedPreferenceUtil sharedPreferenceUtil;
    private NotificationManager notifManager;
    PendingIntent callbackIntent;
    long lastScreentActionTime;
//    KeepLiveManager keepLiveManager;

    public static int numRSSI=0;
    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferenceUtil = new SharedPreferenceUtil(this);
//        sharedPreferenceUtil.setCreateTime(String.valueOf(System.currentTimeMillis()));

//        //以前台服务的方式启动，要调用startForeground，否则会出现arn异常
//        Notification notification = new Notification.Builder(this, NotificationChannel.DEFAULT_CHANNEL_ID)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
//                .build();
//        startForeground(110, notification);
//        NotificationManager mNotificationManager = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
//        mNotificationManager.startServiceInForeground(new Intent(this, ), NOTIFICATION_ID, notification);
        Notification("正在工作");

        //指定扫描到蓝牙后是以什么方式通知到app端，这里将以可见服务的形式进行启动
        callbackIntent = PendingIntent.getForegroundService(
                this,
                1,
                new Intent("com.hungrytree.receiver.BleService").setPackage(getPackageName()),
                PendingIntent.FLAG_UPDATE_CURRENT );

        onOpen();

//        keepLiveManager = new KeepLiveManager(getApplicationContext());
//        keepLiveManager.scheduleJob();

        // 判断手机是否支持蓝牙
        IntentFilter statusFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mStatusReceive, statusFilter);

        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mScreenReceive, intentFilter);

    }


//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        super.onTaskRemoved(rootIntent);
//        startForegroundService(new Intent(this,TestService.class));
//    }
//
//    @Override
//    public void onTrimMemory(int level) {
//        super.onTrimMemory(level);
//        startForegroundService(new Intent(this,TestService.class));
//    }

    @Override
    public boolean stopService(Intent name) {
//        sharedPreferenceUtil.setDestoryTime(String.valueOf(System.currentTimeMillis()));
        return super.stopService(name);
    }

    //Send the Notification after Service start and keep it in background.
    public void Notification(String aMessage) {
        final int NOTIFY_ID = 1003;
        String name = "IBC_SERVICE_CHANNEL";
        String id = "IBC_SERVICE_CHANNEL_1"; // The user-visible name of the channel.
        String description = "IBC_SERVICE_CHANNEL_SHOW"; // The user-visible description of the channel.

        Intent intent;
        PendingIntent pendingIntent;
        android.support.v4.app.NotificationCompat.Builder builder;

        if (notifManager == null) {
            notifManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(false);
                mChannel.enableLights(false);
                //mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new android.support.v4.app.NotificationCompat.Builder(this);
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            builder.setContentTitle(aMessage)  // required
                    .setSmallIcon(R.mipmap.ic_launcher) // required
                    .setContentText(this.getString(R.string.app_name))  // required
                    .setDefaults(Notification.DEFAULT_LIGHTS)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setChannelId(id)
                    .setTicker(aMessage);
            builder.build().sound = null;
            builder.build().vibrate = null;
        } else {
            builder = new NotificationCompat.Builder(this);
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            builder.setContentTitle(aMessage)                           // required
                    .setSmallIcon(R.mipmap.ic_launcher) // required
                    .setContentText(this.getString(R.string.app_name))  // required
                    .setDefaults(Notification.DEFAULT_LIGHTS)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVibrate(new long[]{0L});
        } // else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification notification = builder.build();
        notification.sound = null;
        notification.vibrate = null;
        startForeground(NOTIFY_ID, notification);
        //notifManager.notify(NOTIFY_ID, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() == null) {
            return START_STICKY;
        }

        //获取返回的错误码
        int errorCode = intent.getIntExtra(BluetoothLeScanner.EXTRA_ERROR_CODE, -1);//ScanSettings.SCAN_FAILED_*
        //获取到的蓝牙设备的回调类型
        int callbackType = intent.getIntExtra(BluetoothLeScanner.EXTRA_CALLBACK_TYPE, -1);//ScanSettings.CALLBACK_TYPE_*
        if (errorCode == -1) {
            //扫描到蓝牙设备信息
            List<ScanResult> scanResults = (List<ScanResult>) intent.getSerializableExtra(BluetoothLeScanner.EXTRA_LIST_SCAN_RESULT);
            if (scanResults != null) {
                for (ScanResult result : scanResults) {

                    i++;
                    if(i % 10 == 0){
//                        RxBus.getDefault().send(new Event());
                        sharedPreferenceUtil.setTime(String.valueOf(System.currentTimeMillis()));
                    }

//                    numRSSI++;
//                    Utils.writeFile(getApplicationContext(),"wakeup.log",Integer.toString(numRSSI));
                    Log.i("Wakeup", "onScanResult2: name: " + result.getDevice().getName() +
                            ", address: " + result.getDevice().getAddress() +
                            ", rssi: " + result.getRssi() + ", scanRecord: " + result.getScanRecord());
                }
            }
        } else {
            //此处为扫描失败的错误处理

        }
        return START_STICKY;
    }

    private BroadcastReceiver mScreenReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equals(Intent.ACTION_SCREEN_ON)){
                awakeSysyem();
//                onClose();
//                onOpen();
            }else if(action.equals(Intent.ACTION_SCREEN_OFF)){
                awakeSysyem();
//                onClose();
//                onOpen();
            }else if(action.equals(Intent.ACTION_USER_PRESENT)){

            }
        }
    };

    private void awakeSysyem(){
//        Log.e("EEEEEE","-----"+(System.currentTimeMillis() - lastScreentActionTime)/1000);
        long current = System.currentTimeMillis();
        if((current - lastScreentActionTime) / 1000 >= DateTimeUtil.ONE_MINUTE * 15){
            onClose();
            onOpen();
        }
        lastScreentActionTime = current;
    }

    /*Recevie the status of BLE from system*/
    private BroadcastReceiver mStatusReceive = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                switch (Objects.requireNonNull(intent.getAction())) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                        switch (blueState) {
                            case BluetoothAdapter.STATE_TURNING_ON:
                                break;
                            case BluetoothAdapter.STATE_ON:
                                onOpen();
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                break;
                            case BluetoothAdapter.STATE_OFF:
                                break;
                        }
                        break;
                }
            }
        }
    };

    public void onOpen(){
        //BluetoothManager是向蓝牙设备通讯的入口
        BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        //指定需要识别到的蓝牙设备
        List<ScanFilter> scanFilterList = new ArrayList<>();

        ScanFilter.Builder builder2 = new ScanFilter.Builder();
        builder2.setDeviceName("test2");//你要扫描的设备的名称，如果使用lightble这个app来模拟蓝牙可以直接设置name
        ScanFilter scanFilter2 = builder2.build();

//        scanFilterList.add(scanFilter);
        scanFilterList.add(scanFilter2);

        //指定蓝牙的方式，这里设置的ScanSettings.SCAN_MODE_LOW_LATENCY是比较高频率的扫描方式
        ScanSettings.Builder settingBuilder = new ScanSettings.Builder();
        settingBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        settingBuilder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
        settingBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
        settingBuilder.setLegacy(true);
        ScanSettings settings = settingBuilder.build();


        //启动蓝牙扫描
        bluetoothAdapter.getBluetoothLeScanner().startScan(scanFilterList,settings,callbackIntent);
        // bluetoothAdapter.getBluetoothLeScanner().startScan(scanFilterList,settings,mScanCallback);
    }

    public void onClose(){
        BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        bluetoothAdapter.getBluetoothLeScanner().stopScan(callbackIntent);
        //bluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        sharedPreferenceUtil.setDestoryTime(String.valueOf(System.currentTimeMillis()));
        super.onDestroy();
        startForegroundService(new Intent(this,TestService.class));
    }
}