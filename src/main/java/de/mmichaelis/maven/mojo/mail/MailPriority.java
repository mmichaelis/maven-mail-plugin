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

import static org.codehaus.plexus.util.StringUtils.isEmpty;

/**
 * Represents the different priorities you might apply to an email. Supported formats
 * include the headers "Importance", "Priority" and "X-Priority".
 *
 * @see <a href="https://blog.coremedia.com/cm/post/2258526/Apache_Ant_Sending_Mails_with_Expiry_Date_and_Priority.html">Apache Ant: Sending Mails with Expiry Date and Priority</a>
 * @since 6/2/11 6:53 PM
 */
public enum MailPriority implements MailHeader {
  /**
   * Low priority which should be chosen for informal mails.
   */
  LOW("low", "Non-Urgent", "5"),
  /**
   * Normal priority.
   */
  NORMAL("normal", "Normal", "3"),
  /**
   * Urgent priority. Might be chosen if bugs occur.
   */
  HIGH("high", "Urgent", "1");

  /**
   * Value for the "Importance" header field.
   */
  private final String importance;
  /**
   * Value for the "Priority" header field.
   */
  private final String priority;
  /**
   * Value for the "X-Priority" header field.
   */
  private final String xpriority;

  /**
   * Constructor for the mail priority with the different values for the different
   * headers which represent the priority.
   *
   * @param importance value for header "Importance"
   * @param priority   value for header "Priority"
   * @param xpriority  value for header "X-Priority"
   */
  MailPriority(final String importance, final String priority, final String xpriority) {
    this.importance = importance;
    this.priority = priority;
    this.xpriority = xpriority;
  }

  /**
   * Adds the priority headers to the given email.
   *
   * @param message the message to modify
   * @param log the log to report possible problems or debug statements to
   */
  public void addHeader(final MimeMessage message, final Log log) {
    try {
      message.addHeader("Importance", importance);
      message.addHeader("Priority", priority);
      message.addHeader("X-Priority", xpriority);
    } catch (MessagingException e) {
      log.warn("Could not add priority headers.", e);
    }
  }

  /**
   * Evaluates if the given argument is a valid description of this priority.
   * @param arg the argument to analyse
   * @return true if the priority matches, false if not
   */
  private boolean matches(final String arg) {
    return importance.equalsIgnoreCase(arg) ||
            priority.equalsIgnoreCase(arg) ||
            xpriority.equalsIgnoreCase(arg);
  }
  
  /**
   * Parses the given priority. Argument will be trimmed.
   * @param arg the priority to parse; null and empty string will cause the default value to use.
   * @param log where to log problems to
   * @return the parsed priority; guaranteed to be non-null
   */
  public static MailPriority parse(final String arg, final Log log) {
    MailPriority result = null;
    if (isEmpty(arg)) {
      result = LOW;
    } else {
      final MailPriority[] values = MailPriority.values();
      for (final MailPriority prio : values) {
        if (prio.matches(arg)) {
          result = prio;
          break;
        }
      }
      if (result == null) {
        log.warn("Could not parse priority '" + arg + "'. Using default priority.");
        result = LOW;
      }
    }
    return result;
  }
  
}
