package com.teachermanagement.teacher_management.common.interfaces;

import org.springframework.data.repository.CrudRepository;

import com.teachermanagement.teacher_management.common.model.CachedTranslation;


public interface IBaseTranslationCache extends CrudRepository<CachedTranslation, String> {}
