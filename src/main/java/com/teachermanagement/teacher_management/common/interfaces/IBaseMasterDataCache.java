package com.teachermanagement.teacher_management.common.interfaces;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

import com.teachermanagement.teacher_management.common.model.CachedMasterData;


public interface IBaseMasterDataCache extends CrudRepository<CachedMasterData, String> {
  List<CachedMasterData> findAllByType(String type);
}
