package com.middleware.wyd.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseDTO {
    public String title;
    public String description;
}
