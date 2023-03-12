package nettal.deepclear;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public final class Utilities {
    /*Debug*/
    static final boolean DEBUG = BuildConfig.DEBUG;

    public static String printLog(String s) {
        if (DEBUG)
            Log.e("Info", s);
        return s;
    }

    public static String printLog(Throwable e) {
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

    public static void writeStringToFile(String destination, String text) {
        try {
            FileWriter fileWriter = new FileWriter(destination, true);
            fileWriter.write("-----------------------------------------------------");
            fileWriter.write("\nTime: " + new Date() + ": " + System.lineSeparator());
            fileWriter.write(text);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            Utilities.printLog(e);
        }
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

    @SuppressWarnings("unchecked")
    public static <T> T loadObjectFromFile(Context context, String file) throws Exception {
        FileInputStream fis = new FileInputStream(
                context.getFileStreamPath(file).getAbsolutePath());
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object obj = ois.readObject();
        ois.close();
        fis.close();
        return (T) obj;
    }

    /*Toast*/
    public static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void toast(final String s, final Context context) {
        mainHandler.post(() -> Toast.makeText(context, s, Toast.LENGTH_SHORT).show());
    }

    /*AppInfo*/
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
    String exampleFor11 = """
            ACTIVITY MANAGER RECENT TASKS (dumpsys activity recents)
                name=recents_animation_input_consumer pid=9977 user=UserHandle{0}
                #55: vis    TOP  LCM 9977:app.lawnchair/u0a413 act:activities|recents
                #53: fg     TOP  LCM 28490:org.connectbot/u0a373 act:activities|recents
                #50: prev   LAST --- 21978:cn.wps.moffice_eng:pdfreader1/u0a383 act:activities|recents
                #49: prev   SVC  --- 21980:com.tencent.mobileqq/u0a368 act:activities|recents
                #46: cch+15 SVC  --- 21977:com.netease.cloudmusic/u0a396 act:activities|recents""";

    String exampleFor10 = """
            ACTIVITY MANAGER RECENT TASKS (dumpsys activity recents)
                #53: fore   TOP  25936:org.connectbot/u0a349  activity=activities|recents
                #52: vis    TOP  3325:ch.deletescape.lawnchair.ci/u0a325  activity=activities|recents
                #51: prev   LAST 25938:us.mathlab.android.calc.edu/u0a339  activity=activities|recents
                #50: cch    CAC  21925:com.aide.ui/u0a350  activity=activities|recents
                #48: prcp   FGS  21277:com.dv.adm.pay/u0a317  activity=activities|recents
                #46: cch+ 5 SVC  21280:nettal.deepclear/u0a422  activity=activities|recents
                #45: cch+10 CAC  17630:com.android.chrome/u0a311  activity=activities|recents""";

    String exampleFor10And7 = """
              * ContentProviderRecord{1b0bf07 u0 com.nowcasting.activity/razerdp.basepopup.BasePopupRuntimeTrojanProvider}
              * ServiceRecord{349806e u0 com.eg.android.AlipayGphone/com.alipay.dexaop.power.RuntimePowerService}
                baseIntent=Intent { flg=0x14000000 cmp=com.android.settings/.Settings$DevRunningServicesActivity }
                 #0 ActivityRecord{ea1a3c2 u0 com.android.settings/.Settings$DevRunningServicesActivity t1689} type=standard mode=fullscreen override-mode=undefined
                Running activities (most recent first):
                    Run #1: ActivityRecord{46b5be5 u0 org.connectbot/.ConsoleActivity t1690}
                    Run #0: ActivityRecord{d5294f2 u0 org.connectbot/.HostListActivity t1690}
                Running activities (most recent first):
                    Run #0: ActivityRecord{6c05bd4 u0 ch.deletescape.lawnchair.ci/ch.deletescape.lawnchair.LawnchairLauncher t1043}
                  intent={flg=0x14000000 cmp=com.android.settings/.Settings$DevRunningServicesActivity}
                  mActivityComponent=com.android.settings/.Settings$DevRunningServicesActivity
                  Activities=[ActivityRecord{ea1a3c2 u0 com.android.settings/.Settings$DevRunningServicesActivity t1689}]
                    Hist #0: ActivityRecord{ea1a3c2 u0 com.android.settings/.Settings$DevRunningServicesActivity t1689}
                      Intent { flg=0x14000000 cmp=com.android.settings/.Settings$DevRunningServicesActivity }
                Running activities (most recent first):
                    Run #0: ActivityRecord{ea1a3c2 u0 com.android.settings/.Settings$DevRunningServicesActivity t1689}
                Running activities (most recent first):
                    Run #0: ActivityRecord{ecf75ae u0 com.eg.android.AlipayGphone/.AlipayLogin t1688}
                    com.eg.android.AlipayGphone/com.alipay.dexaop.power.RuntimePowerService<=Proc{23940:com.eg.android.AlipayGphone:tools/u0a326}
            """;

    public static ArrayList<String> getRunningAppPackages(Command command) throws IOException {
        return android.os.Build.VERSION.SDK_INT > 29//Android Q;SDK 29
                ? getRunningAppPackagesFromRecents(command.exec("dumpsys activity | grep recents"))
                : getRunningAppPackagesFromRun(command.exec("dumpsys activity | grep Run"));
    }

    /*Available for 10 and above*/
    public static ArrayList<String> getRunningAppPackagesFromRecents(String s) {//dumpsys activity | grep recents
        ArrayList<String> packageList = new ArrayList<>(16);
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i) == '#') {
                do i++;
                while (i < s.length() && s.charAt(i) != ':');
                do i++;
                while (i < s.length() && s.charAt(i) != ':');
                int colonIndex = i;
                do i++;
                while (i < s.length() - 1 && s.charAt(i) != '/' && s.charAt(i) != ':');
                if (colonIndex + 1 < i && s.length() > i)
                    packageList.add(s.substring(colonIndex + 1, i));
            }
        }
        return packageList;
    }

    /*Available for 10 and below*/
    public static ArrayList<String> getRunningAppPackagesFromRun(String s) {//dumpsys activity | grep Run
        ArrayList<String> packageList = new ArrayList<>(16);
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i) == '#') {
                do i++;
                while (i < s.length() && s.charAt(i) != '{');
                do i++;
                while (i < s.length() && s.charAt(i) != ' ');
                do i++;
                while (i < s.length() && s.charAt(i) != ' ');
                int spaceIndex = i;
                do i++;
                while (i < s.length() && s.charAt(i) != '/');
                if (spaceIndex + 1 < i && s.length() > i)
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
