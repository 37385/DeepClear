package nettal.deepclear;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.HashMap;

public class ForceStopService extends Service {
    private boolean isStart;
    private ForceStopThread forceStopThread;

    @Override
    public IBinder onBind(Intent p1) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isStart)
            return super.onStartCommand(intent, flags, startId);
        try {//获取到白名单
            HashMap<String, Boolean> hashMap = (HashMap<String, Boolean>) Utilities.loadObjectFromFile(this, MainActivity.FileName);
            forceStopThread = new ForceStopThread(this, hashMap);
            forceStopThread.start();
        } catch (Exception e) {//没获取到白名单
            forceStopThread = new ForceStopThread(this, new HashMap<>());
            forceStopThread.start();
        }
        isStart = true;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        isStart = false;
        forceStopThread.stopSelf();
    }
}

class ForceStopThread extends Thread {
    private boolean running;
    private Context context;
    private HashMap<String, Boolean> hashMap;

    public ForceStopThread(Context context, HashMap<String, Boolean> hashMap) {
        this.context = context;
        this.hashMap = hashMap;
        running = true;
    }

    @Override
    public void run() {
        try {
            Command command = new Command();
            ArrayList<String> packageListBefore = Utilities.getAppPackagesFromRecents(
                    command.exec("dumpsys activity | grep recents"));
            while (running) {
                sleep(1000);
                ArrayList<String> packageList = Utilities.getAppPackagesFromRecents(
                        command.exec("dumpsys activity | grep recents"));
                StringBuilder stringBuilder = new StringBuilder();
                for (String packageName : packageListBefore) {
                    if (!packageList.contains(packageName) && !hashMap.getOrDefault(packageName,
                            Utilities.isSystemApp(packageName, context) || packageName.equals(context.getPackageName()))) {
                        stringBuilder.append(packageName);
                        stringBuilder.append(";");
                        command.exec("am force-stop " + packageName);
                    }
                }
                if (stringBuilder.length() != 0) {
                    Utilities.toast("Killed:" + stringBuilder.deleteCharAt(stringBuilder.length() - 1), context);
                }
                packageListBefore = packageList;
            }
        } catch (Exception e) {
            Utilities.printLog(e);
        }
    }

    public void stopSelf() {
        running = false;
    }
}
