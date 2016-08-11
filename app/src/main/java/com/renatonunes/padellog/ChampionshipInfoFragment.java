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
    private RecyclerView mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (RecyclerView) inflater.inflate(R.layout.fragment_championship_info, container, false);
        return mRootView;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRecyclerView();
    }

    private void initRecyclerView() {
        Championship c = new Championship();
        c.setPartner("Teste");
        c.setPlace("Teste");
        c.setCategory("Teste");
        c.setFinalDate("Teste");
        c.setInitialDate("Teste");

        mRootView.setAdapter(new ChampionshipInfoAdapter(getActivity().getApplicationContext(), c));
    }

    public static Fragment newInstance() {

        return new ChampionshipInfoFragment();
    }
}