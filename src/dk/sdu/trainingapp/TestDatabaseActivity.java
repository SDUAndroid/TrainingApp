package dk.sdu.trainingapp;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

/**
 * Mandatory 4 extension by
 * 
 * @author Alejandro Jorge �lvarez & Lucas Grzegorczyk
 */

public class TestDatabaseActivity extends ListActivity {
	private CommentsDataSource datasource;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_test_database);

		this.datasource = new CommentsDataSource(this);
		this.datasource.open();

		List<Counter> values = this.datasource.getAllCounters();

		// Use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<Counter> adapter = new ArrayAdapter<Counter>(this,
				android.R.layout.simple_list_item_1, values);
		this.setListAdapter(adapter);
	}

	// Will be called via the onClick attribute
	// of the buttons in main.xml
	public void onClick(View view) {
		@SuppressWarnings("unchecked")
		ArrayAdapter<Counter> adapter = (ArrayAdapter<Counter>) this
				.getListAdapter();
		Counter counter = null;
		switch (view.getId()) {
		case R.id.add:
//			int[] counters = new int[] { 0,3,5 };
//			int nextInt = new Random().nextInt(3);
			// Save the new comment to the database
			counter = this.datasource.createMaxCount(MainActivity.stretchCounter);
			adapter.add(counter);
			MainActivity.stretchCounter = 0;
			break;
		case R.id.delete:
			if (this.getListAdapter().getCount() > 0) {
				counter = (Counter) this.getListAdapter().getItem(0);
				this.datasource.deleteCounter(counter);
				adapter.remove(counter);
			}
			break;
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		this.datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		this.datasource.close();
		super.onPause();
	}

}