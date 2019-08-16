package com.project.mobile.movie_db_training.detail;

import com.project.mobile.movie_db_training.data.model.Movie;
import com.project.mobile.movie_db_training.data.model.Video;

import java.util.List;

public class MovieDetailContract {
    interface View {
        void showInfo(Movie movie);

        void showVideos(List<Video> videos);

        void loadingFail(String message);
    }

    interface Presenter {
        void setView(View view);

        void destroy();

        void fetchVideos(String movieId);
    }
}