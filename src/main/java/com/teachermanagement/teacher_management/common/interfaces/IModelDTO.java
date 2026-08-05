package com.teachermanagement.teacher_management.common.interfaces;

import com.teachermanagement.teacher_management.common.util.DTOMapper;

public interface IModelDTO<E extends IModel> extends IResourceDTO {
  default void additionalMapper(E entity, DTOMapper mapper) {}

  default void additionalFetchMapper(E entity, DTOMapper mapper) {}

  default void additionalResourceMapper(E entity, DTOMapper mapper) {}
}
