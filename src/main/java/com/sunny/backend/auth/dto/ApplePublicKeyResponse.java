package com.sunny.backend.auth.dto;

import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplePublicKeyResponse {
  private List<Key> keys;

  @Getter
  @Setter
  public static class Key{
    private String kty;
    private String kid;
    private String use;
    private String alg;
    private String n;
    private String e;
  }

  public Optional<ApplePublicKeyResponse.Key> getMatchedKeyBy(String kid,String alg){
    return this.keys.stream()
        .filter(key->key.getKid().equals(kid)&& key.getAlg().equals(alg))
        .findFirst();
  }

}
