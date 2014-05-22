package edu.ucsb.cs.cs185.dvalderrama.dvalderramaMultiTouch;

import java.io.File;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MainActivity extends ActionBarActivity {

	private DialogFragment helpFrag = new HelpFragment();
	private DialogFragment settingsFrag = new SettingsFragment();
	private String mCurrentPhotoPath;
	private static int RESULT_LOAD_IMAGE = 1;
	private static TouchView touchView;
	private Drawable bg_drawable;
	private Bitmap bitmap;
	private static Marker marker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		switch (item.getItemId()) {
		case R.id.action_picture:
			Log.d("action", "picture action");
			Intent i = new Intent(
					Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

			startActivityForResult(i, RESULT_LOAD_IMAGE);
			return true;
		case R.id.action_settings:
			settingsFrag.show(getSupportFragmentManager(), "settings_menu");
			return true;
		case R.id.action_help:
			helpFrag.show(getSupportFragmentManager(), "help_menu");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		
		}
		
	}
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
 
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
 
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mCurrentPhotoPath = cursor.getString(columnIndex);
            cursor.close();
             
            //ImageView imageView = (ImageView) findViewById(R.id.pictureView);
            //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            scaleAndSetPicture();
        }   
    }
	private void scaleAndSetPicture() {
		//ImageView mImageView = (ImageView) findViewById(R.id.pictureView);
		touchView = (TouchView) findViewById(R.id.touchView);
	    // Get the dimensions of the View
	    int targetW = touchView.getWidth();
	    int targetH = touchView.getHeight();

	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;

	    // Determine how much to scale down the image
	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;

	    //Create the drawable from the bitmap
	    bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    bg_drawable = new BitmapDrawable(bitmap);
	    //Display the drawable on the layout background
	    //touchView.setBackgroundDrawable(bg_drawable);
	    touchView.setImageBitmap(bitmap);

	}
	
	public boolean onTouchEvent(MotionEvent event)
	{
		//Log.d("touch", "this was touched");
		ImageView v = (ImageView)findViewById(R.id.touchView);
		try{
			touchView.onTouch(v, event, bitmap);
		}
		catch(Exception e){
			Log.d("touch", "need to select a picture from gallery before applying transforms!");
		}
		
		//draw marker
		marker = (Marker)findViewById(R.id.marker);
		//get transformation matrix from the touch view to pass to the marker canvas matrix
		Matrix m = touchView.getMatrix();
		marker.onTouch(event, m);
		
		return true;
		
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			//after view has been inflated
			//load default image
			String mPath = "";
			try{
				mPath = (Environment.getExternalStorageDirectory() + 
						File.separator + "ucsbmap.png").toString();
				Log.d("path", mPath);
			}
			catch(Exception e){
				Log.d("file", "ucsbmap.png doesn't exist!");
			}
			if(mPath != ""){
				Bitmap b = BitmapFactory.decodeFile(mPath);
				touchView = (TouchView) rootView.findViewById(R.id.touchView);
				touchView.setImageBitmap(b);
				
				//set Z order
				marker = (Marker) rootView.findViewById(R.id.marker);
				marker.setZOrderOnTop(true);
						
			}
			else
				Log.d("file", "ucsbmap.png failed to load");
			
			return rootView;
		}
	}

}
