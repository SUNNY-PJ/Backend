package com.sunny.backend.auth;

import lombok.Getter;

@Getter
public class ApplePublicKey {
  private String alg; // Algorithm
  private String kty; // Key type
  private String use; // Public key use
  private String kid; // Key ID
  private String n; // Modulus
  private String e; // Exponent

}