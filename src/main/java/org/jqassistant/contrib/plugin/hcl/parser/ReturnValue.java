package org.jqassistant.contrib.plugin.hcl.parser;

public class ReturnValue<T> {
  private final T value;

  public ReturnValue(final T value) {
    this.value = value;
  }

  public T get() {
    return this.value;
  }
}
