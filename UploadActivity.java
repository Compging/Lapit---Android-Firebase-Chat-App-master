package in.tvac.akshaye.lapitchat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

public class UploadActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        new MaterialFilePicker()
                .withActivity(UploadActivity.this)
                .withRequestCode(1000)
                .withRootPath("/storage/")
                //.withFilter(Pattern.compile(".*\\.txt$")) // Filtering files and directories by file name using regexp
                //.withFilterDirectories(true) // Set directories filterable (false by default)
                //.withHiddenFiles(true) // Show hidden files and folders
                .start();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("ResultCode", String.valueOf(resultCode)+"  "+String.valueOf(requestCode) );

        if(requestCode == 1000 && resultCode == RESULT_CANCELED){
            UploadActivity.this.finish();
        }

        if (requestCode == 1000 && resultCode == RESULT_OK) {

            //Uri filePath = data.getData();
            final String fileresult = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            // Do anything with file

            final java.io.File file = new java.io.File(fileresult);

            //mtextview.setText(fileresult);

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(UploadActivity.this);
            builder.setMessage("Are you sure to Upload \" "+file.getName()+" \" to Cloud");
            //builder.setMessage("Are you sure to Upload \" "+filePath+" \" to Cloud");
            AlertDialog.Builder builder1 = builder.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    /* Upload File to Firebase */

                    //try {
                    //    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),fileUri);
                    //} catch (IOException e) {
                    //    e.printStackTrace();
                    //}


                    /* MUST DO "ALEART DAIALOG" if it already has this filename */

                    Uploadfile(fileresult);

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1001: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    //openFilePicker();
                } else {
                    //showError();
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void Uploadfile(String fileresult){


        final java.io.File file = new java.io.File(fileresult);
        final Uri fileuri = Uri.fromFile(file);

        final String my_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference filepath = FirebaseStorage.getInstance().getReference().child("cloud").child(my_uid).child(file.getName());

        final ProgressDialog progressDialog = new ProgressDialog(UploadActivity.this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        filepath.putFile(fileuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content
                progressDialog.dismiss();
                Toast.makeText(UploadActivity.this, "Upload is sucessful", Toast.LENGTH_SHORT).show();
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                uploadDatabase(downloadUrl,file.getName(),my_uid);

                Intent mycloudIntent = new Intent(UploadActivity.this, MyCloudActivity.class);
                UploadActivity.this.finish();
                //UploadActivity.super.onDestroy();
                startActivity(mycloudIntent);
                //UploadActivity.super.onDestroy();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        progressDialog.dismiss();
                        Toast.makeText(UploadActivity.this, "Upload is Unsuccessful \n please try again", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage(((int) progress)+"% Uploaded...");
                    }
                })
        ;

    }

    public void uploadDatabase(Uri DownloadUrl , String filename ,String uid ){

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("File");
        String fid = database.child(uid).push().getKey();
        in.tvac.akshaye.lapitchat.File databasefile = new in.tvac.akshaye.lapitchat.File(filename,DownloadUrl.toString(),fid);

        //Can try Hash map
        database.child(uid).child(fid).setValue(databasefile);
    }


}
