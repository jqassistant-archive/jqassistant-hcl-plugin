package org.jqassistant.contrib.plugin.hcl.parser.model.terraform;

import org.jqassistant.contrib.plugin.hcl.model.TerraformInputVariable;

public class InputVariable extends TerraformObject {
  private String defaultValue;
  private String description;
  private String name;
  private String type;
  private String validationErrorMessage;
  private String validationRule;

  public void setDefaultValue(final String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public void setValidationErrorMessage(final String validationErrorMessage) {
    this.validationErrorMessage = validationErrorMessage;
  }

  public void setValidationRule(final String validationRule) {
    this.validationRule = validationRule;
  }

  /**
   * Converts this object into a {@link TerraformInputVariable}.
   *
   * @param variable the destination object
   * @return <code>variable</code>
   */
  public TerraformInputVariable toStore(final TerraformInputVariable variable) {
    variable.setDefault(this.defaultValue);
    variable.setDescription(this.description);
    variable.setName(this.name);
    variable.setType(this.type);
    variable.setValidationErrorMessage(this.validationErrorMessage);
    variable.setValidationRule(this.validationRule);

    return variable;
  }
}
