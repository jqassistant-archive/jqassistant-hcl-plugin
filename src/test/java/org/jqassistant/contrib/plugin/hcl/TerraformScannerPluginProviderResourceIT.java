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

public class TerraformScannerPluginProviderResourceIT extends AbstractTerraformPluginIT {
  private static final String FILE_TF = "/terraform/provider resource/main.tf";

  @Test
  public void shouldReadAllAttributePerResource_whenScan_givenProviderResource() {
    // given
    final File givenTestFile = new File(this.getClassesDirectory(TerraformScannerPluginProviderResourceIT.class),
        FILE_TF);

    // when
    final TerraformFileDescriptor actualDescriptor = this.getScanner().scan(givenTestFile, FILE_TF, DefaultScope.NONE);

    // then
    assertThat(actualDescriptor.isValid()).isTrue();

    assertThat(actualDescriptor.getModule().getProviderResources())
        .filteredOn(pr -> "aws_instance".equals(pr.getType())).hasSize(1).first().satisfies(pr -> {
          final Map<String, String> actualProperties = readAllProperties(pr);

          assertThat(actualProperties).containsOnly(entry("ami", "data.aws_ami.ami.id"),
              entry("instance_type", "t2.micro"), entry("provider", "AWS"),
              entry("security_groups", "[aws_security_group.server.name]"), entry("tags", "{Name=\"my server\"}"),
              entry("type", "aws_instance"), entry("internalName", "server"),
              entry("fullQualifiedName", ".terraform.aws_instance.server"));
        });
  }

  @Test
  public void shouldReadAllResources_whenScan_givenProviderResources() {
    // given
    final File givenTestFile = new File(this.getClassesDirectory(TerraformScannerPluginProviderResourceIT.class),
        FILE_TF);

    // when
    final TerraformFileDescriptor actualDescriptor = this.getScanner().scan(givenTestFile, FILE_TF, DefaultScope.NONE);

    // then
    assertThat(actualDescriptor.isValid()).isTrue();
    assertThat(actualDescriptor.getModule().getProviderResources()).hasSize(4);
  }

  @BeforeEach
  public void startTransaction() {
    this.store.beginTransaction();
  }
}
