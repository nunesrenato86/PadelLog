/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.renatonunes.padellog;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChampionshipMatchesAdapter extends RecyclerView.Adapter<ChampionshipMatchesViewHolder> {

	private final int numItems;

	public ChampionshipMatchesAdapter(int numItems) {
		this.numItems = numItems;
	}

	@Override public ChampionshipMatchesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

		View itemView = LayoutInflater.from(viewGroup.getContext())
			.inflate(R.layout.list_item_card, viewGroup, false);

		return new ChampionshipMatchesViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(ChampionshipMatchesViewHolder championshipMatchesViewHolder, int i) {
		// do nothing
	}

	@Override public int getItemCount() {
		return numItems;
	}
}

