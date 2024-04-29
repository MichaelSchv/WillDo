package com.example.willdo.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.willdo.Model.Item;
import com.example.willdo.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;


public class ItemEditActivity extends AppCompatActivity{
    private TextInputEditText itemEdit_ET_title;
    private TextInputEditText itemEdit_ET_quantity;
    private AppCompatSpinner itemEdit_SPINNER_units;
    private TextInputEditText itemEdit_ET_comment;
    private ShapeableImageView itemEdit_IMG_image;
    private MaterialButton itemEdit_BTN_changeImage;
    private AppCompatCheckBox itemEdit_CHCKBX_complete;
    private MaterialButton itemEdit_BTN_save;
    private static final int CAMERA_PERMISSION_CODE = 101;

    private boolean isExistingItem;
    private String oldItemId;
    private Item newItem;
    private Item existingItem;
    private String imageURI;
    private ActivityResultLauncher<String> selectImageFromGalleryLauncher;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_item_edit_layout);
        findViews();
        initViews();
        isExistingItem = processIncomingIntent();
        if(isExistingItem){
            existingItem = (Item) getIntent().getSerializableExtra("item");
            prePopulateFiels(existingItem);

        }
        else{
            String title = (String) getIntent().getSerializableExtra("title");
            //Log.d("in !isExistingItem", title);
            if(!title.isEmpty())
                itemEdit_ET_title.setText(title);
            itemEdit_ET_quantity.setText("1");
        }
        itemEdit_BTN_changeImage.setOnClickListener(v->imagePickOptions());
        itemEdit_BTN_save.setOnClickListener(v-> saveItem(isExistingItem));
    }

    private void prePopulateFiels(Item tempItem) {
        itemEdit_ET_title.setText(tempItem.getTitle());
        itemEdit_ET_quantity.setText(String.valueOf(tempItem.getQuantity()));
        itemEdit_SPINNER_units.setSelection(getUnitPosition(tempItem.getUnit()));
        itemEdit_ET_comment.setText(tempItem.getComment());
        if (tempItem.getImageURI() != null && !tempItem.getImageURI().isEmpty()) {
            Uri imageUri = Uri.parse(tempItem.getImageURI());
        }
        itemEdit_CHCKBX_complete.setChecked(tempItem.isCompleted());
    }

    private int getUnitPosition(Item.unitType unit){
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) itemEdit_SPINNER_units.getAdapter();
        // Convert the unitType enum to a string and find its position in the adapter
        return adapter.getPosition(unit.name());
    }


    private boolean processIncomingIntent() {
        Intent intent = getIntent();
        return intent.hasExtra("item");
    }

    private void imagePickOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        String[] options = {"Take Photo", "Choose from Gallery"};
        builder.setItems(options, ((dialog, which) -> {
            if(which == 0)
                requestCameraPermission();
            else if (which == 1)
                openImageChoose();
        }));
        builder.show();
    }

    private void openImageChoose() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectImageFromGalleryLauncher.launch("image/*");
    }

    private void takePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", photoFile);
                imageURI = photoURI.toString();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureLauncher.launch(intent);
            } catch (IOException e) {
                Toast.makeText(this, "Error occurred while creating the image file", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void saveItem(boolean isExistingItem) {
        boolean isValidItem;
        if(itemEdit_ET_title.getText().toString().isEmpty())
        {
            Toast.makeText(this, "No item added", Toast.LENGTH_SHORT).show();
            isValidItem = false;
        }
        else{
            isValidItem = true;
            String title = itemEdit_ET_title.getText().toString();
            long quantity = (itemEdit_ET_quantity.getText().toString().isEmpty()) ? 1L : Integer.parseInt(itemEdit_ET_quantity.getText().toString());
            String comment = itemEdit_ET_comment.getText().toString();
            boolean isCompleted = itemEdit_CHCKBX_complete.isChecked();
            Item.unitType unit = Item.unitType.valueOf(itemEdit_SPINNER_units.getSelectedItem().toString());
            if(!isExistingItem)
            {
                newItem = new Item().setTitle(title).setQuantity(quantity).setUnit(unit).setComment(comment).setCompleted(isCompleted);
                if(imageURI != null)
                    newItem.setImageURI(imageURI);
            }
            else{
                existingItem.setTitle(title).setQuantity(quantity).setUnit(unit).setComment(comment).setCompleted(isCompleted);
                if(imageURI != null)
                    existingItem.setImageURI(imageURI);
            }

        }
        Intent intent = new Intent();
        if(isValidItem && !isExistingItem){
            intent.putExtra("newItem", newItem);
            setResult(RESULT_OK,intent);
        }
        else if(isValidItem && isExistingItem){
            intent.putExtra("updatedItem", existingItem);
            setResult(RESULT_FIRST_USER,intent);
        }
        else
            setResult(RESULT_CANCELED);
        finish();

    }

    private void initViews() {
        existingItem = null;
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getUnitTypeStrings());
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemEdit_SPINNER_units.setAdapter(arrayAdapter);
        imageURI = null;
        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Uri photoUri = Uri.parse(imageURI);
                itemEdit_IMG_image.setImageURI(photoUri);
            }
        });

        selectImageFromGalleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                itemEdit_IMG_image.setImageURI(uri);
                imageURI = uri.toString();

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode == CAMERA_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission is required to use camera", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void requestCameraPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        else
            takePictureIntent();
    }

    public File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        imageURI = image.getAbsolutePath();
        return image;
    }

    private String[] getUnitTypeStrings() {
        return Arrays.stream(Item.unitType.values()).map(Enum::name).toArray(String[]::new);
    }

    public void findViews(){
        itemEdit_ET_title = findViewById(R.id.itemEdit_ET_title);
        itemEdit_ET_quantity = findViewById(R.id.itemEdit_ET_quantity);
        itemEdit_SPINNER_units = findViewById(R.id.itemEdit_SPINNER_units);
        itemEdit_ET_comment = findViewById(R.id.itemEdit_ET_comment);
        itemEdit_IMG_image = findViewById(R.id.itemEdit_IMG_image);
        itemEdit_BTN_changeImage = findViewById(R.id.itemEdit_BTN_changeImage);
        itemEdit_CHCKBX_complete = findViewById(R.id.itemEdit_CHCKBX_complete);
        itemEdit_BTN_save = findViewById(R.id.itemEdit_BTN_save);
    }
}
