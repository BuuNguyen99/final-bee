package com.example.hairstyle.payload.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentResponse {
    private MessageResponse messageResponse;
    private String url;
}
