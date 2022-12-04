package com.example.mymediasocial.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.example.mymediasocial.Add.AddActivity;
import com.example.mymediasocial.Home.HomeActivity;
import com.example.mymediasocial.Notification.NotificationActivity;
import com.example.mymediasocial.Profile.ProfileActivity;
import com.example.mymediasocial.R;
import com.example.mymediasocial.Search.SearchActivity;

public class BottomNavigationHelper {
    public static void enableNavigation(final Context context, final Activity callingActivity, BottomNavigationView view){

      view.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
              switch (item.getItemId()){

                  case R.id.ic_house:
                      Intent intent1 = new Intent(context, HomeActivity.class);//ACTIVITY_NUM = 0
                      context.startActivity(intent1);
                      callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                      break;

                  case R.id.ic_search:
                      Intent intent2  = new Intent(context, SearchActivity.class);//ACTIVITY_NUM = 1
                      context.startActivity(intent2);
                      callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                      break;

                  case R.id.ic_circle:
                      Intent intent3 = new Intent(context, AddActivity.class);//ACTIVITY_NUM = 2
                      context.startActivity(intent3);
                      callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                      break;

                  case R.id.ic_alert:
                      Intent intent4 = new Intent(context, NotificationActivity.class);//ACTIVITY_NUM = 3
                      context.startActivity(intent4);
                      callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                      break;

                  case R.id.ic_android:
                      Intent intent5 = new Intent(context, ProfileActivity.class);//ACTIVITY_NUM = 4
                      context.startActivity(intent5);
                      callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                      break;
              }

              return false;
          }
        });
    }
}
