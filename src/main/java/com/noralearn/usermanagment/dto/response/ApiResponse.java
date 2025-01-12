package com.noralearn.usermanagment.dto.response;

import com.noralearn.usermanagment.enums.ResponseStatusEnum;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiResponse<ResponseData> {
  private ResponseStatusEnum status;
  private ResponseData data;

  public ApiResponse(ResponseStatusEnum status, ResponseData data){
    this.status = status;
    this.data = data;
  }

}
