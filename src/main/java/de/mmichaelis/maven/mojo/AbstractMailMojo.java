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

import de.mmichaelis.maven.mojo.mail.MailBulk;
import de.mmichaelis.maven.mojo.mail.MailExpiration;
import de.mmichaelis.maven.mojo.mail.MailPriority;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Properties;

import static javax.mail.internet.MimeUtility.mimeCharset;
import static org.codehaus.plexus.util.StringUtils.isEmpty;

/**
 * Abstract Mojo for derived mail mojos.
 *
 * @since 5/27/11 11:01 PM
 */
public abstract class AbstractMailMojo extends AbstractMojo {
  protected static final String LF = "\r\n";
  private static final String HOSTNAME;
  private static final String HOSTIP;
  private static final String USERNAME = System.getProperty("user.name");
  private static final String SIGNATURE_SEPARATOR = "-- ";

  /**
   * Can be used to disable sending mails.
   *
   * @parameter default-value="false" expression="${mail.skip}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
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
   *
   * @parameter expression="${mail.from}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String from;

  /**
   * The host to send the mail from.
   *
   * @parameter default-value="localhost" expression="${mail.smtp.host}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String smtphost;

  /**
   * The host to send the mail from.
   *
   * @parameter default-value="localhost" expression="${mail.smtp.port}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String smtpport;

  /**
   * When automatic mails should expire in days.
   *
   * @parameter default-value="1" expression="${mail.expires}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String expires;

  /**
   * Charset for the emails.
   *
   * @parameter default="${project.build.outputEncoding}" expression="${mail.charset}"
   * @see Charset
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String charset;

  /**
   * Will contain the mime encoded charset.
   */
  private String mimeCharSet;

  /**
   * Priority for the notification email.
   *
   * @parameter default="low" expression="${mail.priority}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String priority;

  /**
   * If to fail the build if an error occurs while composing or sending the mail.
   *
   * @parameter default="true" expression="${mail.failOnError}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private boolean failOnError;

  /**
   * Topic to add to the mail subject. Might ease filtering mails. Set it to
   * empty string to omit a prepended topic. Topic will be added like this:
   * <pre>
   *   [&lt;Topic>] &lt;Subject>
   * </pre>
   *
   * @parameter default="maven-mail-plugin" expression="${mail.topic}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String topic;

  /**
   * Subject to add to the email. For possibly supported tokens see the appropriate
   * goal description.
   * <pre>
   *   [&lt;Topic>] &lt;Subject>
   * </pre>
   *
   * @parameter default="${project.groupId}.${project.artifactId}: Automatic Email" expression="${mail.subject}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String subject;

  /**
   * If true the mail won't be send but just logged at INFO level.
   *
   * @parameter default="false" expression="${mail.dryRun}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private boolean dryRun;

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
    // Influences the Message-ID
    properties.setProperty("mail.from", from);
    final Session session = Session.getDefaultInstance(properties);
    session.setDebug(getLog().isDebugEnabled());
    return session;
  }

  private String getSignature() {
    return LF + LF + SIGNATURE_SEPARATOR + LF + MimeUtility.fold(0, "Sent via maven-mail-plugin from " + USERNAME + " on " + HOSTNAME + " (" + HOSTIP + ")");
  }

  private void addHeaderInformation(final MimeMessage message) {
    MailBulk.getInstance().addHeader(message, getLog());
    MailExpiration.parse(expires, getLog()).addHeader(message, getLog());
    MailPriority.parse(priority, getLog()).addHeader(message, getLog());
  }

  /**
   * Execute the Mojo.
   *
   * @throws MojoExecutionException if an unexpected problem occurs.
   *                                Throwing this exception causes a "BUILD ERROR" message to be displayed.
   * @throws MojoFailureException   if an expected problem (such as a compilation failure) occurs.
   *                                Throwing this exception causes a "BUILD FAILURE" message to be displayed.
   */
  @Override
  public final void execute() throws MojoExecutionException, MojoFailureException {
    try {
      final InternetAddress[] addresses = getRecipients();

      if (addresses.length == 0) {
        getLog().debug("No recipients. Skipping to send mail.");
        return;
      }

      final InternetAddress sender = getSender();
      final String text = MimeUtility.fold(0, getPlainText());
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
        getLog().info("maven-mail-plugin dryRun for " + this.getClass().getName() + ". Mail:\n" + message);
      } else {
        try {
          getLog().info("Sending mail to recipients: " + InternetAddress.toString(addresses));
          Transport.send(message);
        } catch (MessagingException e) {
          throw new MojoExecutionException("Failed to send mail.", e);
        }
      }
    } catch (MojoExecutionException e) {
      if (failOnError) {
        throw e;
      }
      getLog().error("failOnError deactivated. Ignoring exception.", e);
    } catch (MojoFailureException e) {
      if (failOnError) {
        throw e;
      }
      getLog().error("failOnError deactivated. Ignoring exception.", e);
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
        getLog().warn("Could not parse sender: '" + from + "'. Using default address.", e);
        senders = getDefaultSenders();
      }
    }
    if (senders.length > 1) {
      getLog().warn("Multiple senders specified. Choosing only the first one. Was: " + from);
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
    final String defaultFrom = USERNAME + "@" + HOSTNAME;
    try {
      senders = InternetAddress.parse(defaultFrom);
    } catch (AddressException e) {
      throw new MojoExecutionException("Could not parse default sender mail address " + defaultFrom + ".", e);
    }
    return senders;
  }

  /**
   * Return the charset in MIME-format.
   * @return charset
   */
  protected final String getMimeCharSet() {
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
  protected String getTopic() throws MojoExecutionException, MojoFailureException {
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
  protected String getSubject() throws MojoExecutionException, MojoFailureException {
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
  protected abstract InternetAddress[] getRecipients() throws MojoExecutionException, MojoFailureException;

  /**
   * Get the text body for this email.
   *
   * @return the text of the email
   * @throws MojoExecutionException if an unexpected problem occurs.
   *                                Throwing this exception causes a "BUILD ERROR" message to be displayed.
   * @throws MojoFailureException   if an expected problem (such as a compilation failure) occurs.
   *                                Throwing this exception causes a "BUILD FAILURE" message to be displayed.
   */
  protected abstract String getPlainText() throws MojoExecutionException, MojoFailureException;

}
