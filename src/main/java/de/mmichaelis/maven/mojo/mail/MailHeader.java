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
 * A header element (or a group of elements).
 * @since 6/2/11 9:30 PM
 */
public interface MailHeader {
  /**
   * Adds the header information to the given message.
   * @param message message to add the header to
   * @param log the log to report possible problems or debug statements to
   */
  void addHeader(final MimeMessage message, final Log log);
}
