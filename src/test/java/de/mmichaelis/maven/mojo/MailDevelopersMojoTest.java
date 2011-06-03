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

import com.dumbster.smtp.SmtpMessage;
import org.apache.maven.model.Developer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests {@link MailDevelopersMojo}.
 *
 * @since 5/27/11 11:26 PM
 */
public class MailDevelopersMojoTest extends AbstractMailMojoTestBase {
  /**
   * Logger Instance.
   */
  private static final Logger LOG = LoggerFactory.getLogger(MailDevelopersMojoTest.class);

  // Actually we cannot have more than 2 developers as the third one will be wrapped and dumpster seems to be
  // unable to handle wrapped headers.
  private static final int MAX_DEVELOPERS = 2;

  private MailDevelopersMojoWrapper mojoWrapper;
  private Developer[] developers;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    developers = new Developer[MAX_DEVELOPERS];
    for (int i = 0; i < MAX_DEVELOPERS; i++) {
      final Developer developer = new Developer();
      developer.setId("id" + i);
      developer.setEmail("dev" + i + "@example.org");
      developer.setName("Deve Loper " + i);
      developers[i] = developer;
    }

    mojoWrapper = new MailDevelopersMojoWrapper(new MailDevelopersMojo());

    mojoWrapper.setSmtpPort(String.valueOf(smtpPort));
  }

  @Test
  public void testMailToOneDevelopers() throws Exception {
    doTestMailToDevelopers(1);
  }

  @Test
  public void testMailToTwoDevelopers() throws Exception {
    doTestMailToDevelopers(2);
  }

  private void doTestMailToDevelopers(final int numDevelopers) throws IllegalAccessException, MojoExecutionException, MojoFailureException {
    final MavenProject project = mock(MavenProject.class);
    when(project.getDevelopers()).thenReturn(Arrays.asList(Arrays.copyOf(developers, numDevelopers)));
    mojoWrapper.setProject(project);
    final MailDevelopersMojo mojo = mojoWrapper.getMojo();
    mojo.execute();
    assertEquals("Should have received one email.", 1, smtpServer.getReceivedEmailSize());
    final SmtpMessage mail = (SmtpMessage) smtpServer.getReceivedEmail().next();
    LOG.debug("Mail for one developer:\n" + mail);
    final String tos = Arrays.toString(mail.getHeaderValues("To"));
    LOG.debug("Header(To): " + tos);
    for (int i = 0; i < numDevelopers; i++) {
      assertTrue("Mail of developer no. " + i + " should be contained in header.", tos.contains(developers[i].getEmail()));
    }
  }

  /**
   * Simple test to send an email.
   *
   * @throws Exception in case of an error
   */
/*
  @Test
  public void testSimple() throws Exception {
    mojoWrapper.execute();
    assertTrue(smtpServer.getReceivedEmailSize() == 1);
    final SmtpMessage next = (SmtpMessage) smtpServer.getReceivedEmail().next();
    System.out.println(next);
    final String[] tos = next.getHeaderValues("To");
    for (int i = 0; i < tos.length; i++) {
      String to = tos[i];
      System.out.println(to);
    }
  }
*/

}
