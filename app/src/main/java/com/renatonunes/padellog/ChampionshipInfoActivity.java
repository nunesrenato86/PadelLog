package com.renatonunes.padellog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.renatonunes.padellog.domain.Championship;
import com.renatonunes.padellog.domain.util.ImageFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChampionshipInfoActivity extends AppCompatActivity
	implements AppBarLayout.OnOffsetChangedListener {

	private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private static Context context;
    private boolean mIsAvatarShown = true;

    @BindView(R.id.championship_detail_title)
    TextView textTitle;

    @BindView(R.id.championship_detail_subtitle)
    TextView textSubtitle;

    @BindView(R.id.championship_detail_top_picture)
    ImageView TopImage;

    @BindView(R.id.fab_add_match)
    FloatingActionButton fabAddMatch;

    @BindView(R.id.materialup_profile_image)
	ImageView mProfileImage;

	private int mMaxScrollSize;

	public static Championship currentChampionship;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_championship_info);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.materialup_tabs);
		ViewPager viewPager = (ViewPager) findViewById(R.id.materialup_viewpager);
		AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.materialup_appbar);

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

		Toolbar toolbar = (Toolbar) findViewById(R.id.materialup_toolbar);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				onBackPressed();
			}
		});

        fabAddMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMatchActivity.start(context, currentChampionship);
            }
        });

		appbarLayout.addOnOffsetChangedListener(this);
		mMaxScrollSize = appbarLayout.getTotalScrollRange();

		viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager()));
		tabLayout.setupWithViewPager(viewPager);

        ButterKnife.setDebug(true);
        ButterKnife.bind(this);

        //setting top image
        if (currentChampionship.getImageStr().isEmpty()){
            TopImage.setImageResource(R.drawable.no_photo);
        }else{
            TopImage.setImageBitmap(ImageFactory.imgStrToImage(currentChampionship.getImageStr()));
        }

        //setting championship title
        textTitle.setText(currentChampionship.getName());

        //setting championship title
        textSubtitle.setText(currentChampionship.getInitialDate()
                + " até "
                + currentChampionship.getFinalDate());
	}

	public static void start(Context c, Championship championship) {
        context = c;
		currentChampionship = championship;

		c.startActivity(new Intent(c, ChampionshipInfoActivity.class));
	}

	@Override
	public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
		if (mMaxScrollSize == 0)
			mMaxScrollSize = appBarLayout.getTotalScrollRange();

		int percentage = (Math.abs(i)) * 100 / mMaxScrollSize;

		if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
			mIsAvatarShown = false;
			mProfileImage.animate().scaleY(0).scaleX(0).setDuration(200).start();
		}

		if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
			mIsAvatarShown = true;

			mProfileImage.animate()
				.scaleY(1).scaleX(1)
				.start();
		}
	}

    @Override
    protected void onResume() {
        super.onResume();
        updateUi();
    }

    private void updateUi(){
		if (currentChampionship.getResult() == 8){
			mProfileImage.setVisibility(View.VISIBLE);
			mProfileImage.setImageResource(R.drawable.trophy_gold);
		}else if (currentChampionship.getResult() == 7){
			mProfileImage.setVisibility(View.VISIBLE);
			mProfileImage.setImageResource(R.drawable.trophy_silver);
		}else
			mProfileImage.setVisibility(View.INVISIBLE);

	}

	class TabsAdapter extends FragmentPagerAdapter {
		public TabsAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 2;
		}
//		public int getCount() {
//			return 3;
//		}

		@Override
		public Fragment getItem(int i) {
			switch(i) {
				case 0: return ChampionshipInfoFragment.newInstance(currentChampionship);
				case 1: return MatchListFragment.newInstance(currentChampionship, context);
//				case 2: return MatchListFragment.newInstance();
			}
			return null;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch(position) {
				case 0: return "Informações";
				case 1: return "Jogos";
//				case 2: return "Estatísticas";
			}
			return "";
		}
	}

}