package com.teacher.common.service;

import com.google.common.collect.ImmutableMap;
import com.teacher.common.exception.NotFoundException;
import com.teacher.common.interfaces.IModel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

public abstract class BaseService<E extends IModel, I> {
  /**
   * Find entity by id
   *
   * @param id id of entity
   * @return Optional entity
   */
  public abstract Optional<E> findById(I id);

  /**
   * Find all entities by id
   *
   * @param ids list of entity ids
   * @return found entities, not found entities will not include in results
   */
  public abstract List<E> findAllById(Collection<I> ids);

  /**
   * Error code should be thrown when not found by id
   *
   * @return
   */
  public abstract String notFoundByIdErrorCode();

  /**
   * Error code should be thrown when not found by ids
   *
   * @return
   */
  public abstract String notFoundByIdsErrorCode();

  /**
   * Find entity by id
   *
   * @param id id
   * @throws BadRequestException when entity is not found
   */
  public E findByIdOrThrow(I id) {
    return findById(id)
        .orElseThrow(
            () ->
                new NotFoundException(
                    notFoundByIdErrorCode(), ImmutableMap.of("id", id.toString())));
  }

  /**
   * Find all entities by id
   *
   * @param ids ids
   * @return List<E>
   * @throws BadRequestException when any entity is not found by given id
   */
  public List<E> findAllByIdOrThrow(Collection<I> ids) {
    if (CollectionUtils.isEmpty(ids)) return Collections.emptyList();
    List<E> entities = findAllById(ids);
    if (entities.size() != ids.size()) {
      List<I> filterIds =
          ids.stream()
              .filter(id -> entities.stream().noneMatch(entity -> entity.getId().equals(id)))
              .toList();
      throw new NotFoundException(
          notFoundByIdsErrorCode(),
          ImmutableMap.of(
              "ids", filterIds.stream().map(String::valueOf).collect(Collectors.joining(", "))));
    }
    return entities;
  }
}
