package nettal.deepclear;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;
import java.util.ArrayList;

public class ForceStopService extends Service
{
	public static boolean isStart;
	@Override
	public IBinder onBind(Intent p1)
	{
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if (isStart)
			return super.onStartCommand(intent, flags, startId);
		new ForceStopThread(this).start();
		isStart = true;
		return super.onStartCommand(intent, flags, startId);
	}
}
class ForceStopThread extends Thread
{
	Context context;
	public ForceStopThread(Context context){
		this.context = context;
	}
	@Override
	public void run(){
		try{
			Command command = new Command();
			ArrayList<String> packageListBefore = Utilities.getAppPackagesFromRecents(
					command.exec("dumpsys activity | grep recents"));
			while (ForceStopService.isStart){
				sleep(1000);
				ArrayList<String> packageList = Utilities.getAppPackagesFromRecents(
						command.exec("dumpsys activity | grep recents"));
				StringBuilder stringBuilder = new StringBuilder();
				for (String packageName:packageListBefore) {
					if (!packageList.contains(packageName) && !Utilities.isSystemApp(packageName , context)
							&&!packageName.equals(context.getPackageName())){
						stringBuilder.append(packageName);
						stringBuilder.append(";");
						command.exec("am force-stop "+packageName);
					}
				}
				if (stringBuilder.length()!=0){
					toast("Killed:"+stringBuilder.deleteCharAt(stringBuilder.length()-1));
				}
				packageListBefore = packageList;
			}
		}
		catch (Exception e){
			Utilities.printLog(e);
			ForceStopService.isStart = false;
		}
	}
	
	private void toast(final String s){
		new Handler(Looper.getMainLooper()).post(new Runnable(){
				@Override
				public void run(){
					Toast.makeText(context ,s,Toast.LENGTH_SHORT).show();
				}
			});
	}
}
