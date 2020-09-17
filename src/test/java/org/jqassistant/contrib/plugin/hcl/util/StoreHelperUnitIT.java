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
    // given
    final Map<TerraformModelField, String> givenSearchCriteria = ImmutableMap
        .of(TerraformLogicalModule.FieldName.FULL_QUALIFIED_NAME, "XXX");

    // when
    final StoreHelper s = new StoreHelper(this.store);

    final TerraformLogicalModule actualCreated = s.createOrRetrieveObject(givenSearchCriteria,
        TerraformLogicalModule.class);
    actualCreated.setFullQualifiedName("XXX");

    final TerraformLogicalModule actualFound = s.createOrRetrieveObject(givenSearchCriteria,
        TerraformLogicalModule.class);

    // then
    assertThat(actualCreated.getFullQualifiedName()).isEqualTo(actualFound.getFullQualifiedName());
  }
}
