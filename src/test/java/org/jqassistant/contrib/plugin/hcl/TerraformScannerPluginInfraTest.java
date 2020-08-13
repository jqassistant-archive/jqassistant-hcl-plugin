package org.jqassistant.contrib.plugin.hcl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.jqassistant.contrib.plugin.hcl.model.TerraformFileDescriptor;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;

public class TerraformScannerPluginInfraTest extends AbstractPluginIT {
  private static final String FILE_TEST_TF = "/terraform/file.tf";

  @Before
  public void beginTransaction() {
    store.beginTransaction();
  }

  @After
  public void rollbackTransaction() {
    store.rollbackTransaction();
  }

  @Test
  public void shouldScanTerraformFile() {
    File testFile = new File(getClassesDirectory(TerraformScannerPluginInfraTest.class), FILE_TEST_TF);

    Descriptor descriptor = getScanner().scan(testFile, FILE_TEST_TF, DefaultScope.NONE);
    assertThat(descriptor).isInstanceOf(TerraformFileDescriptor.class);
  }
}
