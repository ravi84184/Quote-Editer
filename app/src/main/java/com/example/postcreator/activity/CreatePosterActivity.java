package com.example.postcreator.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.postcreator.ImageViewActivity;
import com.example.postcreator.R;
import com.example.postcreator.TextEditorDialogFragment;
import com.example.postcreator.adapter.FontsAdapter;
import com.example.postcreator.database.DatabaseHelper;
import com.example.postcreator.utils.FontProvider;
import com.example.postcreator.viewmodel.Font;
import com.example.postcreator.viewmodel.Layer;
import com.example.postcreator.viewmodel.TextLayer;
import com.example.postcreator.widget.MotionView;
import com.example.postcreator.widget.entity.ImageEntity;
import com.example.postcreator.widget.entity.MotionEntity;
import com.example.postcreator.widget.entity.TextEntity;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class CreatePosterActivity extends AppCompatActivity implements View.OnClickListener, TextEditorDialogFragment.OnTextLayerCallback {
    public static final int REQUEST_IMAGE = 100;
    public static final int SELECT_STICKER_REQUEST_CODE = 123;
    private static final String TAG = "CreatePosterActivity";
    private static final String EXTRA_STICKER_ID = "LOL";
    protected MotionView motionView;
    protected View textEntityEditPanel;
    private final MotionView.MotionViewCallback motionViewCallback = new MotionView.MotionViewCallback() {
        @Override
        public void onEntitySelected(@Nullable MotionEntity entity) {
            if (entity instanceof TextEntity) {
                textEntityEditPanel.setVisibility(View.VISIBLE);
            } else {
                textEntityEditPanel.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onEntityDoubleTap(@NonNull MotionEntity entity) {
            startTextEntityEditing();
        }
    };
    private RelativeLayout img_view, rl_bg_menu, rl_text_color;
    private ImageView img_bg;
    private EditText txt_text;
    private SeekBar txt_size;
    private Button btn_save;
    private DatabaseHelper databaseHelper;
    private Bitmap bgImage;
    private FontProvider fontProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poster);

        databaseHelper = new DatabaseHelper(this);
        fontProvider = new FontProvider(getResources());

        initUI();
//        addSticker(R.drawable.pikachu_2);

        initTextEntitiesListeners();
    }

    private void initUI() {
        rl_bg_menu = findViewById(R.id.rl_bg_menu);
        rl_text_color = findViewById(R.id.rl_text_color);
        img_bg = findViewById(R.id.img_bg);
        txt_text = findViewById(R.id.txt_text);
        txt_size = findViewById(R.id.txt_size);
        btn_save = findViewById(R.id.btn_save);
        img_view = findViewById(R.id.img_view);
        motionView = findViewById(R.id.main_motion_view);
        textEntityEditPanel = findViewById(R.id.main_motion_text_entity_edit_panel);
        motionView.setMotionViewCallback(motionViewCallback);


        rl_bg_menu.setOnClickListener(this);
        rl_text_color.setOnClickListener(this);
        txt_text.setOnClickListener(this);
        btn_save.setOnClickListener(this);

        resetData();
    }

    void resetData() {
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
                motionView.unselectEntity();
                txt_text.setFocusable(false);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bgImage.compress(Bitmap.CompressFormat.PNG, 85 /*ignored for PNG*/, bos);
                byte[] bgByte = bos.toByteArray();


                Bitmap bitmap = viewToBitmap(img_view);
                ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 85 /*ignored for PNG*/, bos1);
                byte[] bitmapData1 = bos1.toByteArray();
                databaseHelper.addPoster(txt_text.getText().toString(),
                        String.valueOf(txt_text.getTextSize()),
                        String.valueOf(txt_text.getCurrentTextColor()),
                        bgByte,
                        bitmapData1
                );

                Intent in1 = new Intent(this, ImageViewActivity.class);
                in1.putExtra("image", bitmapData1);
                startActivity(in1);
                finish();

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
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_STICKER_REQUEST_CODE) {
                if (data != null) {
                    int stickerId = data.getIntExtra(EXTRA_STICKER_ID, 0);
                    if (stickerId != 0) {
                        addSticker(stickerId);
                    }
                }
            }
        }
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

    private void addSticker(final int stickerResId) {
        motionView.post(new Runnable() {
            @Override
            public void run() {
                Layer layer = new Layer();
                Bitmap pica = BitmapFactory.decodeResource(getResources(), stickerResId);

                ImageEntity entity = new ImageEntity(layer, pica, motionView.getWidth(), motionView.getHeight());

                motionView.addEntityAndPosition(entity);
            }
        });
    }

    private void initTextEntitiesListeners() {
        findViewById(R.id.text_entity_font_size_increase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseTextEntitySize();
            }
        });
        findViewById(R.id.text_entity_font_size_decrease).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseTextEntitySize();
            }
        });
        findViewById(R.id.text_entity_color_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTextEntityColor();
            }
        });
        findViewById(R.id.text_entity_font_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTextEntityFont();
            }
        });
        findViewById(R.id.text_entity_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTextEntityEditing();
            }
        });
    }

    private void increaseTextEntitySize() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            textEntity.getLayer().getFont().increaseSize(TextLayer.Limits.FONT_SIZE_STEP);
            textEntity.updateEntity();
            motionView.invalidate();
        }
    }

    private void decreaseTextEntitySize() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            textEntity.getLayer().getFont().decreaseSize(TextLayer.Limits.FONT_SIZE_STEP);
            textEntity.updateEntity();
            motionView.invalidate();
        }
    }

    private void changeTextEntityColor() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity == null) {
            return;
        }

        int initialColor = textEntity.getLayer().getFont().getColor();

        ColorPickerDialogBuilder
                .with(CreatePosterActivity.this)
                .setTitle(R.string.select_color)
                .initialColor(initialColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(8) // magic number
                .setPositiveButton(R.string.ok, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        TextEntity textEntity = currentTextEntity();
                        if (textEntity != null) {
                            textEntity.getLayer().getFont().setColor(selectedColor);
                            textEntity.updateEntity();
                            motionView.invalidate();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    private void changeTextEntityFont() {
        final List<String> fonts = fontProvider.getFontNames();
        FontsAdapter fontsAdapter = new FontsAdapter(this, fonts, fontProvider);
        new AlertDialog.Builder(this)
                .setTitle(R.string.select_font)
                .setAdapter(fontsAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        TextEntity textEntity = currentTextEntity();
                        if (textEntity != null) {
                            textEntity.getLayer().getFont().setTypeface(fonts.get(which));
                            textEntity.updateEntity();
                            motionView.invalidate();
                        }
                    }
                })
                .show();
    }

    private void startTextEntityEditing() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            TextEditorDialogFragment fragment = TextEditorDialogFragment.getInstance(textEntity.getLayer().getText());
            fragment.show(getFragmentManager(), TextEditorDialogFragment.class.getName());
        }
    }

    @Nullable
    private TextEntity currentTextEntity() {
        if (motionView != null && motionView.getSelectedEntity() instanceof TextEntity) {
            return ((TextEntity) motionView.getSelectedEntity());
        } else {
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_add_sticker) {
//            Intent intent = new Intent(this, StickerSelectActivity.class);
//            startActivityForResult(intent, SELECT_STICKER_REQUEST_CODE);
//            return true;
        } else if (item.getItemId() == R.id.main_add_text) {
            addTextSticker();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void addTextSticker() {
        TextLayer textLayer = createTextLayer();
        TextEntity textEntity = new TextEntity(textLayer, motionView.getWidth(),
                motionView.getHeight(), fontProvider);
        motionView.addEntityAndPosition(textEntity);

        // move text sticker up so that its not hidden under keyboard
        PointF center = textEntity.absoluteCenter();
        center.y = center.y * 0.5F;
        textEntity.moveCenterTo(center);

        // redraw
        motionView.invalidate();

        startTextEntityEditing();
    }

    private TextLayer createTextLayer() {
        TextLayer textLayer = new TextLayer();
        Font font = new Font();

        font.setColor(TextLayer.Limits.INITIAL_FONT_COLOR);
        font.setSize(TextLayer.Limits.INITIAL_FONT_SIZE);
        font.setTypeface(fontProvider.getDefaultFontName());

        textLayer.setFont(font);

        textLayer.setText("Hello, world :))");


        return textLayer;
    }


    @Override
    public void textChanged(@NonNull String text) {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            TextLayer textLayer = textEntity.getLayer();
            if (!text.equals(textLayer.getText())) {
                textLayer.setText(text);
                textEntity.updateEntity();
                motionView.invalidate();
            }
        }
    }
}
