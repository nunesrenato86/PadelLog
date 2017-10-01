/*
/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.renatonunes.padellog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.renatonunes.padellog.domain.Championship;
import com.renatonunes.padellog.domain.Match;
import com.renatonunes.padellog.domain.util.ImageFactory;
import com.renatonunes.padellog.domain.util.LibraryClass;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MatchInfoActivity extends CommonActivity {

	public static Match mCurrentMatch;
	public static Championship mCurrentChampionship;
	public static Context mContext;

	private static Boolean mIsReadOnly = false;
	private static String mPlayerName;

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

		setUIPermission();

		convertPhoto();
	}

	private void setUIPermission(){
		if (mIsReadOnly){
			fabEditMatch.setVisibility(View.INVISIBLE);
			MainActivity.mPlayer.setCheckedOtherMatch(true);
		}else{
			fabEditMatch.setVisibility(View.VISIBLE);
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
            editMatch();

			return true;
		}else if (id == R.id.action_match_delete){
            //deleteMatch();
			askToDeleteMatch();

			return true;
		}else if (id == android.R.id.home) {
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

	public static void start(Context c, Match currentMatch, Championship currentChampionship, Boolean isReadOnly,
							 String playerName) {
		mContext = c;
		mCurrentMatch = currentMatch;
		mCurrentChampionship = currentChampionship;
		mIsReadOnly = isReadOnly;
		mPlayerName = playerName;
		c.startActivity(new Intent(c, MatchInfoActivity.class));
	}

    private void editMatch(){
        AddMatchActivity.start(mContext, mCurrentChampionship, mCurrentMatch);
    }

    private void deleteMatch(){
		if (mCurrentMatch.isVictory()){
			//mCurrentChampionship.getPlayer().decWin(1);
			mCurrentChampionship.decWin(1);
		}else{
			//mCurrentChampionship.getPlayer().decLoss(1);
			mCurrentChampionship.decLoss(1);
		}

		//TODO: falta quando troca de vitoria para derrota e vice versa
		//TODO: fatta quando deleta um campeonato inteiro
		// - para isso salvar o numero de win e loss e ratio tb no campeonato e usar declos(champ.qtdloss)
		//calc ratio do camponato tb

        FirebaseDatabase.getInstance().getReference()
                .child("matches")
                .child(mCurrentChampionship.getId())
                .child(mCurrentMatch.getId()).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.setValue(null); // This removes the node.
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                mCurrentChampionship.updateResult();
                ChampionshipInfoActivity.currentChampionship = mCurrentChampionship;

                Toast.makeText(MatchInfoActivity.this,
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

	private void updateUI(){
		//setting top image
		if (mCurrentMatch.getPhotoUriDownloaded() != null){
			Picasso.with(mContext).load(mCurrentMatch.getPhotoUriDownloaded().toString()).into(TopImage);
		} else if (mCurrentMatch.isImgFirebase()){

			FirebaseStorage storage = FirebaseStorage.getInstance();

			StorageReference httpsReference = storage.getReferenceFromUrl(mCurrentMatch.getPhotoUrl());

			httpsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
				@Override
				public void onSuccess(Uri uri) {
					Picasso.with(getApplicationContext()).load(uri.toString()).into(TopImage);
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception exception) {
					// Handle any errors
				}
			});

		}else if (mCurrentMatch.isImgStrValid()){
			TopImage.setImageBitmap(ImageFactory.imgStrToImage( mCurrentMatch.getImageStr() ));

			//Usaria o Bitmap

			//TopImage.getDrawingCache()

			//https://stackoverflow.com/questions/41404478/how-to-get-a-bitmap-from-an-imageview
		}
		else{
			TopImage.setImageResource(R.drawable.no_photo);
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
		if (mIsReadOnly){
			textTeam1.setText(mPlayerName + " / " + mCurrentChampionship.getPartner());

		}else {

			textTeam1.setText(mCurrentMatch.getTeam1());
		}

		//TEAM 2
		textTeam2.setText(mCurrentMatch.getTeam2());
	}

	private void convertPhoto(){
		if ((!mIsReadOnly) && (mCurrentMatch.isImgStrValid()) && (!mCurrentMatch.isImgFirebase())){
			Bitmap bitmap = ImageFactory.imgStrToImage(mCurrentMatch.getImageStr());

			if (bitmap != null) {

				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);

				byte[] bytes = baos.toByteArray();

				FirebaseStorage storage = FirebaseStorage.getInstance();

				StorageReference storageRef = storage.getReferenceFromUrl("gs://padellog-b49b1.appspot.com");

				String Id = "images/matches/";

				Id = Id.concat(mCurrentMatch.getId()).concat(".jpg");

				StorageReference playersRef = storageRef.child(Id);

				final ProgressDialog progressDialog = new ProgressDialog(this);
				//progressDialog.setTitle(getResources().getString(R.string.photo_processing));
				progressDialog.show();

				UploadTask uploadTask = playersRef.putBytes(bytes);

				uploadTask.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception exception) {
						// Handle unsuccessful uploads
						progressDialog.dismiss();
					}
				}).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
					@Override
					public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
						progressDialog.dismiss();
						// taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
						Uri downloadUrl = taskSnapshot.getDownloadUrl();

						mCurrentMatch.setPhotoUrl(downloadUrl.toString());
						mCurrentMatch.setImageStr(null);
						mCurrentMatch.setOwner(mCurrentChampionship.getId());

						mCurrentMatch.updateDB();
						ChampionshipListActivity.mNeedToRefreshData = true;

						showSnackbar(fabEditMatch,
								getResources().getString(R.string.msg_championship_converted)
						);
					}
				});

				uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
					@Override
					public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

						double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

						Log.e("RNN", ((int) progress + "% " + getResources().getString(R.string.photo_complete)));

						progressDialog.setMessage(getResources().getString(R.string.msg_converting));
					}
				}).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
					@Override
					public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
						progressDialog.dismiss();
					}
				});

			}
		}
	}


	private void askToDeleteMatch(){

		if (LibraryClass.isNetworkActive(this)) {

			AlertDialog dialogo = new AlertDialog.Builder(this)
					.setTitle(getResources().getString(R.string.title_dlg_confirm_delete))
					.setMessage(getResources().getString(R.string.msg_match_delete))
					.setPositiveButton(getResources().getString(R.string.btn_delete), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							deleteMatch();

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
			showSnackbar(fabEditMatch, getResources().getString(R.string.msg_no_internet) );
		}


	}
}
