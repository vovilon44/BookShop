package com.example.MyBookShopApp.data;

public class RateReviewDto
{
    private Integer reviewId;
    private Integer value;

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "RateReviewDto{" +
                "reviewId=" + reviewId +
                ", value=" + value +
                '}';
    }
}
