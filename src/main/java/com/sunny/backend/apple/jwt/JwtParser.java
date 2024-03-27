package com.sunny.backend.apple.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunny.backend.auth.UnauthorizedException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.security.PublicKey;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

@Component
public class JwtParser {

    private static final String TOKEN_VALUE_DELIMITER = "\\.";
    private static final int HEADER_INDEX = 0;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public Map<String, String> parseHeaders(String token) {
      try {
        String encodedHeader = token.split(TOKEN_VALUE_DELIMITER)[HEADER_INDEX];
        String decodedHeader = new String(Base64Utils.decodeFromUrlSafeString(encodedHeader));
        return OBJECT_MAPPER.readValue(decodedHeader, Map.class);
      } catch (JsonProcessingException | ArrayIndexOutOfBoundsException e) {
        throw UnauthorizedException.invalid();
      }
    }

  public Claims parseClaims(String idToken, PublicKey publicKey) {
    try {
      return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(idToken).getBody();
    } catch (ExpiredJwtException e) {
      throw UnauthorizedException.invalid();
    } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
      throw UnauthorizedException.invalid();
    }
  }
}
