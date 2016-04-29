package com.gg.moviesmanager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

public class MovieListAdapter extends ArrayAdapter<Movie> implements Serializable {
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
    public View getView(int position, View convertView, final ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            v = layoutInflater.inflate(R.layout.movie_list_item, parent, false);
        }

        final Movie m = getItem(position);

        if (m != null) {
            ImageView imgCover = (ImageView) v.findViewById(R.id.movie_cover);
            TextView tvMovieName = (TextView) v.findViewById(R.id.movie_name);
            TextView tvRatings = (TextView) v.findViewById(R.id.movie_ratings);
            TextView tvReleaseDate = (TextView) v.findViewById(R.id.movie_release_date);
            final ImageView imgMore = (ImageView) v.findViewById(R.id.more);

            tvMovieName.setText(m.getTitle());
            tvRatings.setText(getContext().getResources().getString(R.string.parenthesis, String.format("%.2f", m.getRating())));
            tvReleaseDate.setText(m.getReleaseDate());

            imgMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(getContext(), imgMore);
                    popup.getMenuInflater().inflate(R.menu.popup_show_more, popup.getMenu());

                    popup.getMenu().getItem(0).setChecked(m.isWatched());
                    popup.getMenu().getItem(1).setChecked(m.isWatchlist());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                         @Override
                         public boolean onMenuItemClick(MenuItem item) {
                             int id = item.getItemId();
                             if (id == R.id.mark_watched) {
                                 if (item.isChecked()) {
                                     m.setWatched(false);
                                     DbCrud.getInstance(getContext()).setWatched(m.getId(), false);
                                     item.setChecked(false);
                                 } else {
                                     m.setWatched(true);
                                     DbCrud.getInstance(getContext()).setWatched(m.getId(), true);
                                     item.setChecked(true);
                                 }
                                 HomeActivity.getInstance().getPagerAdapter().reloadAdapter(4);
                             } else if (id == R.id.add_watchlist) {
                                 if (item.isChecked()) {
                                     m.setWatchlist(false);
                                     DbCrud.getInstance(getContext()).setWatchlist(m.getId(), false);
                                     item.setChecked(false);
                                 } else {
                                     m.setWatchlist(true);
                                     DbCrud.getInstance(getContext()).setWatchlist(m.getId(), true);
                                     item.setChecked(true);
                                 }
                                 HomeActivity.getInstance().getPagerAdapter().reloadAdapter(3);
                             } else if (id == R.id.see_trailer) {
                                 // TODO: download and launch trailer
                             } else {
                                 return false;
                             }
                             return true;
                         }
                     }

                    );
                        popup.show();
                    }
                });
        }

        return v;
    }
}
