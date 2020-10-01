package org.jqassistant.contrib.plugin.hcl.model;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/**
 * Marks an input variable in a terraform file.
 *
 * @author Matthias Kay
 * @since 1.0
 */
@Label("Module")
public interface TerraformModule extends TerraformBlock {
  enum FieldName implements TerraformModelField {
    SOURCE("source");

    private final String modelName;

    private FieldName(final String modelName) {
      this.modelName = modelName;
    }

    @Override
    public String getModelName() {
      return this.modelName;
    }
  }

  String getCount();

  @Relation("DEPENDS_ON")
  List<TerraformBlock> getDependantResources();

  String getForEach();

  String getProviders();

  String getSource();

  @Relation("IS_SOURCED_FROM")
  TerraformLogicalModule getSourcedFrom();

  String getVersion();

  void setCount(String count);

  void setForEach(String forEach);

  void setProviders(String provider);

  void setSource(String source);

  void setSourcedFrom(TerraformLogicalModule logicalModule);

  void setVersion(String version);
}
