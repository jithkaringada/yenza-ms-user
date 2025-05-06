package com.yenzaga.msuser.config;

import com.nimbusds.oauth2.sdk.auth.verifier.InvalidClientException;
import com.yenzaga.common.config.SignatureVerificationProperties;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

@Configuration
public class JwtConfiguration {
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtConfiguration.class);

  @Autowired
  @Qualifier("vanillaWebClient")
  private WebClient webClient;

  @Autowired
  private SignatureVerificationProperties signatureVerificationProperties;

  @Bean
  public ReactiveJwtDecoder jwtDecoder() throws InvalidClientException {
    NimbusReactiveJwtDecoder jwtDecoder = NimbusReactiveJwtDecoder
        .withPublicKey(getPublicKey()).build();
    return jwtDecoder;
  }

  @Bean
  public RSAPublicKey getPublicKey() throws InvalidClientException {
    HttpEntity<Void> request = new HttpEntity<>(new HttpHeaders());
    Map<String, String> keys = webClient.get()
        .uri(getPublicKeyEndpoint())
        .retrieve()
        .bodyToMono(Map.class)
        .block();
//        String key = (String) restTemplate.exchange(getPublicKeyEndpoint(), HttpMethod.GET, request, Map.class).getBody()
//            .get("public_key");
    //key = "-----BEGIN PUBLIC KEY-----\n" + key + "\n-----END PUBLIC KEY-----";
    String key = keys.get("public_key");
    LOGGER.info("The key is: " + key);
    return parseStringToPublicKey(key);
  }

  private RSAPublicKey parseStringToPublicKey(String publicKeyStr) {
    try {
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      byte[] buffer= Base64.decode(publicKeyStr);
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
      return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    } catch (NoSuchAlgorithmException e) {
      LOGGER.error(e.getMessage());
    }
    catch (InvalidKeySpecException e) {
      LOGGER.error(e.getMessage());
    }
    return null;
  }

  private String getPublicKeyEndpoint() throws InvalidClientException {
    String tokenEndpointUrl = signatureVerificationProperties.getPublicKeyEndpointUri();
    LOGGER.info("Oauth2 token endpoint URL: " + tokenEndpointUrl);
    if(tokenEndpointUrl == null) {
      throw new InvalidClientException("No Token Endpoint configured in application properties");
    }
    return tokenEndpointUrl;
  }

  //    @Bean
//    public JwtDecoder jwtDecoderByIssuerUri(SignatureVerificationProperties properties) {
//        String issuerUri = properties.getPublicKeyEndpointUri();
//        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuerUri);
//        jwtDecoder.setClaimSetConverter(new UsernameSubClaimAdapter());
//        return jwtDecoder;
//    }
}
