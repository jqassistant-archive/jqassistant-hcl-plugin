package org.jqassistant.contrib.plugin.hcl.parser.model.terraform;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.jqassistant.contrib.plugin.hcl.model.TerraformBlock;
import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.jqassistant.contrib.plugin.hcl.model.TerraformOutputVariable;
import org.jqassistant.contrib.plugin.hcl.util.StoreHelper;

import com.google.common.collect.ImmutableMap;

public class OutputVariable extends TerraformObject<TerraformOutputVariable> {
  /**
   * Calculates the full qualified name for an output variable.
   *
   * @param parentFilePath     the path name of the file this module is defined in
   * @param outputVariableName the name of the output variable
   * @return A name which can be used as ID
   */
  public static String calculateFullQualifiedName(final String outputVariableName, final Path parentFilePath) {
    return getFullQualifiedNamePrefix(parentFilePath) + "output." + outputVariableName;
  }

  private final List<String> dependentObjects = new ArrayList<String>();

  private String description;

  private String name;

  private String sensitive;

  private String value;

  public void addDependentObject(final String object) {
    this.dependentObjects.add(object);
  }

  public String getName() {
    return this.name;
  }

  @Override
  protected TerraformOutputVariable saveInternalState(final TerraformOutputVariable object,
      final TerraformLogicalModule partOfModule, final Path filePath, final StoreHelper storeHelper) {
    object.setDescription(this.description);
    object.setName(this.name);
    object.setSensitive(this.sensitive);
    object.setValue(this.value);
    object.setFullQualifiedName(partOfModule.getFullQualifiedName() + "." + this.name);
    object.setInternalName(this.name);

    this.dependentObjects.forEach(dependentObjectName -> {
      final TerraformBlock block = storeHelper.createOrRetrieveObject(
          ImmutableMap.of(TerraformBlock.FieldName.FULL_QUALIFIED_NAME,
              TerraformObject.getFullQualifiedNamePrefix(filePath) + dependentObjectName),
          partOfModule, TerraformBlock.class);
      block.setFullQualifiedName(dependentObjectName);

      object.getDependantObjects().add(block);
    });

    return object;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setSensitive(final String sensitive) {
    this.sensitive = sensitive;
  }

  public void setValue(final String value) {
    this.value = value;
  }
}
