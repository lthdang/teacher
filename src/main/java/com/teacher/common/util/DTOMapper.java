package com.teacher.common.util;

import com.teacher.common.constant.BaseAppConstant;
import com.teacher.common.constant.ETranslationGroup;
import com.teacher.common.constant.IBaseErrorCode;
import com.teacher.common.dto.SearchResponseDTO;
import com.teacher.common.dto.masterdata.MasterDataDTO;
import com.teacher.common.exception.UnexpectedException;
import com.teacher.common.interfaces.IBaseResourceCache;
import com.teacher.common.interfaces.IBaseTranslationCache;
import com.teacher.common.interfaces.IModel;
import com.teacher.common.interfaces.IModelDTO;
import com.teacher.common.interfaces.IResourceDTO;
import com.teacher.common.interfaces.ISearchResult;
import com.teacher.common.interfaces.ISearchResultDTO;
import com.teacher.common.model.CachedResource;
import com.teacher.common.model.CachedTranslation;
import com.teacher.common.model.CachedTranslationId;
import com.teacher.common.model.LocaleInformation;
import com.teacher.common.resources.BaseResourceDTO;
import com.codepoetics.protonpack.StreamUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hibernate.collection.spi.PersistentCollection;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class DTOMapper {
  private static final Condition<Object, Object> ignoreLazyFetchCondition =
      new Condition<Object, Object>() {
        public boolean applies(MappingContext<Object, Object> context) {
          return !(context.getSource() instanceof PersistentCollection);
        }
      };
  private static final Condition<Object, Object> allowFetchCondition =
      new Condition<Object, Object>() {
        public boolean applies(MappingContext<Object, Object> context) {
          return true;
        }
      };

  public interface IToEntityExtraMapping<I, O> {
    void map(O output, I input);
  }

  private final ModelMapper modelMapper;
  private final MasterDataCacheUtils masterDataCacheUtils;
  private final IBaseTranslationCache translationCache;
  private final LocaleInformation currentLocale;
  private final IBaseResourceCache resourceCache;

  @Autowired
  public DTOMapper(
      @Autowired(required = false) ModelMapper modelMapper,
      @Autowired(required = false) MasterDataCacheUtils masterDataCacheUtils,
      @Autowired(required = false) IBaseTranslationCache translationCache,
      @Autowired(required = false) LocaleInformation currentLocale,
      @Autowired(required = false) IBaseResourceCache resourceCache) {
    this.modelMapper = modelMapper != null ? modelMapper : new ModelMapper();
    this.currentLocale = currentLocale;
    this.masterDataCacheUtils = masterDataCacheUtils;
    this.translationCache = translationCache;
    this.resourceCache = resourceCache;
  }

  private TimeZone getCurrentTimezone() {
    try {
      return currentLocale != null ? currentLocale.getTimezone() : TimeZone.getTimeZone(BaseAppConstant.DEFAULT_TIMEZONE);
    } catch (Exception e) {
      return TimeZone.getTimeZone(BaseAppConstant.DEFAULT_TIMEZONE);
    }
  }

  private String getCurrentLanguageCode() {
    try {
      return currentLocale != null ? currentLocale.getLanguageCode() : BaseAppConstant.DEFAULT_LANGUAGE;
    } catch (Exception e) {
      return BaseAppConstant.DEFAULT_LANGUAGE;
    }
  }

  /**
   * Get created field from objects
   *
   * @param <E> any class
   * @param objects list of objects
   * @return Found field or null
   */
  private <E> Field getCreatedDateField(Collection<E> objects) {
    return ReflectionUtils.getFieldFromClass(objects.iterator().next().getClass(), "createdDate")
        .orElse(null);
  }

  /**
   * Set master data to dto with given field and found master data dtos
   *
   * @param <E> any class
   * @param <T> any class
   * @param dtos list of dtos
   * @param objects list of objects
   * @param masterDataFields master data fields
   * @param masterDataDTOs master data dtos
   */
  private <E, T> void setMasterDataToDTO(
      List<T> dtos,
      Collection<E> objects,
      List<Field> masterDataFields,
      Map<String, MasterDataDTO> masterDataDTOs) {
    if (CollectionUtils.isEmpty(masterDataDTOs)) return;
    translateMasterDataName(masterDataDTOs.values().stream().toList());
    StreamUtils.zipWithIndex(objects.stream())
        .forEach(
            indexedObject -> {
              T dto = dtos.get((int) indexedObject.getIndex());
              masterDataFields.forEach(
                  field -> {
                    String code =
                        ReflectionUtils.getStringFromObject(indexedObject.getValue(), field);
                    field.setAccessible(true);
                    try {
                      field.set(dto, masterDataDTOs.get(code));
                    } catch (Exception e) {
                      throw new UnexpectedException(
                          IBaseErrorCode.ERROR_DTO_MAPPER_PROCESSING,
                          ImmutableMap.of("error", e.getMessage()));
                    }
                  });
            });
  }

  private void translateMasterDataName(List<MasterDataDTO> masterDataDTOs) {
    if (CollectionUtils.isEmpty(masterDataDTOs)) return;
    List<MasterDataDTO> validatedInput = masterDataDTOs.stream().filter(Objects::nonNull).toList();
    // Get translation for master data
    String languageCode = getCurrentLanguageCode();
    List<String> translationKeys =
        validatedInput.stream()
            .flatMap(
                masterData ->
                    Stream.of(
                        masterData.getCode(),
                        Optional.ofNullable(masterData.getParent())
                            .map(MasterDataDTO::getCode)
                            .orElse(null)))
            .filter(Objects::nonNull)
            .toList();
    Map<String, CachedTranslation> translations =
        translationCache != null
            ? Streams.stream(
                    translationCache.findAllById(
                        translationKeys.stream()
                            .map(
                                code ->
                                    CachedTranslationId.builder()
                                        .namespace(
                                            ETranslationGroup.TG_MASTER_DATA.getDefaultNamespace())
                                        .key(code)
                                        .language(languageCode)
                                        .build())
                            .collect(Collectors.toSet())))
                .collect(Collectors.toMap(CachedTranslation::getKey, Function.identity()))
            : Collections.emptyMap();
    // Set translation to master data
    validatedInput.forEach(
        masterData -> {
          Optional.ofNullable(translations.get(masterData.getCode()))
              .ifPresent(translation -> masterData.setName(translation.getValue()));
          Optional.ofNullable(masterData.getParent())
              .ifPresent(
                  parent -> {
                    Optional.ofNullable(translations.get(parent.getCode()))
                        .ifPresent(translation -> parent.setName(translation.getValue()));
                  });
        });
  }

  /**
   * Map latest master data to all MasterDataDTO fields in dto with values from source objects
   *
   * @param <E> any class
   * @param <T> any class
   * @param dtos list of dtos
   * @param objects list of objects
   * @param type output type
   */
  private <E, T> void mapLatestMasterData(List<T> dtos, Collection<E> objects, Class<T> type) {
    List<Field> masterDataFields =
        ReflectionUtils.getAllFieldsFromClass(type).stream()
            .filter(field -> field.getType().equals(MasterDataDTO.class))
            .toList();
    Set<String> masterDataCodes = new HashSet<>();
    objects.forEach(
        object -> {
          masterDataCodes.addAll(
              masterDataFields.stream()
                  .map(field -> ReflectionUtils.getStringFromObject(object, field))
                  .toList());
        });
    Map<String, MasterDataDTO> masterDataDTOs =
        masterDataCacheUtils != null
            ? masterDataCacheUtils
                .getAllByCodes(
                    masterDataCodes.stream().filter(Objects::nonNull).collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(MasterDataDTO::getCode, masterData -> masterData))
            : Collections.emptyMap();
    setMasterDataToDTO(dtos, objects, masterDataFields, masterDataDTOs);
  }

  /**
   * Map master data from history before given timestamp to all MasterDataDTO fields in dto with
   * values from source object
   *
   * @param <E> any class
   * @param <T> any class
   * @param dtos list of dtos
   * @param objects list of objects
   * @param type output type
   */
  private <E, T> void mapMasterDataWithHistory(List<T> dtos, Collection<E> objects, Class<T> type) {
    Field createdDateField = getCreatedDateField(objects);
    List<Field> masterDataFields =
        ReflectionUtils.getAllFieldsFromClass(type).stream()
            .filter(field -> field.getType().equals(MasterDataDTO.class))
            .toList();
    Map<LocalDateTime, Set<String>> codesByTimestamp = new HashMap<>();
    objects.forEach(
        entity -> {
          LocalDateTime createdDate =
              Optional.ofNullable(ReflectionUtils.getStringFromObject(entity, createdDateField))
                  .map(LocalDateTime::parse)
                  .orElse(DateTimeUtils.now());
          Set<String> codes = codesByTimestamp.getOrDefault(createdDate, new HashSet<>());
          masterDataFields.forEach(
              field -> codes.add(ReflectionUtils.getStringFromObject(entity, field)));
          codesByTimestamp.put(createdDate, codes);
        });

    Map<String, MasterDataDTO> masterDataDTOs = new HashMap<>();
    if (masterDataCacheUtils != null) {
      codesByTimestamp.forEach(
          (timestamp, masterDataCodes) -> {
            masterDataDTOs.putAll(
                masterDataCacheUtils
                    .getAllByCodesWithHistory(
                        masterDataCodes.stream().filter(Objects::nonNull).collect(Collectors.toSet()),
                        timestamp)
                    .stream()
                    .collect(Collectors.toMap(MasterDataDTO::getCode, masterData -> masterData)));
          });
    }
    setMasterDataToDTO(dtos, objects, masterDataFields, masterDataDTOs);
  }

  /**
   * Scan date time fields and update with given convertor
   *
   * @param <T> any class
   * @param object target object
   * @param convertor timezone convertor
   */
  private <T> void updateDateTimeFields(
      T object, Function<LocalDateTime, LocalDateTime> convertor) {
    List<Field> allOutputFields = ReflectionUtils.getAllFieldsFromObject(object);
    allOutputFields.stream()
        .filter(field -> field.getType().equals(LocalDateTime.class))
        .forEach(
            dateTimeField -> {
              try {
                dateTimeField.setAccessible(true);
                LocalDateTime value = (LocalDateTime) dateTimeField.get(object);
                dateTimeField.set(object, convertor.apply(value));
              } catch (Exception e) {
                return;
              }
            });
  }

  /**
   * Map master data from dto to entity
   *
   * @param dto
   * @param entity
   * @param <E>
   * @param <D>
   */
  private <E extends IModel, D> void mapMasterDataToEntity(D dto, E entity) {
    ReflectionUtils.getAllFieldsFromObject(dto).stream()
        .filter(field -> field.getType().equals(MasterDataDTO.class))
        .forEach(
            field -> {
              field.setAccessible(true);
              ReflectionUtils.getFieldFromObject(entity, field.getName())
                  .ifPresent(
                      entityField -> {
                        try {
                          entityField.setAccessible(true);
                          String masterDataCode = ((MasterDataDTO) field.get(dto)).getCode();
                          Method valueOf = entityField.getType().getMethod("valueOf", String.class);
                          entityField.set(entity, valueOf.invoke(null, masterDataCode));
                        } catch (Exception e) {
                          return;
                        }
                      });
            });
  }

  /**
   * Map a single entity to DTO
   *
   * @param <E> any class
   * @param <T> any class
   * @param entity target entity
   * @param type output class
   * @return T
   */
  private <E extends IModel, T extends IModelDTO<E>> T mapSingleDTO(
      E entity, Class<T> type, TimeZone timezone) {
    T dto = modelMapper.map(entity, type);
    if (Objects.nonNull(timezone)) {
      updateDateTimeFields(dto, value -> DateTimeUtils.toTimezoneFromUtc(value, timezone));
    }
    return dto;
  }

  /**
   * Map a single DTO to entity
   *
   * @param <E> any class
   * @param type output type
   * @return E
   */
  private <E extends IModel, D> E mapSingleEntity(D dto, Class<E> type, TimeZone timezone) {
    E entity = modelMapper.map(dto, type);
    mapMasterDataToEntity(dto, entity);
    if (Objects.nonNull(timezone)) {
      updateDateTimeFields(entity, value -> DateTimeUtils.toUtcFromTimezone(value, timezone));
    }
    return entity;
  }

  /**
   * Map a single DTO to entity
   *
   * @param <E> any class
   * @param entity target entity
   */
  private <E extends IModel, D> void mapSingleEntity(D dto, E entity, TimeZone timezone) {
    modelMapper.map(dto, entity);
    if (Objects.nonNull(timezone)) {
      updateDateTimeFields(entity, value -> DateTimeUtils.toUtcFromTimezone(value, timezone));
    }
  }

  /**
   * DTOMapper core function for mapping from entity to dto
   *
   * @param <E> any class
   * @param <T> any class
   * @param entities list of entities
   * @param type output type
   * @return List<T>
   */
  private <E extends IModel, T extends IModelDTO<E>> List<T> map(
      Collection<E> entities, Class<T> type, boolean isFetchMap, boolean allowAutoFetch) {
    if (Objects.isNull(entities)) return Collections.emptyList();
    entities = entities.stream().filter(Objects::nonNull).toList();
    if (CollectionUtils.isEmpty(entities)) return Collections.emptyList();

    // Mapping with model mapper
    modelMapper
        .getConfiguration()
        .setPropertyCondition(allowAutoFetch ? allowFetchCondition : ignoreLazyFetchCondition);
    TimeZone timezone = getCurrentTimezone();
    List<T> result = entities.stream().map(entity -> mapSingleDTO(entity, type, timezone)).toList();

    // Mapping master data
    Field createdDate = getCreatedDateField(entities);
    if (Objects.nonNull(createdDate)) {
      mapMasterDataWithHistory(result, entities, type);
    } else {
      mapLatestMasterData(result, entities, type);
    }

    // Apply additional mapper
    StreamUtils.zipWithIndex(entities.stream())
        .forEach(
            indexedEntity -> {
              T dto = result.get((int) indexedEntity.getIndex());
              autoMapRelationship(indexedEntity.getValue(), dto, false);
              dto.additionalMapper(indexedEntity.getValue(), this);
              if (isFetchMap) {
                autoMapRelationship(indexedEntity.getValue(), dto, true);
                dto.additionalFetchMapper(indexedEntity.getValue(), this);
              }
            });

    // Auto fetch resource
    autoFetchResource(result);

    return result;
  }

  private <E extends ISearchResult, T, SE extends IModel, ST extends IModelDTO<SE>>
      void autoMapSearchResult(E searchResult, T dto) {
    ReflectionUtils.getAllFieldsFromObject(searchResult).stream()
        .filter(
            field ->
                field.getType().getPackageName().endsWith("model")
                    || (field.getType().equals(List.class)
                        && ((Class<?>)
                                ((ParameterizedType) field.getGenericType())
                                    .getActualTypeArguments()[0])
                            .getPackageName()
                            .endsWith("model")))
        .forEach(field -> autoMap(searchResult, dto, field));
  }

  private <E extends IModel, T extends IModelDTO<E>, SE extends IModel, ST extends IModelDTO<SE>>
      void autoMapRelationship(E entity, T dto, boolean isFetchMap) {
    List<Class<? extends Annotation>> annotations =
        isFetchMap
            ? List.of(OneToMany.class, ManyToMany.class)
            : List.of(ManyToOne.class, OneToOne.class);
    ReflectionUtils.getAllFieldsFromObject(entity).stream()
        .filter(field -> annotations.stream().anyMatch(field::isAnnotationPresent))
        .forEach(field -> autoMap(entity, dto, field));
  }

  @SuppressWarnings("unchecked")
  private <T extends IResourceDTO> void autoFetchResource(List<T> dtos) {
    if (Objects.isNull(resourceCache)) return;

    // Collect all resource id as list
    Set<Long> resourceIds = new HashSet<>();
    dtos.forEach(
        dto -> {
          Map<String, String> mapResourceFields = dto.mapResourceFields();
          if (!CollectionUtils.isEmpty(mapResourceFields)) {
            mapResourceFields
                .entrySet()
                .forEach(
                    entry -> {
                      String resourceIdFieldName = entry.getKey();
                      Field resourceIdField =
                          ReflectionUtils.getFieldFromObject(dto, resourceIdFieldName)
                              .orElseThrow(
                                  () ->
                                      new UnexpectedException(
                                          IBaseErrorCode.ERROR_RESOURCE_ID_FIELD_NOT_FOUND,
                                          ImmutableMap.of("fieldName", resourceIdFieldName)));
                      Object resourceId = ReflectionUtils.getValueFromObject(dto, resourceIdField);
                      if (Objects.nonNull(resourceId)) {
                        if (resourceIdField.getType().equals(List.class)) {
                          resourceIds.addAll((List<Long>) resourceId);
                        } else if (resourceIdField.getType().equals(Long.class)) {
                          resourceIds.add((Long) resourceId);
                        }
                      }
                    });
          }
        });

    // Fetch all resources
    if (!CollectionUtils.isEmpty(resourceIds)) {
      Map<Long, CachedResource> resources =
          Streams.stream(resourceCache.findAllById(new HashSet<>(resourceIds)))
              .collect(Collectors.toMap(CachedResource::getId, Function.identity()));

      // Map resource to dto
      dtos.forEach(
          dto -> {
            Map<String, String> mapResourceFields = dto.mapResourceFields();
            if (!CollectionUtils.isEmpty(mapResourceFields)) {
              mapResourceFields
                  .entrySet()
                  .forEach(
                      entry -> {
                        String resourceIdFieldName = entry.getKey();
                        String resourceFieldName = entry.getValue();
                        Field resourceIdField =
                            ReflectionUtils.getFieldFromObject(dto, resourceIdFieldName)
                                .orElseThrow(
                                    () ->
                                        new UnexpectedException(
                                            IBaseErrorCode.ERROR_RESOURCE_ID_FIELD_NOT_FOUND,
                                            ImmutableMap.of("fieldName", resourceIdFieldName)));
                        Field resourceField =
                            ReflectionUtils.getFieldFromObject(dto, resourceFieldName)
                                .orElseThrow(
                                    () ->
                                        new UnexpectedException(
                                            IBaseErrorCode.ERROR_RESOURCE_FIELD_NOT_FOUND,
                                            ImmutableMap.of("fieldName", resourceFieldName)));
                        Object resourceId =
                            ReflectionUtils.getValueFromObject(dto, resourceIdField);
                        if (Objects.nonNull(resourceId)) {
                          if (resourceIdField.getType().equals(List.class)) {
                            List<Long> localResourceIds = (List<Long>) resourceId;
                            ReflectionUtils.setValueToObject(
                                dto,
                                resourceField,
                                localResourceIds.stream().map(resources::get).toList());
                          } else if (resourceIdField.getType().equals(Long.class)) {
                            Long id = (Long) resourceId;
                            CachedResource resource = resources.get(id);
                            if (resource != null) {
                              BaseResourceDTO baseResource = new BaseResourceDTO();
                              mapObject(resource, baseResource);
                              baseResource.setAvailableSizes(
                                  mapMasterData(resource.getAvailableSizes()));
                              ReflectionUtils.setValueToObject(dto, resourceField, baseResource);
                            }
                          }
                        }
                      });
            }
          });
    }
  }

  @SuppressWarnings("unchecked")
  private <SE extends IModel, ST extends IModelDTO<SE>> void autoMap(
      Object object, Object dto, Field field) {
    try {
      Object fieldValue = ReflectionUtils.getValueFromObject(object, field);
      if (fieldValue instanceof Collection) {
        Collection<SE> fieldEntities = (Collection<SE>) fieldValue;
        Optional<Field> dtoField = ReflectionUtils.getFieldFromObject(dto, field.getName());
        Class<ST> dtoFieldType =
            dtoField
                .map(nonNullField -> (ParameterizedType) nonNullField.getGenericType())
                .map(type -> (Class<ST>) type.getActualTypeArguments()[0])
                .orElse(null);
        if (dtoField.isPresent() && Objects.nonNull(dtoFieldType)) {
          ReflectionUtils.setValueToObject(dto, dtoField.get(), map(fieldEntities, dtoFieldType));
        }
      } else {
        SE fieldEntity = (SE) fieldValue;
        Optional<Field> dtoField = ReflectionUtils.getFieldFromObject(dto, field.getName());
        Class<ST> dtoFieldType =
            dtoField.map(nonNullField -> (Class<ST>) nonNullField.getType()).orElse(null);
        if (Objects.nonNull(fieldEntity) && dtoField.isPresent()) {
          ReflectionUtils.setValueToObject(dto, dtoField.get(), map(fieldEntity, dtoFieldType));
        }
      }
    } catch (Exception ignored) {
    }
  }

  /**
   * DTOMapper core function for mapping from search result to dto
   *
   * @param <S> ISearchResult
   * @param <T> ISearchResultDTO
   * @param searchResults search result
   * @param type output type
   * @return List<ISearchResultDTO>
   */
  private <S extends ISearchResult, T extends ISearchResultDTO<S>> List<T> mapSearchResult(
      Collection<S> searchResults, Class<T> type) {
    if (CollectionUtils.isEmpty(searchResults)) return Collections.emptyList();
    // Mapping with model mapper
    modelMapper.getConfiguration().setPropertyCondition(ignoreLazyFetchCondition);
    TimeZone timezone = getCurrentTimezone();
    List<T> result =
        searchResults.stream()
            .map(
                item -> {
                  T dto = modelMapper.map(item, type);
                  if (Objects.nonNull(timezone)) {
                    updateDateTimeFields(
                        dto, value -> DateTimeUtils.toTimezoneFromUtc(value, timezone));
                  }
                  return dto;
                })
            .toList();

    // Mapping master data
    Field createdDate = getCreatedDateField(searchResults);
    if (Objects.nonNull(createdDate)) {
      mapMasterDataWithHistory(result, searchResults, type);
    } else {
      mapLatestMasterData(result, searchResults, type);
    }

    // Apply additional mapper
    StreamUtils.zipWithIndex(searchResults.stream())
        .forEach(
            indexedResult -> {
              T dto = result.get((int) indexedResult.getIndex());
              autoMapSearchResult(indexedResult.getValue(), dto);
              dto.additionalMapper(indexedResult.getValue(), this);
            });

    // Auto fetch resource
    autoFetchResource(result);

    return result;
  }

  public <E extends IModel, T extends IModelDTO<E>> T map(E entity, Class<T> type) {
    if (Objects.isNull(entity)) return null;
    return map(List.of(entity), type, false, false).stream().findFirst().orElse(null);
  }

  public <E extends IModel, T extends IModelDTO<E>> T map(E entity, T dto) {
    if (Objects.isNull(entity)) return null;
    @SuppressWarnings("unchecked")
    T newDto =
        (T) map(List.of(entity), dto.getClass(), false, false).stream().findFirst().orElse(null);
    ReflectionUtils.getAllFieldsFromObject(dto)
        .forEach(
            field -> {
              Optional.ofNullable(ReflectionUtils.getValueFromObject(newDto, field))
                  .ifPresent(value -> ReflectionUtils.setValueToObject(dto, field, value));
            });
    return dto;
  }

  public <E extends IModel, T extends IModelDTO<E>> T fetchMap(E entity, Class<T> type) {
    if (Objects.isNull(entity)) return null;
    return map(List.of(entity), type, true, true).stream().findFirst().orElse(null);
  }

  public <E extends IModel, T extends IModelDTO<E>> T fetchMap(E entity, T dto) {
    if (Objects.isNull(entity)) return null;
    @SuppressWarnings("unchecked")
    T newDto =
        (T) map(List.of(entity), dto.getClass(), true, true).stream().findFirst().orElse(null);
    ReflectionUtils.getAllFieldsFromObject(dto)
        .forEach(
            field -> {
              Optional.ofNullable(ReflectionUtils.getValueFromObject(newDto, field))
                  .ifPresent(value -> ReflectionUtils.setValueToObject(dto, field, value));
            });
    return dto;
  }

  public <E extends IModel, T extends IModelDTO<E>> List<T> map(
      Collection<E> entities, Class<T> type) {
    if (Objects.isNull(entities)) return null;
    return map(entities, type, false, false);
  }

  public <E extends IModel, T extends IModelDTO<E>> List<T> fetchMap(
      Collection<E> entities, Class<T> type) {
    if (Objects.isNull(entities)) return null;
    return map(entities, type, true, false);
  }

  public <E extends IModel, T extends IModelDTO<E>> SearchResponseDTO<List<T>> map(
      SearchResponseDTO<List<E>> source, Class<T> type) {
    return SearchResponseDTO.success(
        map(source.getData(), type), source.getPage(), source.getLimit(), source.getTotalRecords());
  }

  public <E extends IModel, T extends IModelDTO<E>> SearchResponseDTO<List<T>> fetchMap(
      SearchResponseDTO<List<E>> source, Class<T> type) {
    return SearchResponseDTO.success(
        fetchMap(source.getData(), type),
        source.getPage(),
        source.getLimit(),
        source.getTotalRecords());
  }

  public <S extends ISearchResult, T extends ISearchResultDTO<S>>
      SearchResponseDTO<List<T>> mapSearchResult(SearchResponseDTO<List<S>> source, Class<T> type) {
    return SearchResponseDTO.success(
        mapSearchResult(source.getData(), type),
        source.getPage(),
        source.getLimit(),
        source.getTotalRecords());
  }

  public <E extends IModel, T extends IModelDTO<E>> SearchResponseDTO<List<T>> mapSearchResult(
      Page<E> source, Class<T> type) {
    return SearchResponseDTO.success(
        map(source.getContent(), type),
        source.getPageable().getPageNumber(),
        source.getPageable().getPageSize(),
        source.getTotalElements());
  }

  /**
   * Map dto to entity, skip some of the fields by name
   *
   * @param <E> any model
   * @param <D> any class
   * @param dto source dto
   * @param type output class
   * @param skip list of fields will be ignored
   * @return E
   */
  private <E extends IModel, D> E mapToEntity(
      D dto, Class<E> type, List<String> skip, IToEntityExtraMapping<D, E> extraMapping) {
    TimeZone timezone = getCurrentTimezone();
    E entity = mapSingleEntity(dto, type, timezone);
    List<Field> skipFields =
        ReflectionUtils.getAllFieldsFromObject(entity).stream()
            .filter(field -> skip.contains(field.getName()))
            .toList();
    skipFields.forEach(
        field -> {
          try {
            field.setAccessible(true);
            field.set(entity, null);
          } catch (Exception e) {
            throw new UnexpectedException(
                IBaseErrorCode.ERROR_DTO_MAPPER_PROCESSING,
                ImmutableMap.of("error", e.getMessage()));
          }
        });
    if (Objects.nonNull(extraMapping)) {
      extraMapping.map(entity, dto);
    }
    return entity;
  }

  /**
   * Map dto to existing entity, skip some of fields by name
   *
   * @param <E> any model
   * @param <D> any class
   * @param dto source dto
   * @param skip list of fields will be ignored
   * @return E
   */
  private <E extends IModel, D> E mapToEntity(
      D dto, E entity, List<String> skip, IToEntityExtraMapping<D, E> extraMapping) {
    List<Field> skipFields =
        ReflectionUtils.getAllFieldsFromObject(entity).stream()
            .filter(field -> skip.contains(field.getName()))
            .toList();
    Map<String, Object> defaultSkipValues = new HashMap<>();
    skipFields.forEach(
        field -> {
          try {
            field.setAccessible(true);
            defaultSkipValues.put(field.getName(), field.get(entity));
            field.set(entity, null);
          } catch (Exception e) {
            throw new UnexpectedException(
                IBaseErrorCode.ERROR_DTO_MAPPER_PROCESSING,
                ImmutableMap.of("error", e.getMessage()));
          }
        });
    TimeZone timezone = getCurrentTimezone();
    mapSingleEntity(dto, entity, timezone);
    skipFields.forEach(
        field -> {
          try {
            field.setAccessible(true);
            field.set(entity, defaultSkipValues.get(field.getName()));
          } catch (Exception e) {
            throw new UnexpectedException(
                IBaseErrorCode.ERROR_DTO_MAPPER_PROCESSING,
                ImmutableMap.of("error", e.getMessage()));
          }
        });
    if (Objects.nonNull(extraMapping)) {
      extraMapping.map(entity, dto);
    }
    return entity;
  }

  /**
   * @param source
   * @param destination
   * @param skip
   * @param <S>
   * @param <D>
   */
  public <S, D> void mapObject(S source, D destination, List<String> skip) {
    List<Field> skipFields =
        ReflectionUtils.getAllFieldsFromObject(destination).stream()
            .filter(field -> skip.contains(field.getName()))
            .toList();
    Map<String, Object> defaultSkipValues = new HashMap<>();
    skipFields.forEach(
        field -> {
          try {
            field.setAccessible(true);
            defaultSkipValues.put(field.getName(), field.get(destination));
            field.set(destination, null);
          } catch (Exception e) {
            throw new UnexpectedException(
                IBaseErrorCode.ERROR_DTO_MAPPER_PROCESSING,
                ImmutableMap.of("error", e.getMessage()));
          }
        });
    mapObject(source, destination);
    skipFields.forEach(
        field -> {
          try {
            field.setAccessible(true);
            field.set(destination, defaultSkipValues.get(field.getName()));
          } catch (Exception e) {
            throw new UnexpectedException(
                IBaseErrorCode.ERROR_DTO_MAPPER_PROCESSING,
                ImmutableMap.of("error", e.getMessage()));
          }
        });
  }

  public <S, D> void mapObject(S source, D destination) {
    modelMapper.map(source, destination);
  }

  public <E extends IModel, D> E mapToEntity(D dto, Class<E> type, List<String> skip) {
    return mapToEntity(dto, type, skip, null);
  }

  public <E extends IModel, D> E mapToEntity(D dto, E entity, List<String> skip) {
    return mapToEntity(dto, entity, skip, null);
  }

  public <E extends IModel, D> E mapToEntity(D dto, Class<E> type) {
    return mapToEntity(dto, type, Collections.emptyList(), null);
  }

  public <E extends IModel, D> E mapToEntity(D dto, E entity) {
    return mapToEntity(dto, entity, Collections.emptyList(), null);
  }

  public <E extends IModel, D> List<E> mapToEntity(List<D> dtos, Class<E> type, List<String> skip) {
    return dtos.stream().map(dto -> mapToEntity(dto, type, skip, null)).toList();
  }

  public <E extends IModel, D> List<E> mapToEntity(List<D> dtos, Class<E> type) {
    return dtos.stream().map(dto -> mapToEntity(dto, type)).toList();
  }

  public <E extends IModel, D> List<E> mapToEntity(Map<?, D> dtos, List<E> entities) {
    return entities.stream()
        .peek(
            entity -> {
              D dto = dtos.get(entity.getId());
              mapToEntity(dto, entity);
            })
        .toList();
  }

  public <E extends IModel, D> List<E> mapToEntity(
      Map<?, D> dtos, List<E> entities, List<String> skip) {
    return entities.stream()
        .peek(
            entity -> {
              D dto = dtos.get(entity.getId());
              mapToEntity(dto, entity, skip, null);
            })
        .toList();
  }

  public <E extends IModel, D> List<E> mapToEntity(
      List<D> dtos, Class<E> type, IToEntityExtraMapping<D, E> extraMapping) {
    return dtos.stream()
        .map(dto -> mapToEntity(dto, type, Collections.emptyList(), extraMapping))
        .toList();
  }

  public <E extends IModel, D> List<E> mapToEntity(
      Map<?, D> dtos, List<E> entities, IToEntityExtraMapping<D, E> extraMapping) {
    return entities.stream()
        .peek(
            entity -> {
              D dto = dtos.get(entity.getId());
              mapToEntity(dto, entity, Collections.emptyList(), extraMapping);
            })
        .toList();
  }

  public MasterDataDTO mapMasterData(String code) {
    if (Objects.isNull(code)) return null;
    MasterDataDTO masterDataDTO = masterDataCacheUtils.getByCode(code);
    translateMasterDataName(List.of(masterDataDTO));
    return masterDataDTO;
  }

  public MasterDataDTO mapMasterData(String code, LocalDateTime timestamp) {
    if (Objects.isNull(code)) return null;
    MasterDataDTO masterDataDTO = masterDataCacheUtils.getByCodeWithHistory(code, timestamp);
    translateMasterDataName(List.of(masterDataDTO));
    return masterDataDTO;
  }

  public List<MasterDataDTO> mapMasterData(Collection<String> codes) {
    if (Objects.isNull(codes)) return null;
    List<MasterDataDTO> masterDataDTOs = masterDataCacheUtils.getAllByCodes(new HashSet<>(codes));
    translateMasterDataName(masterDataDTOs);
    return masterDataDTOs;
  }

  public List<MasterDataDTO> mapMasterData(Collection<String> codes, LocalDateTime timestamp) {
    if (Objects.isNull(codes)) return null;
    List<MasterDataDTO> masterDataDTOs =
        masterDataCacheUtils.getAllByCodesWithHistory(new HashSet<>(codes), timestamp);
    translateMasterDataName(masterDataDTOs);
    return masterDataDTOs;
  }
}
