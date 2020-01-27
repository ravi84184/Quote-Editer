package com.example.postcreator.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.postcreator.R;
import com.example.postcreator.database.DatabaseHelper;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class CreatePosterActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_IMAGE = 100;
    private static final String TAG = "CreatePosterActivity";


    RelativeLayout img_view, rl_bg_menu, rl_text_color;
    ImageView img_bg;
    EditText txt_text;
    SeekBar txt_size;
    Button btn_save;
    DatabaseHelper databaseHelper;
    Bitmap bgImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poster);

        databaseHelper = new DatabaseHelper(this);

        initUI();

    }

    private void initUI() {
        rl_bg_menu = findViewById(R.id.rl_bg_menu);
        rl_text_color = findViewById(R.id.rl_text_color);
        img_bg = findViewById(R.id.img_bg);
        txt_text = findViewById(R.id.txt_text);
        txt_size = findViewById(R.id.txt_size);
        btn_save = findViewById(R.id.btn_save);
        img_view = findViewById(R.id.img_view);


        rl_bg_menu.setOnClickListener(this);
        rl_text_color.setOnClickListener(this);
        txt_text.setOnClickListener(this);
        btn_save.setOnClickListener(this);

        resetData();
    }
    void  resetData(){
        txt_text.setText("");
        txt_text.setHint("Type Here");
        txt_text.setFocusable(true);
        AssetManager assetManager = getAssets();
        try (
                InputStream inputStream = assetManager.open("bg1.jpg")
        ) {
            bgImage = BitmapFactory.decodeStream(inputStream);
            img_bg.setImageBitmap(bgImage);
        } catch (IOException ex) {
            //ignored
        }
        txt_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e(TAG, "onProgressChanged: " + progress);
                txt_text.setTextSize(Float.parseFloat(String.valueOf(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_bg_menu:
                final Dialog dialog = new Dialog(this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View view1 = inflater.inflate(R.layout.image_choose_dialog, null);
                view1.findViewById(R.id.btn_gallery).setOnClickListener(v -> {
                    selectImage();
                    dialog.dismiss();
                });
                view1.findViewById(R.id.btn_default).setOnClickListener(v -> {
                    startActivityForResult(new Intent(CreatePosterActivity.this, BackgroundListActivity.class), 1);
                    dialog.dismiss();
                });
                dialog.setContentView(view1);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                break;
            case R.id.rl_text_color:
                ColorPickerDialogBuilder
                        .with(this)
                        .setTitle("Choose color")
                        .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                Log.e(TAG, "onColorSelected: 0x" + Integer.toHexString(selectedColor));
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                Log.e(TAG, "onClick: 0x" + Integer.toHexString(selectedColor));
                                txt_text.setTextColor(selectedColor);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
                break;
            case R.id.btn_save:
                if (txt_text.getText().toString().equals("")) {
                    txt_text.setHint("");
                }
                txt_text.setFocusable(false);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bgImage.compress(Bitmap.CompressFormat.JPEG, 85 /*ignored for PNG*/, bos);
                byte[] bgByte = bos.toByteArray();


                Bitmap bitmap = viewToBitmap(img_view);
                ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85 /*ignored for PNG*/, bos1);
                byte[] bitmapData1 = bos1.toByteArray();
                databaseHelper.addPoster(txt_text.getText().toString(),
                        String.valueOf(txt_text.getTextSize()),
                        String.valueOf(txt_text.getCurrentTextColor()),
                        bgByte,
                        bitmapData1
                );
                /*
                try {
                    FileOutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/file.png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                    output.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                resetData();
                break;
            default:
                break;
        }
    }

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void selectImage() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            AssetManager assetManager = getAssets();
            try (
                    InputStream inputStream = assetManager.open(Objects.requireNonNull(data.getStringExtra("result")))
            ) {
                bgImage = BitmapFactory.decodeStream(inputStream);
                img_bg.setImageBitmap(bgImage);
            } catch (IOException ex) {
                //ignored
            }
        } else if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Uri uri = data.getParcelableExtra("path");
                try {
                    bgImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    img_bg.setImageBitmap(bgImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
