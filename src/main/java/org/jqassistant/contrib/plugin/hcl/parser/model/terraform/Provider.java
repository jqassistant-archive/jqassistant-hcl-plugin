package org.jqassistant.contrib.plugin.hcl.parser.model.terraform;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jqassistant.contrib.plugin.hcl.model.TerraformProvider;
import org.jqassistant.contrib.plugin.hcl.util.StoreHelper;

import com.buschmais.jqassistant.core.store.api.Store;

public class Provider extends TerraformObject {
  private String name;

  Map<String, String> properties = new HashMap<>();

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

  /**
   * Converts this object into a {@link TerraformProvider} and puts it into the
   * store.
   *
   * @param storeHelper helper to access the {@link Store}
   *
   * @return the created {@link TerraformProvider}
   */
  public TerraformProvider toStore(final StoreHelper storeHelper) {
    final TerraformProvider provider = storeHelper.createOrRetrieveObject(Collections.emptyMap(),
        TerraformProvider.class);
    provider.setName(this.name);

    storeHelper.addPropertiesToObject(provider, this.properties);

    return provider;
  }
}
