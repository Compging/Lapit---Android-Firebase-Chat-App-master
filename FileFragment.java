package in.tvac.akshaye.lapitchat;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FileFragment extends android.support.v4.app.Fragment {

    private RecyclerView mFileList;

    private DatabaseReference mFileDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_file, container, false);

        mFileList = (RecyclerView) mMainView.findViewById(R.id.file_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFileDatabase = FirebaseDatabase.getInstance().getReference().child("File").child(mCurrent_user_id);
        mFileDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrent_user_id);
        mUsersDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);


        mFileList.setHasFixedSize(true);
        mFileList.setLayoutManager(linearLayoutManager);

        // Inflate the layout for this fragment
        return mMainView;

    }
    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<File,FileViewHolder> firebaseFileAdapter = new FirebaseRecyclerAdapter<File, FileViewHolder>(
                File.class,
                R.layout.file_single_layout,
                FileViewHolder.class,
                mFileDatabase
        ) {
            @Override
            protected void populateViewHolder(FileViewHolder fileViewHolder, File file, int i) {
                fileViewHolder.setFilename(file.getFilename());
            }
        };

    }

    public class FileViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FileViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setFilename(String filename){
            TextView filenameview = (TextView) mView.findViewById(R.id.file_single_filename);
            filenameview.setText(filename);
        }
    }



}
