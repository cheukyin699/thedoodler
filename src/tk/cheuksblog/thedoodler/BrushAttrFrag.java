package tk.cheuksblog.thedoodler;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

public class BrushAttrFrag extends Fragment {
	
	OnFinishedPickingAttrs mCallback;
	
	private SeekBar alphaSb, redSb, greenSb, blueSb, sizeSb;
	private Button doneBt;
	
	public interface OnFinishedPickingAttrs {
		public void onFinishedPickingAttrs(int color, int size);
	}
	
	public int grabColor() {
		return Color.argb(alphaSb.getProgress(), redSb.getProgress(), greenSb.getProgress(), blueSb.getProgress());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.brush_attr_frag, container, false);
		
		// Set background
		v.setBackgroundColor(Color.BLACK);
		
		alphaSb = (SeekBar) v.findViewById(R.id.alphaSb);
		redSb = (SeekBar) v.findViewById(R.id.redSb);
		greenSb = (SeekBar) v.findViewById(R.id.greenSb);
		blueSb = (SeekBar) v.findViewById(R.id.blueSb);
		sizeSb = (SeekBar) v.findViewById(R.id.brushsizeSb);
		doneBt = (Button) v.findViewById(R.id.doneBt);
		
		SeekBar.OnSeekBarChangeListener sbcListener = new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				v.setBackgroundColor(grabColor());
				mCallback.onFinishedPickingAttrs(grabColor(), sizeSb.getProgress()+1);
			}
		};
		
		alphaSb.setOnSeekBarChangeListener(sbcListener);
		redSb.setOnSeekBarChangeListener(sbcListener);
		greenSb.setOnSeekBarChangeListener(sbcListener);
		blueSb.setOnSeekBarChangeListener(sbcListener);
		
		doneBt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onFinishedPickingAttrs(grabColor(), sizeSb.getProgress()+1);
				((MainActivity)getActivity()).fm.popBackStack();
			}
		});
		
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			mCallback = (OnFinishedPickingAttrs) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnFinishedPickingAttrs");
		}
	}
	
}
