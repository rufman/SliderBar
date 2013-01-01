package applab.sliderbar;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Example for an Activity that displays the results of the slider widget
 */
public class ResultsPane extends FragmentActivity {
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apps_overview);
	}
	
}
