package org.jqassistant.contrib.plugin.hcl.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.jqassistant.contrib.plugin.hcl.model.TerraformModelField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import com.google.common.collect.ImmutableMap;

public class StoreHelperUnitIT extends AbstractPluginIT {
  @BeforeEach
  public void beginTransaction() {
    this.store.beginTransaction();
  }

  @Test
  public void shouldReturnSameObject_whenCreateOrRetrieveObject_givenObjectExists() {
    final StoreHelper s = new StoreHelper(this.store);

    final Map<TerraformModelField, String> searchCriteria = ImmutableMap
        .of(TerraformLogicalModule.FieldName.FULL_QUALIFIED_NAME, "XXX");

    final TerraformLogicalModule created = s.createOrRetrieveObject(searchCriteria, TerraformLogicalModule.class);
    created.setFullQualifiedName("XXX");

    final TerraformLogicalModule found = s.createOrRetrieveObject(searchCriteria, TerraformLogicalModule.class);

    assertThat(created.getFullQualifiedName()).isEqualTo(found.getFullQualifiedName());
  }
}
