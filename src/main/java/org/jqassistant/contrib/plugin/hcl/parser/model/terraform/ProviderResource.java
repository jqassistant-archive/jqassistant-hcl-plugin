package org.jqassistant.contrib.plugin.hcl.parser.model.terraform;

import java.util.HashMap;
import java.util.Map;

import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.jqassistant.contrib.plugin.hcl.model.TerraformProviderResource;
import org.jqassistant.contrib.plugin.hcl.util.StoreHelper;

public class ProviderResource extends TerraformObject<TerraformProviderResource> {
  private String name;
  private final Map<String, String> properties = new HashMap<>();

  private String providerName;
  private String type;

  public String getName() {
    return this.name;
  }

  @Override
  protected TerraformProviderResource saveInternalState(final TerraformProviderResource object,
      final TerraformLogicalModule partOfModule, final StoreHelper storeHelper) {
    object.setInternalName(this.name);
    object.setProvider(TerraformProviderResource.Provider.fromString(this.providerName));
    object.setType(this.type);

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

  public void setProviderName(final String providerName) {
    this.providerName = providerName;
  }

  public void setType(final String type) {
    this.type = type;
  }
}
