/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.renatonunes.padellog.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.renatonunes.padellog.R;
import com.renatonunes.padellog.domain.Championship;

public class ChampionshipInfoAdapter extends RecyclerView.Adapter<ChampioshipInfoViewHolder> {

	Context context;
    Championship currentChampionship;
	Boolean mIsReadOnly;
	String mFirstName;

	public ChampionshipInfoAdapter(Context context, Championship championship, Boolean isReadOnly, String firstName) {
		this.currentChampionship = championship;
		this.context = context;
		this.mIsReadOnly = isReadOnly;
		this.mFirstName = firstName;
	}

	@Override public ChampioshipInfoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

		View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_championship_info, viewGroup, false);

		return new ChampioshipInfoViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(ChampioshipInfoViewHolder champioshipInfoViewHolder, int i) {
		// do nothing
//        <string name="championship_info">
//        Campeonato disputado em %1$s, de %2$s até %3$s. %4$s e você
//        obtiveram o seguinte resultado na %5$s: %6$s.</string>

        Resources res = context.getResources();

		String firstNameToAppear = "";
		String secondNameToAppear = "";

		if (mIsReadOnly){ //paul and james
			firstNameToAppear = mFirstName;
			secondNameToAppear = currentChampionship.getPartner();
		}else{ //partner and you
			firstNameToAppear = currentChampionship.getPartner();
			secondNameToAppear = res.getString(R.string.str_you);
		}


        String text = String.format(res.getString(R.string.championship_info),
                currentChampionship.getPlace(),
                currentChampionship.getInitialDateStr(),
                currentChampionship.getFinalDateStr(),
                firstNameToAppear,//fulano    ou amadeu
				secondNameToAppear,//e voce    e kadao
                currentChampionship.getCategoryStr(),
                currentChampionship.getResultStr());

//        String name = championships.get(position).getName();
        champioshipInfoViewHolder.championshipInfoText.setText(text);
	}

	@Override public int getItemCount() {
		return 1;
	}

}

