package org.jqassistant.contrib.plugin.hcl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.io.File;

import org.jqassistant.contrib.plugin.hcl.model.TerraformFileDescriptor;
import org.jqassistant.contrib.plugin.hcl.test.AbstractTerraformPluginIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;

public class TerraformScannerPluginProviderIT extends AbstractTerraformPluginIT {
  private static final String FILE_TF = "/terraform/provider/main.tf";

  @Test
  public void shouldReadAllAttributes_whenScan_givenProvider() {
    // given
    final File givenTestFile = new File(this.getClassesDirectory(TerraformScannerPluginProviderIT.class), FILE_TF);

    // when
    final TerraformFileDescriptor actualDescriptor = this.getScanner().scan(givenTestFile, FILE_TF, DefaultScope.NONE);

    // then
    assertThat(actualDescriptor.isValid()).isTrue();
    assertThat(readAllProperties(actualDescriptor.getModule().getProviders().get(0))).containsOnly(
        entry("fullQualifiedName", ".terraform.provider.google"), entry("internalName", "google"),
        entry("name", "google"), entry("project", "acme-app"), entry("region", "us-central1"));
  }

  @BeforeEach
  public void startTransaction() {
    this.store.beginTransaction();
  }
}
