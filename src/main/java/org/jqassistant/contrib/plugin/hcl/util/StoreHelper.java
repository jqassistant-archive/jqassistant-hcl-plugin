package org.jqassistant.contrib.plugin.hcl.util;

import java.util.Map;
import java.util.stream.Stream;

import org.jqassistant.contrib.plugin.hcl.model.TerraformBlock;
import org.jqassistant.contrib.plugin.hcl.model.TerraformDescriptor;
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
      // fix name clashes
      if (name.equals("name")) {
        // the name attribute of a node is transferred to internalName
        name = TerraformDescriptor.FieldName.INTERNAL_NAME.getModelName();
      }

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

    final String query = String.format("match (n:Terraform %s) where (n:%s or n:Block) return n", fieldClause, label);
    storeResult = this.store.executeQuery(query);

    T objectInStore;

    if (storeResult.hasResult()) {
      logger.debug("Object found in database");
      objectInStore = (T) storeResult.getSingleResult().get("n", TerraformBlock.class);
    } else {
      logger.debug("Creating a new object in the database");
      objectInStore = this.store.create(clazz);
    }

    // TODO rework please. Looks ugly.
    // if the descriptor is a TerraformBlock but we requested something else -->
    // convert the descriptor
    final boolean blockNeedsConversion = !clazz.equals(TerraformBlock.class)
        && Stream.of(objectInStore.getClass().getAnnotatedInterfaces())
            .anyMatch(p -> p.getType().getTypeName().equals(TerraformBlock.class.getTypeName()));

    if (blockNeedsConversion) {
      // we read a TerraformBlock. This might happen when we did not know the correct
      // type at time of creation. We correct the descriptor now
      objectInStore = this.store.addDescriptorType(objectInStore, clazz);
    }

    final String fullQualifiedName = searchCriteria.get(TerraformDescriptor.FieldName.FULL_QUALIFIED_NAME);
    objectInStore.setFullQualifiedName(fullQualifiedName);

    return objectInStore.as(clazz);
  }
}
