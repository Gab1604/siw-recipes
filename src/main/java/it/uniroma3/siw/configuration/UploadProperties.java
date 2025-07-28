package it.uniroma3.siw.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "upload")
public class UploadProperties {
  /**
   * Directory base dove mettere i file caricati.
   */
  private String baseDir;

  public String getBaseDir() {
    return baseDir;
  }
  public void setBaseDir(String baseDir) {
    this.baseDir = baseDir;
  }
}