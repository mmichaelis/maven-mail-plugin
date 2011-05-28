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

import de.mmichaelis.maven.mojo.mail.MailPriority;
import org.apache.maven.plugin.AbstractMojo;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import static org.codehaus.plexus.util.StringUtils.isEmpty;

/**
 * Abstract Mojo for derived mail mojos.
 *
 * @since 5/27/11 11:01 PM
 */
public abstract class AbstractMailMojo extends AbstractMojo {
  private static final String HOSTNAME;
  private static final String HOSTIP;
  private static final String USERNAME = System.getProperty("user.name");
  private static final SimpleDateFormat MAIL_TIMESTAMP_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss Z");

  /**
   * Can be used to disable sending mails.
   *
   * @parameter default-value="false" expression="${mail.skip}"
   */
  private boolean skip;

  /**
   * <p>
   * The address to send the email from. Examples:
   * <pre>
   *   john.doe@example.com
   *   John Doe &lt;john.doe@example.com>
   * </pre>
   * or any valid email address.
   * </p>
   * <p>
   * Use  {@link #getFrom()} to get the email-address to ensure that a good default
   * value is chosen.
   * </p>
   *
   * @parameter expression="${mail.from}"
   * @see #getFrom()
   */
  private String from;

  /**
   * The host to send the mail from.
   *
   * @parameter default-value="localhost" expression="${mail.smtp.host}"
   */
  private String smtphost;

  /**
   * The host to send the mail from.
   *
   * @parameter default-value="localhost" expression="${mail.smtp.port}"
   */
  private String smtpport;

  /**
   * When automatic mails should expire in days.
   *
   * @parameter default-value="7" expression="${mail.expires}"
   */
  private int expires;

  /**
   * Charset for the emails.
   * @parameter default="${project.build.outputEncoding}" expression="${mail.charset}"
   */
  private String charset;

  static {
    String hostname;
    String hostip;
    try {
      final InetAddress addr = InetAddress.getLocalHost();
      hostip = addr.getHostAddress();
      hostname = addr.getHostName();
    } catch (UnknownHostException e) {
      hostname = "localhost";
      hostip = "127.0.0.1";
    }
    HOSTNAME = hostname;
    HOSTIP = hostip;
  }

  /**
   * Create the mail session.
   *
   * @return session to send mails with
   */
  protected Session getSession() {
    final Properties properties = new Properties();
    properties.setProperty("mail.smtp.host", smtphost);
    properties.setProperty("mail.smtp.port", smtpport);
    final Session session = Session.getDefaultInstance(properties);
    session.setDebug(getLog().isDebugEnabled());
    return session;
  }

  /**
   * Retrieve the mail address to send the email from. If nothing was specified defaults to
   * username@hostname
   *
   * @return the from-address
   */
  private String getFrom() {
    if (isEmpty(from)) {
      from = USERNAME + "@" + HOSTNAME;
    }
    return from;
  }

  protected String getSignature() {
    return "-- \nSend via maven-mail-plugin from " + USERNAME + " on " + HOSTNAME + " (" + HOSTIP + ")";
  }

  protected void addHeaderInformation(final MimeMessage message) throws MessagingException {
    addExpiryDate(message);
    message.addHeader("Precedence", "bulk");
    message.addHeader("X-Auto-Response-Suppress", "OOF");
  }

  private void addExpiryDate(final MimeMessage message) throws MessagingException {
    final Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, expires);
    message.setHeader("Expiry-Date", MAIL_TIMESTAMP_FORMAT.format(calendar.getTime()));
  }

  protected MimeMessage createMessage(final MailPriority priority) throws MessagingException {
    final Session session = getSession();
    final MimeMessage message = new MimeMessage(session);
    addHeaderInformation(message);
    priority.addPriority(message);
    message.setText(getText(), charset);
    final InternetAddress sender = new InternetAddress(getFrom());
    message.setSender(sender);
    return message;
  }

  protected abstract String getText();

  protected MimeMessage createMessage() throws MessagingException {
    return createMessage(MailPriority.LOW);
  }
}
