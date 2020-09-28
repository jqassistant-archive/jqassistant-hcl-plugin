package org.jqassistant.contrib.plugin.hcl.parser.model.terraform;

import java.util.HashMap;
import java.util.Map;

import org.jqassistant.contrib.plugin.hcl.model.TerraformDescriptor;
import org.jqassistant.contrib.plugin.hcl.model.TerraformProviderResource;
import org.jqassistant.contrib.plugin.hcl.util.StoreHelper;

import com.buschmais.jqassistant.core.store.api.Store;
import com.google.common.collect.ImmutableMap;

public class ProviderResource extends TerraformObject {
  private String name;
  private final Map<String, String> properties = new HashMap<>();

  private String providerName;
  private String type;

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

  /**
   * Converts this object into a {@link TerraformProviderResource} and puts it
   * into the store.
   *
   * @param storeHelper helper to access the {@link Store}
   *
   * @return the created {@link TerraformProviderResource}
   */
  public TerraformProviderResource toStore(final StoreHelper storeHelper) {
    final TerraformProviderResource providerResource = storeHelper.createOrRetrieveObject(
        ImmutableMap.of(TerraformDescriptor.FieldName.NAME, this.name), TerraformProviderResource.class);
    providerResource.setName(this.name);
    providerResource.setProvider(TerraformProviderResource.Provider.fromString(this.providerName));
    providerResource.setType(this.type);

    storeHelper.addPropertiesToObject(providerResource, this.properties);

    return providerResource;
  }
}
