package org.jqassistant.contrib.plugin.hcl.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Defines the label which is shared by all nodes representing the terraform
 * structure.
 *
 * @author Matthias Kay
 * @since 1.0
 */
@Label("Terraform")
public interface TerraformDescriptor extends Descriptor {
  enum FieldName implements TerraformModelField {
    /**
     * An identical name within the whole project.
     */
    FULL_QUALIFIED_NAME("fullQualifiedName"),
    /**
     * The terraform name, e.g. name of a resource or variable. This is not the name
     * property of some resources!
     */
    INTERNAL_NAME("internal_name");

    private final String modelName;

    private FieldName(final String modelName) {
      this.modelName = modelName;
    }

    @Override
    public String getModelName() {
      return this.modelName;
    }
  }

  String getFullQualifiedName();

  /**
   * @return The name attribute of the node from the terraform code.
   */
  String getInternalName();

  /**
   * @return The name which is shown by default when browsing the database.
   */
  String getName();

  void setFullQualifiedName(String fullQualifiedName);

  void setInternalName(String name);

  void setName(String name);
}
