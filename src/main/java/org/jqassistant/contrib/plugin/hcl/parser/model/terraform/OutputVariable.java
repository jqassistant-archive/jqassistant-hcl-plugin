package org.jqassistant.contrib.plugin.hcl.parser.model.terraform;

import java.util.List;

import org.jqassistant.contrib.plugin.hcl.model.TerraformOutputVariable;

public class OutputVariable extends TerraformObject {
  private List<TerraformObject> dependentObjects;

  private String description;

  private String name;

  private String sensitive;

  private String value;

  public void addDependentObject(final TerraformObject object) {
    this.dependentObjects.add(object);
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

  /**
   * Converts this object into a {@link TerraformOutputVariable}.
   *
   * @param variable the destination object
   * @return <code>variable</code>
   */
  public TerraformOutputVariable toStore(final TerraformOutputVariable variable) {
    variable.setDescription(this.description);
    variable.setName(this.name);
    variable.setSensitive(this.sensitive);
    variable.setValue(this.value);

    return variable;
  }
}
