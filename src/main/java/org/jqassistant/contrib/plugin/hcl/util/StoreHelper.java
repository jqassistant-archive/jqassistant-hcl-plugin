package org.jqassistant.contrib.plugin.hcl.util;

import java.util.Map;

import org.jqassistant.contrib.plugin.hcl.model.TerraformBlock;

import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.xo.api.Query.Result;
import com.buschmais.xo.api.Query.Result.CompositeRowObject;
import com.buschmais.xo.neo4j.api.annotation.Label;

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
   * @param <T>            Creates an object of this type.
   * @param searchCriteria A field name to value map
   * @param clazz          {@link Class} of the object to create/find in the
   *                       store.
   *
   * @return Either the existing object from the store or a new one.
   */
  public <T extends TerraformBlock> T createOrRetrieveObject(final Map<String, String> searchCriteria,
      final Class<T> clazz) {
    final Label labelAnnotation = clazz.getAnnotation(Label.class);
    final String label = labelAnnotation.value();

    final StringBuffer fieldClause = new StringBuffer();
    // replace special characters
    searchCriteria
        .forEach((field, value) -> fieldClause.append(String.format("%s: '%s',", field, value.replace("\\", "\\\\"))));
    // remove trailing ','
    fieldClause.deleteCharAt(fieldClause.length() - 1);

    final Result<CompositeRowObject> storeResult = this.store
        .executeQuery(String.format("match (n:Terraform {%s}) where n:%s return n;", fieldClause, label));

    return storeResult.hasResult() ? storeResult.getSingleResult().as(clazz) : this.store.create(clazz);
  }
}
