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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static javax.mail.internet.MimeUtility.fold;
import static javax.mail.internet.MimeUtility.mimeCharset;
import static org.codehaus.plexus.util.StringUtils.isEmpty;

/**
 * This helper class can be used to combine standard and reporting mojos to use the same base.
 * To use a new instance is created, configured and then provides helper methods.
 *
 * @since 6/7/11 10:33 PM
 */
public final class MailBase {
  private final Log log;

  private InternetAddress[] recipients;
  private String plainText;

  /**
   * Can be used to disable sending mails.
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
   */
  private String from;

  /**
   * The host to send the mail from.
   */
  private String smtphost;

  /**
   * The host to send the mail from.
   */
  private Integer smtpport;

  /**
   * When automatic mails should expire in days.
   */
  private String expires;

  /**
   * Charset for the emails.
   *
   * @see java.nio.charset.Charset
   */
  private String charset;

  /**
   * Will contain the mime encoded charset.
   */
  private String mimeCharSet;

  /**
   * Priority for the notification email.
   */
  private String priority;

  /**
   * If to fail the build if an error occurs while composing or sending the mail.
   */
  private boolean failOnError;

  /**
   * Topic to add to the mail subject. Might ease filtering mails. Set it to
   * empty string to omit a prepended topic. Topic will be added like this:
   * <pre>
   *   [&lt;Topic>] &lt;Subject>
   * </pre>
   */
  private String topic;

  /**
   * Subject to add to the email. For possibly supported tokens see the appropriate
   * goal description.
   * <pre>
   *   [&lt;Topic>] &lt;Subject>
   * </pre>
   */
  private String subject;

  /**
   * If true the mail won't be send but just logged at INFO level.
   */
  private boolean dryRun;

  public MailBase(final Log log) {
    this.log = log;
  }

  /**
   * Create the mail session.
   *
   * @return session to send mails with
   */
  private Session getSession() {
    final Properties properties = new Properties();
    properties.setProperty("mail.smtp.host", smtphost);
    properties.setProperty("mail.smtp.port", smtpport.toString());
    // Influences the Message-ID
    properties.setProperty("mail.from", from == null ? MailConstants.DEFAULT_FROM : from);
    final Session session = Session.getDefaultInstance(properties);
    session.setDebug(log.isDebugEnabled());
    return session;
  }

  private String getSignature() {
    return MailConstants.LF + MailConstants.LF + MailConstants.SIGNATURE_SEPARATOR + MailConstants.LF + fold(0, "Sent via maven-mail-plugin from " + MailConstants.USERNAME + " on " + MailConstants.HOSTNAME + " (" + MailConstants.HOSTIP + ")");
  }

  private void addHeaderInformation(final MimeMessage message) {
    MailBulk.getInstance().addHeader(message, log);
    MailExpiration.parse(expires, log).addHeader(message, log);
    MailPriority.parse(priority, log).addHeader(message, log);
  }

  /**
   * Execute the Mojo.
   *
   * @throws MojoExecutionException
   *          if an unexpected problem occurs.
   *          Throwing this exception causes a "BUILD ERROR" message to be displayed.
   * @throws MojoFailureException
   *          if an expected problem (such as a compilation failure) occurs.
   *          Throwing this exception causes a "BUILD FAILURE" message to be displayed.
   */
  public final void execute() throws MojoExecutionException, MojoFailureException {
    if (skip) {
      return;
    }
    try {
      final InternetAddress[] addresses = getRecipients();

      if (addresses.length == 0) {
        log.debug("No recipients. Skipping to send mail.");
        return;
      }

      final InternetAddress sender = getSender();
      final String text = fold(0, getPlainText());
      final String signedText = text + getSignature();
      final String subject = getSubject();
      final String topic = getTopic();
      final String completeSubject = topic == null ? subject : "[" + topic + "] " + subject;

      final Session session = getSession();
      final MimeMessage message = new MimeMessage(session);
      addHeaderInformation(message);
      try {
        message.setSentDate(new Date());
        message.addRecipients(RecipientType.TO, getRecipients());
        message.setSender(sender);
        message.setSubject(completeSubject, getMimeCharSet());
        message.setText(signedText, getMimeCharSet(), "plain");
      } catch (MessagingException e) {
        throw new MojoExecutionException("Failed to compose email message.", e);
      }
      if (dryRun) {
        log.info("maven-mail-plugin dryRun for " + this.getClass().getName() + ". Mail:\n" + message);
      } else {
        try {
          log.info("Sending mail to recipients: " + InternetAddress.toString(addresses));
          Transport.send(message);
        } catch (MessagingException e) {
          throw new MojoExecutionException("Failed to send mail.", e);
        }
      }
    } catch (MojoExecutionException e) {
      if (failOnError) {
        throw e;
      }
      log.error("failOnError deactivated. Ignoring exception.", e);
    } catch (MojoFailureException e) {
      if (failOnError) {
        throw e;
      }
      log.error("failOnError deactivated. Ignoring exception.", e);
    }
  }

  /**
   * Get the sender for the given email. Multiple configured senders are ignored.
   * If no sender is configured or parsing the sender-string fails a default sender
   * will be chosen.
   *
   * @return an address to use as sender
   * @throws MojoExecutionException if parsing the sender fails
   */
  private InternetAddress getSender() throws MojoExecutionException {
    InternetAddress[] senders;
    if (isEmpty(from)) {
      senders = getDefaultSenders();
    } else {
      try {
        senders = InternetAddress.parse(from);
      } catch (AddressException e) {
        log.warn("Could not parse sender: '" + from + "'. Using default address.", e);
        senders = getDefaultSenders();
      }
    }
    if (senders.length > 1) {
      log.warn("Multiple senders specified. Choosing only the first one. Was: " + from);
    }
    return senders[0];
  }

  /**
   * Get the default sender if no sender is configured or parsing the configured sender fails.
   *
   * @return the list of default senders (should be actually only one)
   * @throws MojoExecutionException if parsing the default mail address fails
   */
  private static InternetAddress[] getDefaultSenders() throws MojoExecutionException {
    final InternetAddress[] senders;
    try {
      senders = InternetAddress.parse(MailConstants.DEFAULT_FROM);
    } catch (AddressException e) {
      throw new MojoExecutionException("Could not parse default sender mail address " + MailConstants.DEFAULT_FROM + ".", e);
    }
    return senders;
  }

  /**
   * Return the charset in MIME-format.
   *
   * @return charset
   */
  private String getMimeCharSet() {
    if (mimeCharSet == null && charset != null) {
      mimeCharSet = mimeCharset(charset);
    }
    return mimeCharSet;
  }

  /**
   * Topic to add to the mail subject.
   *
   * @return topic to add or <code>null</code> for no topic prefix
   * @throws MojoExecutionException if an unexpected problem occurs.
   *                                Throwing this exception causes a "BUILD ERROR" message to be displayed.
   * @throws MojoFailureException   if an expected problem (such as a compilation failure) occurs.
   *                                Throwing this exception causes a "BUILD FAILURE" message to be displayed.
   */
  private String getTopic() throws MojoExecutionException, MojoFailureException {
    return topic;
  }

  /**
   * Return the subject for the email. Derived Mojos may override the default which just returns
   * the subject as configured in the Mojo.
   *
   * @return the subject of the email
   * @throws MojoExecutionException if an unexpected problem occurs.
   *                                Throwing this exception causes a "BUILD ERROR" message to be displayed.
   * @throws MojoFailureException   if an expected problem (such as a compilation failure) occurs.
   *                                Throwing this exception causes a "BUILD FAILURE" message to be displayed.
   */
  private String getSubject() throws MojoExecutionException, MojoFailureException {
    return subject;
  }

  /**
   * Get the recipients for this email. If the length of the array is <code>null</code> no mail will be sent.
   *
   * @return the list of recipients
   * @throws MojoExecutionException if an unexpected problem occurs.
   *                                Throwing this exception causes a "BUILD ERROR" message to be displayed.
   * @throws MojoFailureException   if an expected problem (such as a compilation failure) occurs.
   *                                Throwing this exception causes a "BUILD FAILURE" message to be displayed.
   */
  private InternetAddress[] getRecipients() throws MojoExecutionException, MojoFailureException {
    return recipients;
  }

  /**
   * Get the text body for this email.
   *
   * @return the text of the email
   * @throws MojoExecutionException if an unexpected problem occurs.
   *                                Throwing this exception causes a "BUILD ERROR" message to be displayed.
   * @throws MojoFailureException   if an expected problem (such as a compilation failure) occurs.
   *                                Throwing this exception causes a "BUILD FAILURE" message to be displayed.
   */
  private String getPlainText() throws MojoExecutionException, MojoFailureException {
    return plainText;
  }

  public void setRecipients(final InternetAddress[] recipients) {
    this.recipients = recipients;
  }

  public void setPlainText(final String plainText) {
    this.plainText = plainText;
  }

  public void setSkip(final boolean skip) {
    this.skip = skip;
  }

  public void setFrom(final String from) {
    this.from = from;
  }

  public void setSmtphost(final String smtphost) {
    this.smtphost = smtphost;
  }

  public void setSmtpport(final Integer smtpport) {
    this.smtpport = smtpport;
  }

  public void setExpires(final String expires) {
    this.expires = expires;
  }

  public void setCharset(final String charset) {
    this.charset = charset;
  }

  public void setPriority(final String priority) {
    this.priority = priority;
  }

  public void setFailOnError(final boolean failOnError) {
    this.failOnError = failOnError;
  }

  public void setTopic(final String topic) {
    this.topic = topic;
  }

  public void setSubject(final String subject) {
    this.subject = subject;
  }

  public void setDryRun(final boolean dryRun) {
    this.dryRun = dryRun;
  }
}
