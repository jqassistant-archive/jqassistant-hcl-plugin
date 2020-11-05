package org.jqassistant.contrib.plugin.hcl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.jqassistant.contrib.plugin.hcl.model.TerraformConfiguration;
import org.jqassistant.contrib.plugin.hcl.model.TerraformFileDescriptor;
import org.jqassistant.contrib.plugin.hcl.test.AbstractTerraformPluginIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;

public class TerraformScannerPluginConfigurationIT extends AbstractTerraformPluginIT {
  private static final String FILE_TF = "/terraform/configuration/terraform.tf";

  @Test
  public void shouldReadAllAttributes_whenScan_givenConfiguration() {
    // given
    final File givenTestFile = new File(this.getClassesDirectory(TerraformScannerPluginConfigurationIT.class), FILE_TF);

    // when
    final TerraformFileDescriptor actualDescriptor = this.getScanner().scan(givenTestFile, FILE_TF, DefaultScope.NONE);

    // then
    assertThat(actualDescriptor.isValid()).isTrue();

    final TerraformConfiguration actualConfiguration = actualDescriptor.getModule().getConfiguration().get(0);

    assertThat(actualConfiguration.getBackend())
        .isEqualTo("{bucket=\"mybucket\"key=\"path/to/my/key\"region=\"us-east-1\"}");
    assertThat(actualConfiguration.getExperiments()).isEqualTo("[example]");
    assertThat(actualConfiguration.getProviderMeta()).isEqualTo("{hello=\"world\"}");
    assertThat(actualConfiguration.getRequiredProviders())
        .isEqualTo("{aws={version=\">= 2.7.0\"source=\"hashicorp/aws\"}}");
    assertThat(actualConfiguration.getRequiredVersion()).isEqualTo(">=1.2.0");
    assertThat(actualConfiguration.getName()).isEqualTo("terraform");
    assertThat(actualConfiguration.getInternalName()).isEqualTo("terraform");
  }

  @BeforeEach
  public void startTransaction() {
    this.store.beginTransaction();
  }
}
