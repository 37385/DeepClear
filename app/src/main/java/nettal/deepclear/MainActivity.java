package nettal.deepclear;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import android.content.Intent;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		Button startService = findViewById(R.id.activity_main_StartService);
		startService.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1){
					if(Utilities.ignoreBatteryOptimization(MainActivity.this))
					startService(new Intent(MainActivity.this , ForceStopService.class));
				}
		});
    }
    
}
