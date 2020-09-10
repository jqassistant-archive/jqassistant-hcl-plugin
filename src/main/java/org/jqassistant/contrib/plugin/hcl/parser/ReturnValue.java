package org.jqassistant.contrib.plugin.hcl.parser;

/**
 * Encapsulates the return value for assignments of properties in terraform
 * files.
 *
 * @author Matthias Kay
 * @since 1.0
 *
 * @param <T> The type of the assigned value, usually {@link String}
 */
public class ReturnValue<T> {
  private final T value;

  public ReturnValue(final T value) {
    this.value = value;
  }

  public T get() {
    return this.value;
  }
}
