package tk.cheuksblog.thedoodler;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class MainInterFrag extends Fragment {
	
	public OnLongClickBts mCallback;
	public String filename = "untitled.png";
	
	private Button fgBt, bgBt;
	private DrawingView canvasDv;
	
	private int fgColor, bgColor;
	private boolean success = false;
	
	public interface OnLongClickBts {
		public void onLongClick(char c);
	}
	
	public void updateColor(int color) {
		canvasDv.updateColor(color);
	}
	
	public void updateBS(int size) {
		canvasDv.updateBS(size);
	}
	
	public void setFgC(int c) {
		fgBt.setBackgroundColor(c);
		fgColor = c;
		fgBt.postInvalidate();
	}
	
	public void setBgC(int c) {
		bgBt.setBackgroundColor(c);
		bgColor = c;
		bgBt.postInvalidate();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.main_inter_frag, container, false);

		fgBt = (Button) v.findViewById(R.id.fgBt);
		bgBt = (Button) v.findViewById(R.id.bgBt);
		canvasDv = (DrawingView) v.findViewById(R.id.canvasDv);
		
		// Set default colours
		fgColor = Color.BLACK;
		bgColor = Color.WHITE;
		fgBt.setBackgroundColor(fgColor);
		bgBt.setBackgroundColor(bgColor);
		
		// For a short press, just switch to that color
		fgBt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				canvasDv.updateColor(fgColor);
			}
		});
		bgBt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				canvasDv.updateColor(bgColor);
			}
		});
		
		// For longer presses, switch to the brush attr fragment
		fgBt.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				mCallback.onLongClick('F');
				return true;
			}
		});
		bgBt.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				mCallback.onLongClick('B');
				return true;
			}
		});
		
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			mCallback = (OnLongClickBts) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnLongClickBts");
		}
	}
	
	public void setBitmap(Bitmap bm) {
		canvasDv.setBitmap(bm);
	}
	
	public void saveToFile() {
		final Bitmap bm = canvasDv.getBitmap();
		final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
		
		// Build dialog
		AlertDialog.Builder bdr = new AlertDialog.Builder(this.getActivity());
		bdr.setTitle("Enter Filename");
		final EditText intxt = new EditText(this.getActivity());
		// Specify the type of input
		intxt.setInputType(InputType.TYPE_CLASS_TEXT);
		intxt.setText(filename);
		bdr.setView(intxt);
		// Set up buttons
		bdr.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				filename = intxt.getText().toString();
				success = true;
				// If successfully pressed save
				if (success && filename != "") {
					File f = new File(path + File.separator + filename);
					
					try {
						if (!f.exists()) {
							f.createNewFile();
						}
						FileOutputStream fos = new FileOutputStream(f);
						bm.compress(CompressFormat.PNG, 10, fos);
						fos.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					success = false;		// for next time
				}
			}
		});
		bdr.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		bdr.show();
	}
	
	public void clear_canvas() {
		canvasDv.reset_fg();
	}
	
	public int getCanvasHeight() {
		return canvasDv.getHeight();
	}
	
	public int getCanvasWidth() {
		return canvasDv.getWidth();
	}
	
}
