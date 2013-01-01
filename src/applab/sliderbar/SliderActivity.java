package applab.sliderbar;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import applab.sliderbar.SliderFragment.OnSliderChangedListener;
import applab.sliderbar.widget.SliderBar;

/**
 * Example Activty of a how to use the slider widget
 *
 */
public class SliderActivity extends FragmentActivity implements
		OnSliderChangedListener {

	private ResultsPaneFragment pane;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		pane = (ResultsPaneFragment) getSupportFragmentManager()
				.findFragmentById(R.id.pane);
		if (pane == null) {
			// Make new fragment to show this selection.
			pane = new ResultsPaneFragment();

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.replace(R.id.pane, pane);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
		}
	}
	
	@Override
	public void onSliderChanged(List<SliderBar> sliders,
			List<Integer> slidersChanged) {
		// Pass on the event to the Fragment that is responsible for displaying the results
		pane.onSliderChange(sliders, slidersChanged);
	}
}
