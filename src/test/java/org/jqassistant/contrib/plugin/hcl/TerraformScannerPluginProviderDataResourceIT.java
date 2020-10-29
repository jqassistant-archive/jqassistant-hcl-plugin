package org.jqassistant.contrib.plugin.hcl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.io.File;
import java.util.Map;

import org.jqassistant.contrib.plugin.hcl.model.TerraformFileDescriptor;
import org.jqassistant.contrib.plugin.hcl.test.AbstractTerraformPluginIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;

public class TerraformScannerPluginProviderDataResourceIT extends AbstractTerraformPluginIT {
  private static final String FILE_TF = "/terraform/provider data resource/main.tf";

  @Test
  public void shouldReadAllAttributeFromDataResource_whenScan_givenProviderDataResource() {
    // given
    final File givenTestFile = new File(this.getClassesDirectory(TerraformScannerPluginProviderDataResourceIT.class),
        FILE_TF);

    // when
    final TerraformFileDescriptor actualDescriptor = this.getScanner().scan(givenTestFile, FILE_TF, DefaultScope.NONE);

    // then
    assertThat(actualDescriptor.isValid()).isTrue();

    assertThat(actualDescriptor.getModule().getProviderDataResources()).filteredOn(pr -> "aws_ami".equals(pr.getType()))
        .hasSize(1).first().satisfies(pr -> {
          final Map<String, String> actualProperties = readAllProperties(pr);

          assertThat(actualProperties).containsOnly(entry("most_recent", "true"), entry("name_regex", "my-ami"),
              entry("owners", "[\"self\"]"), entry("provider", "aws"), entry("type", "aws_ami"),
              entry("fullQualifiedName", ".terraform.aws_ami.ami"), entry("internalName", "ami"));
        });
  }

  @BeforeEach
  public void startTransaction() {
    this.store.beginTransaction();
  }
}
