package org.jqassistant.contrib.plugin.hcl.parser.model.terraform;

import java.nio.file.Path;

import org.jqassistant.contrib.plugin.hcl.model.TerraformInputVariable;
import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.jqassistant.contrib.plugin.hcl.util.StoreHelper;

public class InputVariable extends TerraformObject<TerraformInputVariable> {
  /**
   * Calculates the full qualified name for an input variable.
   *
   * @param parentFilePath the path name of the file this module is defined in
   * @param variableName   the name of the module
   * @return A name which can be used as ID
   */
  public static String calculateFullQualifiedName(final String variableName, final Path parentFilePath) {
    return getFullQualifiedNamePrefix(parentFilePath) + "var." + variableName;
  }

  private String defaultValue;
  private String description;
  private String name;
  private String type;
  private String validationErrorMessage;

  private String validationRule;

  public String getName() {
    return this.name;
  }

  @Override
  protected TerraformInputVariable saveInternalState(final TerraformInputVariable object,
      final TerraformLogicalModule partOfModule, final Path filePath, final StoreHelper storeHelper) {
    object.setDefault(this.defaultValue);
    object.setDescription(this.description);
    object.setName(this.name);
    object.setType(this.type);
    object.setValidationErrorMessage(this.validationErrorMessage);
    object.setValidationRule(this.validationRule);
    object.setInternalName(partOfModule.getInternalName() + "." + this.name);

    return object;
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
}
