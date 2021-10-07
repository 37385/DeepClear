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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utilities
{
	static boolean DEBUG = true;
	public static String printLog(String s){
		if (DEBUG)
			Log.e("Info" , s);
		return s;
	}

	public static String printLog(Exception e){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(e.toString());
		for(StackTraceElement h :e.getStackTrace()){
			stringBuilder.append("\n");
			stringBuilder.append("    at ");
			stringBuilder.append(h);
		}
		if (DEBUG)
			Log.e("Exception" , stringBuilder.toString());
		return stringBuilder.toString();
	}

	public static ArrayList<String> printLog(ArrayList<String> arrayList){
		if (DEBUG)
			Log.e("Info" , arrayList.toString());
		return arrayList;
	}

	public static void saveObjectToFile(Context context,Object obj,String file) throws Exception {
		FileOutputStream fos = new FileOutputStream(
				context.getFileStreamPath(file).getAbsolutePath());
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(obj);
		oos.flush();
		oos.close();
		fos.close();
	}

	public static Object loadObjectFromFile(Context context,String file) throws Exception {
		FileInputStream fis = new FileInputStream(
				context.getFileStreamPath(file).getAbsolutePath());
		ObjectInputStream ios = new ObjectInputStream(fis);
		Object obj = ios.readObject();
		ios.close();
		fis.close();
		return obj;
	}

	public static void toast(final String s, final Context context){
		new Handler(Looper.getMainLooper()).post(new Runnable(){
			@Override
			public void run(){
				Toast.makeText(context ,s,Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static List<ApplicationInfo> getAllApplications(Context context){
		return context.getPackageManager().getInstalledApplications(0);
	}
	
	public static boolean isSystemApp(ApplicationInfo info){
		return (info.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
	}
	
	public static boolean isSystemApp(String packageName , Context context) throws PackageManager.NameNotFoundException{
		return isSystemApp(context.getPackageManager().getPackageInfo(packageName,0).applicationInfo);
	}

	public static ArrayList<String> getAppPackagesFromRecents(String s){//dumpsys activity | grep recents
		ArrayList<String> packageList = new ArrayList<>();
		for (int i = 0; i < s.length()-1; i++) {
			if (s.charAt(i)=='#'){
				int colonIndex = 0;
				for (i= i+5 ; i<s.length() ; i++){
					if (s.charAt(i)==':'){
						colonIndex = i;
						break;
					}
				}
				for (i++ ; i<s.length() && colonIndex !=0 ; i++){
					if (s.charAt(i)=='/'|| s.charAt(i)==':'){
						packageList.add(s.substring(colonIndex+1 ,i));
						break;
					}
				}
			}
		}
		return packageList;
	}

	@Deprecated
	public static ArrayList<String> getAppPackagesFromAffinity(String s){//dumpsys activity | grep affinity
		ArrayList<String> packageList = new ArrayList<>();
		for (int i = 0; i < s.length()-1; i++) {
			if (s.charAt(i)=='\n'){
				for (int j = i; ; j--){
					if (s.charAt(j)==':'){
						packageList.add(s.substring(j+1 ,i));
						break;
					}
					if (s.charAt(j)==' ' || j == 0){
						break;
					}
				}
			}
		}
		return packageList;
	}

	@Deprecated
	public static HashMap<String,String> getAppPackagesFromStackList(String s){//am stack list
		 HashMap<String ,String> packageMap = new HashMap<>();
        for (int i = 0; i < s.length()-1; i++) {
            if (s.charAt(i)=='\n'&&s.charAt(i+1)=='\n'){
                int indexBraceLeft = 0;
                int indexSlash=0;
                for (int j = i; ; j--){
                    if (s.charAt(j)=='{'){
                        indexBraceLeft = j;
                    }
                    if (s.charAt(j)=='/'){
                        indexSlash = j;
                    }
                    if (s.charAt(j)=='='){
                        break;
                    }
                }
                if (indexBraceLeft>=indexSlash) break;
                packageMap.put(s.substring(indexBraceLeft+1,indexSlash) ,s.substring(indexBraceLeft+1,indexSlash));
            }
        }
		return packageMap;
	}
	
	public static boolean ignoreBatteryOptimization(Context context){
		PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		if(!powerManager.isIgnoringBatteryOptimizations(context.getPackageName())){
			Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:"+context.getPackageName()));
			context.startActivity(intent);
			return false;
		}
		return true;
	}
}
