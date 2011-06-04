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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.maven.model.Developer;
import org.apache.maven.project.MavenProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Address;
import javax.mail.Message;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests {@link MailDevelopersMojo}.
 *
 * @since 5/27/11 11:26 PM
 */
public class MailDevelopersMojoTest {
  /**
   * Logger Instance.
   */
  private static final Logger LOG = LoggerFactory.getLogger(MailDevelopersMojoTest.class);

  // Actually we cannot have more than 2 developers as the third one will be wrapped and dumpster seems to be
  // unable to handle wrapped headers.
  private static final int MAX_DEVELOPERS = 4;

  private MailDevelopersMojoWrapper mojoWrapper;
  private MessageWrapper messageWrapper;
  private Developer[] developers;

  @Before
  public void setUp() throws Exception {
    developers = new Developer[MAX_DEVELOPERS];
    for (int i = 0; i < MAX_DEVELOPERS; i++) {
      final Developer developer = new Developer();
      developer.setId("id" + i);
      developer.setEmail("dev" + i + "@example.org");
      developer.setName("Deve Loper " + i);
      developers[i] = developer;
    }

    mojoWrapper = new MailDevelopersMojoWrapper(new MailDevelopersMojo());
    messageWrapper = new MessageWrapper(new de.mmichaelis.maven.mojo.Message());
    messageWrapper.setText("Lorem Ipsum Dolor Sit Amet.");
    mojoWrapper.setMessage(messageWrapper.getWrapped());
  }

  @After
  public void tearDown() throws Exception {
    Mailbox.clearAll();
  }

  @Test
  public void testMailToOneDevelopers() throws Exception {
    doTestMailToDevelopers(1);
  }

  @Test
  public void testMailToTwoDevelopers() throws Exception {
    doTestMailToDevelopers(2);
  }

  @Test
  public void testMailToThreeDevelopers() throws Exception {
    doTestMailToDevelopers(3);
  }

  @Test
  public void testMailToFourDevelopers() throws Exception {
    doTestMailToDevelopers(4);
  }

  private void doTestMailToDevelopers(final int numDevelopers) throws Exception {
    final MavenProject project = mock(MavenProject.class);
    when(project.getDevelopers()).thenReturn(Arrays.asList(Arrays.copyOf(developers, numDevelopers)));
    mojoWrapper.setProject(project);
    mojoWrapper.execute();
    for (int i = 0; i < numDevelopers; i++) {
      final Developer developer = developers[i];
      final Mailbox inbox = Mailbox.get(developer.getEmail());
      assertEquals("Should have received one email.", 1, inbox.size());
      final Message message = inbox.get(0);
      final InputStream stream = message.getInputStream();
      final int messageSize = message.getSize();
      final StringWriter writer;
      try {
        writer = new StringWriter(messageSize < 0 ? 512 : messageSize);
        IOUtils.copy(stream, writer);
      } finally {
        stream.close();
      }
      LOG.debug("Mail for one developer:\n" + writer.toString());
    }
  }

  @Test
  public void testFullyConfiguredMail() throws Exception {
    final MavenProject project = mock(MavenProject.class);
    when(project.getDevelopers()).thenReturn(Arrays.asList(developers));
    mojoWrapper.setProject(project);
    mojoWrapper.setCharset("UTF-8");
    mojoWrapper.setExpires("2");
    final String from = "John Doe <johndoe@github.com>";
    mojoWrapper.setFrom(from);
    mojoWrapper.setPriority("high");
    final String subject = "testFullyConfiguredMail";
    mojoWrapper.setSubject(subject);
    final String topic = "MyTopic";
    mojoWrapper.setTopic(topic);
    final Date now = new Date();
    mojoWrapper.execute();
    final Mailbox inbox = Mailbox.get(developers[0].getEmail());
    assertEquals("One new email for the first developer.", 1, inbox.getNewMessageCount());
    final Message message = inbox.get(0);
    assertTrue("Sent date should signal to be today.", DateUtils.isSameDay(now, message.getSentDate()));
    assertEquals("Size of recipients should match number of developers.", developers.length, message.getAllRecipients().length);
    final Address[] senders = message.getFrom();
    assertNotNull("Sender address should be set.", senders);
    assertEquals("Number of senders should be 1.", 1, senders.length);
    assertEquals("Sender in message should match original sender.", from, senders[0].toString());
    final String messageSubject = message.getSubject();
    assertTrue("Subject should contain original subject.", messageSubject.contains(subject));
    assertTrue("Subject should contain topic.", messageSubject.contains(topic));

    // TODO: Check additional headers
  }
}
