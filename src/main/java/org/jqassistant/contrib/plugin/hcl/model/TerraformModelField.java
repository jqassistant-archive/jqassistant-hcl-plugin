package org.jqassistant.contrib.plugin.hcl.model;

/**
 * Interface to retrieve the name of a field in the model. This is usually
 * implemented by the enum representing all fields of a Terraform object.
 *
 * @author Matthias
 * @since 1.0
 */
public interface TerraformModelField {
  String getModelName();

}
