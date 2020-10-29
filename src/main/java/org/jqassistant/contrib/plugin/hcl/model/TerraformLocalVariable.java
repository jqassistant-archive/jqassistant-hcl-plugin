package org.jqassistant.contrib.plugin.hcl.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Marks a local variable in a terraform file.
 *
 * @author Matthias Kay
 * @since 1.0
 */
@Label("LocalVariable")
public interface TerraformLocalVariable extends TerraformBlock {
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

  String getValue();

  void setName(String name);

  void setValue(String value);
}
