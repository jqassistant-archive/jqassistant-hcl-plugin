package org.jqassistant.contrib.plugin.hcl.model.internal;

import java.util.List;

public class OutputVariable extends TerraformObject {
  private List<TerraformObject> dependantObjects;

  private String description;

  private String name;

  private String sensitive;

  private String type;

  private String value;

  public void setDescription(final String description) {
    this.description = description;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setSensitive(final String sensitive) {
    this.sensitive = sensitive;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public void setValue(final String value) {
    this.value = value;
  }
}
