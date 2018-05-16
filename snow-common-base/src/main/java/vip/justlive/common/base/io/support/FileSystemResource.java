/*
 * Copyright (C) 2018 justlive1
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package vip.justlive.common.base.io.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import vip.justlive.common.base.io.SourceResource;
import vip.justlive.common.base.util.Checks;
import vip.justlive.common.base.util.ResourceUtils;

/**
 * 文件系统资源，包括File，Path
 * 
 * @author wubo
 *
 */
public class FileSystemResource implements SourceResource {

  private File file;
  private String filePath;
  private Path path;

  /**
   * 通过文件路径创建一个 {@code FileSystemResource}
   * 
   * @param filePath
   */
  public FileSystemResource(String filePath) {
    Checks.notNull(filePath);
    this.file = new File(filePath);
    this.filePath = file.toPath().normalize().toString();
  }

  /**
   * 通过{@link File}创建一个 {@code FileSystemResource}
   * 
   * @param file
   */
  public FileSystemResource(File file) {
    Checks.notNull(file);
    this.file = file;
  }

  /**
   * 通过{@link Path}创建一个 {@code FileSystemResource}
   * 
   * @param path
   */
  public FileSystemResource(Path path) {
    Checks.notNull(path);
    this.path = path;
    this.file = path.toFile();
  }

  @Override
  public String path() {
    if (filePath == null) {
      filePath = file.toPath().normalize().toString();
    }
    return filePath;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    if (path == null) {
      path = file.toPath();
    }
    return Files.newInputStream(path);
  }

  @Override
  public boolean isFile() {
    return file.isFile();
  }

  @Override
  public File getFile() throws IOException {
    return file;
  }

  @Override
  public URL getURL() throws IOException {
    return file.toURI().toURL();
  }

  /**
   * 获取文件Path
   * 
   * @return
   */
  public Path getPath() {
    if (path == null) {
      path = file.toPath();
    }
    return path;
  }

  @Override
  public SourceResource createRelative(String path) {
    return new FileSystemResource(ResourceUtils.relativePath(this.path(), path));
  }

}
