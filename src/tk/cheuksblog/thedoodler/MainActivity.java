package tk.cheuksblog.thedoodler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.graphics.pdf.PdfRenderer.Page;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements BrushAttrFrag.OnFinishedPickingAttrs,
															MainInterFrag.OnLongClickBts
{
	final int SELECT_PHOTO = 1;
	final int SELECT_PDF = 2;
	
	FragmentManager fm;
	MainInterFrag mif;
	BrushAttrFrag baf;
	HelpFrag hf;
	
	char pickingmode = 'N';
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		mif = new MainInterFrag();
		baf = new BrushAttrFrag();
		hf = new HelpFrag();
		ft.add(R.id.ui_container, mif);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.brush_settings) {
			// Switch fragments to brush attribute fragment
			FragmentTransaction ft = fm.beginTransaction();
			ft.add(R.id.ui_container, baf);
			ft.addToBackStack(null);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.commit();
			return true;
		} else if (id == R.id.save_item) {
			// If you want to save the image
			mif.saveToFile();
			return true;
		} else if (id == R.id.load_item) {
			// If you want to load image from file
			Intent photopick = new Intent(Intent.ACTION_PICK);
			photopick.setType("image/*");
			startActivityForResult(photopick, SELECT_PHOTO);
			return true;
		} else if (id == R.id.clear_screen) {
			// Clear the canvas
			mif.clear_canvas();
			return true;
		} else if (id == R.id.import_pdf) {
			// Your phone API must be above or equal to API 21
			// to use this feature
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				// Import first page of pdf file
				// Render to canvas
				Intent pdfpick = new Intent(Intent.ACTION_PICK);
				pdfpick.setType("pdf/*");
				startActivityForResult(pdfpick, SELECT_PDF);
			}
			else {
				// Display toast
				// not supported
				Toast.makeText(getApplicationContext(),
						getResources().getText(R.string.api_nosupport),
						Toast.LENGTH_LONG).show();
			}
			return true;
		} else if (id == R.id.help) {
			// HALP
			FragmentTransaction ft = fm.beginTransaction();
			ft.add(R.id.ui_container, hf);
			ft.addToBackStack(null);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.commit();
			return true;
		} else if (id == R.id.about) {
			// About with dialog
			String name = getResources().getText(R.string.app_name).toString();
			String version = "Unknown version";
			try {
				version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			AlertDialog.Builder bdr = new AlertDialog.Builder(this);
			bdr.setTitle("About " + name);
			bdr.setMessage(name + " " + version + "\nBy Cheuk Yin Ng <cheukyin699@yahoo.com>");
			bdr.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onFinishedPickingAttrs(int color, int size) {
		if (pickingmode == 'N') {
			mif.updateColor(color);
		} else if (pickingmode == 'F') {
			// Foreground
			mif.setFgC(color);
		} else if (pickingmode == 'B') {
			// Background
			mif.setBgC(color);
		}
		mif.updateBS(size);
	}

	@Override
	public void onLongClick(char c) {
		pickingmode = c;
		
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.ui_container, baf);
		ft.addToBackStack(null);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.commit();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onActivityResult(int request, int result, Intent retIntent) {
		super.onActivityResult(request, result, retIntent);
		
		switch (request) {
		case SELECT_PHOTO:
			if (result == RESULT_OK) {
				try {
					final Uri imgUri = retIntent.getData();
					mif.filename = imgUri.getLastPathSegment();
					final InputStream imgStream = getContentResolver().openInputStream(imgUri);
					final Bitmap selected = BitmapFactory.decodeStream(imgStream);
					mif.setBitmap(selected);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			break;
		case SELECT_PDF:
			if (result == RESULT_OK) {
				try {
					final Uri pdfUri = retIntent.getData();
					mif.filename = pdfUri.getLastPathSegment();
					File f = new File(pdfUri.toString());
					ParcelFileDescriptor pfd = ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY);
					PdfRenderer prend = new PdfRenderer(pfd);
					
					// Only do the first page
					Page page = prend.openPage(0);
					Bitmap bm = Bitmap.createBitmap(mif.getCanvasWidth(), mif.getCanvasHeight(), Bitmap.Config.ARGB_8888);
					page.render(bm, null, null, Page.RENDER_MODE_FOR_DISPLAY);
					
					// Send to background
					mif.setBitmap(bm);
					
					page.close();
					
					prend.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		default:
			break;
		}
	}
}
