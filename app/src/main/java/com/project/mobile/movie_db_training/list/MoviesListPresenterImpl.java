package com.project.mobile.movie_db_training.list;

import android.content.Context;

import androidx.annotation.NonNull;

import com.project.mobile.movie_db_training.BuildConfig;
import com.project.mobile.movie_db_training.data.local.MovieDatabase;
import com.project.mobile.movie_db_training.data.model.FavoriteEntity;
import com.project.mobile.movie_db_training.data.model.Movie;
import com.project.mobile.movie_db_training.data.model.MovieResponse;
import com.project.mobile.movie_db_training.network.NetworkModule;
import com.project.mobile.movie_db_training.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesListPresenterImpl implements MoviesListContract.Presenter {
    private static final String TAG = MoviesListPresenterImpl.class.getSimpleName();
    private MoviesListContract.View mView;
    private boolean mIsLoading = false;
    private int mTotalPages;
    private int mPage = 1;
    private MovieDatabase mMovieDatabase;
    private List<Movie> mMovies = new ArrayList<>();

    public MoviesListPresenterImpl(Context context) {
        mMovieDatabase = MovieDatabase.getInstance(context);
    }

    public int getTotalPages() {
        return mTotalPages;
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    @Override
    public void setView(MoviesListContract.View view) {
        mView = view;
    }

    @Override
    public void destroy() {
        mView = null;
    }

    @Override
    public void fetchMovies(String option) {
        if (option == null) {
            //get local
            List<FavoriteEntity> favorites = mMovieDatabase.favoritesDao().getAll();
            for (int i = 0; i < favorites.size(); i++) {
                mMovies.add(favoriteEntityToMovie(favorites.get(i)));
            }
            mView.showMovies(mMovies);
        } else if (option.equals(Constants.NOW_PLAYING) ||
                option.equals(Constants.POPULAR) ||
                option.equals(Constants.TOP_RATED) ||
                option.equals(Constants.UPCOMING)) {
            getMoviesByListType(option);
        } else {
            getMoviesByGenre(option);
        }
    }

    //get movies: now coming, popular, top rated, upcoming
    private void getMoviesByListType(String listType) {
        if (mIsLoading) return;
        mView.showLoading(Constants.LOADING_START);
        mIsLoading = true;
        NetworkModule.getTMDbService()
                .getMovieList(listType, BuildConfig.TMDB_API_KEY, mPage)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            onFetchSuccess(response.body());
                        } else {
                            onFetchFail(Constants.RESPONSE_ERROR);
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        onFetchFail(t.getMessage());
                    }
                });

    }

    //get movies by genre. Eg: Action, Comedy,...
    private void getMoviesByGenre(String genreId) {
        if (mIsLoading) return;
        mView.showLoading(Constants.LOADING_START);
        mIsLoading = true;
        NetworkModule.getTMDbService()
                .getMoviesByGenre(BuildConfig.TMDB_API_KEY, genreId, mPage)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            onFetchSuccess(response.body());
                        } else {
                            onFetchFail(Constants.RESPONSE_ERROR);
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        onFetchFail(t.getMessage());
                    }
                });

    }

    private void onFetchSuccess(@NonNull MovieResponse movieResponse) {
        mIsLoading = false;
        mTotalPages = movieResponse.getTotalPages();
        mView.showMovies(movieResponse.getMovies());
    }

    private void onFetchFail(String message) {
        mView.showLoading(message);
    }

    private Movie favoriteEntityToMovie(FavoriteEntity favoriteEntity) {
        Movie movie = new Movie();
        movie.setId(favoriteEntity.getId());
        movie.setBackdropPath(favoriteEntity.getBackdropPath());
        movie.setTitle(favoriteEntity.getTitle());
        movie.setReleaseDate(favoriteEntity.getReleaseDate());
        movie.setOverview(favoriteEntity.getOverview());
        movie.setVoteAverage(favoriteEntity.getVoteAverage());
        return movie;
    }

    @Override
    public void loadMore(String option) {
        if (isLoading() || mPage > getTotalPages()) return;
        mPage++;
        fetchMovies(option);
    }
}