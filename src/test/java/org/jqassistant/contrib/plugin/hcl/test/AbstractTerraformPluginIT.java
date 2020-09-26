package org.jqassistant.contrib.plugin.hcl.test;

import java.util.Map;

import org.jqassistant.contrib.plugin.hcl.model.TerraformBlock;

import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import com.buschmais.xo.api.Query.Result;
import com.buschmais.xo.api.Query.Result.CompositeRowObject;

public abstract class AbstractTerraformPluginIT extends AbstractPluginIT {
  public Map<String, String> readAllProperties(final TerraformBlock object) {
    final Result<CompositeRowObject> objectFromStore = this.store.executeQuery(
        String.format("match (n:Terraform) where ID(n)=%s return properties(n)", object.getId().toString()));

    return objectFromStore.getSingleResult().get("properties(n)", Map.class);
  }
}
