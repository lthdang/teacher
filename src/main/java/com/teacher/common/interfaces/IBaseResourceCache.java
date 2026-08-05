package com.teacher.common.interfaces;

import org.springframework.data.repository.CrudRepository;

import com.teacher.common.model.CachedResource;


public interface IBaseResourceCache extends CrudRepository<CachedResource, Long> {}
