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

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @since 6/7/11 10:21 PM
 */
public class MailConstants {
  public static final String LF = "\r\n";
  public static final String HOSTNAME;
  public static final String HOSTIP;
  public static final String DEFAULT_FROM;
  public static final String USERNAME = System.getProperty("user.name");
  public static final String SIGNATURE_SEPARATOR = "-- ";

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

    DEFAULT_FROM = USERNAME + "@" + HOSTNAME;
  }

}
