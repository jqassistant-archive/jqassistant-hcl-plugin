package org.jqassistant.contrib.plugin.hcl.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.jqassistant.contrib.plugin.hcl.model.TerraformBlock;
import org.jqassistant.contrib.plugin.hcl.model.TerraformDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.xo.api.CompositeObject;

public class StoreHelperUnitTest {
  private static final class CompositeId implements CompositeObject {
    @Override
    public <T> T as(final Class<T> type) {
      return null;
    }

    @Override
    public <D> D getDelegate() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public <I> I getId() {
      return (I) "A";
    }

  }

  private StoreHelper storeHelper;

  @Mock
  private Store stubbedStore;

  @BeforeEach
  public void initClassUnderTest() {
    MockitoAnnotations.initMocks(this);

    this.storeHelper = new StoreHelper(this.stubbedStore);
  }

  @Test
  public void shouldPreserveTheDisplayNameAndAddAnInternalName_whenAddPropertiesToObject_givenNameAttribute() {
    // given
    final TerraformBlock stubbedTerraformBlock = Mockito.mock(TerraformBlock.class);
    when(stubbedTerraformBlock.getId()).thenReturn(new CompositeId());

    final Map<String, String> givenAdditionalProperties = new HashMap();
    givenAdditionalProperties.put("name", "XY");

    // when
    this.storeHelper.addPropertiesToObject(stubbedTerraformBlock, givenAdditionalProperties);

    // then
    final ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    Mockito.verify(this.stubbedStore).executeQuery(queryCaptor.capture());
    final String actualQuery = queryCaptor.getValue();

    assertThat(actualQuery).contains(TerraformDescriptor.FieldName.INTERNAL_NAME.getModelName());
  }
}
