package com.example.mymediasocial.Profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.example.mymediasocial.R;
import com.example.mymediasocial.models.Comment;
import com.example.mymediasocial.models.UserAccountSettings;
import com.example.mymediasocial.models.UserSettings;
import com.example.mymediasocial.models.Video;
import com.example.mymediasocial.utils.BottomNavigationHelper;
import com.example.mymediasocial.utils.FirebaseMethods;
import com.example.mymediasocial.utils.UniversalImageLoader;
import com.example.mymediasocial.utils.VideoAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sprylab.android.widget.TextureVideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoFragment extends Fragment {

    private static final String TAG = "VideoFragment";
    private static final int ACTIVITY_NUM = 4;

    private VideoAdapter adapter;


    public interface OnCommentThreadSelectedListener {
        void onCommentThreadSelectedListener(Video video);
    }

    OnCommentThreadSelectedListener mOnCommentThreadSelectedListener;

    private ArrayList<Video> mVideos;
    private ArrayList<Video> mPaginatedVideos;
    private ArrayList<String> mFollowing;
    private int recursionIterator = 0;
    //    private ListView mListView;
    private ListView mListView;
    private int resultsCount = 0;
    private ArrayList<UserAccountSettings> mUserAccountSettings;
    private TextureVideoView mVideoView;


    private static final int NUM_GRID_COLUMNS = 3;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;


    //widgets
    private TextView mPosts, mFollowers, mFollowings, mDisplayName, mUsername, mWebsite, mDescription;
    private ProgressBar mProgressBar;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationView bottomNavigationView;
    private Context mContext;
    private ImageButton play;
    private GestureDetector mGestureDetector;


    //vars
    private int mFollowersCount = 0;
    private int mFollowingCount = 0;
    private int mPostsCount = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        play = (ImageButton) view.findViewById(R.id.play);
        mDisplayName = (TextView) view.findViewById(R.id.display_name);
        mUsername = (TextView) view.findViewById(R.id.username);
        mWebsite = (TextView) view.findViewById(R.id.website);
        mDescription = (TextView) view.findViewById(R.id.description);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mPosts = (TextView) view.findViewById(R.id.tvPosts);
        mFollowers = (TextView) view.findViewById(R.id.tvFollowers);
        mFollowings = (TextView) view.findViewById(R.id.tvFollowing);
        mProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        gridView = (GridView) view.findViewById(R.id.gridView);
        toolbar = (Toolbar) view.findViewById(R.id.profileToolBar);
        profileMenu = (ImageView) view.findViewById(R.id.profileMenu);
        bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottomNavViewBar);
        mContext = getActivity();
        mVideoView = (TextureVideoView) view.findViewById(R.id.post_image);

        mFirebaseMethods = new FirebaseMethods(getActivity());
        mGestureDetector = new GestureDetector(getActivity(), new GestureListeners());

        Log.d(TAG, "onCreateView: stared.");
        mListView = (ListView) view.findViewById(R.id.text_list);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));

        setupBottomNavigationView();
        setupToolbar();

        setupFirebaseAuth();
//        setupGridView();
        initListViewRefresh();

        getFollowersCount();
        getFollowingCount();
        getPostsCount();
        getFollowing();


        TextView editProfile = (TextView) view.findViewById(R.id.textEditProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to " + mContext.getString(R.string.edit_profile_fragment));
                Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        return view;
    }

    private void initListViewRefresh() {
        mListView.setHorizontalFadingEdgeEnabled(true);
        mListView.setAdapter(adapter);
    }


    public class GestureListeners extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Toast.makeText(getContext(), "SingleTap", Toast.LENGTH_SHORT).show();
            if (!mVideoView.isPlaying()) {
                mVideoView.start();
            } else {
                mVideoView.pause();
                play.setVisibility(View.VISIBLE);

            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap: double tap detected.");

            Toast.makeText(getContext(), "DoubleTap", Toast.LENGTH_SHORT).show();
//            if (!mVideoView.isPlaying()) {
//                mVideoView.start();
//            } else {
//                mVideoView.pause();
//                play.setVisibility(View.VISIBLE);
//
//            }
            return true;
        }

    }


    private void getFriendsAccountSettings() {
        Log.d(TAG, "getFriendsAccountSettings: getting friends account settings.");

        for (int i = 0; i < mFollowing.size(); i++) {
            Log.d(TAG, "getFriendsAccountSettings: user: " + mFollowing.get(i));
            final int count = i;
            Query query = FirebaseDatabase.getInstance().getReference()
                    .child(getString(R.string.dbname_user_account_settings))
                    .orderByKey()
                    .equalTo(mFollowing.get(i));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d(TAG, "getFriendsAccountSettings: got a user: " + snapshot.getValue(UserAccountSettings.class).getDisplay_name());
                        mUserAccountSettings.add(snapshot.getValue(UserAccountSettings.class));

                        if (count == 0) {
                            JSONObject userObject = new JSONObject();
                            try {
                                userObject.put(getString(R.string.field_display_name), mUserAccountSettings.get(count).getDisplay_name());
                                userObject.put(getString(R.string.field_username), mUserAccountSettings.get(count).getUsername());
                                userObject.put(getString(R.string.field_profile_photo), mUserAccountSettings.get(count).getProfile_photo());
                                userObject.put(getString(R.string.field_user_id), mUserAccountSettings.get(count).getUser_id());
                                JSONObject userSettingsStoryObject = new JSONObject();
                                userSettingsStoryObject.put(getString(R.string.user_account_settings), userObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    private void clearAll() {
        if (mFollowing != null) {
            mFollowing.clear();
        }
        if (mVideos != null) {
            mVideos.clear();
            if (adapter != null) {
                adapter.clear();
                adapter.notifyDataSetChanged();
            }
        }
        if (mUserAccountSettings != null) {
            mUserAccountSettings.clear();
        }
        if (mPaginatedVideos != null) {
            mPaginatedVideos.clear();
        }
        mFollowing = new ArrayList<>();
        mVideos = new ArrayList<>();
        mPaginatedVideos = new ArrayList<>();
        mUserAccountSettings = new ArrayList<>();
    }


    private void getFollowersCount() {
        mFollowersCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_followers))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found follower:" + singleSnapshot.getValue());
                    mFollowersCount++;
                }
                mFollowers.setText(String.valueOf(mFollowersCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFollowingCount() {
        mFollowingCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found following user:" + singleSnapshot.getValue());
                    mFollowingCount++;
                }
                mFollowings.setText(String.valueOf(mFollowingCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPostsCount() {
        mPostsCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_videos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found post:" + singleSnapshot.getValue());
                    mPostsCount++;
                }
                mPosts.setText(String.valueOf(mPostsCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setProfileWidgets(UserSettings userSettings) {
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getSettings().getUsername());


        //User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();
//        ImageLoader imageLoader = ImageLoader.getInstance();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");

//        Glide.with(getActivity())
//                .load(settings.getProfile_photo())
//                .into(mProfilePhoto);

        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mWebsite.setText(settings.getWebsite());
        mDescription.setText(settings.getDescription());
        mProgressBar.setVisibility(View.GONE);
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Toast.makeText(getActivity(), "SingleTap", Toast.LENGTH_SHORT).show();
            if (!mVideoView.isPlaying()) {
                mVideoView.start();
            } else {
                mVideoView.pause();
                play.setVisibility(View.VISIBLE);

            }
            return true;
        }
    }


        /**
         * Responsible for setting up the profile toolbar
         */
        private void setupToolbar() {

            ((ProfileActivity) getActivity()).setSupportActionBar(toolbar);

            profileMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: navigating to account settings.");
                    Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        }

        /**
         * BottomNavigationView setup
         */
        private void setupBottomNavigationView() {
            Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
            BottomNavigationHelper.enableNavigation(mContext, getActivity(), bottomNavigationView);
            Menu menu = bottomNavigationView.getMenu();
            MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
            menuItem.setChecked(true);
        }

      /*
    ------------------------------------ Firebase ---------------------------------------------
     */

        /**
         * Setup the firebase auth object
         */
        private void setupFirebaseAuth() {
            Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

            mAuth = FirebaseAuth.getInstance();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            myRef = mFirebaseDatabase.getReference();

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();


                    if (user != null) {
                        // User is signed in
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    } else {
                        // User is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                    // ...
                }
            };


            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //retrieve user information from the database
                    setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));

                    //retrieve images for the user in question

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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

        private void getFollowing() {
            Log.d(TAG, "getFollowing: searching for following");

            clearAll();
            //also add your own id to the list
            mFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

            Query query = FirebaseDatabase.getInstance().getReference()
                    .child(getActivity().getString(R.string.dbname_following))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        Log.d(TAG, "getFollowing: found user: " + singleSnapshot
                                .child(getString(R.string.field_user_id)).getValue());

                        mFollowing.add(singleSnapshot
                                .child(getString(R.string.field_user_id)).getValue().toString());
                    }

                    getVideos();
//                getMyUserAccountSettings();
                    getFriendsAccountSettings();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

        }

        private void getVideos() {
            Log.d(TAG, "getPhotos: getting list of photos");

            for (int i = 0; i < mFollowing.size(); i++) {
                final int count = i;
                Query query = FirebaseDatabase.getInstance().getReference()
                        .child(getActivity().getString(R.string.dbname_user_videos))
                        .child(mFollowing.get(i))
                        .orderByChild(getString(R.string.field_user_id))
                        .equalTo(mFollowing.get(i));
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                            Video newVideo = new Video();
                            Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                            newVideo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                            newVideo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                            newVideo.setVideo_id(objectMap.get(getString(R.string.field_video_id)).toString());
                            newVideo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                            newVideo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                            newVideo.setVideo_path(objectMap.get(getString(R.string.field_video_path)).toString());
                            Uri myUri = Uri.parse(newVideo.getVideo_path());
                            mVideoView.setVideoURI(myUri);



                            Log.d(TAG, "getPhotos: photo: " + newVideo.getVideo_id());
                            List<Comment> commentsList = new ArrayList<Comment>();
                            for (DataSnapshot dSnapshot : singleSnapshot
                                    .child(getString(R.string.field_comments)).getChildren()) {
                                Map<String, Object> object_map = (HashMap<String, Object>) dSnapshot.getValue();
                                Comment comment = new Comment();
                                comment.setUser_id(object_map.get(getString(R.string.field_user_id)).toString());
                                comment.setComment(object_map.get(getString(R.string.field_comment)).toString());
                                comment.setDate_created(object_map.get(getString(R.string.field_date_created)).toString());
                                commentsList.add(comment);
                            }
                            newVideo.setComments(commentsList);
                            mVideos.add(newVideo);
                        }
                        if (count >= mFollowing.size() - 1) {
                            //display the photos
                            displayVideos();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: query cancelled.");
                    }
                });

            }
        }

        private void displayVideos() {
//        mPaginatedPhotos = new ArrayList<>();
            if (mVideos != null) {

                try {

                    //sort for newest to oldest
                    Collections.sort(mVideos, new Comparator<Video>() {
                        public int compare(Video o1, Video o2) {
                            return o2.getDate_created().compareTo(o1.getDate_created());
                        }
                    });

                    //we want to load 10 at a time. So if there is more than 10, just load 10 to start
                    int iterations = mVideos.size();
                    if (iterations > 10) {
                        iterations = 10;
                    }
//
                    resultsCount = 0;
                    for (int i = 0; i < iterations; i++) {
                        mPaginatedVideos.add(mVideos.get(i));
                        resultsCount++;
                        Log.d(TAG, "displayPhotos: adding a photo to paginated list: " + mVideos.get(i).getVideo_id());
                    }

                    adapter = new VideoAdapter(getActivity(), R.layout.fragment_view_video, mPaginatedVideos);
                    mListView.setAdapter(adapter);
                } catch (IndexOutOfBoundsException e) {
                    Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage());
                } catch (NullPointerException e) {
                    Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage());
                }
            }
        }

        public void displayMoreVideos() {
            Log.d(TAG, "displayMorePhotos: displaying more photos");

            try {

                if (mVideos.size() > resultsCount && mVideos.size() > 0) {

                    int iterations;
                    if (mVideos.size() > (resultsCount + 10)) {
                        Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                        iterations = 10;
                    } else {
                        Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                        iterations = mVideos.size() - resultsCount;
                    }

                    //add the new photos to the paginated list
                    for (int i = resultsCount; i < resultsCount + iterations; i++) {
                        mPaginatedVideos.add(mVideos.get(i));
                    }

                    resultsCount = resultsCount + iterations;
                    adapter.notifyDataSetChanged();
                }
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage());
            } catch (NullPointerException e) {
                Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage());
            }
        }


}

