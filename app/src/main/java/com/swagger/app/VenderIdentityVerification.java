package com.swagger.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.swagger.app.model.Document;
import com.swagger.app.model.Service;
import com.swagger.app.utils.ImageFilePath;
import com.swagger.app.utils.SharedPreferenceClass;
import com.swagger.app.utils.StaticVariables;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
    File doc_file1 = null,doc_file2=null;
    boolean isDoc1Clicked=false,isDoc2Clicked=false;
    AsyncHttpClient client = null;
    ProgressBar pBar;
    SharedPreferenceClass sharedPreferenceClass;
    Spinner spnrDocTypes;
    ArrayList<Document> al_doc_types;
    String selected_doc_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identity_verification);

        mActivity = VenderIdentityVerification.this;
        sharedPreferenceClass = new SharedPreferenceClass(mActivity);

        pBar = findViewById(R.id.pBar);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(this);

        spnrDocTypes=(Spinner)findViewById(R.id.spnrDocTypes);

        iv_doc1=(ImageView)findViewById(R.id.iv_doc1);
        iv_doc1.setOnClickListener(this);


        getServiceList();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_doc1:
                isDoc1Clicked=true;
                isDoc2Clicked=false;
                alert();
                break;
            case R.id.btnUpload:
               if(doc_file1==null)
               {
                   Toast.makeText(mActivity, "Please select document Image", Toast.LENGTH_SHORT).show();
               }
               else
               {
                   apiUploadDocumet(doc_file1);
               }
                break;
        }

    }

    public void alert() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkWriteExternalStoragePermissionGallery();
        } else {
            pickFromGallery();
        }
    }

    public File uploadfile(String selectedFile) throws IOException {
        File doc_file = null;
        doc_file = new File(selectedFile);
        return doc_file;
    }

    private void pickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_FROM_FILE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of CropImageActivity

            switch (requestCode) {

                case PICK_FROM_FILE:

                    if (data != null) {
                        mImageCaptureUri = data.getData();
                        Uri uri = Uri.parse(mImageCaptureUri.toString());
                        System.out.println("*** 1ST IMAGE GALLERY URI == " + uri);
                        System.out.println("*** 1ST IMAGE GALLERY REAL PATH == " + ImageFilePath.getPath(mActivity, uri));
                        try {
                            //bitmap1 = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), uri);
                            String real_path = ImageFilePath.getPath(mActivity, uri);
                            bitmap1 = decodeSampledBitmapFromResource(real_path, 0, 0);
                            if (isDoc1Clicked) {
                                iv_doc1.setImageBitmap(bitmap1);
                                doc_file1 = uploadfile(real_path);
                                String my_file = doc_file1.getPath();
                                String fileName = my_file.substring(my_file.lastIndexOf("/") + 1);
                            }
                            /*if(isDoc2Clicked)
                            {
                                iv_doc2.setImageBitmap(bitmap1);
                                doc_file2 = uploadfile(real_path);
                                String my_file = doc_file2.getPath();
                                String fileName = my_file.substring(my_file.lastIndexOf("/") + 1);
                            }*/

                        } catch (OutOfMemoryError error) {
                            error.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    break;

            }
        }


    @TargetApi(Build.VERSION_CODES.M)
    private void checkWriteExternalStoragePermissionGallery() {
        int hasWriteContactsPermission = mActivity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_WRITE_PERMISSIONS_GALLERY);
                    return;
                }
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_WRITE_PERMISSIONS_GALLERY);
                return;
            }
            return;
        }
        pickFromGallery();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
           /* case REQUEST_CODE_ASK_WRITE_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    captureFromCamera();
                } else {
                    // Permission Denied
                    Toast.makeText(mActivity, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;*/
            case REQUEST_CODE_ASK_WRITE_PERMISSIONS_GALLERY:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    pickFromGallery();
                } else {
                    // Permission Denied
                    Toast.makeText(mActivity, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    public static Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = 4;//calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap decodeBitmapFromResourceWithoutScaling(String path, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(path, options);
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) {

        ExifInterface ei = null;
        try {
            ei = new ExifInterface(selectedImage.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap b = null;
        try {
            b = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                    true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }



    public void apiUploadDocumet(File doc_file1) {
        client = new AsyncHttpClient();
        client.setTimeout(30000);

        client.addHeader("Authorization", "bearer " + sharedPreferenceClass.getValue_string(StaticVariables.ACCESS_TOKEN));
        client.addHeader("Content-Type","multipart/form-data");

        try {

            RequestParams params = new RequestParams();

            params.put("DocumentTypeId", selected_doc_id);
            params.put("UserId", sharedPreferenceClass.getValue_string(StaticVariables.USER_ID));
            params.put("Id","0");
            params.put("image", doc_file1);

            System.out.println("***** Request for upload document ***** "+params.toString());
           // StringEntity entity = new StringEntity(params.toString());

            client.post(Common.partnerDocumentInsert, params, new TextHttpResponseHandler() {

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

                    System.out.println("*** Response ***" + response);
                    Toast.makeText(mActivity, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String response) {

                    System.out.println("****** Response for document upload *****" + response);
                    Toast.makeText(mActivity, "Document uploaded successfully.", Toast.LENGTH_LONG).show();
                    finish();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

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
}
