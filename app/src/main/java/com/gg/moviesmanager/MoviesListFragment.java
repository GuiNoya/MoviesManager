package com.gg.moviesmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MoviesListFragment extends ListFragment {
    private int type;

    static MoviesListFragment newInstance(int type, Context context) {
        MoviesListFragment f = new MoviesListFragment();
        f.type = type;
        f.setListAdapter(new MovieListAdapter(context, R.layout.movie_list_item));
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

       /* if (args != null) {
            //Log.wtf("MoviesListFragment", "Creating view from argument");
            //List<Movie> data = args.getParcelableArrayList("data");
            //MovieListAdapter adapter = (MovieListAdapter) getListAdapter();
            //adapter.clear();
            //adapter.addAll(data);
        } else {
            Log.wtf("MoviesListFragment", "Creating view with nothing");
            // TODO: If needed, update the the db
        }*/

        return inflater.inflate(R.layout.fragment_pager_list, container, false);
    }

    public void reload() {
        List<Movie> list;

        switch (type) {
            case 0: // Latest
                list = DbCrud.getInstance(getContext()).selectList(DbCrud.Lists.LATEST);
                break;
            case 1: // Upcoming
                list = DbCrud.getInstance(getContext()).selectList(DbCrud.Lists.UPCOMING);
                break;
            case 2: // Populars
                list = DbCrud.getInstance(getContext()).selectList(DbCrud.Lists.POPULAR);
                break;
            case 3: // Watchlist
                list = DbCrud.getInstance(getContext()).selectList(DbCrud.Lists.WATCHLIST);
                break;
            case 4: // Watched
                list = DbCrud.getInstance(getContext()).selectList(DbCrud.Lists.WATCHED);
                break;
            default: // Shouldn't enter here
                list = new ArrayList<>();
                break;
        }

        if (list == null) {
            list = new ArrayList<>();
        }
        ((MovieListAdapter) getListAdapter()).clear();
        ((MovieListAdapter) getListAdapter()).addAll(list);
        ((MovieListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reload();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FragmentList", "Item clicked: " + id);
        Intent intent = new Intent(getContext(), MovieActivity.class);
        Movie obj = (Movie) getListAdapter().getItem(position);
        intent.putExtra("movie_id", obj.getId());
        startActivity(intent);
    }
}
