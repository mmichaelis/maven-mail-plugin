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

import com.dumbster.smtp.SimpleSmtpServer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @since 5/28/11 9:01 PM
 */
public abstract class AbstractMailMojoTestBase {

  protected SimpleSmtpServer smtpServer;
  protected static int smtpPort;

  private static int findFreePort() throws IOException {
    final ServerSocket server = new ServerSocket(0);
    final int port;
    try {
      port = server.getLocalPort();
    } finally {
      server.close();
    }
    return port;
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
    smtpPort = findFreePort();
  }

  @Before
  public void setUp() throws Exception {
    smtpServer = SimpleSmtpServer.start(smtpPort);
  }

  @After
  public void tearDown() throws Exception {
    smtpServer.stop();
  }
}
