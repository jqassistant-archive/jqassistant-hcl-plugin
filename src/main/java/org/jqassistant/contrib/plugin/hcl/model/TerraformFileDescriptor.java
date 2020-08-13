package org.jqassistant.contrib.plugin.hcl.model;

import java.util.List;

import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/**
 * Represents a Terraform file. The labels are inherited from
 * {@link TerraformDescriptor} and {@link FileDescriptor}.
 * 
 * @author Matthias Kay
 * @since 1.0
 */
public interface TerraformFileDescriptor extends TerraformDescriptor, FileDescriptor {
  @Relation("DECLARES_INPUT_VARIABLE")
  List<TerraformInputVariable> getInputVariables();

  @Relation("DECLARES_OUTPUT_VARIABLE")
  List<TerraformOutputVariable> getOutputVariables();
}