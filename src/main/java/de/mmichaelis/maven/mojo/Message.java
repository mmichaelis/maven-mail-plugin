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

import java.io.File;

/**
 * Represents the message to be sent. If both, text and textFile is set textFile will be taken.
 * @since 6/4/11 11:26 PM
 */
public class Message {
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

  public String getText() {
    return text;
  }

  public File getTextFile() {
    return textFile;
  }
}
