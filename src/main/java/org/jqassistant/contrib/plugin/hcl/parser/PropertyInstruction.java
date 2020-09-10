package org.jqassistant.contrib.plugin.hcl.parser;

import java.util.function.Consumer;

/**
 * The instruction how to parse a specific field from the AST.
 *
 * @author Matthias Kay
 * @since 1.0
 */
public class PropertyInstruction {
  enum ResultType {
    LIST, STRING
  }

  private static final Consumer<ReturnValue<?>> DO_NOTHING = s -> {
  };

  public static final PropertyInstruction IGNORE = new PropertyInstruction(ResultType.STRING, DO_NOTHING);

  private final ResultType resultType;

  private final Consumer<ReturnValue<?>> setter;

  public PropertyInstruction(final ResultType resultType, final Consumer<ReturnValue<?>> setter) {
    this.resultType = resultType;
    this.setter = setter;
  }

  public ResultType getResultType() {
    return this.resultType;
  }

  public Consumer<ReturnValue<?>> getSetter() {
    return this.setter;
  }

  public void setValue(final ReturnValue<?> r) {
    this.setter.accept(r);
  }
}
