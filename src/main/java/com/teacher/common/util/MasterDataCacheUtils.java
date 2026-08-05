package com.teacher.common.util;
import com.google.common.collect.ImmutableMap;
import com.teacher.common.constant.EMasterDataType;
import com.teacher.common.constant.IBaseErrorCode;
import com.teacher.common.dto.masterdata.MasterDataDTO;
import com.teacher.common.exception.UnexpectedException;
import com.teacher.common.interfaces.IBaseMasterDataCache;
import com.teacher.common.model.CachedMasterData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class MasterDataCacheUtils {
  private final IBaseMasterDataCache masterDataCache;

  public MasterDataCacheUtils(@Autowired(required = false) IBaseMasterDataCache masterDataCache) {
    this.masterDataCache = masterDataCache;
  }

  /**
   * Find all master data by codes with latest information
   *
   * @param codes list of codes
   * @param isActiveOnly get active master data only
   * @return List<MasterDataDTO>
   */
  public List<MasterDataDTO> getAllByCodes(Set<String> codes, boolean isActiveOnly) {
    List<MasterDataDTO> result =
        StreamSupport.stream(masterDataCache.findAllById(codes).spliterator(), true)
            .filter(masterData -> !isActiveOnly || masterData.getIsActive())
            .map(MasterDataDTO::from)
            .toList();
    if (result.size() != codes.size()) {
      throw new UnexpectedException(
          IBaseErrorCode.ERROR_SOME_MASTER_DATA_NOT_FOUND,
          ImmutableMap.of(
              "codes",
              codes.stream()
                  .filter(
                      code ->
                          result.stream()
                              .noneMatch(masterData -> masterData.getCode().equals(code)))
                  .collect(Collectors.joining(", "))));
    }
    return result;
  }

  /**
   * Get all master data by type
   *
   * @param type master data type
   * @param isActiveOnly true will return active master data only
   * @return List<MasterDataDTO>
   */
  public List<MasterDataDTO> getAllByType(String type, boolean isActiveOnly) {
    List<CachedMasterData> masterDatas = masterDataCache.findAllByType(type);
    if (CollectionUtils.isEmpty(masterDatas)) {
      throw new UnexpectedException(
          IBaseErrorCode.ERROR_NOT_FOUND_MASTER_DATA_TYPE, ImmutableMap.of("type", type));
    }
    return masterDatas.stream()
        .filter(masterData -> (!isActiveOnly || masterData.getIsActive()))
        .map(MasterDataDTO::from)
        .toList();
  }

  /**
   * Get all master data DTOs has latest created date before given timestamp from master data
   * history cache
   *
   * @param masterDatas list of master data
   * @param timestamp if timestamp not null, master data history will be evaluated to get response
   * @return List<MasterDataDTO>
   */
  private List<MasterDataDTO> getAllByCacheHistoryLatestBefore(
      List<CachedMasterData> masterDatas, LocalDateTime timestamp) {
    return masterDatas.stream()
        .map(
            masterData ->
                Optional.ofNullable(masterData.getHistories())
                    .flatMap(
                        masterDataHistories ->
                            masterDataHistories.stream()
                                .filter(
                                    history ->
                                        history.getCreatedDate().isBefore(timestamp)
                                            || history.getCreatedDate().isEqual(timestamp))
                                .max(Comparator.comparing(CachedMasterData::getCreatedDate)))
                    .orElse(null))
        .filter(Objects::nonNull)
        .map(MasterDataDTO::from)
        .toList();
  }

  /**
   * Get all master data by codes with information in history before given timestamp If data type is
   * not enable history tracking, the latest information will be found as result
   *
   * @param codes list of master data codes
   * @param timestamp if timestamp not null, master data history will be evaluated to get response
   * @return List<MasterDataDTO>
   */
  public List<MasterDataDTO> getAllByCodesWithHistory(Set<String> codes, LocalDateTime timestamp) {
    List<CachedMasterData> listMasterData =
        StreamSupport.stream(masterDataCache.findAllById(codes).spliterator(), true).toList();
    if (listMasterData.size() != codes.size()) {
      throw new UnexpectedException(
          IBaseErrorCode.ERROR_SOME_MASTER_DATA_NOT_FOUND,
          ImmutableMap.of(
              "codes",
              codes.stream()
                  .filter(
                      code ->
                          listMasterData.stream()
                              .noneMatch(masterData -> masterData.getCode().equals(code)))
                  .collect(Collectors.joining(", "))));
    }
    Map<Boolean, List<CachedMasterData>> listMasterDataPartition =
        listMasterData.stream()
            .collect(Collectors.partitioningBy(CachedMasterData::getEnableHistory));
    List<CachedMasterData> enableHistoryTrackData = listMasterDataPartition.get(Boolean.TRUE);
    List<CachedMasterData> disableHistoryTrackData = listMasterDataPartition.get(Boolean.FALSE);
    List<MasterDataDTO> result = new ArrayList<>();
    if (!CollectionUtils.isEmpty(enableHistoryTrackData)) {
      result.addAll(getAllByCacheHistoryLatestBefore(enableHistoryTrackData, timestamp));
    }
    if (!CollectionUtils.isEmpty(disableHistoryTrackData)) {
      result.addAll(
          getAllByCodes(
              disableHistoryTrackData.stream()
                  .map(CachedMasterData::getCode)
                  .collect(Collectors.toSet()),
              false));
    }
    return result;
  }

  /**
   * Get all master data by type from history before given timestamp
   *
   * @param type master data type
   * @param timestamp if timestamp not null, master data history will be evaluated to get response
   * @return List<MasterDataDTO>
   */
  public List<MasterDataDTO> getAllByTypeWithHistory(String type, LocalDateTime timestamp) {
    List<CachedMasterData> masterDatas = masterDataCache.findAllByType(type);
    if (CollectionUtils.isEmpty(masterDatas)) {
      throw new UnexpectedException(
          IBaseErrorCode.ERROR_NOT_FOUND_MASTER_DATA_TYPE, ImmutableMap.of("type", type));
    }
    if (!masterDatas.stream().findFirst().get().getEnableHistory()) {
      throw new UnexpectedException(
          IBaseErrorCode.ERROR_MASTER_DATA_NOT_ENABLE_HISTORY_TRACKING,
          ImmutableMap.of("type", type));
    }
    return getAllByCacheHistoryLatestBefore(masterDatas, timestamp);
  }

  /**
   * Get active master data only by code
   *
   * @param code master data code
   * @return MasterDataDTO
   */
  public MasterDataDTO getActiveByCode(String code) {
    return getAllByCodes(Set.of(code), true).stream()
        .findFirst()
        .orElseThrow(
            () ->
                new UnexpectedException(
                    IBaseErrorCode.ERROR_MASTER_DATA_NOT_FOUND, ImmutableMap.of("code", code)));
  }

  /**
   * Get all master data by codes
   *
   * @param codes list of master data codes
   * @return List<MasterDataDTO>
   */
  public List<MasterDataDTO> getAllByCodes(Set<String> codes) {
    return getAllByCodes(codes, false);
  }

  /**
   * Get master data by code
   *
   * @param code master data code
   * @return MasterDataDTO
   */
  public MasterDataDTO getByCode(String code) {
    return getAllByCodes(Set.of(code), false).stream()
        .findFirst()
        .orElseThrow(
            () ->
                new UnexpectedException(
                    IBaseErrorCode.ERROR_MASTER_DATA_NOT_FOUND, ImmutableMap.of("code", code)));
  }

  /**
   * Get master data from history before given timestamp by code
   *
   * @param code master data code
   * @param timestamp if timestamp not null, master data history will be evaluated to get response
   * @return MasterDataDTO
   */
  public MasterDataDTO getByCodeWithHistory(String code, LocalDateTime timestamp) {
    return getAllByCodesWithHistory(Set.of(code), timestamp).stream()
        .findFirst()
        .orElseThrow(
            () ->
                new UnexpectedException(
                    IBaseErrorCode.ERROR_MASTER_DATA_NOT_FOUND, ImmutableMap.of("code", code)));
  }

  /**
   * Get all master data by type (active master data only)
   *
   * @param type master data type
   * @return List<MasterDataDTO>
   */
  public List<MasterDataDTO> getAllActiveByType(String type) {
    return getAllByType(type, true);
  }

  /**
   * Get all active master data only by codes
   *
   * @param codes list of master data code
   * @return List<MasterDataDTO>
   */
  public List<MasterDataDTO> getAllActiveByCodes(Set<String> codes) {
    return getAllByCodes(codes, true);
  }

  /**
   * Get all master data by type include inactive master data
   *
   * @param type master data type
   * @return List<MasterDataDTO>
   */
  public List<MasterDataDTO> getAllByType(String type) {
    return getAllByType(type, false);
  }

  /**
   * Get all master data by type (active master data only)
   *
   * @param type master data type
   * @return List<MasterDataDTO>
   */
  public List<MasterDataDTO> getAllActiveByType(EMasterDataType type) {
    return getAllByType(type.toString(), true);
  }
}
