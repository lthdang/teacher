package com.teachermanagement.teacher_management.common.util;

import com.google.common.collect.ImmutableMap;
import com.teachermanagement.teacher_management.common.constant.IBaseErrorCode;
import com.teachermanagement.teacher_management.common.exception.UnexpectedException;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
  /**
   * Hash given source to string using SHA-256 algorithms
   *
   * @param source
   * @return
   */
  public static String SHA256(byte[] source) {
    try {
      byte[] hash = MessageDigest.getInstance("SHA-256").digest(source);
      return new BigInteger(1, hash).toString(16);
    } catch (NoSuchAlgorithmException e) {
      throw new UnexpectedException(
          IBaseErrorCode.ERROR_COULD_NOT_HASH_DATA, ImmutableMap.of("error", e.getMessage()));
    }
  }
}
