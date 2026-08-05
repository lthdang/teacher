package com.teacher.common.interfaces;

import org.springframework.data.repository.CrudRepository;

import com.teacher.common.model.CachedTranslation;


public interface IBaseTranslationCache extends CrudRepository<CachedTranslation, String> {}
