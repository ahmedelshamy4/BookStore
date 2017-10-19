package com.eslamelhoseiny.bookstore;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.eslamelhoseiny.bookstore.util.ActivityLauncher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    String mCurrentPhotoPath;
    File imageFile;
    Uri selectedImageUri;
    ////
    EditText etEmail,etPassword,etName,etAddress;
    ImageView ivProfile;
    Button btnRegistration;
    //////
    ProgressBar progressBar;
    private static final int GALLERY_REQUEST = 1;
    private static final int Camera_REQUEST = 2;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
       initViews();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                  //Todo Save Author Info & Profile Image  //Todo Open MyBooks Activity
                    ActivityLauncher.openMyBooksActivity(RegisterActivity.this);
                    finish();
                } else {
                    // User is signed out
                   // Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };

    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void initViews() {
        ivProfile = (ImageView) findViewById(R.id.iv_profile);
        etEmail=(EditText)findViewById(R.id.et_email);
        etPassword=(EditText)findViewById(R.id.et_password);
        etName=(EditText)findViewById(R.id.et_name);
        etAddress=(EditText)findViewById(R.id.et_address);
        btnRegistration=(Button)findViewById(R.id.btn_register);
        progressBar=(ProgressBar)findViewById(R.id.progress_register) ;
        progressBar.getIndeterminateDrawable().
                setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);

        btnRegistration.setOnClickListener(this);
        ivProfile.setOnClickListener(this);
    }
    //when press of picture this dialog is display.to choose galary or camera
    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose)
                .setCancelable(true)
                .setItems(new String[]{getString(R.string.galary), getString(R.string.camera)},
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //any app to choose picture
                            //uri pictures.
                            Intent i = new Intent(Intent.ACTION_PICK);
                            i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, GALLERY_REQUEST);
                        } else {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            // Ensure that there's a camera activity to handle the intent
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                // Create the File where the photo should go
                                File photoFile = null;
                                try {
                                    photoFile = createImageFile();
                                } catch (IOException ex) {
                                    // Error occurred while creating the File
                                    ex.printStackTrace();
                                }
                                // Continue only if the File was successfully created
                                if (photoFile != null) {
                                    Uri photoURI = FileProvider.getUriForFile(RegisterActivity.this, "com.eslamelhoseiny.bookstore.Fileprovider", photoFile);
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(takePictureIntent, Camera_REQUEST);
                                }
                            }
                        }
                    }
                }).show();
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = ivProfile.getWidth();
        int targetH = ivProfile.getHeight();
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        if(targetH==0||targetW==0){
            targetH=300;
            targetW=300;
        }
        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        ivProfile.setImageBitmap(bitmap);
    }

    private File createImageFile() throws IOException {
        // Create an image file name unique.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data!=null) {
            selectedImageUri = data.getData();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                readFileFromSelectedURI();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        } else if (requestCode == Camera_REQUEST && resultCode == RESULT_OK) {
            setPic();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            readFileFromSelectedURI();
        } else {
            Toast.makeText(this, R.string.cannot_read_imag, Toast.LENGTH_SHORT).show();
        }
    }

    private void readFileFromSelectedURI() {
        Cursor cursor = getContentResolver().query(selectedImageUri,
                new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String imagePath = cursor.getString(0);
            cursor.close();
            imageFile = new File(imagePath);
            Bitmap image = BitmapFactory.decodeFile(imagePath);
            ivProfile.setImageBitmap(image);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_profile)
            showImagePickerDialog();
        else {
            registerNewUser();
        }
    }

    private void registerNewUser() {
        progressBar.setVisibility(View.VISIBLE);
        btnRegistration.setVisibility(View.GONE);
        String email=etEmail.getText().toString();
        String name=etName.getText().toString();
        String password=etPassword.getText().toString();
        String address=etAddress.getText().toString();
        if (name.isEmpty()){
            etName.setError(getString(R.string.Enter_name));
        }else if(email.isEmpty()){
            etEmail.setError(getString(R.string.Enter_email));
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError(getString(R.string.Email_not_formatted));
        }else if(password.isEmpty()){
            etPassword.setError(getString(R.string.Enter_password));
        }else if(etPassword.length()<6){
            etPassword.setError(getString(R.string.Password_length_error));
        }else {
            //Register new user in fire-base
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    btnRegistration.setVisibility(View.VISIBLE);
                  if(!task.isSuccessful()){
                      if(task.getException() instanceof FirebaseAuthUserCollisionException)
                      Toast.makeText(RegisterActivity.this, R.string.email_already_exists,Toast.LENGTH_SHORT).show();
                  else
                      Toast.makeText(RegisterActivity.this, R.string.error_in_conection,Toast.LENGTH_LONG).show();
                  }
                }
            });
        }
    }

    //when the two layout is different .
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("path",mCurrentPhotoPath);
        outState.putSerializable("file",imageFile);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentPhotoPath=savedInstanceState.getString("path");
        imageFile= (File) savedInstanceState.getSerializable("file");
    }
    ///////////////////////////////////////////////////////////////////
}
