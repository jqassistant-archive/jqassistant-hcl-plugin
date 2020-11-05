package org.jqassistant.contrib.plugin.hcl.parser.model.terraform;

import java.nio.file.Path;

import org.jqassistant.contrib.plugin.hcl.model.TerraformLocalVariable;
import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.jqassistant.contrib.plugin.hcl.util.StoreHelper;

public class LocalVariable extends TerraformObject<TerraformLocalVariable> {
  /**
   * Calculates the full qualified name.
   *
   * @param parentFilePath    the path name of the file this module is defined in
   * @param localVariableName the name of the output variable
   * @return A name which can be used as ID
   */
  public static String calculateFullQualifiedName(final String localVariableName, final Path parentFilePath) {
    return getFullQualifiedNamePrefix(parentFilePath) + "local." + localVariableName;
  }

  private String name;

  private String value;

  public String getName() {
    return this.name;
  }

  @Override
  protected TerraformLocalVariable saveInternalState(final TerraformLocalVariable object,
      final TerraformLogicalModule partOfModule, final Path filePath, final StoreHelper storeHelper) {
    object.setName(this.name);
    object.setValue(this.value);
    object.setFullQualifiedName(partOfModule.getFullQualifiedName() + "." + this.name);
    object.setInternalName(this.name);

    return object;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setValue(final String value) {
    this.value = value;
  }
}
