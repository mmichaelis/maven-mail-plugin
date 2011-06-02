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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static org.codehaus.plexus.util.StringUtils.isEmpty;

/**
 * The mail expiration header.
 * @since 6/2/11 9:04 PM
 */
public final class MailExpiration implements MailHeader {
  /**
   * Expiration date header as suggested by RFC 1327. Supported by for example Microsoft Outlook.
   */
  private static final String RFC1327_HEADER = "Expiry-Date";
  /**
   * Expiration date as used for example in Usenet. RFC 2076 suggest that it replaces the RFC 1327 header.
   */
  private static final String RFC1036_HEADER = "Expires";

  private static final SimpleDateFormat MAIL_TIMESTAMP_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss Z", Locale.US);
  private static final int DEFAULT_EXPIRATION_DAYS = 1;

  private final Date date;

  /**
   * Constructor with the date when the mail should expire. If date is null no expiration information will be
   * added.
   * @param date the date when the mail shall expire; <code>null</code> if it should not expire
   */
  private MailExpiration(final Date date) {
    this.date = date;
  }

  /**
   * Adds the header information to the given message.
   *
   * @param message message to add the header to
   * @param log the log to report possible problems or debug statements to
   */
  @Override
  public void addHeader(final MimeMessage message, final Log log) {
    if (date != null) {
      final String headerValue = MAIL_TIMESTAMP_FORMAT.format(date);
      try {
        message.addHeader(RFC1036_HEADER, headerValue);
        message.addHeader(RFC1327_HEADER, headerValue);
      } catch (MessagingException e) {
        log.warn("Could not add expiration headers.", e);
      }
    }
  }

  /**
   * Parses the mail expiration argument. If it is an integer it is assumed that the
   * expiration is given in days.
   *
   * @param arg the expiration argument as number of days; <code>null</code>, negative values, empty string or parsing errors will cause default expiration 1 day; 0 will disable expiration
   * @param log where to write log information to
   * @return the mail expiration
   */
  public static MailExpiration parse(final String arg, final Log log) {
    final Calendar calendar = Calendar.getInstance();
    final Date expDate;
    if (isEmpty(arg)) {
      calendar.add(Calendar.DAY_OF_MONTH, DEFAULT_EXPIRATION_DAYS);
      expDate = calendar.getTime();
    } else {
      int parsedInteger;
      try {
        parsedInteger = Integer.parseInt(arg);
        if (parsedInteger < 0) {
          log.warn("Expiration days is negative. Using default value as expiration.");
          parsedInteger = DEFAULT_EXPIRATION_DAYS;
        }
      } catch (NumberFormatException e) {
        log.warn("Expiration argument '" + arg + "' is not a number. Using default value as expiration.");
        parsedInteger = DEFAULT_EXPIRATION_DAYS;
      }
      if (parsedInteger == 0) {
        expDate = null;
      } else {
        calendar.add(Calendar.DAY_OF_MONTH, parsedInteger);
        expDate = calendar.getTime();
      }
    }
    return new MailExpiration(expDate);
  }
}
