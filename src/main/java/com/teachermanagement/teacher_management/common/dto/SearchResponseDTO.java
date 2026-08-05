package com.teachermanagement.teacher_management.common.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.teachermanagement.teacher_management.common.constant.BaseAppConstant;
import com.teachermanagement.teacher_management.common.exception.CommonException.SubError;
import com.teachermanagement.teacher_management.common.util.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponseDTO<T> extends ResponseDTO<T> {
  private Integer page;
  private Integer limit;
  private long totalRecords;

  public SearchResponseDTO(
      int status,
      String message,
      String code,
      List<SubError> subErrors,
      T data,
      LocalDateTime timestamp,
      Integer page,
      Integer limit,
      long totalRecords) {
    super(status, message, code, subErrors, data, timestamp);
    this.page = page;
    this.limit = limit;
    this.totalRecords = totalRecords;
  }

  public static <T> SearchResponseDTO<T> success(
      T data, Integer page, Integer limit, long totalRecords) {
    return new SearchResponseDTO<T>(
        HttpStatus.OK.value(),
        BaseAppConstant.DEFAULT_SUCCESS_MESSAGE,
        null,
        null,
        data,
        DateTimeUtils.now(),
        page,
        limit,
        totalRecords);
  }

  @SuppressWarnings("unchecked")
  public static SearchResponseDTO<?> success() {
    return success(Collections.emptyList(), 0, 0, 0);
  }
}
