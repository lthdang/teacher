package com.teacher.common.interfaces;

import com.teacher.common.util.DTOMapper;

public interface IModelDTO<E extends IModel> extends IResourceDTO {
  default void additionalMapper(E entity, DTOMapper mapper) {}

  default void additionalFetchMapper(E entity, DTOMapper mapper) {}

  default void additionalResourceMapper(E entity, DTOMapper mapper) {}
}
