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

import java.util.List;

public class MovieListAdapter extends ArrayAdapter<Movie> {
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
            tvRatings.setText(getContext().getResources().getString(R.string.parenthesis, String.format("%.2f", m.getRating())));
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
                            if (item.getItemId() == R.id.mark_watched)
                                return true;
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
