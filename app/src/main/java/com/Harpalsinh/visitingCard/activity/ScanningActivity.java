package com.Harpalsinh.visitingCard.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.Harpalsinh.visitingCard.R;
import com.Harpalsinh.visitingCard.other.ContactBean;
import com.Harpalsinh.visitingCard.other.Filtering;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class ScanningActivity extends AppCompatActivity {

    private final String TAG = "ScanningActivity";
    private LinearLayout ll;
    Spinner sparrat[];
    EditText etarray[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ll = (LinearLayout) findViewById(R.id.linearLayoutScanningActivity);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //For Image hangling
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                Log.d("Error", e.getMessage());
                Toast.makeText(ScanningActivity.this, e.getMessage(), Toast.LENGTH_LONG);
            }

            @Override
            public void onImagesPicked(List<File> imagesFiles, EasyImage.ImageSource source, int type) {

                Bitmap bitmap = null;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                ExifInterface ei = null;
                try {
                    ei = new ExifInterface(imagesFiles.get(0).getAbsolutePath());
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(imagesFiles.get(0)), null, options);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("ImageRotation", "Error in ImageRotation");
                }
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                switch (orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        bitmap = rotateImage(bitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        bitmap = rotateImage(bitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        bitmap = rotateImage(bitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:

                    default:
                        break;
                }
                handllimage(bitmap);
            }
        });
    }
    public void buttonclicked(View v) {
        ll.removeAllViews();
        EasyImage.openChooserWithGallery(ScanningActivity.this,"Choose from",1);
        EasyImage.configuration(this).setImagesFolderName("My app images").setAllowMultiplePickInGallery(false);
    }
    public void handllimage(Bitmap bitmap) {

        //Setting whole image
        // imgView.setImageBitmap(bitmap);
        if (bitmap != null) {
            TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
            if (!textRecognizer.isOperational()) {
                Log.w(TAG, "Detector dependencies are not yet available.");
                IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;
                if (hasLowStorage) {
                    Toast.makeText(this, "Low Storage", Toast.LENGTH_LONG).show();
                    Log.w(TAG, "Low Storage");
                }
            }
            //Frame Builder for Extraction
            Frame imageFrame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
            String fina = "";
            Bitmap workingFrame = imageFrame.getBitmap();
            sparrat=new Spinner[textBlocks.size()];
            etarray=new EditText[textBlocks.size()];
            List<String> spinner_value=new ArrayList<String>();
            spinner_value.add("Discard");
            spinner_value.add("Name");
            spinner_value.add("Email");
            spinner_value.add("Number");
            spinner_value.add("Website");
            spinner_value.add("Adress");
            ArrayAdapter<String> aa = new ArrayAdapter<String>(ScanningActivity.this, R.layout.support_simple_spinner_dropdown_item, spinner_value);
            aa.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            for (int i = 0; i < textBlocks.size(); i++) {

                TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                List<Text> temp = (List<Text>) textBlock.getComponents();
                Filtering f = new Filtering();
                f.filterr(temp);
                List<String> ext = f.getList(temp);
                CardView cv = new CardView(this);
                LinearLayout cvll = new LinearLayout(this);
                cvll.setOrientation(LinearLayout.VERTICAL);
                etarray[i]= new EditText(this);
                etarray[i].setText(textBlock.getValue());
                sparrat[i] = new Spinner(this);

                sparrat[i].setAdapter(aa);
                cvll.addView(etarray[i]);
                cvll.addView(sparrat[i]);
                cv.addView(cvll);
                ll.addView(cv);
                //                String text = textBlock.getValue();
//                fina += text + "\t\t" + textBlock.getBoundingBox().flattenToString() + "\n\n";

            }
            Button bsave=new Button(this);
            bsave.setText("Save to Contact");
            ll.addView(bsave);
            bsave.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    saveToContact();
                }
            });

            // TextView tv=(TextView)findViewById(R.id.textView);
            //  tv.setText(fina);
        }
    }
    public void saveToContact()
    {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        ContactBean cb=new ContactBean();
        for(int i=0;i<sparrat.length;i++)
        {
            Spinner sptemp=sparrat[i];
            EditText edtemp=etarray[i];
            switch(sptemp.getSelectedItemPosition())
            {
                case 1:
                    cb.setName(edtemp.getText().toString());
                    intent.putExtra(ContactsContract.Intents.Insert.NAME, cb.getName());
                    break;
                case 2:
                    cb.setEmail(edtemp.getText().toString());
                    intent.putExtra(ContactsContract.Intents.Insert.EMAIL, cb.getEmail());
                    break;
                case 3:
                    cb.setMobile(edtemp.getText().toString());
                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, cb.getMobile());
                    break;
                case 4:
                    cb.setWebsite(edtemp.getText().toString());
                    intent.putExtra(ContactsContract.Intents.Insert.COMPANY, cb.getWebsite());
                    break;
                case 5:
                    cb.setAdress(edtemp.getText().toString());
                    intent.putExtra(ContactsContract.Intents.Insert.NOTES, cb.getAdress());
                    break;

            }
        }
        startActivity(intent);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        if (true)
            return source;
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}
