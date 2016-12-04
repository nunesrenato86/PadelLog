package com.renatonunes.padellog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.renatonunes.padellog.domain.Championship;
import com.renatonunes.padellog.domain.util.ImageFactory;
import com.renatonunes.padellog.domain.util.LibraryClass;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChampionshipInfoActivity extends CommonActivity
	implements AppBarLayout.OnOffsetChangedListener {

	private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private static final int PERCENTAGE_TO_ANIMATE_TITLES = 80;
    private static Context context;
    private boolean mIsAvatarShown = true;
    private boolean mIsTitlesShown = true;
    private static String mFirstName;

    private static Boolean mIsReadOnly = false;

    private static ProgressDialog mProgressDialog;

    @BindView(R.id.championship_detail_title)
    TextView textTitle;

    @BindView(R.id.championship_detail_subtitle)
    TextView textSubtitle;

    @BindView(R.id.championship_detail_top_picture)
    ImageView TopImage;

	@BindView(R.id.btn_menu_championship_info)
	ImageButton btnMenuChampionshipInfo;

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

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(R.string.msg_loading));
        mProgressDialog.setIndeterminate(true);

		Toolbar toolbar = (Toolbar) findViewById(R.id.championship_info_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
//        actionbar.setTitle("");


//
//		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//			@Override public void onClick(View v) {
//				onBackPressed();
//			}
//		});

        fabAddMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMatchActivity.start(context, currentChampionship, null);
            }
        });

		registerForContextMenu(btnMenuChampionshipInfo);

		appbarLayout.addOnOffsetChangedListener(this);
		mMaxScrollSize = appbarLayout.getTotalScrollRange();

		viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager()));
		tabLayout.setupWithViewPager(viewPager);

		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {

                //fabAddMatch.show();

                if (tab.getPosition() == 1) {
                    new Wait().execute();
                }

                fabAddMatch.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});

        ButterKnife.setDebug(true);
        ButterKnife.bind(this);

        setUIPermission();
	}

    private void setUIPermission(){
        if (mIsReadOnly){
            fabAddMatch.setVisibility(View.INVISIBLE);
        }else{
            fabAddMatch.setVisibility(View.VISIBLE);
        }

    }

//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//		super.onCreateContextMenu(menu, v, menuInfo);
//
//		getMenuInflater().inflate(R.menu.context_menu, menu);
//
////		if(v.getId() == R.id.btn_menu_championship_info){
////			getMenuInflater().inflate(R.menu.context_menu, menu);
////		}
//	}

//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//		switch(item.getItemId()){
//			case R.id.menuEdit:
//				AddChampionshipActivity.start(context, currentChampionship);
//
//				break;
//			case R.id.menuDelete:
//                deleteMatchesThenChampionship();
//
//				break;
//			default:
//				break;
//		}
//		return super.onContextItemSelected(item);
//	}

    public void deleteMatchesThenChampionship(){

        FirebaseDatabase.getInstance().getReference()
                .child("matches")
                .child(currentChampionship.getId()).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.setValue(null); // This removes the node.
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                deleteChampionship();
            }
        });

    }

    public void deleteChampionship(){
        currentChampionship.getPlayer().decTotalChampionship();

        FirebaseDatabase.getInstance().getReference()
                .child("championships")
                .child(currentChampionship.getOwner())
                .child(currentChampionship.getId()).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.setValue(null); // This removes the node.
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                Toast.makeText(ChampionshipInfoActivity.this,
                        getResources().getString(R.string.msg_action_deleted),
                        Toast.LENGTH_SHORT).show();

//                        Snackbar.make(fabAddMatch,
//								getResources().getString(R.string.msg_action_deleted),
//								Snackbar.LENGTH_LONG)
//								.setAction("Action", null).show();

                finish();
            }
        });
    }

	public void callEditChampionship(View v){
		openContextMenu(v);
	}

	public static void start(Context c, Championship championship, Boolean isReadOnly, String firstName) {
        context = c;
		currentChampionship = championship;
        mIsReadOnly = isReadOnly;
        mFirstName = firstName;

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

        if (percentage >= PERCENTAGE_TO_ANIMATE_TITLES && mIsTitlesShown) {
            mIsTitlesShown = false;
            textTitle.animate().scaleY(0).scaleX(0).setDuration(200).start();
            textSubtitle.animate().scaleY(0).scaleX(0).setDuration(200).start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_TITLES && !mIsTitlesShown) {
            mIsTitlesShown = true;

            textTitle.animate()
                    .scaleY(1).scaleX(1)
                    .start();

            textSubtitle.animate()
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
		//setting top image
		if (currentChampionship.getImageStr().isEmpty()){
			TopImage.setImageResource(R.drawable.no_photo);
		}else{
			TopImage.setImageBitmap(ImageFactory.imgStrToImage(currentChampionship.getImageStr()));
		}

		//setting championship title
		textTitle.setText(currentChampionship.getName());

		//setting championship title
		textSubtitle.setText(currentChampionship.getInitialDateStr()
				+ " até "
				+ currentChampionship.getFinalDateStr());

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
				case 0: return ChampionshipInfoFragment.newInstance(currentChampionship, mIsReadOnly, mFirstName);
				case 1: return MatchListFragment.newInstance(currentChampionship, context, mIsReadOnly, mFirstName);
//				case 2: return MatchListFragment.newInstance();
			}
			return null;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch(position) {
				case 0: return getResources().getString(R.string.tab_info);
				case 1: return getResources().getString(R.string.tab_matches);
//				case 2: return "Estatísticas";
			}
			return "";
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!mIsReadOnly) {
            getMenuInflater().inflate(R.menu.menu_match_info, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_match_edit) {
            AddChampionshipActivity.start(context, currentChampionship, currentChampionship.getCategory(), currentChampionship.getPlayer());

            return true;
        }else if (id == R.id.action_match_delete){
            askToDeleteChampionship();
            return true;
        }else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void askToDeleteChampionship(){

        if (LibraryClass.isNetworkActive(this)) {
            AlertDialog dialogo = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.title_dlg_confirm_delete))
                    .setMessage(getResources().getString(R.string.msg_championship_delete))
                    .setPositiveButton(getResources().getString(R.string.btn_delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteMatchesThenChampionship();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.btn_cancel), null)
                    .create();

            dialogo.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = ((AlertDialog) dialog)
                            .getButton(AlertDialog.BUTTON_POSITIVE);

                    positiveButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                    Button negativeButton = ((AlertDialog) dialog)
                            .getButton(AlertDialog.BUTTON_NEGATIVE);

                    negativeButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            });

            dialogo.show();

        }else{
            showSnackbar(fabAddMatch, getResources().getString(R.string.msg_no_internet) );
        }
    }

    private class Wait extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            openProgressBar();



            if (((MatchListFragment.matches != null)) && (MatchListFragment.matches.size() == 0)) {
                showProgressDialog();
            }
//            Toast.makeText(getActivity().getApplicationContext(), "Carregando", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(5000);
            }
            catch (InterruptedException ie) {
                Log.d("RNN2", ie.toString());
            }

            if (MatchListFragment.matches != null){
                return(MatchListFragment.matches.size() == 0);
            }else{
                return(true);
            }
            //return(true);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool) {
                //closeProgressBar();
                hideProgressDialog();
//                Toast.makeText(getActivity().getApplicationContext(), "Carregado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void openProgressBar(){
        progressBar.setVisibility( View.VISIBLE );
    }

    protected void closeProgressBar(){
        progressBar.setVisibility( View.INVISIBLE );
    }

    public void showProgressDialog() {
//        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(this);
//            mProgressDialog.setMessage(getResources().getString(R.string.msg_loading));
//            mProgressDialog.setIndeterminate(true);
//        }

        mProgressDialog.show();
    }

    public static void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


}
