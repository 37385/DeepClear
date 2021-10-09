package nettal.deepclear;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {

    public static final String FileName = "WhiteList";
    public Button whiteList;
    SearchableDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startService = findViewById(R.id.activity_main_StartService);
        whiteList = findViewById(R.id.activity_main_WhiteList);
        Button about = findViewById(R.id.activity_main_About);
        startService.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View p1) {
                stopService(new Intent(MainActivity.this, ForceStopService.class));
                if (Utilities.ignoreBatteryOptimization(MainActivity.this))
                    startService(new Intent(MainActivity.this, ForceStopService.class));
            }
        });
        whiteList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        whiteList.setEnabled(false);
        whiteList.post(new Runnable() {
            @Override
            public void run() {
                final List<ApplicationInfo> fullAppList = Utilities.getAllApplications(MainActivity.this);
                final ArrayList<DialogView> fullAppView = new ArrayList<>();
                try {//获取到白名单
                    HashMap<String, Boolean> hashMap = (HashMap<String, Boolean>) Utilities.loadObjectFromFile(MainActivity.this, FileName);
                    for (ApplicationInfo info : fullAppList) {
                        DialogView dv = new DialogView(MainActivity.this, info, (int) (whiteList.getWidth() / 1.6), whiteList.getTextSize() / 1.7f);
                        dv.setEnabled(hashMap.getOrDefault(info.packageName, Utilities.isSystemApp(info)));
                        fullAppView.add(dv);
                    }
                } catch (Exception e) {//没获取到白名单
                    for (ApplicationInfo info : fullAppList) {
                        DialogView dv = new DialogView(MainActivity.this, info, (int) (whiteList.getWidth() / 1.6), whiteList.getTextSize() / 1.7f);
                        dv.setEnabled(Utilities.isSystemApp(info) || info.packageName.equals(MainActivity.this.getPackageName()));
                        fullAppView.add(dv);
                    }
                }
                dialog = new SearchableDialog(MainActivity.this, fullAppView);
                dialog.setPositiveButton("OK", null);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        HashMap<String, Boolean> hashMap = new HashMap<>();
                        for (DialogView dialogView : fullAppView) {
                            hashMap.put(dialogView.getApplicationInfo().packageName, dialogView.isEnabled());
                        }
                        try {
                            Utilities.saveObjectToFile(MainActivity.this, hashMap, FileName);
                        } catch (Exception e) {
                            Utilities.toast(Utilities.printLog(e), MainActivity.this);
                        }
                        stopService(new Intent(MainActivity.this, ForceStopService.class));
                        if (Utilities.ignoreBatteryOptimization(MainActivity.this))
                            startService(new Intent(MainActivity.this, ForceStopService.class));
                    }
                });
                whiteList.setEnabled(true);
            }
        });
        about.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, R.string.about_detail, Toast.LENGTH_LONG).show();
            }
        });
        about.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/37385/DeepClear")));
                return true;
            }
        });
    }
}

class DialogView extends LinearLayout {
    private final TextView textView;
    private final ApplicationInfo applicationInfo;

    public DialogView(Context context, ApplicationInfo info, int iconSize, float textSize) {
        super(context);
        applicationInfo = info;
        textView = new TextView(context);
        textView.setTextSize(textSize);
        textView.setText(context.getPackageManager().getApplicationLabel(info).toString().equals(info.packageName)
                ? info.packageName : context.getPackageManager().getApplicationLabel(info) + "\n" + info.packageName);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageDrawable(context.getPackageManager().getApplicationIcon(info));
        addView(imageView, iconSize, iconSize);
        addView(textView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        this.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
    }

    @Override
    public String toString() {
        return textView.getText().toString();
    }

    @Override
    public void setEnabled(boolean enabled) {
        textView.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }
}
