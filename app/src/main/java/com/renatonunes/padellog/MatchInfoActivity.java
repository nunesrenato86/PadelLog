/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.renatonunes.padellog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.renatonunes.padellog.domain.Championship;
import com.renatonunes.padellog.domain.Match;
import com.renatonunes.padellog.domain.util.ImageFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MatchInfoActivity extends AppCompatActivity {

	public static Match mCurrentMatch;
	public static Championship mCurrentChampionship;
	public static Context mContext;

	@BindView(R.id.main_collapsing)
	CollapsingToolbarLayout collapsingToolbarLayout;

	@BindView(R.id.fab_edit_match)
	FloatingActionButton fabEditMatch;

	@BindView(R.id.img_top_match)
	ImageView TopImage;

	@BindView(R.id.text_match_info_set_1_score_1)
	TextView textSet1Score1;

	@BindView(R.id.text_match_info_set_1_score_2)
	TextView textSet1Score2;

	@BindView(R.id.text_match_info_set_2_score_1)
	TextView textSet2Score1;

	@BindView(R.id.text_match_info_set_2_score_2)
	TextView textSet2Score2;

	@BindView(R.id.text_match_info_set_3_score_1)
	TextView textSet3Score1;

	@BindView(R.id.text_match_info_set_3_score_2)
	TextView textSet3Score2;

	@BindView(R.id.text_match_info_team_1)
	TextView textTeam1;

	@BindView(R.id.text_match_info_team_2)
	TextView textTeam2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.match_info_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

		ButterKnife.bind(this);
		ButterKnife.setDebug(true);

		fabEditMatch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
                editMatch();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_match_info, menu);
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
            editMatch();

			return true;
		}else if (id == R.id.action_match_delete){
			return true;
		}if (id == android.R.id.home) {
            finish();
            return true;
        }

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateUI();
	}

	public static void start(Context c, Match currentMatch, Championship currentChampionship) {
		mContext = c;
		mCurrentMatch = currentMatch;
		mCurrentChampionship = currentChampionship;
		c.startActivity(new Intent(c, MatchInfoActivity.class));
	}

    private void editMatch(){
        AddMatchActivity.start(mContext, mCurrentChampionship, mCurrentMatch);
    }

	private void updateUI(){
		//setting top image
		if (mCurrentMatch.getImageStr().isEmpty()){
			TopImage.setImageResource(R.drawable.no_photo);
		}
		else{
			TopImage.setImageBitmap(ImageFactory.imgStrToImage( mCurrentMatch.getImageStr() ));
		}

		collapsingToolbarLayout.setTitle(mCurrentMatch.getRoundStr());

		//SET 1
		textSet1Score1.setText(mCurrentMatch.getSet1Score1().toString());
		textSet1Score2.setText(mCurrentMatch.getSet1Score2().toString());

		//SET 2
		textSet2Score1.setText(mCurrentMatch.getSet2Score1().toString());
		textSet2Score2.setText(mCurrentMatch.getSet2Score2().toString());

		//SET 3
		textSet3Score1.setText(mCurrentMatch.getSet3Score1().toString());
		textSet3Score2.setText(mCurrentMatch.getSet3Score2().toString());

		//TEAM 1
		textTeam1.setText(mCurrentMatch.getTeam1());

		//TEAM 2
		textTeam2.setText(mCurrentMatch.getTeam2());
	}
}
