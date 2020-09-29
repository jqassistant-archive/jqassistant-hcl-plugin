package org.jqassistant.contrib.plugin.hcl.parser.model.terraform;

import java.util.HashMap;
import java.util.Map;

import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.jqassistant.contrib.plugin.hcl.model.TerraformProvider;
import org.jqassistant.contrib.plugin.hcl.util.StoreHelper;

public class Provider extends TerraformObject<TerraformProvider> {
  private String name;

  Map<String, String> properties = new HashMap<>();

  public String getName() {
    return this.name;
  }

  @Override
  protected TerraformProvider saveInternalState(final TerraformProvider object,
      final TerraformLogicalModule partOfModule, final StoreHelper storeHelper) {
    object.setInternalName(this.name);

    storeHelper.addPropertiesToObject(object, this.properties);

    return object;
  }

  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Stores the value of a named property.
   *
   * @param name  name of the property
   * @param value value of the property
   */
  public void setProperty(final String name, final String value) {
    this.properties.put(name, value);
  }
}
