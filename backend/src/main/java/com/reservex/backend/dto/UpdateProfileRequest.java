package com.reservex.backend.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateProfileRequest {
    
    @Size(max = 100, message = "Business name must be less than 100 characters")
    private String businessName;
}