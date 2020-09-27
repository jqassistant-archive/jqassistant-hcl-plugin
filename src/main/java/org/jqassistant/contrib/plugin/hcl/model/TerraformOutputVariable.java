package org.jqassistant.contrib.plugin.hcl.model;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/**
 * Marks an output variable in a terraform file.
 *
 * @author Matthias Kay
 * @since 1.0
 */
@Label("OutputVariable")
public interface TerraformOutputVariable extends TerraformBlock {
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

  @Relation("DEPENDS_ON")
  List<TerraformBlock> getDependantObjects();

  String getDescription();

  String getName();

  String getSensitive();

  String getType();

  String getValue();

  void setDescription(String description);

  void setName(String name);

  void setSensitive(String sesitive);

  void setType(String type);

  void setValue(String value);
}
