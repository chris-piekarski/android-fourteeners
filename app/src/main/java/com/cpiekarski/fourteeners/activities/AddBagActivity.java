package com.cpiekarski.fourteeners.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cpiekarski.fourteeners.R;
import com.cpiekarski.fourteeners.SummitRegister;
import com.cpiekarski.fourteeners.register.RegisterEntry;
import com.cpiekarski.fourteeners.utils.Mountain;
import com.cpiekarski.fourteeners.utils.Mountains;
import com.cpiekarski.fourteeners.utils.RegisterDate;
import com.cpiekarski.fourteeners.utils.SRLOG;
// Analytics removed

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AddBagActivity extends Activity implements OnItemSelectedListener {
    private static final String TAG = "RegisterAddBagActivity";
    private Spinner mRanges;
    private Spinner mPeaks;
    private TextView mStartTime;
    private TextView mEndTime;
    private TextView mDate;
    private EditText mNotes;
    private ImageView mProof;
    private RegisterDate mStartDate;
    private RegisterDate mEndDate;
    private String mProofPath;
    


    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private URI fileUri;
    
    private File imageDir;
    
    private ArrayAdapter<String> peakAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbag);

        mRanges = (Spinner) findViewById(R.id.ranges_spinner);
        mPeaks = (Spinner) findViewById(R.id.peak_spinner);
        mStartTime = (TextView) findViewById(R.id.start_time);
        mEndTime = (TextView) findViewById(R.id.end_time);
        mDate = (TextView) findViewById(R.id.date);
        mProof = (ImageView) findViewById(R.id.image_proof);
        mNotes = (EditText) findViewById(R.id.notes);
        
        mStartDate = new RegisterDate();
        mEndDate = new RegisterDate();
        
        imageDir = getApplicationContext().getDir("proof", Context.MODE_PRIVATE); // create a directory to store images

        
        peakAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list_item);
        peakAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPeaks.setAdapter(peakAdapter);
        
        ArrayAdapter<String> rangeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list_item);
        rangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rangeAdapter.add("All");
        rangeAdapter.addAll(Mountains.getInstance(this).getRanges());        
        mRanges.setAdapter(rangeAdapter);
        mRanges.setOnItemSelectedListener(this);
        
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    private String getStartTime() {    
        return mStartDate.getStrDate();  
    }
    
    private String getEndTime() {
        return mEndDate.getStrDate();
    }
    
    public void addAddPeak(View view) {
        RegisterEntry entry = new RegisterEntry(this);
        Mountain mnt = Mountains.getInstance(this).getMountain(mPeaks.getSelectedItem().toString(), 
                mRanges.getSelectedItem().toString());
        entry.setMountain(mnt);
        entry.setPeekElevation(mnt.getElevation());
        entry.setStartTime(getStartTime());
        entry.setEndTime(getEndTime());
        entry.setProof(mProofPath);
        entry.setNotes(mNotes.getText().toString());
        entry.setReachedSummit(true);
        
        if(entry.createEntry()) {
            SRLOG.d(TAG, "Entry created.");
            Toast.makeText(this, "Entry added!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            SRLOG.e(TAG, "Entry not added; failed to create.");
            Toast.makeText(this, "Failed to add entry...", Toast.LENGTH_SHORT).show();
        }
    }
    
    public void cancelAddPeak(View view) {
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        String range = (String) arg0.getItemAtPosition(arg2);
        SRLOG.d(TAG, "Spinner range set to " + range );
        
        if(range.equals("All")) {
            peakAdapter.clear();
            peakAdapter.addAll(Mountains.getInstance(this).getAllPeakNames());
        } else {
            peakAdapter.clear();
            peakAdapter.addAll(Mountains.getInstance(this).getNamesInRange(range));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
        
    }
    
    public void showStartTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "startTimePicker");
    }
    
    public void showEndTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "endTimePicker");
    }
    
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
    
    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }
    
    public void sendGetProofIntent(View v) {
        // create Intent to take a picture and return control to the calling application
        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //intent.setAction(Intent.ACTION_PICK);
        
        SRLOG.v(TAG, ""+imageDir);
        File imageFile = new File(imageDir, "blah.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFile.toURI()); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    
    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        
        if (height > reqHeight || width > reqWidth) {
        
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
        
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        
        return inSampleSize;
    }
    
    public Bitmap decodeSampledBitmapFromUri(String pathName, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
/*                Toast.makeText(this, "Image saved to:\n" +
                         data.getData(), Toast.LENGTH_LONG).show();*/
                SRLOG.d(TAG, data.getData().toString());
                try {
                    Uri selectedImageUri = data.getData();
                    String selectedImagePath = getPath(selectedImageUri);
                    mProofPath = selectedImagePath;
                    SRLOG.d(TAG, "Content Picked: " + data.getData());
                    SRLOG.d(TAG, "Content Absolute Path : " + selectedImagePath);
                    
                    mProof.setImageBitmap(decodeSampledBitmapFromUri(selectedImagePath, mProof.getWidth(), mProof.getHeight()));

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    SRLOG.e(TAG, e.toString());
                    e.printStackTrace();
                }
                //File imageFile = new File(imageDir, );

            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }
    
    public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }
        
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            
            if(this.getTag().equals("startTimePicker")) {
                mStartTime.setText(""+hourOfDay+":"+minute);
                mStartDate.setTime(hourOfDay, minute, 0);
            } else {
                mEndTime.setText(""+hourOfDay+":"+minute);
                mEndDate.setTime(hourOfDay, minute, 0);
            }
        }
    }
    
    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            
            
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
        
        public void onDateSet(DatePicker view, int year, int month, int day) {
            mDate.setText(""+(month+1)+"/"+day+"/"+year);
            
            mStartDate.setDate(month, day, year);
            mEndDate.setDate(month, day, year);
        }
    }
}