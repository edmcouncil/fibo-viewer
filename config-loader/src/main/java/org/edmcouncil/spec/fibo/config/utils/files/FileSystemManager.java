package org.edmcouncil.spec.fibo.config.utils.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.edmcouncil.spec.fibo.config.configuration.properties.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
@Component
public class FileSystemManager {

  private static final String WEASEL_DEFAULT_HOME_DIR_NAME = ".weasel";

  private static final Logger LOG = LoggerFactory.getLogger(FileSystemManager.class);

  private final String workingDir;
  private final String viewerConfigFileName;
  private final String defaultOntologyFileName;

  @Autowired
  public FileSystemManager(AppProperties appProperties) {
    workingDir = appProperties.getDefaultHomePath();
    viewerConfigFileName = appProperties.getViewerConfigFileName();
    defaultOntologyFileName = appProperties.getDefaultOntologyFileName();
  }

  public Path getviewerHomeDir() {
    Path userHomeDir = null;
    String userHomeProperty = null;
    switch (workingDir) {
      case "user.home":
        userHomeProperty = System.getProperty("user.home");
        userHomeDir = Paths.get(userHomeProperty);
        LOG.trace("User home dir is '{}'.", userHomeDir);
        userHomeDir = userHomeDir.resolve(WEASEL_DEFAULT_HOME_DIR_NAME);
        break;
      case "*":
        userHomeDir = Paths.get("");
        LOG.trace("Working directory is '{}'.", userHomeDir.toAbsolutePath());
        break;
      default:
        userHomeDir = Paths.get(userHomeProperty);
        LOG.trace("Application working directory is '{}'.", userHomeDir);
        break;
    }
    return userHomeDir;
  }

  private Path createDirIfNotExists(Path dirToCreate) throws IOException {
    if (Files.notExists(dirToCreate)) {
      createDir(dirToCreate);
    }
    return dirToCreate;
  }

  public void createDir(Path dirPath) throws IOException {
    try {
      Files.createDirectory(dirPath);
    } catch (IOException ex) {
      String msg = String.format("Unable to create a dir '%s'.", dirPath);
      throw new IOException(msg, ex);
    }
  }

  public Path getDefaultPathToOntologyFile() throws IOException {
    Path homeDir = getviewerHomeDir();
    return createDirIfNotExists(homeDir).resolve(defaultOntologyFileName);
  }

  public Path getPathToWeaselConfigFile() throws IOException {
    Path homeDir = getviewerHomeDir();
    return createDirIfNotExists(homeDir).resolve(viewerConfigFileName);
  }

  public Path getPathToOntologyFile(String pathString) throws IOException {
    Path path = Paths.get(pathString);
    if (path.isAbsolute()) {
      return path;

    } else {
      Path homeDir = getviewerHomeDir();
      return createDirIfNotExists(homeDir).resolve(path);
    }
  }

  public Path getPathToApiKey() {
    return getviewerHomeDir().resolve("api.key");
  }

  public Path getPathToUpdateHistory() {
    return getviewerHomeDir().resolve("updateHistory.json");
  }

}
