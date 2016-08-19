package com.renatonunes.padellog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.renatonunes.padellog.domain.Championship;

public class ChampionshipInfoFragment extends Fragment {
    private static Championship currentChampionship;
    private RecyclerView mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (RecyclerView) inflater.inflate(R.layout.fragment_championship_info, container, false);

//        mRootView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                FloatingActionButton fab = ((FloatingActionButton) getActivity().findViewById(R.id.fab_add_match));
//
//                if (dy > 0)
//                    fab.hide();
//                else if (dy < 0)
//                    fab.show();
//            }
//        });

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        initRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initRecyclerView();
    }

    private void initRecyclerView() {
        mRootView.setAdapter(new ChampionshipInfoAdapter(getActivity().getApplicationContext(), currentChampionship));
    }

    public static Fragment newInstance(Championship championship) {
        
        currentChampionship = championship;

        return new ChampionshipInfoFragment();
    }
}