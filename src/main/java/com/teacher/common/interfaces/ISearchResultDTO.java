package com.teacher.common.interfaces;

import com.teacher.common.util.DTOMapper;

public interface ISearchResultDTO<R extends ISearchResult> extends IResourceDTO {
  default void additionalMapper(R searchResult, DTOMapper mapper) {}

  default void additionalResourceMapper(R searchResult, DTOMapper mapper) {}
}
