package com.swagger.app;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.swagger.app.model.Document;
import com.swagger.app.utils.SharedPreferenceClass;
import com.swagger.app.utils.StaticVariables;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by imabh on 17-01-2019.
 */

public class VenderIdentityVerification extends AppCompatActivity implements View.OnClickListener {

    Button btnUpload;
    ImageView iv_doc1;
    Activity mActivity;
    final private int REQUEST_CODE_ASK_WRITE_PERMISSIONS_GALLERY = 125;
    private Uri mImageCaptureUri;
    private static final int PICK_FROM_FILE = 2;
    Bitmap bitmap1 = null;
    File doc_file1 = null;
    boolean isDoc1Clicked = false;
    AsyncHttpClient client = null;
    ProgressBar pBar;
    SharedPreferenceClass sharedPreferenceClass;
    Spinner spnrDocTypes;
    ArrayList<Document> al_doc_types;
    String selected_doc_id = "";
    File fileSDImage;
    Uri imageToUploadUri;
    int CAMERA_PHOTO = 111;
    int GALLERY_PHOTO = 112;
    String imageName = "";
    private boolean isImageAvailable = false;
    EditText et_past_work;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identity_verification);

        mActivity = VenderIdentityVerification.this;
        sharedPreferenceClass = new SharedPreferenceClass(mActivity);

        pBar = findViewById(R.id.pBar);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(this);

        spnrDocTypes = (Spinner) findViewById(R.id.spnrDocTypes);

        iv_doc1 = (ImageView) findViewById(R.id.iv_doc1);
        iv_doc1.setOnClickListener(this);

        LinearLayout ll_doc_type=findViewById(R.id.ll_doc_type);
        TextView tv_heading=findViewById(R.id.tv_heading);
        et_past_work=(EditText)findViewById(R.id.et_past_work);

        if(getIntent().getStringExtra("PastWorkDetails").equalsIgnoreCase("true"))
        {
            ll_doc_type.setVisibility(View.GONE);
            tv_heading.setText("UPLOAD YOUR PAST WORK DETAILS");
            et_past_work.setVisibility(View.VISIBLE);

        }
        else
        {
            ll_doc_type.setVisibility(View.VISIBLE);
            et_past_work.setVisibility(View.GONE);
            tv_heading.setText("WE NEED YOUR IDENTITY VERIFICATION");
            getServiceList();
           // fetchDocumentIfPresent();
        }



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_doc1:
                isDoc1Clicked = true;
                showImageUploadPopUp(view);
                break;
            case R.id.btnUpload:
                if(et_past_work.getVisibility()==View.VISIBLE)
                {
                    if(et_past_work.getText().toString().length()==0)
                    {
                        Toast.makeText(mActivity, "Past work details cannot be blank", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        asyncMethod(0);
                    }
                }
                else
                {
                    asyncMethod(1);
                }

                break;
        }

    }


    private void pickFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        startActivityForResult(i, GALLERY_PHOTO);
    }

    // Method to show photo upload pop up
    private void showImageUploadPopUp(View view) {
        PopupMenu popup = new PopupMenu(VenderIdentityVerification.this, view);
        popup.getMenuInflater().inflate(R.menu.pop_up_image, popup.getMenu());
        if (isPermissionGranted()) {
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {

                    try {
                        if (item.getTitle().equals("Camera")) {

                            if (Build.VERSION.SDK_INT >= 24) {
                                try {
                                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                                    m.invoke(null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            captureCameraImage();
                        } else if (item.getTitle().equals("Gallery"))
                            pickFromGallery();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return true;
                }

            });
            popup.show();
        }


    }

    // Method to invoke camera for image Upload
    private void captureCameraImage() throws Exception {
        Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File root = new File(Environment.getExternalStorageDirectory() + File.separator + "EventLog" + File.separator);
        root.mkdirs();
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = df.format(c.getTime());
        // imageName = Long.toString(System.currentTimeMillis()) + ".jpg";
        imageName = formattedDate + ".jpg";

        fileSDImage = new File(root, imageName);
        imageToUploadUri = Uri.fromFile(fileSDImage);
        chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageToUploadUri);
        // imageToUploadUri = Uri.fromFile(f);
        startActivityForResult(chooserIntent, CAMERA_PHOTO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_PHOTO) {

                InputStream myImage = null;
                boolean isSaved = false;

                try {
                    myImage = new FileInputStream(fileSDImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (myImage == null) {
                    try {
                        imageToUploadUri = data.getData();
                        myImage = getContentResolver().openInputStream(imageToUploadUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else
                    isSaved = true; // indicates a hard copy has been saved

                // added SND - storing in external storage
                String fullName = Environment.getExternalStorageDirectory() + File.separator + "Eventlog" + File.separator + "EL_" + Build.SERIAL + imageName;
                Common.imagePath = fullName;
                // ImageView img = (ImageView) findViewById(R.id.imgPhoto);

                // copy the image to the internal directory as a bitmap
                if (myImage != null) {
                    try {
                        Bitmap bmNew = null;
                        FileOutputStream fout = new FileOutputStream(Common.imagePath);
                        Bitmap bm = BitmapFactory.decodeStream(myImage);

                        // resize
                        int w = bm.getWidth();
                        int h = bm.getHeight();
                        double ratio = (double) w / (double) h;
                        double newW = 0;
                        double newH = 0;

                        if (w > 1024) {

                            newW = 1024.0;
                            newH = Math.round((double) 1024 / (double) w * (double) h);

                            if (newH > 1024) {
                                newH = 1024.0;
                                newW = Math.round((double) 1024 / (double) h * (double) w);

                            }
                            bmNew = Bitmap.createScaledBitmap(bm, (int) newW, (int) newH, true);
                            bmNew.compress(Bitmap.CompressFormat.JPEG, 60, fout);
                            bmNew = rotateImageIfRequired(VenderIdentityVerification.this, bmNew, imageToUploadUri);

                            iv_doc1.setImageBitmap(bmNew);

                            // showPic.setRotation(90.0f);

                            bm.recycle();
                        } else {
                            bm.compress(Bitmap.CompressFormat.JPEG, 60, fout);
                            iv_doc1.setImageBitmap(bm);

                        }

                        fout.close();
                        iv_doc1.setVisibility(View.VISIBLE);
                        // bm.recycle();

                        // release myImage
                        myImage.close();
                        myImage = null;

                        // delete the image from the SD directory
                        if (isSaved) {
                            fileSDImage.delete();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(VenderIdentityVerification.this, "Error in capturing image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == GALLERY_PHOTO && null != data) {
                imageToUploadUri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(imageToUploadUri, filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imagepathName = cursor.getString(columnIndex);
                cursor.close();
                Bitmap bmNew = null;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                byte[] data1 = bos.toByteArray();
                Common.imagePath = new String(Base64.encode(data1, GALLERY_PHOTO));
                Bitmap bm = BitmapFactory.decodeFile(imagepathName);
                int w = bm.getWidth();
                int h = bm.getHeight();
                double ratio = (double) w / (double) h;
                double newW = 0;
                double newH = 0;

                if (w > 1024) {

                    newW = 1024.0;
                    newH = Math.round((double) 1024 / (double) w * (double) h);

                    if (newH > 1024) {
                        newH = 1024.0;
                        newW = Math.round((double) 1024 / (double) h * (double) w);

                    }
                    bmNew = Bitmap.createScaledBitmap(bm, (int) newW, (int) newH, true);
                    bmNew.compress(Bitmap.CompressFormat.JPEG, 60, bos);


                    iv_doc1.setImageBitmap(bmNew);

                    // showPic.setRotation(90.0f);

                    bm.recycle();
                } else {
                    bm.compress(Bitmap.CompressFormat.JPEG, 60, bos);
                    iv_doc1.setImageBitmap(bm);

                }
                iv_doc1.setVisibility(View.VISIBLE);
                File fimage = new File(imagepathName);
                if (fimage.exists()) {
                    Calendar c = Calendar.getInstance();

                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                    String formattedDate = df.format(c.getTime());
                    imageName = formattedDate + ".jpg";
                    Common.imagePath = Environment.getExternalStorageDirectory() + File.separator + "Eventlog" + File.separator + "EL_" + Build.SERIAL + imageName;
                    File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Eventlog" + File.separator);
                    root.mkdirs();
                    fileSDImage = new File(root, imageName);
                }
                try {
                    copyFile(fimage, new File(Common.imagePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(VenderIdentityVerification.this, "Error in capturing image", Toast.LENGTH_SHORT).show();
            }

        }


    }


    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("Message", "Permission is granted");
                return true;
            } else {

                Log.d("Message", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { // permission is automatically granted on sdk<23 upon
            // installation
            Log.d("Message", "Permission is granted");
            return true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
    }


    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }


    public void setDocumentTypes() {
        ArrayAdapter adapter = new ArrayAdapter(mActivity, R.layout.my_spinner_textview, al_doc_types);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        spnrDocTypes.setAdapter(adapter);

        spnrDocTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                selected_doc_id = al_doc_types.get(i).getDocId();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getServiceList() {
        client = new AsyncHttpClient();

        client.setTimeout(30000);


        // add headers
        client.addHeader("Authorization", "bearer " + sharedPreferenceClass.getValue_string(StaticVariables.ACCESS_TOKEN));
        client.addHeader("Content-Type", "application/json");


        client.get(mActivity, Common.documentType, new TextHttpResponseHandler() {


            @Override
            public void onStart() {
                super.onStart();
                pBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pBar.setVisibility(View.GONE);

                return;
            }

            @Override
            public void onFailure(int i, Header[] headers, String response, Throwable throwable) {
                Toast.makeText(mActivity, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String response) {

                System.out.println("****** Response for document types ******" + response);

                try {
                    al_doc_types = new ArrayList<Document>();
                    JSONArray jArray = null;
                    try {
                        jArray = new JSONArray(response);
                        for (int a = 0; a < jArray.length(); a++) {
                            JSONObject jsonOb = jArray.getJSONObject(a);
                            Document obj = new Document();
                            obj.setDocName(jsonOb.getString("documentTypeName"));
                            obj.setDocId(jsonOb.getString("id"));
                            al_doc_types.add(obj);

                        }
                        if (al_doc_types.size() > 0) {

                            setDocumentTypes();
                            fetchDocumentIfPresent();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public JSONObject uploadDoc(String sourceImageFile, int value) {
        RequestBody requestBody;
        try {
            File sourceFile = new File(sourceImageFile);

            Log.d("TAG", "File...::::" + sourceFile + " : " + sourceFile.exists());

            final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/*");

            String filename = sourceImageFile.substring(sourceImageFile.lastIndexOf("/") + 1);

            /**
             * OKHTTP3
             */
            String id = "";
            String url = "";
            SharedPreferences sharedPreferences = getSharedPreferences("imagePref", MODE_PRIVATE);
            id = sharedPreferences.getString("id", "0");
               if(value==1) {
                   if (id.equals("0")) {
                       url = Common.partnerDetailsInsert;
                   } else {
                       url = Common.partnerDocumentUpdateUrl;
                   }
               }
            if(value==0)
            {
                url=  Common.partnerPastWorkInsertUrl;
                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("uploaded_file", filename, RequestBody.create(MediaType.parse("multipart/form-data"), sourceFile))
                        .addFormDataPart("Id", "0")
                        .addFormDataPart("Remark", et_past_work.getText().toString())
                        .addFormDataPart("UserId", sharedPreferenceClass.getValue_string(StaticVariables.USER_ID))

                        .build();
            }
            else {
                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("uploaded_file", filename, RequestBody.create(MediaType.parse("multipart/form-data"), sourceFile))
                        .addFormDataPart("Id", id)
                        .addFormDataPart("DocumentTypeId", selected_doc_id)
                        .addFormDataPart("UserId", sharedPreferenceClass.getValue_string(StaticVariables.USER_ID))

                        .build();
            }
            Request request = new Request.Builder()
                    .addHeader("Authorization", "bearer " + sharedPreferenceClass.getValue_string(StaticVariables.ACCESS_TOKEN))
                    .url(url)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            Log.e("TAG", "Error: " + res);
            return new JSONObject(res);

        } catch (UnknownHostException | UnsupportedEncodingException e) {
            Log.e("TAG", "Error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e("TAG", "Other Error: " + e.getLocalizedMessage());
        }
        return null;
    }


    public void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }

    }

    private void fetchDocumentIfPresent() {
        client = new AsyncHttpClient();
        client.setTimeout(30000);

        // add headers
        client.addHeader("Authorization", "bearer " + sharedPreferenceClass.getValue_string(StaticVariables.ACCESS_TOKEN));
        client.addHeader("Content-Type", "application/json");


        client.get(mActivity, Common.partnerDocumentFetchUrl + sharedPreferenceClass.getValue_string(StaticVariables.USER_ID), new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                pBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(int i, Header[] headers, String response, Throwable throwable) {
                Toast.makeText(mActivity, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String response) {

                System.out.println("****** Response for document fetching ******" + response);

                try {
                    JSONArray jArray = null;
                    jArray = new JSONArray(response);
                    for (int a = 0; a < jArray.length(); a++) {
                        JSONObject jsonOb = jArray.getJSONObject(a);
                        String imagePath = jsonOb.getString("imagePath");
                        String id = jsonOb.getString("id");
                        String documentTypeId = jsonOb.getString("documentTypeId");
                        if (!imagePath.equals("")) {
                            SharedPreferences sharedPreferences = getSharedPreferences("imagePref", MODE_PRIVATE);
                            sharedPreferences.edit().putString("id", id).apply();
                            Picasso.get().load("http://"+imagePath).into(iv_doc1);
                           // spnrDocTypes.setSelection(Integer.parseInt(documentTypeId));
                            spnrDocTypes.setSelection(getIndex(spnrDocTypes, documentTypeId));
                            isImageAvailable = true;
                        }
                      /*  else {
                            getServiceList();
                        }*/

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private int getIndex(Spinner spinner, String doc_id) {

        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {

            String service_id = al_doc_types.get(i).getDocId().toString();

            if (service_id.equals(doc_id)) {
                index = i;
            }
        }
        return index;
    }

    public void asyncMethod(int value)
    {
        if (Common.checkNetworkConnection(this)) {
            if (!Common.imagePath.equals("")) {
                //Async method to upload image to server
                new AsyncTask<Void, Integer, Boolean>() {

                    ProgressDialog progressDialog;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog = new ProgressDialog(VenderIdentityVerification.this);
                        progressDialog.setMessage("Uploading please wait..");
                        progressDialog.show();
                    }

                    @Override
                    protected Boolean doInBackground(Void... params) {

                        try {
                            JSONObject jsonObject = uploadDoc(Common.imagePath,value);
                            if (jsonObject != null)
                                return jsonObject.getString("message").equalsIgnoreCase("success");

                        } catch (JSONException e) {
                            Log.i("TAG", "Error : " + e.getLocalizedMessage());
                        }
                        return false;
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        super.onPostExecute(aBoolean);
                        if (progressDialog != null)

                            progressDialog.dismiss();

                        if (aBoolean) {
                            Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else
                            Toast.makeText(getApplicationContext(), "Image Upload failed", Toast.LENGTH_LONG).show();


                    }
                }.execute();
            } else
                Toast.makeText(this, "Error in capture", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }
}
