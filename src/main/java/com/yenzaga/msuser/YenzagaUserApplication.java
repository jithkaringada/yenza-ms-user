package com.yenzaga.msuser;

import com.yenzaga.common.config.AppCoreProperties;
import com.yenzaga.common.config.CoreConstants;
import com.yenzaga.common.config.SignatureVerificationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class, WebMvcAutoConfiguration.class})
@EnableDiscoveryClient
@EnableReactiveMongoRepositories
@Import(value = MongoAutoConfiguration.class)
public class YenzagaUserApplication {

  private static final Logger log = LoggerFactory.getLogger(YenzagaUserApplication.class);

  private static final String SPRING_PROFILE_DEFAULT = "spring.profiles.default";
  private final Environment env;

  public YenzagaUserApplication(Environment env) { this.env = env; }

  @PostConstruct
  public void initApplication() {
    Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
    if(activeProfiles.contains(CoreConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(CoreConstants.SPRING_PROFILE_PRODUCTION)) {
      log.error("You have misconfigured your application! It should not run with both the 'dev' and 'prod' profiles at the same time.");
    }
    if(activeProfiles.contains(CoreConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(CoreConstants.SPRING_PROFILE_CLOUD)) {
      log.error("You have misconfigured your application! It should not run with both the 'dev' and 'cloud' profiles at the same time.");
    }
  }

  public static void main(String[] args) throws UnknownHostException {
    SpringApplication app = new SpringApplication(YenzagaUserApplication.class);
    app.setWebApplicationType(WebApplicationType.SERVLET);
    Map<String, Object> defProperties = new HashMap<>();
    defProperties.put(SPRING_PROFILE_DEFAULT, CoreConstants.SPRING_PROFILE_DEVELOPMENT);
    app.setDefaultProperties(defProperties);
    Environment env = app.run(args).getEnvironment();
    String protocol = "http";
    if(env.getProperty("server.ssl.key-store") != null) {
      protocol = "https";
    }
    log.info("\n-------------------------------------------------------------\n\t" +
            "Application '{}' is running! Access URLs:\n\t" +
            "Local: \t\t{}://localhost:{}\n\t" +
            "External: \t{}://{}:{}\n\t" +
            "Profile(s): \t{}\n-------------------------------------------------------------",
        env.getProperty("spring.application.name"),
        protocol,
        env.getProperty("server.port"),
        protocol,
        InetAddress.getLocalHost().getHostAddress(),
        env.getProperty("server.port"),
        env.getActiveProfiles());

    String configServerStatus = env.getProperty("configserver.status");
    log.info("\n----------------------------------------------------------\n\t" +
            "Config Server: \t{}\n----------------------------------------------------------",
        configServerStatus == null ? "Not found or not setup for this application" : configServerStatus);
  }
}
