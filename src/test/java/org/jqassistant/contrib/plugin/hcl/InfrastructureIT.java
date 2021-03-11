package org.jqassistant.contrib.plugin.hcl;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.buschmais.jqassistant.core.rule.api.model.RuleException;
import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;

/**
 * Dummy class which can be used to run tests against "real" infrastructure code
 * which is not included in this repository. Just a helper. Put your sources in
 * src/test/resources/infrastructure and go for it.
 */
public class InfrastructureIT extends AbstractPluginIT {
  private static final String INFRASTRUCTURE_DIRECTORY = "/infrastructure/";

  @Test
  public void willAnalyzeTheWholeInfrastructureCode() throws RuleException {
    // given
    final File directoryToScan = new File(this.getClassesDirectory(TerraformScannerPluginIT.class),
        INFRASTRUCTURE_DIRECTORY);

    // when
    this.getScanner().scan(directoryToScan, INFRASTRUCTURE_DIRECTORY, DefaultScope.NONE);

    // then
    assertTrue("Don't forget to ignore this dummy test again!", false);
  }
}
