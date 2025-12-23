package com.poly.assignment.dto;

import lombok.Data;

@Data
public class ReviewRequestDTO {
    private Integer productId; // Mã sản phẩm được đánh giá
    private Integer rating;    // Số sao (1-5)
    private String comment;    // Nội dung bình luận
}