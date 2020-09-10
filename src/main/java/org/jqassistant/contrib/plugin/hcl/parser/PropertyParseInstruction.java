package org.jqassistant.contrib.plugin.hcl.parser;

import java.util.function.Consumer;

/**
 * The instruction how to parse a specific field from the AST.
 *
 * @author Matthias Kay
 * @since 1.0
 */
public class PropertyParseInstruction {
  enum ResultType {
    LIST, STRING
  }

  private static final Consumer<ReturnValue<?>> DO_NOTHING = s -> {
  };

  public static final PropertyParseInstruction IGNORE = new PropertyParseInstruction(ResultType.STRING, DO_NOTHING);

  private final ResultType resultType;

  private final Consumer<ReturnValue<?>> setter;

  /**
   *
   * @param resultType To differentiate between various parsing methods
   * @param setter     A {@link Consumer} to set the value
   */
  public PropertyParseInstruction(final ResultType resultType, final Consumer<ReturnValue<?>> setter) {
    this.resultType = resultType;
    this.setter = setter;
  }

  public ResultType getResultType() {
    return this.resultType;
  }

  public Consumer<ReturnValue<?>> getSetter() {
    return this.setter;
  }

  /**
   * Invokes the {@link Consumer} to set the value of the property.
   *
   * @param r The value to set.
   */
  public void setValue(final ReturnValue<?> r) {
    this.setter.accept(r);
  }
}
