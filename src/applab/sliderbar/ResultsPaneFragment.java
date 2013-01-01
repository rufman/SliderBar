package applab.sliderbar;

import java.util.List;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import applab.sliderbar.widget.SliderBar;

/**
 * Example Fragement implementation showing how to use the slider widget
 */
public class ResultsPaneFragment extends Fragment {

	private AlertDialog.Builder builder;
	private GridView gridview; // An example of a potencial layout for the results of the slider changes

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		gridview = (GridView) getActivity().findViewById(
				R.id.apps_overview_grid);

		builder = new AlertDialog.Builder(this.getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.activity_apps_overview, container,
				false);
	}

	/**
	 * Method that is called when a slider is changed.
	 * 
	 * This handles everything that needs to happen to display the proper apps
	 * in the {@link GridView}.
	 */
	public void onSliderChange(List<SliderBar> sliders,
			List<Integer> slidersChanged) {
		// Display the results of the changed sliders here. 
		// The popup is just to show that it works
		String slidersString = "";
		for (Integer id : slidersChanged){
			if (!id.equals(-1)){
				slidersString += id.toString()+", ";
			}
		}
		builder.setMessage("Changed the sliders: " + slidersString)
				.setCancelable(true);
		AlertDialog slidersChangedDialog = builder.create();
		slidersChangedDialog.show();
	}
}
