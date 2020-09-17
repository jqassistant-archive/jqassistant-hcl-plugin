package org.jqassistant.contrib.plugin.hcl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.jqassistant.contrib.plugin.hcl.model.TerraformFileDescriptor;
import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import com.buschmais.xo.api.Query.Result;
import com.buschmais.xo.api.Query.Result.CompositeRowObject;

public class TerraformScannerPluginIT extends AbstractPluginIT {
  private static final String FILE_TEST_TF = "/terraform/main.tf";

  @BeforeEach
  public void beginTransaction() {
    this.store.beginTransaction();
  }

  @Test
  public void shouldScanTerraformFile() {
    final File testFile = new File(this.getClassesDirectory(TerraformScannerPluginIT.class), FILE_TEST_TF);

    final TerraformFileDescriptor descriptor = this.getScanner().scan(testFile, FILE_TEST_TF, DefaultScope.NONE);

    assertThat(descriptor.isValid()).isTrue();
  }

  @Test
  public void testCreateAndFind() {
    final TerraformLogicalModule module = this.store.create(TerraformLogicalModule.class);
    module.setName("X");

    final Result<CompositeRowObject> executeQuery = this.store
        .executeQuery("match (n:Terraform {name: 'X'}) where n:LogicalModule return n");

    assertThat(executeQuery.hasResult()).isTrue();
  }
}
