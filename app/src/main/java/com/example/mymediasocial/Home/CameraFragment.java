package com.example.mymediasocial.Home;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymediasocial.R;

public class CameraFragment extends Fragment {
    private RecyclerView recyclerView_story;
    //    private StoryAdapter storyAdapter;
//    private List<Story> storyList;
    private static final String TAG = "CameraFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceStace) {
//        recyclerView_story= view.findViewById(R.id.recycler_view_story);
//        recyclerView_story.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(getContext(),
//                LinearLayoutManager.HORIZONTAL,false );
//        recyclerView_story.setLayoutManager(linearLayoutManager1);
//        storyList= new ArrayList<>();
//        storyAdapter=new StoryAdapter(getContext(), storyList);
//        recyclerView_story.setAdapter(storyAdapter);

        View view=inflater.inflate(R.layout.fragment_camera,container,false);
        return view;
    }

}
