package dk.sdu.trainingapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * 		Mandatory 3 extension by
 * @author Alejandro Jorge Álvarez & Lucas Grzegorczyk
 *
 */
public class MainActivity extends Activity {

	private static final Boolean D = true;
	private static final String TAG = "MainActivity";

	private EditText mET1 = null;
	private TextView tv = null;
	private SeekBar sb = null;
	private int mET1_lines = 0;

	private BtMultiResponseReceiver btMultiResponseReceiver = null;
	private IntentFilter multiFilter = null;
	private IntentFilter multiHrFilter = null;

	private static final int BT_DEVICE_1_ID = 1;
	private static final String BT_DEVICE_1_MAC = "00:06:66:49:59:0F";

	private BtConnectorThreaded btct1 = null, btct2 = null;
	private BtConnectorPolarThreaded btct3 = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D)
			Log.d(TAG, "+++ On Create +++");

		setContentView(R.layout.activity_main);

		// Setup views
		mET1 = (EditText) findViewById(R.id.editText1);
		tv = (TextView) findViewById(R.id.textView1);
		sb = (SeekBar) findViewById(R.id.seekBar1);

		mET1.setEnabled(false);

		((Button) findViewById(R.id.button1))
				.setOnClickListener(mOnClickListener);
		((Button) findViewById(R.id.button2))
		.setOnClickListener(mOnClickListener);
	
		// Setup broadcast receiver
		btMultiResponseReceiver = new BtMultiResponseReceiver();
		multiFilter = new IntentFilter(BtConnectorThreaded.BT_NEW_DATA_INTENT);
		multiHrFilter = new IntentFilter(
				BtConnectorPolarThreaded.BT_NEW_DATA_INTENT);

	}

	@Override
	protected void onStart() {
		super.onStart();
		if (D)
			Log.d(TAG, "++ On Start ++");
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (D)
			Log.d(TAG, "+ On Resume +");
		registerReceiver(btMultiResponseReceiver, multiFilter);
		registerReceiver(btMultiResponseReceiver, multiHrFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (D)
			Log.d(TAG, "- On Pause -");
		unregisterReceiver(btMultiResponseReceiver);
	}

	@Override
	protected void onStop() {
		if (D)
			Log.d(TAG, "-- On Stop --");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (D)
			Log.d(TAG, "--- On Destroy ---");
		if (btct1 != null)
			btct1.disconnect();
		if (btct2 != null)
			btct2.disconnect();
		if (btct3 != null)
			btct3.disconnect();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (D)
			Log.d(TAG, "+ On Restore Instance State +");
		super.onRestoreInstanceState(savedInstanceState);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (D)
			Log.d(TAG, "- On Save Instance State -");
		super.onSaveInstanceState(outState);
	}


	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			
			switch (v.getId()) {

			case R.id.button1: {
				mET1.append("\nEnabled...waiting data");
				btct1 = new BtConnectorThreaded(getApplicationContext(),
						BT_DEVICE_1_MAC, BT_DEVICE_1_ID);
				btct1.connect();
			}
			break;
			
			case R.id.button2:{
				
				Intent myIntent = new Intent(v.getContext(), TestDatabaseActivity.class);
                startActivityForResult(myIntent, 0);
			}
			break;
			
			
		
			}

		}
	};

	private class BtMultiResponseReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			int id = 0;
			String line = "";
			

			if (intent
					.hasExtra(BtConnectorThreaded.BT_NEW_DATA_INTENT_EXTRA_BT_DATA)) {
				line = intent
						.getStringExtra(BtConnectorThreaded.BT_NEW_DATA_INTENT_EXTRA_BT_DATA);
				id = intent
						.getIntExtra(
								BtConnectorThreaded.BT_NEW_DATA_INTENT_EXTRA_BT_DATA_STREAM_ID,
								0);

			} else if (intent
					.hasExtra(BtConnectorPolarThreaded.BT_NEW_DATA_INTENT_EXTRA_BT_HR_DATA)) {
				line = ""
						+ intent.getIntExtra(
								BtConnectorPolarThreaded.BT_NEW_DATA_INTENT_EXTRA_BT_HR_DATA,
								0);
				id = intent
						.getIntExtra(
								BtConnectorPolarThreaded.BT_NEW_DATA_INTENT_EXTRA_BT_DATA_STREAM_ID,
								0);
			}

			switch (id) {

			case BT_DEVICE_1_ID:
				mET1_lines++;
				// mET1.append("\n" + mET1_lines + ": " + line);
				//mET1.setText("\n" + mET1_lines + ": " + line);

				tv.setText(this.getStrength(line));
				sb.setProgress(Integer.parseInt(line)-42000);

				break;

			}

		}

		/**
		 * 
		 * @param value
		 *            from the sensor
		 * @return conversion to low,normal,hard strength
		 */
		private String getStrength(String value) {

			int strengthBT = Integer.parseInt(value);
			String strength = null;

			if (strengthBT < 48000) {

				strength = "More please";// low
			}
			if (strengthBT > 55000) {
				strength = "Woaaah";
			}
			if (strengthBT >= 48000 && strengthBT <= 55000) {
				strength = "Okay...";
			}

			return strength;
		}

	}

}
