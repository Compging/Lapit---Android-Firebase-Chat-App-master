package in.tvac.akshaye.lapitchat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyCloudActivity extends AppCompatActivity {

    private Button mupload;
    //private TextView mtextview;
    //private ImageView mimageView;

    private RecyclerView mfilelist;
    private LinearLayoutManager mLayoutManager;

    private DatabaseReference mFileDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private FirebaseStorage mFirebase;

    static List<String> filedl;
    static List<in.tvac.akshaye.lapitchat.File> AllfileDl;

    private Button mDLbtn;
    private Button mdelete;
    private Toolbar mToolbar;

    //private MaterialFilePicker filepicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycloud);


        //For Show files that exist in Cloud of this user


        //For browsefile and upload it to firebase
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1001);
        }
        mupload = (Button) findViewById(R.id.upload_browse);
        //mtextview = (TextView) findViewById(R.id.upload_textView);
        //mimageView = (ImageView) findViewById(R.id.upload_image);

        mLayoutManager = new LinearLayoutManager(this);

        mfilelist = (RecyclerView) findViewById(R.id.filelist);
        mfilelist.setHasFixedSize(true);
        mfilelist.setLayoutManager(mLayoutManager);
        mAuth = FirebaseAuth.getInstance();

        mDLbtn = (Button) findViewById(R.id.download);
        mdelete = (Button) findViewById(R.id.delete);

        mFileDatabase = FirebaseDatabase.getInstance().getReference().child("File").child(mAuth.getCurrentUser().getUid());

        mFirebase = FirebaseStorage.getInstance();
        //mToolbar = (Toolbar) findViewById(R.id.mycloud_appbar);
        //mToolbar.setTitle("My Cloud");

        filedl = new ArrayList<String>();
        AllfileDl = new ArrayList<in.tvac.akshaye.lapitchat.File>();


        mupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                new MaterialFilePicker()
                        .withActivity(MyCloudActivity.this)
                        .withRequestCode(1000)
                        .withRootPath("/storage/")
                        //.withFilter(Pattern.compile(".*\\.txt$")) // Filtering files and directories by file name using regexp
                        //.withFilterDirectories(true) // Set directories filterable (false by default)
                        //.withHiddenFiles(true) // Show hidden files and folders
                        .start();



                /*
                Intent browse = new Intent();
                browse.setType("image/*");
                browse.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(browse, "SELECT IMAGE"),1000);
                */

            }
        });

        mDLbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filedl.size()>0){

                    final ProgressDialog progressDialog2 = new ProgressDialog(view.getContext());
                    progressDialog2.setTitle("Download...");
                    progressDialog2.setCanceledOnTouchOutside(false);
                    progressDialog2.show();


                    for(int i=0;i<AllfileDl.size();i++) {
                        StorageReference httpsReference = mFirebase.getReferenceFromUrl(AllfileDl.get(i).getFilepath());
                        //final File localFile = File.createTempFile("images", "jpg");
                        File rootpath = new File(Environment.getExternalStorageDirectory(), "XMD");
                        if (!rootpath.exists()) {
                            rootpath.mkdirs();
                        }
                        final File localFile = new File(rootpath, AllfileDl.get(i).filename);


                        httpsReference.getFile(localFile)
                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Local temp file has been created
                                Log.d("Fileee", localFile.getPath());
                                Log.d("Fileee1", localFile.toString());

                                Toast.makeText(MyCloudActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Toast.makeText(MyCloudActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                                progressDialog2.setMessage(((int) progress)+"% Downloaded...");
                            }
                        })


                        ;
                    }
                    //progressDialog2.setCanceledOnTouchOutside(true);
                    progressDialog2.dismiss();


                }
            }
        });

        mdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mFileDatabase = FirebaseDatabase.getInstance().getReference().child("File").child(mAuth.getCurrentUser().getUid());
                if(AllfileDl.size()>0){
                    for(int i=0;i<AllfileDl.size();i++) {

                        StorageReference httpsReference = mFirebase.getReferenceFromUrl(AllfileDl.get(i).getFilepath());
                        httpsReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                deleteDatabase();
                                                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MyCloudActivity.this,"STORAGE ERROE",Toast.LENGTH_LONG);
                            }
                        });

                    }


                }


            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<in.tvac.akshaye.lapitchat.File,FileViewHolder> firebaseFileAdapter = new FirebaseRecyclerAdapter<in.tvac.akshaye.lapitchat.File, FileViewHolder>(
                in.tvac.akshaye.lapitchat.File.class,
                R.layout.file_single_layout,
                FileViewHolder.class,
                mFileDatabase
        ) {
            @Override
            protected void populateViewHolder(FileViewHolder fileViewHolder, in.tvac.akshaye.lapitchat.File file, int i) {

                fileViewHolder.setFilename(file.getFilename(),file.getFilepath(),file);


                String fid =getRef(i).getKey();

                fileViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Click and go on

                    }
                });

            }
        };


        mfilelist.setAdapter(firebaseFileAdapter);


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK) {

            //Uri filePath = data.getData();
            final String fileresult = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            // Do anything with file

            final File file = new File(fileresult);

            //mtextview.setText(fileresult);

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(MyCloudActivity.this);
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


        final File file = new File(fileresult);
        final Uri fileuri = Uri.fromFile(file);

        final String my_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference filepath = FirebaseStorage.getInstance().getReference().child("cloud").child(my_uid).child(file.getName());

        final ProgressDialog progressDialog = new ProgressDialog(MyCloudActivity.this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        filepath.putFile(fileuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content
                progressDialog.dismiss();
                Toast.makeText(MyCloudActivity.this, "Upload is sucessful", Toast.LENGTH_SHORT).show();
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                uploadDatabase(downloadUrl,file.getName(),my_uid);

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        progressDialog.dismiss();
                        Toast.makeText(MyCloudActivity.this, "Upload is Unsuccessful \n please try again", Toast.LENGTH_SHORT).show();
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

    public void deleteDatabase(){
        if(AllfileDl.size()>0){
            for(int i=0;i<AllfileDl.size();i++) {
                DatabaseReference Key = mFileDatabase.child(AllfileDl.get(i).getFileid());
                Log.d("KEYYYY", Key.toString());
                Key.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MyCloudActivity.this,"Deleted",Toast.LENGTH_LONG);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MyCloudActivity.this,"Database Error",Toast.LENGTH_LONG);
                    }
                });
            }
        }
    }



    public static class FileViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public FileViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setFilename(String filename, final String mfilepath, final in.tvac.akshaye.lapitchat.File filee){


            final CheckedTextView simpleCheckedTextView = (CheckedTextView) mView.findViewById(R.id.file_single_filename);
            // perform on Click Event Listener on CheckedTextView
            simpleCheckedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (simpleCheckedTextView.isChecked()) {
                        // set cheek mark drawable and set checked property to false
                        //simpleCheckedTextView.setCheckMarkDrawable(R.drawable.ic_check_box_outline_blank_black_24dp);
                        simpleCheckedTextView.setChecked(false);
                        filedl.remove(mfilepath);
                        AllfileDl.remove(filee);

                    } else {
                        // set cheek mark drawable and set checked property to true
                        //simpleCheckedTextView.setCheckMarkDrawable(R.drawable.ic_check_box_black_24dp);
                        simpleCheckedTextView.setChecked(true);
                        filedl.add(mfilepath);
                        AllfileDl.add(filee);
                    }
                    Log.d("Hi",Integer.toString(filedl.size()));

                }
            });
            TextView filenameView = (TextView) mView.findViewById(R.id.file_single_filename);
            filenameView.setText(filename);
        }
    }

}
