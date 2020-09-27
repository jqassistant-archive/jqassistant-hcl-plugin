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
    NAME("name");

    private final String modelName;

    private FieldName(final String modelName) {
      this.modelName = modelName;
    }

    @Override
    public String getModelName() {
      return this.modelName;
    }
  }

  String getName();

  void setName(String fileName);
}
