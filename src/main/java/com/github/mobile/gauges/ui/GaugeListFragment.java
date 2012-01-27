package com.github.mobile.gauges.ui;

import static com.github.mobile.gauges.IntentConstants.GAUGE;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.authenticator.ApiKeyProvider;
import com.github.mobile.gauges.core.Gauge;
import com.github.mobile.gauges.core.GaugesService;
import com.google.inject.Inject;
import com.madgag.android.listviews.ReflectiveHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.madgag.android.listviews.ViewInflator;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Fragment to display a list of gauges
 */
public class GaugeListFragment extends ListLoadingFragment<Gauge> {

	private final static String TAG = "GLF";

	@Inject
	ApiKeyProvider apiKeyProvider;

	public Loader<List<Gauge>> onCreateLoader(int id, Bundle args) {
		return new AsyncLoader<List<Gauge>>(getActivity()) {

			public List<Gauge> loadInBackground() {
				try {
					return new GaugesService(apiKeyProvider.getAuthKey())
							.getGauges();
				} catch (IOException e) {
					Log.d(TAG, "Exception getting gauges", e);
					return Collections.emptyList();
				}
			}
		};
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(getActivity(), GaugeViewActivity.class);
		i.putExtra(GAUGE, (Serializable) l.getItemAtPosition(position));
		startActivity(i);
	}

	protected ListAdapter adapterFor(List<Gauge> items) {
		return new ViewHoldingListAdapter<Gauge>(items,
				ViewInflator.viewInflatorFor(getActivity(),
						layout.gauge_list_item),
				ReflectiveHolderFactory.reflectiveFactoryFor(
						GaugeViewHolder.class, getActivity().getResources()));
	}
}