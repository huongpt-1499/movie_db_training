package com.project.mobile.movie_db_training.data.model;

import com.google.gson.annotations.SerializedName;

public class Movie {
    @SerializedName("id")
    private int mId;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("vote_average")
    private String mVoteAverage;
    @SerializedName("backdrop_path")
    private String mBackdropPath;
    @SerializedName("release_date")
    private String mReleaseDate;
    @SerializedName("overview")
    private String mOverview;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }


    public String getVoteAverage() {
        return mVoteAverage;
    }


    public String getBackdropPath() {
        return mBackdropPath;
    }


    public String getReleaseDate() {
        return mReleaseDate;
    }

}