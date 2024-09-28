package com.example.rcmd_sys.model;

public class StoreSimilarity {
    private long storeId;
    private double similarity;

    public StoreSimilarity(long storeId, double similarity) {
        this.storeId = storeId;
        this.similarity = similarity;
    }

    // Getters and setters
    public long getStoreId() {
        return storeId;
    }

    public void setStoreId(long storeId) {
        this.storeId = storeId;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }
}
