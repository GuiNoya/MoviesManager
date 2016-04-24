package com.gg.moviesmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class MoviesListFragment extends ListFragment {

    static MoviesListFragment newInstance() {
        return new MoviesListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pager_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Movie[] values = {new Movie("Movie 1", 9, "01/23/1234"), new Movie("Movie 2", 7, "20/02/2001")};
        setListAdapter(new MovieListAdapter(getActivity(), R.layout.movie_list_item, values));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FragmentList", "Item clicked: " + id);
        Intent intent = new Intent(getContext(), MovieActivity.class);
        Movie obj = (Movie) getListAdapter().getItem(position);
        intent.putExtra("movie", obj);
        startActivity(intent);
    }
}
