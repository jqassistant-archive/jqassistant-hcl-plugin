package org.jqassistant.contrib.plugin.hcl.util;

import java.util.Map;

import org.jqassistant.contrib.plugin.hcl.model.TerraformBlock;
import org.jqassistant.contrib.plugin.hcl.model.TerraformModelField;

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
  }

  /**
   * Adds properties to the relationship <code>relationshipName</code> from
   * <code>source</code> to <code>destination</code>.
   *
   * @param source           the source node of the relationship
   * @param destination      the destination node of the relationship
   * @param relationshipName the name of the relationship
   * @param properties       the properties to add
   */
  public void addPropertiesToRelationship(final TerraformBlock source, final TerraformBlock destination,
      final String relationshipName, final Map<String, String> properties) {
    final StringBuffer addFieldClause = new StringBuffer();
    // replace special characters
    properties.forEach(
        (field, value) -> addFieldClause.append(String.format("SET r.%s = '%s'", field, value.replace("\\", "\\\\"))));
    System.out.println(String.format("match (s:Block)-[r:%s]-(d:Block) where ID(s) = %s and ID(d) = %s %s return r",
        relationshipName, source.getId().toString(), destination.getId().toString(), addFieldClause));
    this.store
        .executeQuery(String.format("match (s:Block)-[r:%s]-(d:Block) where ID(s) = %s and ID(d) = %s %s return r",
            relationshipName, source.getId().toString(), destination.getId().toString(), addFieldClause));
  }

  /**
   * Add the property <code>name</code> with <code>value</code> to the
   * <code>object</code>.
   *
   * @param object to add the name/value to
   * @param name   property name
   * @param value  property value
   */
  public void addPropertyToObject(final TerraformBlock object, final String name, final String value) {
    this.store.executeQuery(String.format("match (s:Terraform) where ID(s) =  %s set s.%s = '%s' return s.id",
        object.getId().toString(), name, value));
  }

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
  public <T extends TerraformBlock> T createOrRetrieveObject(final Map<TerraformModelField, String> searchCriteria,
      final Class<T> clazz) {
    final Label labelAnnotation = clazz.getAnnotation(Label.class);
    final String label = labelAnnotation.value();

    final StringBuffer fieldClause = new StringBuffer("{");

    if (!searchCriteria.isEmpty()) {
      // replace special characters
      searchCriteria.forEach((field, value) -> fieldClause
          .append(String.format("%s: '%s',", field.getModelName(), value.replace("\\", "\\\\"))));
      // remove trailing ','
      fieldClause.deleteCharAt(fieldClause.length() - 1);
    }

    fieldClause.append("}");

    final Result<CompositeRowObject> storeResult = this.store
        .executeQuery(String.format("match (n:Terraform %s) where n:%s return n", fieldClause, label));

    return storeResult.hasResult() ? storeResult.getSingleResult().get("n", clazz) : this.store.create(clazz);
  };
}
