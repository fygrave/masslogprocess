/*_############################################################################
  _## 
  _##  SNMP4J - TransportIpAddress.java  
  _## 
  _##  Copyright (C) 2003-2008  Frank Fock and Jochen Katz (SNMP4J.org)
  _##  
  _##  Licensed under the Apache License, Version 2.0 (the "License");
  _##  you may not use this file except in compliance with the License.
  _##  You may obtain a copy of the License at
  _##  
  _##      http://www.apache.org/licenses/LICENSE-2.0
  _##  
  _##  Unless required by applicable law or agreed to in writing, software
  _##  distributed under the License is distributed on an "AS IS" BASIS,
  _##  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  _##  See the License for the specific language governing permissions and
  _##  limitations under the License.
  _##  
  _##########################################################################*/



package org.snmp4j.smi;

import java.util.StringTokenizer;
import java.io.IOException;
import org.snmp4j.asn1.BERInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.snmp4j.log.LogFactory;
import org.snmp4j.log.LogAdapter;

/**
 * The <code>TransportIpAddress</code> is the abstract base class for all
 * transport addresses on top of IP network addresses.
 *
 * @author Frank Fock
 * @version 1.5
 */
public abstract class TransportIpAddress extends IpAddress {

  private static final LogAdapter logger =
      LogFactory.getLogger(TransportIpAddress.class);

  static final long serialVersionUID = 695596530250216972L;

  protected int port = 0;

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    if ((port < 0) || (port > 65535)) {
      throw new IllegalArgumentException("Illegal port specified: " + port);
    }
    this.port = port;
  }

  public boolean isValid() {
    return super.isValid() && (port >= 0) && (port <= 65535);
  }

  public int compareTo(Object o) {
    int result = super.compareTo(o);
    if (result == 0) {
      return (port - ((TransportIpAddress) o).getPort());
    }
    return result;
  }

  public boolean equals(Object o) {
    if (o instanceof TransportIpAddress) {
      return (super.equals(o) && ((TransportIpAddress)o).getPort() == port);
    }
    return false;
  }

  public boolean parseAddress(String address) {
    try {
      StringTokenizer st = new StringTokenizer(address, "/");
      String addr = st.nextToken();
      String port = st.nextToken();
      if (super.parseAddress(addr)) {
        this.port = Integer.parseInt(port);
        return true;
      }
      return false;
    }
    catch (Exception ex) {
      return false;
    }
  }

  public static Address parse(String address) {
    try {
      UdpAddress a = new UdpAddress();
      if (a.parseAddress(address)) {
        return a;
      }
    }
    catch (Exception ex) {
    }
    return null;
  }

  public String toString() {
    return super.toString()+"/"+port;
  }

  public int hashCode() {
    return super.hashCode()^2 + port;
  }

  /**
   * Sets this transport address from an OcetString containing the address
   * value in format as specified by the TRANSPORT-ADDRESS-MIB.
   * @param transportAddress
   *    an OctetString containing the IP address bytes and the two port bytes
   *    in network byte order.
   * @throws UnknownHostException
   *    if the address is invalid.
   */
  public void setTransportAddress(OctetString transportAddress) throws
      UnknownHostException {
    OctetString inetAddr =
        transportAddress.substring(0, transportAddress.length()-2);
    setInetAddress(InetAddress.getByAddress(inetAddr.getValue()));
    port = ((transportAddress.get(transportAddress.length()-2) & 0xFF) << 8);
    port += (transportAddress.get(transportAddress.length()-1) & 0xFF);
  }

  /**
   * Returns the address value as a byte array.
   * @return
   *    a byte array with IP address bytes and two additional bytes containing
   *    the port in network byte order.
   * @since 1.5
   */
  public byte[] getValue() {
    byte[] addr = getInetAddress().getAddress();
    byte[] retval = new byte[addr.length+2];
    System.arraycopy(addr, 0, retval, 0, addr.length);
    retval[addr.length] = (byte)((port >> 8) & 0xFF);
    retval[addr.length+1] = (byte)(port & 0xFF);
    return retval;
  }

  public void decodeBER(BERInputStream inputStream) throws IOException {
    OctetString os = new OctetString();
    os.decodeBER(inputStream);
    try {
      setTransportAddress(os);
    }
    catch (Exception ex) {
      String txt = "Wrong encoding of TransportAddress";
      logger.error(txt);
      throw new IOException(txt+": "+ex.getMessage());
    }
  }

  public void encodeBER(OutputStream outputStream) throws IOException {
    OctetString os = new OctetString(getValue());
    os.encodeBER(outputStream);
  }

  public int getBERLength() {
    return getInetAddress().getAddress().length + 2;
  }

  public int getBERPayloadLength() {
    return getBERLength();
  }

  public int getSyntax() {
    return SMIConstants.SYNTAX_OCTET_STRING;
  }

}
