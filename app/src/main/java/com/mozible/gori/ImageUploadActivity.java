package com.mozible.gori;

/**
 * Created by JunLee on 7/17/16.
 */

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.mozible.gori.models.PostResult;
import com.mozible.gori.utils.GoriPreferenceManager;
import com.mozible.gori.utils.ServerInterface;
import com.soundcloud.android.crop.Crop;

import java.io.File;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class ImageUploadActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton image_button_upload_preview;
    private EditText edit_text_description;
    private CoordinatorLayout coordinator;
    private int mContentId = -1;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        initView();
        initContentUpload1();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        image_button_upload_preview = (ImageButton) findViewById(R.id.image_button_upload_preview);
        image_button_upload_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFilePicker();
            }
        });
        edit_text_description = (EditText) findViewById(R.id.edit_text_description);
        coordinator = (CoordinatorLayout) findViewById(R.id.coordinator);
        setSupportActionBar(toolbar);
    }

    private void initContentUpload1() {
        try {
            String session = GoriPreferenceManager.getInstance(this).getSession();
            ServerInterface api = GoriApplication.getInstance().getServerInterface();
            api.uploadContent1(session, 1, new Callback<PostResult>() {

                @Override
                public void success(PostResult s, Response response) {
                    if(s.status.toLowerCase().equals("success")) {
                        showSnackbar("content upload1 success");
                        mContentId = s.content_id;
                        Intent i = new Intent();
                        i.putExtra("CONTENT_ID", mContentId);
                        setResult(RESULT_CANCELED, i);
                        startFilePicker();
                    } else {
                        showSnackbar(s.desc);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    showSnackbar("content upload failed!");
                }
            });
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_image_upload, menu);
        menuItem = menu.findItem(R.id.action_upload);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_upload:
                String session = GoriPreferenceManager.getInstance(this).getSession();
                ServerInterface api = GoriApplication.getInstance().getServerInterface();
                if(edit_text_description.getText() != null) {
                    String description = edit_text_description.getText().toString();
                    api.uploadContent3(session, description, mContentId, new Callback<PostResult>() {

                        @Override
                        public void success(PostResult s, Response response) {
                            if(s.status.toLowerCase().equals("success")) {
                                showSnackbar("content image upload complete!");
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                showSnackbar(s.desc);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            showSnackbar("content upload failed!");
                        }
                    });
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startFilePicker() {
        Crop.pickImage(this);
    }

    public void beginCrop(Uri source) {
        String username = GoriPreferenceManager.getInstance(this).getUsername();
        String imageName = username + System.currentTimeMillis() + ".jpg";
        Uri destination = Uri.fromFile(new File(this.getCacheDir(), imageName));
        Crop.of(source, destination).asSquare().start(this);
    }

    public void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            try {
                image_button_upload_preview.setImageURI(uri);
                TypedFile uploadFile = new TypedFile("multipart/form-data", new File(uri.getPath()));
                String session = GoriPreferenceManager.getInstance(this).getSession();
                ServerInterface api = GoriApplication.getInstance().getServerInterface();
                api.uploadContent2(session, uploadFile, mContentId, new Callback<PostResult>() {

                    @Override
                    public void success(PostResult s, Response response) {
                        if(s.status.toLowerCase().equals("success")) {
                            showSnackbar("content image upload success!");
                        } else {
                            showSnackbar(s.desc);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        showSnackbar("content upload failed!");
                    }
                });
            }catch(Exception ex) {
                ex.printStackTrace();
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
        }
    }
    public void showSnackbar(String text) {
        Snackbar.make(coordinator, text, Snackbar.LENGTH_SHORT).show();
    }

}
