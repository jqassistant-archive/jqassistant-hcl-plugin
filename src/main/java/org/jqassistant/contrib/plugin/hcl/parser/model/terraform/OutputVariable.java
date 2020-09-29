package org.jqassistant.contrib.plugin.hcl.parser.model.terraform;

import java.util.ArrayList;
import java.util.List;

import org.jqassistant.contrib.plugin.hcl.model.TerraformBlock;
import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.jqassistant.contrib.plugin.hcl.model.TerraformOutputVariable;
import org.jqassistant.contrib.plugin.hcl.util.StoreHelper;

import com.google.common.collect.ImmutableMap;

public class OutputVariable extends TerraformObject<TerraformOutputVariable> {
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
      final TerraformLogicalModule partOfModule, final StoreHelper storeHelper) {
    object.setDescription(this.description);
    object.setName(this.name);
    object.setSensitive(this.sensitive);
    object.setValue(this.value);

    this.dependentObjects.forEach(dependentObjectName -> {
      final TerraformBlock block = storeHelper.createOrRetrieveObject(
          ImmutableMap.of(TerraformBlock.FieldName.FULL_QUALIFIED_NAME, dependentObjectName), partOfModule,
          TerraformBlock.class);
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
