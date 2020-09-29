package org.jqassistant.contrib.plugin.hcl.parser.model.terraform;

import org.jqassistant.contrib.plugin.hcl.model.TerraformInputVariable;
import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.jqassistant.contrib.plugin.hcl.util.StoreHelper;

public class InputVariable extends TerraformObject<TerraformInputVariable> {
  private String defaultValue;
  private String description;
  private String internalName;
  private String type;
  private String validationErrorMessage;
  private String validationRule;

  public String getInternalName() {
    return this.internalName;
  }

  @Override
  protected TerraformInputVariable saveInternalState(final TerraformInputVariable object,
      final TerraformLogicalModule partOfModule, final StoreHelper storeHelper) {
    object.setDefault(this.defaultValue);
    object.setDescription(this.description);
    object.setInternalName(this.internalName);
    object.setType(this.type);
    object.setValidationErrorMessage(this.validationErrorMessage);
    object.setValidationRule(this.validationRule);

    return object;
  }

  public void setDefaultValue(final String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public void setInternalName(final String internalName) {
    this.internalName = internalName;
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
}
