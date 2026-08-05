package com.teachermanagement.teacher_management.common.interfaces;

import com.teachermanagement.teacher_management.common.util.DTOMapper;

public interface ISearchResultDTO<R extends ISearchResult> extends IResourceDTO {
  default void additionalMapper(R searchResult, DTOMapper mapper) {}

  default void additionalResourceMapper(R searchResult, DTOMapper mapper) {}
}
