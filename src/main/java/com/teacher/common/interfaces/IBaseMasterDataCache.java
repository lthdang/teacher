package com.teacher.common.interfaces;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

import com.teacher.common.model.CachedMasterData;


public interface IBaseMasterDataCache extends CrudRepository<CachedMasterData, String> {
  List<CachedMasterData> findAllByType(String type);
}
