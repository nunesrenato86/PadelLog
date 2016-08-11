/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.renatonunes.padellog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ChampioshipInfoViewHolder extends RecyclerView.ViewHolder {

	private final Context context;
	TextView championshipInfoText;

		public ChampioshipInfoViewHolder(View itemView) {
			super(itemView);
			championshipInfoText = (TextView)itemView.findViewById(R.id.championship_info_text);

			context = itemView.getContext();
		}
	}