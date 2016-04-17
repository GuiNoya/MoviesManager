package com.gg.moviesmanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.List;

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
    }

    public static class MovieListAdapter extends ArrayAdapter<Movie> {
        public MovieListAdapter(Context context, int resource) {
            super(context, resource);
        }

        public MovieListAdapter(Context context, int resource, Movie[] objects) {
            super(context, resource, objects);
        }

        public MovieListAdapter(Context context, int resource, List<Movie> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                v = layoutInflater.inflate(R.layout.movie_list_item, parent, false);
            }

            Movie m = getItem(position);

            if (m != null) {
                ImageView imgCover = (ImageView) v.findViewById(R.id.movie_cover);
                TextView tvMovieName = (TextView) v.findViewById(R.id.movie_name);
                TextView tvRatings = (TextView) v.findViewById(R.id.movie_ratings);
                TextView tvReleaseDate = (TextView) v.findViewById(R.id.movie_release_date);
                final ImageView imgMore = (ImageView) v.findViewById(R.id.more);

                tvMovieName.setText(m.getName());
                tvRatings.setText(String.format("%.2f", m.getRating()));
                tvReleaseDate.setText(m.getReleaseDate());

                imgMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(getContext(), imgMore);
                        popup.getMenuInflater().inflate(R.menu.popup_show_more, popup.getMenu());

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Log.i("MoviesManager", "Menu clicked: " + item.getItemId() + " " +
                                        item.getTitle());
                                return true;
                            }
                        });

                        popup.show();
                    }
                });
            }

            return v;
        }
    }
}
