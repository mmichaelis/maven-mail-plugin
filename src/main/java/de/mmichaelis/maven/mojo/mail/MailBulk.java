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

package de.mmichaelis.maven.mojo.mail;

import org.apache.maven.plugin.logging.Log;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Adds common headers for bulk emails such as notification mails.
 * @since 6/2/11 10:05 PM
 */
public class MailBulk implements MailHeader {
  private static final MailBulk instance = new MailBulk();

  /**
   * Constructor.
   */
  private MailBulk() {
    // use the instance
  }

  /**
   * Adds the header information to the given message.
   *
   * @param message message to add the header to
   * @param log the log to report possible problems or debug statements to
   */
  @Override
  public void addHeader(final MimeMessage message, final Log log) {
    try {
      message.addHeader("Precedence", "bulk");
      message.addHeader("X-Auto-Response-Suppress", "OOF");
    } catch (MessagingException e) {
      log.warn("Could not add headers for bulk emails.", e);
    }
  }

  /**
   * Return the instance.
   * @return instance
   */
  public static MailBulk getInstance() {
    return instance;
  }
}
