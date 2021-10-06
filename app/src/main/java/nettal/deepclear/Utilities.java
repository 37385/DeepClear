package nettal.deepclear;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utilities
{
	static boolean shouldPrintLog = true;
	public static String printLog(String s){
		if (shouldPrintLog)
			Log.e("Info" , s);
		return s;
	}

	public static Exception printLog(Exception e){
		if (shouldPrintLog){
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(e.toString());
			for(StackTraceElement h :e.getStackTrace()){
				stringBuilder.append("\n");
				stringBuilder.append("    at ");
				stringBuilder.append(h);
				}
			Log.e("Exception" , stringBuilder.toString());
		}
		return e;
	}

	public static ArrayList<String> printLog(ArrayList<String> arrayList){
		if (shouldPrintLog)
			Log.e("Info" , arrayList.toString());
		return arrayList;
	}

	public static List<PackageInfo> getAllApplications(Context context){
		return context.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES|PackageManager.GET_SERVICES);
	}
	
	public static boolean isSystemApp(PackageInfo packageinfo){
		return (packageinfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
	}
	
	public static boolean isSystemApp(String packageName , Context context) throws PackageManager.NameNotFoundException{
		return isSystemApp(context.getPackageManager().getPackageInfo(packageName,0));
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
