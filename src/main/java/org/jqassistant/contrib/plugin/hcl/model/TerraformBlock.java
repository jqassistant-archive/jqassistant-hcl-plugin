package org.jqassistant.contrib.plugin.hcl.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Identifies a block in the terraform file.
 *
 * @author Matthias Kay
 * @since 1.0
 */
@Label("Block")
public interface TerraformBlock extends TerraformDescriptor {
  enum FieldName implements TerraformModelField {
    FULL_QUALIFIED_NAME("fullQualifiedName");

    private final String modelName;

    private FieldName(final String modelName) {
      this.modelName = modelName;
    }

    @Override
    public String getModelName() {
      return this.modelName;
    }
  }

  public String getFullQualifiedName();

  public void setFullQualifiedName(String fullQualifiedName);
}
