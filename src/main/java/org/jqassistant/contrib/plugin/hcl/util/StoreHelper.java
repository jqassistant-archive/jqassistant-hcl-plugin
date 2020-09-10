package org.jqassistant.contrib.plugin.hcl.util;

import org.jqassistant.contrib.plugin.hcl.model.TerraformBlock;

import com.buschmais.jqassistant.core.store.api.Store;

public class StoreHelper {
  private final Store store;

  public StoreHelper(final Store store) {
    this.store = store;
  };

  public <T extends TerraformBlock> T createOrRetrieveObject(final String id, final Class<T> clazz) {
    // TODO search for object with id and return it if present else create new
    // object
    return this.store.create(clazz);
  }
}
