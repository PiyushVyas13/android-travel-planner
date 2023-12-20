package com.example.travelplanner.unsplash;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UnsplashResponse {

    @SerializedName("results")
    private List<UnsplashPhoto> photos;

    public List<UnsplashPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<UnsplashPhoto> photos) {
        this.photos = photos;
    }

    public static class UnsplashPhoto {
        @SerializedName("urls")
        private UnsplashUrls urls;

        public  UnsplashUrls getUrls() {
            return urls;
        }

        public void setUrls(UnsplashUrls urls) {
            this.urls = urls;
        }
    }

    public static class UnsplashUrls {
        @SerializedName("regular")
        private String regularUrl;

        public String getRegularUrl() {
            return regularUrl;
        }

        public void setRegularUrl(String regularUrl) {
            this.regularUrl = regularUrl;
        }
    }
}
