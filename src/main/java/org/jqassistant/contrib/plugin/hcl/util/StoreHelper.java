package org.jqassistant.contrib.plugin.hcl.util;

import org.jqassistant.contrib.plugin.hcl.model.TerraformBlock;

import com.buschmais.jqassistant.core.store.api.Store;

/**
 * Some useful helper methods to access the object store.
 *
 * @author Matthias Kay
 * @since 1.0
 */
public class StoreHelper {
  private final Store store;

  public StoreHelper(final Store store) {
    this.store = store;
  };

  /**
   * Retrieves the object with <code>id</code> from the store or creates a new
   * object if it does not exist.
   *
   * @param <T>
   * @param id    Used to find the object in the store.
   * @param clazz {@link Class} of the object to create.
   *
   * @return Either the existing object from the store or a new one.
   */
  public <T extends TerraformBlock> T createOrRetrieveObject(final String id, final Class<T> clazz) {
    // TODO search for object with id and return it if present else create new
    // object
    final T object = this.store.create(clazz);
    object.setTerraformId(id);

    return object;
  }
}
