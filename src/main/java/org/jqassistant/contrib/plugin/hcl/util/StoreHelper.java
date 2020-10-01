package org.jqassistant.contrib.plugin.hcl.util;

import java.util.Map;

import org.jqassistant.contrib.plugin.hcl.model.TerraformBlock;
import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.jqassistant.contrib.plugin.hcl.model.TerraformModelField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private static final Logger logger = LoggerFactory.getLogger(StoreHelper.class);

  private final Store store;

  public StoreHelper(final Store store) {
    this.store = store;
  }

  /**
   * Add the property <code>name</code> with <code>value</code> to the
   * <code>object</code>.
   *
   * @param object     to add the name/value to
   * @param properties The properties to add
   */
  public void addPropertiesToObject(final TerraformBlock object, final Map<String, String> properties) {
    properties.forEach((name, value) -> {
      this.store.executeQuery(String.format("match (s:Terraform) where ID(s) =  %s set s.%s = '%s' return s.id",
          object.getId().toString(), name, value));
    });
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
    return createOrRetrieveObject(searchCriteria, null, clazz);
  };

  /**
   * Retrieves the object with <code>id</code> from the store or creates a new
   * object if it does not exist.
   *
   * @param <T>            Creates an object of this type.
   * @param searchCriteria A field name to value map
   * @param partOfModule   the existing object must belong to this module
   * @param clazz          {@link Class} of the object to create/find in the
   *                       store.
   *
   * @return Either the existing object from the store or a new one.
   */
  public <T extends TerraformBlock> T createOrRetrieveObject(final Map<TerraformModelField, String> searchCriteria,
      final TerraformLogicalModule partOfModule, final Class<T> clazz) {
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

    Result<CompositeRowObject> storeResult;

    logger.trace("Query database for object: {}, {}", clazz.getSimpleName(), searchCriteria);

    if (partOfModule == null) {
      final String query = String.format("match (n:Terraform %s) where n:%s return n", fieldClause, label);
      storeResult = this.store.executeQuery(query);
    } else {
      final String query = String.format("match (n:Terraform %s)-[*]-(m:LogicalModule {%s: '%s'}) where n:%s return n",
          fieldClause, TerraformLogicalModule.FieldName.FULL_QUALIFIED_NAME.getModelName(),
          partOfModule.getFullQualifiedName(), label);
      storeResult = this.store.executeQuery(query);
    }

    if (storeResult.hasResult()) {
      logger.trace("Object found in database");
      return storeResult.getSingleResult().get("n", clazz);
    } else {
      logger.trace("Creating a new object in the database");
      return this.store.create(clazz);
    }
  }
}
