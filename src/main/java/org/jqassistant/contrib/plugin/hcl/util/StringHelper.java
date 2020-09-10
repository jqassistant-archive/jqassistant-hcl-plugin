package org.jqassistant.contrib.plugin.hcl.util;

public class StringHelper {
  /**
   * Removes double quotes at the start and the end of the string if present.
   *
   * @param s string to sanitize
   * @return <code>s</code> without the leading and trailing double quotes
   */
  public final static String removeQuotes(final String s) {
    final StringBuffer sb = new StringBuffer(s);

    // remove leading double quote
    if (sb.length() > 0) {
      if (sb.charAt(0) == '"') {
        sb.deleteCharAt(0);
      }
    }

    // remove trailing double quote
    if (sb.length() > 0) {
      final int lastIndex = sb.length() - 1;

      if (sb.charAt(lastIndex) == '"') {
        sb.deleteCharAt(lastIndex);
      }
    }

    return sb.toString();
  }
}
