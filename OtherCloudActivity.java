package in.tvac.akshaye.lapitchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherCloudActivity extends AppCompatActivity {

    private String mUserId;
    private String mUserName;

    private Button mDownloadBtn;
    private RecyclerView mOtherfilelist;
    private LinearLayoutManager mLayoutManager;

    private Toolbar mCloudToolbar;
    private TextView mTitleView;
    private TextView mLastSeenView;
    private CircleImageView mProfileImage;

    private DatabaseReference mFileDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private FirebaseStorage mFirebase;

    static List<File> AllfileDl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_cloud);

        mUserId = getIntent().getStringExtra("user_id");
        mUserName = getIntent().getStringExtra("user_name");

        mCloudToolbar = (Toolbar) findViewById(R.id.othercloud_appbar);
        setSupportActionBar(mCloudToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);
        // ---- Custom Action bar Items ----
        mTitleView = (TextView) findViewById(R.id.custom_bar_title);
        mLastSeenView = (TextView) findViewById(R.id.custom_bar_seen);
        mProfileImage = (CircleImageView) findViewById(R.id.custom_bar_image);
        mTitleView.setText(mUserName);

        mDownloadBtn = (Button) findViewById(R.id.othercloud_DL_btn);

        mLayoutManager = new LinearLayoutManager(this);
        mOtherfilelist = (RecyclerView) findViewById(R.id.otherfilelist);
        mOtherfilelist.setHasFixedSize(true);
        mOtherfilelist.setLayoutManager(mLayoutManager);


        mFileDatabase = FirebaseDatabase.getInstance().getReference().child("File").child(mUserId);
        mFirebase = FirebaseStorage.getInstance();
        AllfileDl = new ArrayList<File>();


        mDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AllfileDl.size()>0){
                    Download();
                }
                else{
                    Toast.makeText(OtherCloudActivity.this,"Please select file",Toast.LENGTH_LONG);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<File,OtherCloudActivity.FileViewHolder> firebaseFileAdapter = new FirebaseRecyclerAdapter<in.tvac.akshaye.lapitchat.File, OtherCloudActivity.FileViewHolder>(
                in.tvac.akshaye.lapitchat.File.class,
                R.layout.file_single_layout,
                OtherCloudActivity.FileViewHolder.class,
                mFileDatabase
        ) {
            @Override
            protected void populateViewHolder(final OtherCloudActivity.FileViewHolder fileViewHolder, in.tvac.akshaye.lapitchat.File file, int i) {

                fileViewHolder.setFilename(file.getFilename(),file.getFilepath(),file);
                fileViewHolder.SelectedFile(file);

                String file_id = file.getFileid();

                fileViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Log.d("Clicked",view.getTransitionName());
                    }
                });


            }
        };
        mOtherfilelist.setAdapter(firebaseFileAdapter);
    }





    public void Download(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Download...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        for(int i=0;i<AllfileDl.size();i++) {

            java.io.File rootpath = new java.io.File(Environment.getExternalStorageDirectory(), "XMD");
            if (!rootpath.exists()) {
                rootpath.mkdirs();
            }
            final java.io.File localFile = new java.io.File(rootpath, AllfileDl.get(i).filename);

            StorageReference httpsReference = mFirebase.getReferenceFromUrl(AllfileDl.get(i).getFilepath());
            httpsReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Local temp file has been created
                            Log.d("Fileee", localFile.getPath());
                            Log.d("Fileee1", localFile.toString());

                            Toast.makeText(OtherCloudActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage(((int) progress)+"% Downloaded...");
                        }
                    });
        }
        //progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.dismiss();

    }



    public static class FileViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public FileViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setFilename(String filename, final String mfilepath, final in.tvac.akshaye.lapitchat.File filee){
            TextView filenameView = (TextView) mView.findViewById(R.id.file_single_filename);
            filenameView.setText(filename);
        }

        public void SelectedFile(final File fileinlist){
            final CheckedTextView simpleCheckedTextView = (CheckedTextView) mView.findViewById(R.id.file_single_filename);
            // perform on Click Event Listener on CheckedTextView
            simpleCheckedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (simpleCheckedTextView.isChecked()) {
                        // set cheek mark drawable and set checked property to false
                        //simpleCheckedTextView.setCheckMarkDrawable(R.drawable.ic_check_box_outline_blank_black_24dp);
                        simpleCheckedTextView.setChecked(false);
                        AllfileDl.remove(fileinlist);

                    } else {
                        // set cheek mark drawable and set checked property to true
                        //simpleCheckedTextView.setCheckMarkDrawable(R.drawable.ic_check_box_black_24dp);
                        simpleCheckedTextView.setChecked(true);
                        AllfileDl.add(fileinlist);
                    }
                }
            });
        }
    }
}
