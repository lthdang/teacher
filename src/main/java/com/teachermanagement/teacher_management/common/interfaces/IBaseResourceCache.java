package com.teachermanagement.teacher_management.common.interfaces;

import org.springframework.data.repository.CrudRepository;

import com.teachermanagement.teacher_management.common.model.CachedResource;


public interface IBaseResourceCache extends CrudRepository<CachedResource, Long> {}
