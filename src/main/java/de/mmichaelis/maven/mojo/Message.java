/******************************************************************************
 * Copyright 2011 Mark Michaelis                                              *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package de.mmichaelis.maven.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Represents the message to be sent. If both, text and textFile is set textFile will be taken.
 * @since 6/4/11 11:26 PM
 */
public final class Message {
  /**
   * The message to send.
   */
  private String text;
  /**
   * The message to be sent will be read from the given file.
   */
  private File textFile;

  public Message() {
  }

  public String getText(final Log log) throws MojoExecutionException, MojoFailureException {
    if (text == null && textFile == null) {
      throw new MojoExecutionException("You should either specify <text> or <textFile> as message.");
    }
    if (text != null && textFile != null) {
      log.warn("Specified both <text> and <textFile> as message. <textFile> will be taken.");
    }
    if (textFile != null) {
      return getPlainTextFromFile(textFile);
    }
    return text;
  }

  private String getPlainTextFromFile(final File textFile) throws MojoExecutionException, MojoFailureException {
    try {
      return FileUtils.fileRead(textFile);
    } catch (IOException e) {
      throw new MojoExecutionException("Failed to read file " + textFile.getAbsolutePath(), e);
    }
  }

}
