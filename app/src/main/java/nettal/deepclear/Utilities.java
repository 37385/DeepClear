package nettal.deepclear;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utilities {
    /*Debug*/
    static boolean DEBUG = true;

    public static String printLog(String s) {
        if (DEBUG)
            Log.e("Info", s);
        return s;
    }

    public static String printLog(Exception e) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(e.toString());
        for (StackTraceElement h : e.getStackTrace()) {
            stringBuilder.append("\n");
            stringBuilder.append("    at ");
            stringBuilder.append(h);
        }
        if (DEBUG)
            Log.e("Exception", stringBuilder.toString());
        return stringBuilder.toString();
    }

    public static ArrayList<String> printLog(ArrayList<String> arrayList) {
        if (DEBUG)
            Log.e("ArrayList", arrayList.toString());
        return arrayList;
    }

    /*Serializable*/
    public static void saveObjectToFile(Context context, Object obj, String file) throws Exception {
        FileOutputStream fos = new FileOutputStream(
                context.getFileStreamPath(file).getAbsolutePath());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(obj);
        oos.flush();
        oos.close();
        fos.close();
    }

    public static Object loadObjectFromFile(Context context, String file) throws Exception {
        FileInputStream fis = new FileInputStream(
                context.getFileStreamPath(file).getAbsolutePath());
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object obj = ois.readObject();
        ois.close();
        fis.close();
        return obj;
    }

    /*Toast*/
    public static final Handler mainHandler = new Handler();

    public static void toast(final String s, final Context context) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static List<ApplicationInfo> getAllApplications(Context context) {
        return context.getPackageManager().getInstalledApplications(0);
    }

    public static boolean isSystemApp(ApplicationInfo info) {
        return (info.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
    }

    public static boolean isSystemApp(String packageName, Context context) {
        try {
            return isSystemApp(context.getPackageManager().getPackageInfo(packageName, 0).applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            printLog(e);
            return false;
        }
    }

    /*Example*/
    String exampleFor11 = "ACTIVITY MANAGER RECENT TASKS (dumpsys activity recents)\n" +
            "    name=recents_animation_input_consumer pid=9977 user=UserHandle{0}\n" +
            "    #55: vis    TOP  LCM 9977:app.lawnchair/u0a413 act:activities|recents\n" +
            "    #53: fg     TOP  LCM 28490:org.connectbot/u0a373 act:activities|recents\n" +
            "    #50: prev   LAST --- 21978:cn.wps.moffice_eng:pdfreader1/u0a383 act:activities|recents\n" +
            "    #49: prev   SVC  --- 21980:com.tencent.mobileqq/u0a368 act:activities|recents\n" +
            "    #46: cch+15 SVC  --- 21977:com.netease.cloudmusic/u0a396 act:activities|recents";

    String exampleFor10 = "ACTIVITY MANAGER RECENT TASKS (dumpsys activity recents)\n" +
            "    #53: fore   TOP  25936:org.connectbot/u0a349  activity=activities|recents\n" +
            "    #52: vis    TOP  3325:ch.deletescape.lawnchair.ci/u0a325  activity=activities|recents\n" +
            "    #51: prev   LAST 25938:us.mathlab.android.calc.edu/u0a339  activity=activities|recents\n" +
            "    #50: cch    CAC  21925:com.aide.ui/u0a350  activity=activities|recents\n" +
            "    #48: prcp   FGS  21277:com.dv.adm.pay/u0a317  activity=activities|recents\n" +
            "    #46: cch+ 5 SVC  21280:nettal.deepclear/u0a422  activity=activities|recents\n" +
            "    #45: cch+10 CAC  17630:com.android.chrome/u0a311  activity=activities|recents";

    String exampleFor10And7 = "    Running activities (most recent first):\n" +
            "        Run #1: ActivityRecord{d47636c u0 org.connectbot/.ConsoleActivity t279}\n" +
            "        Run #0: ActivityRecord{da211d6 u0 org.connectbot/.HostListActivity t279}\n" +
            "    Running activities (most recent first):\n" +
            "        Run #0: ActivityRecord{7e5f1cf u0 ch.deletescape.lawnchair.ci/ch.deletescape.lawnchair.LawnchairLauncher t44}\n" +
            "    Running activities (most recent first):\n" +
            "        Run #0: ActivityRecord{14877e9 u0 com.speedsoftware.rootexplorer/.RootExplorer t285}\n" +
            "    Running activities (most recent first):\n" +
            "        Run #0: ActivityRecord{b375e8e u0 cn.wps.moffice_eng/cn.wps.moffice.pdf.multiactivity.PDFReader1 t284}\n" +
            "    Running activities (most recent first):\n" +
            "        Run #0: ActivityRecord{63791b u0 cn.wps.moffice_eng/cn.wps.moffice.main.local.home.HomeActivity t283}\n" +
            "    Running activities (most recent first):\n" +
            "        Run #0: ActivityRecord{c3e8ab0 u0 com.aide.ui/.MainActivity t275}\n" +
            "    Running activities (most recent first):\n" +
            "        Run #0: ActivityRecord{c63cc68 u0 nettal.deepclear/.MainActivity t277}";

    public static ArrayList<String> getRunningAppPackages(Command command) throws IOException {
        return android.os.Build.VERSION.SDK_INT > 29//Android Q;SDK 29
                ? getRunningAppPackagesFromRecents(command.exec("dumpsys activity | grep recents"))
                : getRunningAppPackagesFromRun(command.exec("dumpsys activity | grep Run"));
    }

    /*Available for 10 and above*/
    public static ArrayList<String> getRunningAppPackagesFromRecents(String s) {//dumpsys activity | grep recents
        ArrayList<String> packageList = new ArrayList<>();
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i) == '#') {
                int colonIndex = 0;
                while (i < s.length() - 1 && s.charAt(i++) != ':') {
                }
                while (i < s.length() - 1 && s.charAt(i++) != ':') {
                }
                colonIndex = i - 1;
                while (i++ < s.length() - 1 && s.charAt(i) != '/' && s.charAt(i) != ':') {
                }
                packageList.add(s.substring(colonIndex + 1, i));
            }
        }
        return packageList;
    }

    /*Available for 10 and below*/
    public static ArrayList<String> getRunningAppPackagesFromRun(String s) {//dumpsys activity | grep Run
        ArrayList<String> packageList = new ArrayList<>();
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i) == '{') {
                int spaceIndex = 0;
                while (i < s.length() - 1 && !(s.charAt((i++) - 2) == ' ' && s.charAt(i - 2) == 'u'
                        && s.charAt(i - 1) == '0' && s.charAt(i) == ' ')) {
                }
                spaceIndex = i++;
                while (i++ < s.length() - 1 && s.charAt(i) != '/') {
                }
                packageList.add(s.substring(spaceIndex + 1, i));
            }
        }
        return packageList;
    }

    @Deprecated
    public static ArrayList<String> getRunningAppPackagesFromAffinity(String s) {//dumpsys activity | grep affinity
        ArrayList<String> packageList = new ArrayList<>();
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i) == '\n') {
                for (int j = i; ; j--) {
                    if (s.charAt(j) == ':') {
                        packageList.add(s.substring(j + 1, i));
                        break;
                    }
                    if (s.charAt(j) == ' ' || j == 0) {
                        break;
                    }
                }
            }
        }
        return packageList;
    }

    @Deprecated
    public static HashMap<String, String> getRunningAppPackagesFromStackList(String s) {//am stack list
        HashMap<String, String> packageMap = new HashMap<>();
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i) == '\n' && s.charAt(i + 1) == '\n') {
                int indexBraceLeft = 0;
                int indexSlash = 0;
                for (int j = i; ; j--) {
                    if (s.charAt(j) == '{') {
                        indexBraceLeft = j;
                    }
                    if (s.charAt(j) == '/') {
                        indexSlash = j;
                    }
                    if (s.charAt(j) == '=') {
                        break;
                    }
                }
                if (indexBraceLeft >= indexSlash) break;
                packageMap.put(s.substring(indexBraceLeft + 1, indexSlash), s.substring(indexBraceLeft + 1, indexSlash));
            }
        }
        return packageMap;
    }

    /*Battery Check*/
    public static boolean ignoreBatteryOptimization(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (!powerManager.isIgnoringBatteryOptimizations(context.getPackageName())) {
            Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
            return false;
        }
        return true;
    }
}
