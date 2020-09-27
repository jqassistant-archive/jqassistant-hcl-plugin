package org.jqassistant.contrib.plugin.hcl.parser.model.terraform;

import org.jqassistant.contrib.plugin.hcl.model.TerraformInputVariable;
import org.jqassistant.contrib.plugin.hcl.util.StoreHelper;

import com.buschmais.jqassistant.core.store.api.Store;
import com.google.common.collect.ImmutableMap;

public class InputVariable extends TerraformObject {
  private String defaultValue;
  private String description;
  private String name;
  private String type;
  private String validationErrorMessage;
  private String validationRule;

  public String getName() {
    return this.name;
  }

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
   * @param storeHelper helper to access the {@link Store}
   * @return <code>variable</code>
   */
  public TerraformInputVariable toStore(final StoreHelper storeHelper) {
    final TerraformInputVariable variable = storeHelper.createOrRetrieveObject(
        ImmutableMap.of(TerraformInputVariable.FieldName.NAME, this.name), TerraformInputVariable.class);

    variable.setDefault(this.defaultValue);
    variable.setDescription(this.description);
    variable.setName(this.name);
    variable.setType(this.type);
    variable.setValidationErrorMessage(this.validationErrorMessage);
    variable.setValidationRule(this.validationRule);

    return variable;
  }
}
