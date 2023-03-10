package nettal.deepclear;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class ForceStopService extends Service {
    private boolean running;
    private ForceStopThread forceStopThread;

    @Override
    public IBinder onBind(Intent p1) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (running)
            return super.onStartCommand(intent, flags, startId);
        try {//获取到白名单
            HashMap<String, Boolean> hashMap = Utilities.loadObjectFromFile(this, MainActivity.FileName);
            forceStopThread = new ForceStopThread(this, hashMap);
            forceStopThread.start();
        } catch (Exception e) {//没获取到白名单
            forceStopThread = new ForceStopThread(this, new HashMap<>());
            forceStopThread.start();
        }
        running = true;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        running = false;
        forceStopThread.stopSelf();
    }
}

class ForceStopThread extends Thread {
    private final Context context;
    private final HashMap<String, Boolean> hashMap;
    private boolean running;

    public ForceStopThread(Context context, HashMap<String, Boolean> hashMap) {
        this.context = context;
        this.hashMap = hashMap;
        running = true;
    }

    @Override
    public void run() {
        try {
            Command command = Command.getCommand();
            ArrayList<String> packageListBefore = Utilities.getRunningAppPackages(command);
            while (running) {
                sleep(1000);
                try {
                    ArrayList<String> packageList = Utilities.getRunningAppPackages(command);
                    Log.e("PackageList: ", packageList.toString());
                    StringBuilder stringBuilder = new StringBuilder();
                    a:
                    for (String packageName : packageListBefore) {
                        if (!packageList.contains(packageName) && Boolean.FALSE.equals(hashMap.getOrDefault(packageName,
                                Utilities.isSystemApp(packageName, context)))) {
                            for (String s : stringBuilder.toString().split(";")) {
                                if (s.equals(packageName))
                                    continue a;
                            }
                            stringBuilder.append(packageName);
                            Log.e(packageName, packageName);
                            stringBuilder.append(";");
                            command.exec("am force-stop " + packageName);
                            command.exec("am force-stop " + packageName);
                            command.exec("am force-stop " + packageName);
                        }
                    }
                    if (stringBuilder.length() != 0) {
                        Utilities.toast("Killed:" + stringBuilder.deleteCharAt(stringBuilder.length() - 1), context);
                    }
                    packageListBefore = packageList;
                } catch (Throwable e) {
                    Utilities.toast(Utilities.printLog(e), context);
                }
            }
            command.close();
        } catch (Throwable e) {
            Utilities.toast(Utilities.printLog(e), context);
        }
    }

    public void stopSelf() {
        running = false;
    }
}
