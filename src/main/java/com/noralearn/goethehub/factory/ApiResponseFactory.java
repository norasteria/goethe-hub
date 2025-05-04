package com.noralearn.goethehub.factory;

import com.noralearn.goethehub.enums.ResponseStatusEnum;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Slf4j
public class ApiResponseFactory<ResponseData> {
  private ResponseStatusEnum status;
  private ResponseData data;
  private ErrorDetail error;

  private ApiResponseFactory(ResponseStatusEnum status, ResponseData data, ErrorDetail error){
    this.status = status;
    this.data = data;
    this.error = error;
  }

  public static <ResponseData> ApiResponseFactory<ResponseData> success(ResponseData data){
    return new ApiResponseFactory<>(ResponseStatusEnum.success, data, null);
  }

  public static ApiResponseFactory<?> error(String code, String message){
    ErrorDetail errorDetail = new ErrorDetail(code, message, Instant.now());
    return new ApiResponseFactory<>(ResponseStatusEnum.error, null, errorDetail);
  }

    public record ErrorDetail(
        String code,
        String message,
        Instant timestamp
    ) {
  }

}
