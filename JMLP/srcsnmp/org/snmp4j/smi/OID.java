/*_############################################################################
  _##
  _##  SNMP4J - OID.java
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

import java.io.*;
import java.util.*;
import org.snmp4j.asn1.BER;
import org.snmp4j.asn1.BERInputStream;

/**
 * The Object Identifier Class.
 *
 * The Object Identifier (OID) class is the encapsulation of an
 * SMI object identifier. The SMI object is a data identifier for a
 * data element found in a Management Information Base (MIB), as
 * defined by a MIB definition. The <code>OID</code> class allows definition and
 * manipulation of object identifiers.
 *
 * @author Frank Fock
 * @version 1.9.1
 */
public class OID extends AbstractVariable implements AssignableFromString {

  private static final long serialVersionUID = 7521667239352941172L;

  public static final int MAX_OID_LEN = 128;
  public static final int MAX_SUBID_VALUE = 0xFFFFFFFF;

  private static final int[] NULL_OID = new int[0];

  private int[] value = NULL_OID;

  /**
   * Constructs a zero length OID.
   */
  public OID() {
  }

  /**
   * Constructs an <code>OID</code> from a dotted string. The string can contain
   * embedded strings enclosed by a single quote (') that are converted to
   * the corresponding OIO value. For example the following OID pairs are equal:
   * <pre>
   *     OID a = new OID("1.3.6.2.1.5.'hallo'.1");
   *     OID b = new OID("1.3.6.2.1.5.104.97.108.108.111.1");
   *     assertEquals(a, b);
   *     a = new OID("1.3.6.2.1.5.'hal.lo'.1");
   *     b = new OID("1.3.6.2.1.5.104.97.108.46.108.111.1");
   *     assertEquals(a, b);
   *     a = new OID("1.3.6.2.1.5.'hal.'.'''.'lo'.1");
   *     b = new OID("1.3.6.2.1.5.104.97.108.46.39.108.111.1");
   * </pre>
   * @param oid
   *    a dotted OID String, for example "1.3.6.1.2.2.1.0"
   */
  public OID(String oid) {
    value = parseDottedString(oid);
  }

  /**
   * Constructs an <code>OID</code> from an array of integer values.
   * @param rawOID
   *    an array of <code>int</code> values. The array
   *    is copied. Later changes to <code>rawOID</code> will therefore not
   *    affect the OID's value.
   */
  public OID(int[] rawOID) {
    this(rawOID, 0, rawOID.length);
  }

  /**
   * Constructs an <code>OID</code> from two arrays of integer values where
   * the first represents the OID prefix (i.e., the object class ID) and
   * the second one represents the OID suffix (i.e., the instance identifier).
   * @param prefixOID
   *    an array of <code>int</code> values. The array
   *    is copied. Later changes to <code>prefixOID</code> will therefore not
   *    affect the OID's value.
   * @param suffixOID
   *    an array of <code>int</code> values which will be appended to the
   *    <code>prefixOID</code> OID. The array is copied. Later changes to
   *    <code>suffixOID</code> will therefore not affect the OID's value.
   * @since 1.8
   */
  public OID(int[] prefixOID, int[] suffixOID) {
    this.value = new int[prefixOID.length+suffixOID.length];
    System.arraycopy(prefixOID, 0, value, 0, prefixOID.length);
    System.arraycopy(suffixOID, 0, value, prefixOID.length, suffixOID.length);
  }

  /**
   * Constructs an <code>OID</code> from an array of integer values.
   * @param rawOID
   *    an array of <code>int</code> values. The array
   *    is copied. Later changes to <code>rawOID</code> will therefore not
   *    affect the OID's value.
   * @param offset
   *    the zero based offset into the <code>rawOID</code> that points to the
   *    first sub-identifier of the new OID.
   * @param length
   *    the length of the new OID, where <code>offset + length</code> must be
   *    less or equal the length of <code>rawOID</code>. Otherwise an
   *    <code>IndexOutOfBoundsException</code> is thrown.
   */
  public OID(int[] rawOID, int offset, int length) {
    setValue(rawOID, offset, length);
  }

  /**
   * Copy constructor.
   * @param other OID
   */
  public OID(OID other) {
    this(other.getValue());
  }

  private static int[] parseDottedString(String oid) {
    StringTokenizer st = new StringTokenizer(oid, ".", true);
    int size = st.countTokens();
    int[] value = new int[size];
    size = 0;
    StringBuffer buf = null;
    while (st.hasMoreTokens()) {
      String t = st.nextToken();
      if ((buf == null) && t.startsWith("'")) {
        buf = new StringBuffer();
        t = t.substring(1);
      }
      if ((buf != null) && (t.endsWith("'"))) {
        buf.append(t.substring(0, t.length()-1));
        OID o = new OctetString(buf.toString()).toSubIndex(true);
        int[] h = value;
        value = new int[st.countTokens()+h.length+o.size()];
        System.arraycopy(h, 0, value, 0, size);
        System.arraycopy(o.getValue(), 0, value, size, o.size());
        size += o.size();
        buf = null;
      }
      else if (buf != null) {
        buf.append(t);
      }
      else if (!".".equals(t)) {
        value[size++] = (int) Long.parseLong(t);
      }
    }
    if (size < value.length) {
      int[] h = value;
      value = new int[size];
      System.arraycopy(h, 0, value, 0, size);
    }
    return value;
  }


  public final int getSyntax() {
    return SMIConstants.SYNTAX_OBJECT_IDENTIFIER;
  }

  public int hashCode() {
    int hash = 0;
    for (int i=0; i<value.length; i++) {
      hash += value[i]*31^((value.length-1)-i);
    }
    return hash;
  }

  public final boolean equals(Object o) {
    if (o instanceof OID) {
      OID other = (OID)o;
      if (other.value.length != value.length) {
        return false;
      }
      for (int i=0; i<value.length; i++) {
        if (value[i] != other.value[i]) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Returns a copy of this OID where sub-identifiers have been set to zero
   * for all n-th sub-identifier where the n-th bit of mask is zero.
   * @param mask
   *    a mask where the n-th bit corresponds to the n-th sub-identifier.
   * @return
   *    the masked OID.
   * @since 1.5
   */
  public OID mask(OctetString mask) {
    int[] masked = new int[value.length];
    System.arraycopy(value, 0, masked, 0, value.length);
    for (int i=0; (i<mask.length()*8) && (i<masked.length); i++) {
      byte b = (byte) (0x80 >> (i%8));
      if ((mask.get(i/8) & b) == 0) {
        masked[i] = 0;
      }
    }
    return new OID(masked);
  }

  public final int compareTo(Object o) {
    if (o instanceof OID) {
      OID other = (OID)o;
      int min = Math.min(value.length, other.value.length);
      int result = leftMostCompare(min, other);
      if (result == 0) {
        return (value.length - other.value.length);
      }
      return result;
    }
    throw new ClassCastException(o.getClass().getName());
  }

  public String toString() {
    StringBuffer buf = new StringBuffer(10*value.length);
    for (int i=0; i<value.length; i++) {
      if (i != 0) {
        buf.append('.');
      }
      buf.append((value[i] & 0xFFFFFFFFL));
    }
    return buf.toString();
  }

  /**
   * Returns the content of the as a byte array. This method can be used
   * to convert an index value to an <code>OctetString</code> or
   * <code>IpAddress</code> instance.
   *
   * @return
   *    the sub-identifies of this <code>OID</code> as a byte array. Each
   *    sub-identifier value is masked with 0xFF to form a byte value.
   * @since 1.2
   */
  public byte[] toByteArray() {
    byte[] b = new byte[value.length];
    for (int i=0; i<value.length; i++) {
      b[i] = (byte) (value[i] & 0xFF);
    }
    return b;
  }

  public void encodeBER(OutputStream outputStream) throws java.io.IOException {
    BER.encodeOID(outputStream, BER.OID, value);
  }

  public int getBERLength() {
    int length = 1; // for first 2 subids

    for (int i = 2; i < value.length; i++)
    {
      long v = value[i] & 0xFFFFFFFFL;

      if      (v <       0x80) { //  7 bits long subid
        length += 1;
      }
      else if (v <     0x4000) { // 14 bits long subid
        length += 2;
      }
      else if (v <   0x200000) { // 21 bits long subid
        length += 3;
      }
      else if (v < 0x10000000) { // 28 bits long subid
        length += 4;
      }
      else {                    // 32 bits long subid
        length += 5;
      }
    }
    return length + BER.getBERLengthOfLength(length) + 1;
  }

  public void decodeBER(BERInputStream inputStream) throws java.io.IOException {
    BER.MutableByte type = new BER.MutableByte();
    int[] v = BER.decodeOID(inputStream, type);
    if (type.getValue() != BER.OID) {
      throw new IOException("Wrong type encountered when decoding OID: "+
                            type.getValue());
    }
    setValue(v);
  }

  public void setValue(String value) {
    this.value = parseDottedString(value);
  }

  /**
   * Sets the value from an array of integer values.
   *
   * @param value
   *    The new value
   * @throws IllegalArgumentException
   *    if value == null.
   */
  public final void setValue(int[] value) {
    if (value == null) {
      throw new IllegalArgumentException("OID value must not be set to null");
    }
    this.value = value;
  }

  private void setValue(int[] rawOID, int offset, int length) {
    value = new int[length];
    System.arraycopy(rawOID, offset, value, 0, length);
  }

  /**
   * Gets all sub-identifiers as an int array.
   *
   * @return int arry of all sub-identifiers
   */
  public final int[] getValue() {
    return value;
  }

  /**
   * Gets the sub-identifier value at the specified position.
   * @param index
   *    a zero-based index into the <code>OID</code>.
   * @throws ArrayIndexOutOfBoundsException
   *    if the index is out of range (index < 0 || index >= size()).
   * @return
   *    the sub-indentifier value at <code>index</code>. NOTE: The returned
   *    value may be negative if the sub-identifier value is greater than
   *    <code>2^31</code>.
   */
  public final int get(int index) {
    return value[index];
  }

  /**
   * Gets the unsigned sub-identifier value at the specified position.
   * @param index int
   * @return
   *    the sub-indentifier value at <code>index</code> as an unsigned long
   *    value.
   */
  public final long getUnsigned(int index) {
    return value[index] & 0xFFFFFFFFL;
  }

  /**
   * Sets the sub-identifier at the specified position.
   * @param index
   *    a zero-based index into the <code>OID</code>.
   * @param value
   *    a 32bit unsigned integer value.
   * @throws ArrayIndexOutOfBoundsException
   *    if the index is out of range (index < 0 || index >= size()).
   */
  public final void set(int index, int value) {
    this.value[index] = value;
  }

  /**
   * Appends a dotted String OID to this <code>OID</code>.
   * @param oid
   *    a dotted String with numerical sub-identifiers.
   */
  public final void append(String oid) {
    OID suffix = new OID(oid);
    append(suffix);
  }

  /**
   * Appends an <code>OID</code> to this OID.
   * @param oid
   *    an <code>OID</code> instance.
   */
  public final void append(OID oid) {
    int[] newValue = new int[value.length+oid.value.length];
    System.arraycopy(value, 0, newValue, 0, value.length);
    System.arraycopy(oid.value, 0, newValue, value.length, oid.value.length);
    value = newValue;
  }

  /**
   * Appends a sub-identifier to this OID.
   * @param subID
   *    an integer value.
   */
  public final void append(int subID) {
    int[] newValue = new int[value.length+1];
    System.arraycopy(value, 0, newValue, 0, value.length);
    newValue[value.length] = subID;
    value = newValue;
  }

  /**
   * Appends an unsigned long sub-identifier value to this OID.
   * @param subID
   *    an unsigned long value less or equal to 2^32-1.
   * @since 1.2
   */
  public final void appendUnsigned(long subID) {
    append((int)(subID & 0xFFFFFFFFL));
  }

  /**
   * Checks whether this <code>OID</code> can be BER encoded.
   * @return
   *    <code>true</code> if size() >= 2 and size() <= 128 and if the first
   *    two sub-identifiers are less than 3 and 40 respectively.
   */
  public boolean isValid() {
    return ((size() >= 2) && (size() <= 128) &&
            ((value[0] & 0xFFFFFFFFL) <= 2l) &&
            ((value[1] & 0xFFFFFFFFL) < 40l));
  }

  /**
   * Returns the number of sub-identifiers in this <code>OID</code>.
   * @return
   *    an integer value between 0 and 128.
   */
  public final int size() {
    return value.length;
  }

  /**
   * Compares the n leftmost sub-identifiers with the given <code>OID</code>
   * in left-to-right direction.
   * @param n
   *    the number of sub-identifiers to compare.
   * @param other
   *    an <code>OID</code> to compare with this <code>OID</code>.
   * @return
   *    <UL>
   *    <LI>0 if the first <code>n</code> sub-identifiers are the same.
   *    <LI>&lt;0 if the first <code>n</code> sub-identifiers of this
   *    <code>OID</code> are lexicographic less than those of the comparand.
   *    <LI>&gt;0 if the first <code>n</code> sub-identifiers of this
   *    <code>OID</code> are lexicographic greater than those of the comparand.
   *    </UL>
   */
  public int leftMostCompare(int n, OID other) {
    for (int i=0; i<n; i++) {
      if (value[i] == other.value[i]) {
        continue;
      }
      else if ((value[i] & 0xFFFFFFFFL) <
               (other.value[i] & 0xFFFFFFFFL)) {
        return -1;
      }
      else {
        return 1;
      }
    }
    return 0;
  }

  /**
   * Compares the n rightmost sub-identifiers in direction right-to-left
   * with those of the given <code>OID</code>.
   * @param n
   *    the number of sub-identifiers to compare.
   * @param other
   *    an <code>OID</code> to compare with this <code>OID</code>.
   * @return
   *    <UL>
   *    <LI>0 if the first <code>n</code> sub-identifiers are the same.
   *    <LI>&lt;0 if the first <code>n</code> sub-identifiers of this
   *    <code>OID</code> are lexicographic less than those of the comparand.
   *    <LI>&gt;0 if the first <code>n</code> sub-identifiers of this
   *    <code>OID</code> are lexicographic greater than those of the comparand.
   *    </UL>
   */
  public int rightMostCompare(int n, OID other) {
    int cursorA = value.length-1;
    int cursorB = other.value.length-1;
    for (int i=n-1; i>=0; i--,cursorA--,cursorB--) {
      if (value[cursorA] == other.value[cursorB]) {
        continue;
      }
      else if (value[cursorA] < other.value[cursorB]) {
        return -1;
      }
      else {
        return 1;
      }
    }
    return 0;
  }

  /**
   * Check if the OID starts with the given OID.
   *
   * @param other
   *    the OID to compare to
   * @return
   *    false if the sub-identifiers do not match.
   */
  public boolean startsWith(OID other) {
    if (other.value.length > value.length) {
      return false;
    }
    int min = Math.min(value.length, other.value.length);
    return (leftMostCompare(min, other) == 0);
  }

  public Object clone() {
    return new OID(value);
  }

  /**
   * Returns the last sub-identifier as an integer value. If this OID is
   * empty (i.e. has no sub-identifiers) then a
   * <code>NoSuchElementException</code> is thrown
   * @return
   *    the value of the last sub-identifier of this OID as an integer value.
   *    Sub-identifier values greater than 2^31-1 will be returned as negative
   *    values!
   * @since 1.2
   */
  public final int last() {
    if (value.length > 0) {
      return value[value.length-1];
    }
    throw new NoSuchElementException();
  }

  /**
   * Returns the last sub-identifier as an unsigned long value. If this OID is
   * empty (i.e. has no sub-identifiers) then a
   * <code>NoSuchElementException</code> is thrown
   * @return
   *    the value of the last sub-identifier of this OID as an unsigned long.
   * @since 1.2
   */
  public final long lastUnsigned() {
    if (value.length > 0) {
      return value[value.length-1] & 0xFFFFFFFFL;
    }
    throw new NoSuchElementException();
  }

  /**
   * Removes the last sub-identifier (if available) from this <code>OID</code>
   * and returns it.
   * @return
   *    the last sub-identifier or -1 if there is no sub-identifier left in
   *    this <code>OID</code>.
   */
  public int removeLast() {
    if (value.length == 0) {
      return -1;
    }
    int[] newValue = new int[value.length-1];
    System.arraycopy(value, 0, newValue, 0, value.length-1);
    int retValue = value[value.length-1];
    value = newValue;
    return retValue;
  }

  /**
   * Remove the n rightmost subidentifiers from this OID.
   * @param n
   *    the number of subidentifiers to remove. If <code>n</code> is zero or
   *    negative then this OID will not be changed. If <code>n</code> is greater
   *    than {@link #size()} all subidentifiers will be removed from this OID.
   */
  public void trim(int n) {
    if (n > 0) {
      if (n > value.length) {
        n = value.length;
      }
      int[] newValue = new int[value.length-n];
      System.arraycopy(value, 0, newValue, 0, value.length-n);
      value = newValue;
    }
  }

  public int toInt() {
    throw new UnsupportedOperationException();
  }

  public long toLong() {
    throw new UnsupportedOperationException();
  }

  public final OID toSubIndex(boolean impliedLength) {
    if (impliedLength) {
      return new OID(value);
    }
    OID subIndex = new OID(new int[] { size() });
    subIndex.append(this);
    return subIndex;
  }

  public final void fromSubIndex(OID subIndex, boolean impliedLength) {
    int offset = 1;
    if (impliedLength) {
      offset = 0;
    }
    setValue(subIndex.getValue(), offset, subIndex.size()-offset);
  }

  /**
   * Returns the successor OID for this OID.
   * @return
   *    an OID clone of this OID with a zero sub-identifier appended.
   * @since 1.7
   */
  public final OID successor() {
    if (value.length == MAX_OID_LEN) {
      for (int i=MAX_OID_LEN-1; i>=0; i--) {
        if (value[i] != MAX_SUBID_VALUE) {
          int[] succ = new int[i+1];
          System.arraycopy(value, 0, succ, 0, i+1);
          succ[i]++;
          return new OID(succ);
        }
      }
      return new OID();
    }
    else {
      int[] succ = new int[value.length + 1];
      System.arraycopy(value, 0, succ, 0, value.length);
      succ[value.length] = 0;
      return new OID(succ);
    }
  }

  /**
   * Returns the predecessor OID for this OID.
   * @return
   *    if this OID ends on 0, then a {@link #MAX_OID_LEN}
   *    sub-identifier OID is returned where each sub-ID for index greater
   *    or equal to {@link #size()} is set to {@link #MAX_SUBID_VALUE}.
   * @since 1.7
   */
  public final OID predecessor() {
    if (last() != 0) {
      int[] pval = new int[MAX_OID_LEN];
      System.arraycopy(value, 0, pval, 0, value.length);
      Arrays.fill(pval, value.length, pval.length, MAX_SUBID_VALUE);
      OID pred = new OID(pval);
      pred.set(size()-1, last()-1);
      return pred;
    }
    else {
      OID pred = new OID(this);
      pred.removeLast();
      return pred;
    }
  }

  /**
   * Returns the next following OID with the same or lesser size (length).
   * @return OID
   *    the next OID on the same or upper level or a clone of this OID, if
   *    it has a zero length or is 2^32-1.
   * @since 1.7
   */
  public final OID nextPeer() {
    OID next = new OID(this);
    if ((next.size() > 0) && (last() != MAX_SUBID_VALUE)) {
      next.set(next.size()-1, last()+1);
    }
    else if (next.size() > 1) {
      next.trim(1);
      next = nextPeer();
    }
    return next;
  }

  /**
   * Returns the greater of the two OID values.
   * @param a
   *    an OID.
   * @param b
   *    an OID.
   * @return
   *    <code>a</code> if a &gt;= b, <code>b</code> otherwise.
   * @since 1.7
   */
  public static final OID max(OID a, OID b) {
    if (a.compareTo(b) >= 0) {
      return a;
    }
    return b;
  }

  /**
   * Returns the lesser of the two OID values.
   * @param a
   *    an OID.
   * @param b
   *    an OID.
   * @return
   *    <code>a</code> if a &lt;= b, <code>b</code> otherwise.
   * @since 1.7
   */
  public static final OID min(OID a, OID b) {
    if (a.compareTo(b) <= 0) {
      return a;
    }
    return b;
  }

}

