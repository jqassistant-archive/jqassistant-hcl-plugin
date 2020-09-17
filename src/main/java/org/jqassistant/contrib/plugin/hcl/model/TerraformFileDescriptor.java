package org.jqassistant.contrib.plugin.hcl.model;

import java.util.List;

import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.model.ValidDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/**
 * Represents a Terraform file. The labels are inherited from
 * {@link TerraformDescriptor} and {@link FileDescriptor}.
 *
 * @author Matthias Kay
 * @since 1.0
 */
public interface TerraformFileDescriptor extends TerraformDescriptor, FileDescriptor, ValidDescriptor {
  @Relation("REFERENCES")
  List<TerraformBlock> getBlocks();

  @Relation("IS_PART_OF_MODULE")
  TerraformLogicalModule getModule();

  void setModule(TerraformLogicalModule module);
}