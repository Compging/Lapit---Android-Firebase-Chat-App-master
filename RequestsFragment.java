package in.tvac.akshaye.lapitchat;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private View mMainView;
    private RecyclerView mRequestlist;

    private DatabaseReference mRequestDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private FirebaseStorage mFirebase;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //return inflater.inflate(R.layout.fragment_requests, container, false);
        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);
        mRequestlist = (RecyclerView) mMainView.findViewById(R.id.request_list);

        mAuth = FirebaseAuth.getInstance();
        mRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mAuth.getCurrentUser().getUid());
        mRequestDatabase.keepSynced(true);
        String test = mRequestDatabase.getKey();
        Log.d("test",test);

        mRequestlist.setHasFixedSize(true);
        mRequestlist.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        Log.d("On  Start", "Hi");



        FirebaseRecyclerAdapter<Friend_Request, RequestViewHolder> firebaseRequestAdapter = new FirebaseRecyclerAdapter<Friend_Request, RequestViewHolder>(
                Friend_Request.class,
                R.layout.request_single_layout,
                RequestViewHolder.class,
                mRequestDatabase
        ) {
            @Override
            protected void populateViewHolder(RequestViewHolder RequestViewHolder, Friend_Request friend_request, int i) {
                if(friend_request.getRequest_type()=="received"){

                }
                else{
                    //Do NOTHING
                }

            }
        };
        mRequestlist.setAdapter(firebaseRequestAdapter);


    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public RequestViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setStatus(String date){

            TextView userStatusView = (TextView) mView.findViewById(R.id.request_single_status);
            userStatusView.setText(date);

        }

        public void setName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.request_single_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.request_single_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

        }

    }

}




