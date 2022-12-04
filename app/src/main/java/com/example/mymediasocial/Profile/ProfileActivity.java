package com.example.mymediasocial.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.example.mymediasocial.R;
import com.example.mymediasocial.models.Photo;
import com.example.mymediasocial.models.Text;
import com.example.mymediasocial.models.User;
import com.example.mymediasocial.models.Video;
import com.example.mymediasocial.utils.TextAdapter;
import com.example.mymediasocial.utils.VideoAdapter;
import com.example.mymediasocial.utils.ViewCommentsFragment;
import com.example.mymediasocial.utils.ViewPostFragment;
import com.example.mymediasocial.utils.ViewPostTextCommentsFragment;
import com.example.mymediasocial.utils.ViewProfilFragment;
import com.example.mymediasocial.utils.ViewVideoCommentsFragment;


public class ProfileActivity extends AppCompatActivity implements

        ViewProfilFragment.OnGridImageSelectedListener,
        ViewPostFragment.OnCommentThreadSelectedListener,
        ProfileFragment.OnGridImageSelectedListener,
        TextAdapter.OnLoadMoreItemsListener,
        VideoAdapter.OnLoadMoreItemsListener,
        TextFragment.OnCommentThreadSelectedListener,
        VideoFragment.OnCommentThreadSelectedListener
        {
    private static final String TAG="ProfileActivity";
    private Context mContext=ProfileActivity.this;

    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;

            @Override
            public void onLoadMoreItems() {
                Log.d(TAG, "onLoadMoreItems: displaying more photos");
                TextFragment fragment = (TextFragment)getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.viewpager_container);
                if(fragment != null){
                    fragment.displayMoreTexts();
                }
            }
            public void onLoadMoreVideos() {
                Log.d(TAG, "onLoadMoreItems: displaying more photos");
                VideoFragment fragment = (VideoFragment)getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.viewpager_container);
                if(fragment != null){
                    fragment.displayMoreVideos();
                }
            }



    @Override
    public void onCommentThreadSelectedListener(Photo photo) {
        Log.d(TAG, "onCommentThreadSelectedListener:  selected a comment thread");

        ViewCommentsFragment fragment = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.containers, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();
    }
            @Override
            public void onCommentThreadSelectedListener(Video video) {
                Log.d(TAG, "onCommentThreadSelectedListener:  selected a comment thread");

                ViewVideoCommentsFragment fragment = new ViewVideoCommentsFragment();
                Bundle args = new Bundle();
                args.putParcelable(getString(R.string.video), video);
                fragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.containers, fragment);
                transaction.addToBackStack(getString(R.string.view_comments_fragment));
                transaction.commit();
            }
    @Override
    public void onGridImageSelected(Photo photo, int activityNumber) {
        Log.d(TAG, "onGridImageSelected: selected an image gridview: " + photo.toString());

        ViewPostFragment fragment = new ViewPostFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putInt(getString(R.string.activity_number), activityNumber);

        fragment.setArguments(args);

        FragmentTransaction transaction  = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.containers, fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mFrameLayout = (FrameLayout) findViewById(R.id.containers);
        init();

    }
    public void showLayout(){
        Log.d(TAG, "hideLayout: showing layout");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE);
    }

    private void init(){
        Log.d(TAG, "init: inflating " + getString(R.string.profile_fragment));

        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "init: searching for user object attached as intent extra");
            if(intent.hasExtra(getString(R.string.intent_user))){
                User user = intent.getParcelableExtra(getString(R.string.intent_user));
                if(!user.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Log.d(TAG, "init: inflating view profile");
                    ViewProfilFragment fragment = new ViewProfilFragment();
                    Bundle args = new Bundle();
                    args.putParcelable(getString(R.string.intent_user),
                            intent.getParcelableExtra(getString(R.string.intent_user)));
                    fragment.setArguments(args);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.containers, fragment);
                    transaction.addToBackStack(getString(R.string.view_profile_fragment));
                    transaction.commit();
                }else{
                    Log.d(TAG, "init: inflating Profile");
                    ProfileFragment fragment = new ProfileFragment();
                    FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.containers, fragment);
                    transaction.addToBackStack(getString(R.string.post_text_fragment));
                    transaction.commit();
                }
            }else{
                Toast.makeText(mContext, "something went wrong", Toast.LENGTH_SHORT).show();
            }

        }else{  ProfileFragment fragment = new ProfileFragment();
            FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.containers, fragment);
            transaction.addToBackStack(getString(R.string.post_text_fragment));
            transaction.commit();
        }

    }

    @Override
    public void onCommentThreadSelectedListener(Text text) {
        Log.d(TAG, "onCommentThreadSelected: selected a comment thread");

        ViewPostTextCommentsFragment fragment  = new ViewPostTextCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.text), text);
        args.putString(getString(R.string.profile_activity), getString(R.string.profile_activity));
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.containers, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }
    public void onCommentThreadSelected(Text text, String callingActivity){
        Log.d(TAG, "onCommentThreadSelected: selected a coemment thread");

        ViewPostTextCommentsFragment fragment  = new ViewPostTextCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.text), text);
        args.putString(getString(R.string.profile_activity), getString(R.string.profile_activity));
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.containers, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }
            public void onCommentThreadSelected(Video video, String callingActivity){
                Log.d(TAG, "onCommentThreadSelected: selected a comment thread");

                ViewVideoCommentsFragment fragment  = new ViewVideoCommentsFragment();
                Bundle args = new Bundle();
                args.putParcelable(getString(R.string.video), video);
                args.putString(getString(R.string.profile_activity), getString(R.string.profile_activity));
                fragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.containers, fragment);
                transaction.addToBackStack(getString(R.string.view_comments_fragment));
                transaction.commit();

            }

    public void hideLayout(){
        Log.d(TAG, "hideLayout: hiding layout");
        mFrameLayout.setVisibility(View.VISIBLE);
    }

}



