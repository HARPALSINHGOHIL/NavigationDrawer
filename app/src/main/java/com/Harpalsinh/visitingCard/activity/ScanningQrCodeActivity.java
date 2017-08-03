package com.Harpalsinh.visitingCard.activity;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Harpalsinh.visitingCard.R;
import com.Harpalsinh.visitingCard.other.ContactBean;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanningQrCodeActivity extends AppCompatActivity {

    private IntentIntegrator qrScan;
    private LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning_qr_code);
        qrScan = new IntentIntegrator(this);
        ll = (LinearLayout) findViewById(R.id.linearLayout_Vertical);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                //try {
                //converting the data to json
                Gson gson = new Gson();
                ContactBean cb = gson.fromJson(result.getContents(), ContactBean.class);
                TextView tv=new TextView(this);
                tv.setText(result.getContents());
                ll.addView(tv);
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.NAME, cb.getName());
                intent.putExtra(ContactsContract.Intents.Insert.EMAIL, cb.getEmail());
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, cb.getMobile());
                intent.putExtra(ContactsContract.Intents.Insert.COMPANY, cb.getWebsite());
                intent.putExtra(ContactsContract.Intents.Insert.NOTES, cb.getAdress());

                startActivity(intent);
                //setting values to textviews

                //textViewAddress.setText(obj.getString("address"));
                // } catch (JSONException e) {
                //  e.printStackTrace();
                //if control comes here
                //that means the encoded format not matches
                //in this case you can display whatever data is available on the qrcode
                //to a toast
                //Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                // }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void buttonclicked(View v) {
        //initiating the qr code scan
        qrScan.initiateScan();
    }
}
