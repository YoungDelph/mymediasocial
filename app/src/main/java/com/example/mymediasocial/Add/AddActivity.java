package com.example.mymediasocial.Add;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.mymediasocial.Home.SectionPagerAdapter;
import com.example.mymediasocial.R;
import com.example.mymediasocial.utils.Permissions;
import com.example.mymediasocial.utils.Videos;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity {
    private static final String TAG = "AddActivity";
    private static final int ACTIVITY_NUM = 2;
    private Context mContext=AddActivity.this;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private ViewPager2 mViewPager;
    private BottomNavigationView view;
    private ArrayList<Fragment> arr;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Log.d(TAG, "onCreate: starting.");
//        setupBottomNavigationView();
//        button=findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
                showDialog();
//            }
//        });

        if(checkPermissionsArray(Permissions.PERMISSIONS)){
              setupViewPager();
        }else{
            verifyPermissions(Permissions.PERMISSIONS);
        }
    }
    public int getCurrentTabNumber(){
        return mViewPager.getCurrentItem();
    }

    /**
     * setup viewpager for manager the tabs
     */
    private void setupViewPager(){

        arr=new ArrayList<>();
        arr.add(new GalleryFragment());
        arr.add(new PhotoFragment());

        SectionPagerAdapter adapter=new SectionPagerAdapter(this,arr);
        mViewPager=findViewById(R.id.viewpager_container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsBottom);
//        new TabLayoutMediator(tabLayout, mViewPager,
//                (tab, position) -> tab.getCustomView()
//        ).attach();
        new TabLayoutMediator(tabLayout, mViewPager, (tab, position) -> {
            if (position==0){
                tab.setText(getString(R.string.gallery));
            }
            else if(position==1) {
                tab.setText(getString(R.string.photo));
            }
        }).attach();

    }


    public int getTask(){
        Log.d(TAG, "getTask: TASK: " + getIntent().getFlags());
        return getIntent().getFlags();
    }

    /**
     * verifiy all the permissions passed to the array
     * @param permissions
     */
    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                AddActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    /**
     * Check an array of permissions
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for(int i = 0; i< permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }

    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(AddActivity.this, permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        }
        else{
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }
    private void showDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pop_thing);

        LinearLayout textLayout = dialog.findViewById(R.id.layoutText);
        LinearLayout photoLayout = dialog.findViewById(R.id.layoutPhoto);
        LinearLayout videoLayout = dialog.findViewById(R.id.layoutVideo);

        textLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(mContext,"Edit is Clicked",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),AddTextActivity.class));

            }
        });

        photoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        videoLayout.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                dialog.dismiss();
                startActivity(new Intent(getApplicationContext(), Videos.class));
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }
//    private void setupSettingsList(){
//        Log.d(TAG, "setupSettingsList: initializing 'Account Settings' list.");
//        ListView listView = (ListView) findViewById(R.id.lvAccountSettings);
//
//        ArrayList<String> options = new ArrayList<>();
//        options.add(getString(R.string.post_text_fragment)); //fragment 0
//        options.add(getString(R.string.post_video_fragment)); //fragement 1
//
//        ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, options);
//        listView.setAdapter(adapter);
//
//        listView.setOnItemClickListener((parent, view, position, id) -> {
//            Log.d(TAG, "onItemClick: navigating to fragment#: " + position);
//            setViewPager(position);
//        });
//
//    }
}
