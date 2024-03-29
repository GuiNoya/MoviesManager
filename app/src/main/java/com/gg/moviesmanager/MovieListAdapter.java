package com.gg.moviesmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Adapter to be used to display a list of movies.
 */
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

            if (m.getPoster() != null) {
                if (!m.getPoster().equals("")) {
                    try {
                        FileInputStream fs = getContext().openFileInput(m.getPoster());
                        Bitmap img = BitmapFactory.decodeStream(fs);
                        fs.close();
                        imgCover.setImageBitmap(img);
                    } catch (IOException e) {
                        Log.e("ListAdapter", "Could not load image file");
                    }
                }
            }

            tvMovieName.setText(m.getTitle());
            tvRatings.setText(getContext().getResources().getString(R.string.parenthesis, String.format("%.2f", m.getRating())));
            tvReleaseDate.setText(m.getReleaseDate());

            imgMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Inflates a popup menu with to checkable items (watched and watchlist) and
                    // and option to the movie's trailer.
                    PopupMenu popup = new PopupMenu(getContext(), imgMore);
                    popup.getMenuInflater().inflate(R.menu.popup_show_more, popup.getMenu());

                    popup.getMenu().getItem(0).setChecked(m.isWatched());
                    popup.getMenu().getItem(1).setChecked(m.isWatchlist());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == R.id.mark_watched) {
                                // Updates the movie's attribute Watched accordingly,
                                // and updates the database.
                                if (item.isChecked()) {
                                    m.setWatched(false);
                                    DbCrud.getInstance(getContext()).setWatched(m.getId(), false);
                                    item.setChecked(false);
                                    Toast.makeText(getContext(),
                                            String.format(getContext().getResources().getString(R.string.unmarked_watched),
                                                    m.getTitle()), Toast.LENGTH_SHORT).show();
                                } else {
                                    m.setWatched(true);
                                    DbCrud.getInstance(getContext()).setWatched(m.getId(), true);
                                    item.setChecked(true);
                                    Toast.makeText(getContext(),
                                            String.format(getContext().getResources().getString(R.string.marked_watched),
                                                    m.getTitle()), Toast.LENGTH_SHORT).show();
                                }
                                HomeActivity.getInstance().getPagerAdapter().reloadAdapter(4);
                            } else if (id == R.id.add_watchlist) {
                                // Updates the movie's attribute Watchlist accordingly,
                                // and updates the database.
                                if (item.isChecked()) {
                                    m.setWatchlist(false);
                                    DbCrud.getInstance(getContext()).setWatchlist(m.getId(), false);
                                    item.setChecked(false);
                                    Toast.makeText(getContext(),
                                            getContext().getResources().getString(R.string.removed_watchlist),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    m.setWatchlist(true);
                                    DbCrud.getInstance(getContext()).setWatchlist(m.getId(), true);
                                    item.setChecked(true);
                                    Toast.makeText(getContext(),
                                            getContext().getResources().getString(R.string.added_watchlist),
                                            Toast.LENGTH_SHORT).show();
                                }
                                HomeActivity.getInstance().getPagerAdapter().reloadAdapter(3);
                            } else if (id == R.id.see_trailer) {
                                // If the movie doesn't have a trailer, it tries to download it.
                                String ytId = m.getTrailer();
                                if (ytId == null || ytId.equals("")) {
                                    ConnectivityManager cm = (ConnectivityManager)
                                            getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                                    NetworkInfo netInfo = cm.getActiveNetworkInfo();
                                    if (netInfo != null && netInfo.isConnected()) {
                                        TrailerUrlDownloader t = new TrailerUrlDownloader();
                                        t.execute(m.getId());
                                    } else {
                                        Toast.makeText(getContext(), R.string.no_internet,
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    // Create an Intent to show the trailer.
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ytId));
                                    getContext().startActivity(intent);
                                }
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

    /**
     * Class downloads and parse the movie to get the trailer.
     * If there is a trailer URL, creates an Intent to show the video.
     */
    private static class TrailerUrlDownloader extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            return JSONParser.getTrailer(DataDownloader.getVideos(params[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            if (!"".equals(s)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                HomeActivity.getInstance().startActivity(intent);
            }
        }
    }
}
