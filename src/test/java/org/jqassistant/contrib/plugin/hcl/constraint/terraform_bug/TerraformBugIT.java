package org.jqassistant.contrib.plugin.hcl.constraint.terraform_bug;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.jqassistant.contrib.plugin.hcl.TerraformScannerPluginIT;
import org.jqassistant.contrib.plugin.hcl.model.TerraformBlock;
import org.junit.jupiter.api.Test;

import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.report.api.model.Result.Status;
import com.buschmais.jqassistant.core.rule.api.model.Constraint;
import com.buschmais.jqassistant.core.rule.api.model.RuleException;
import com.buschmais.jqassistant.core.rule.api.model.Severity;
import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;

public class TerraformBugIT extends AbstractPluginIT {
    private static final String EGRESS_TF = "/constraint/terraform-bug/egress.tf";
    private static final String INGRESS_TF = "/constraint/terraform-bug/ingress.tf";

    @Test
    public void shouldFindEgressBlocksInSecurityGroups() throws RuleException {
        // given
        final File testFile = new File(this.getClassesDirectory(TerraformScannerPluginIT.class), EGRESS_TF);
        this.getScanner().scan(testFile, EGRESS_TF, DefaultScope.NONE);

        // when
        final Result<Constraint> actualConstraint = validateConstraint("hcl:terraform-bug:egress");

        // then
        assertThat(actualConstraint.getSeverity()).isEqualTo(Severity.MAJOR);
        assertThat(actualConstraint.getStatus()).isEqualTo(Status.FAILURE);

        this.store.beginTransaction();

        assertThat(actualConstraint.getRows()).hasSize(1);

        final TerraformBlock actualInvalidObject = (TerraformBlock) actualConstraint.getRows().get(0).get("n");
        assertThat(actualInvalidObject.getInternalName()).isEqualTo("test_egress");

        this.store.rollbackTransaction();
    }

    @Test
    public void shouldFindIngressBlocksInSecurityGroups() throws RuleException {
        // given
        final File testFile = new File(this.getClassesDirectory(TerraformScannerPluginIT.class), INGRESS_TF);
        this.getScanner().scan(testFile, INGRESS_TF, DefaultScope.NONE);

        // when
        final Result<Constraint> actualConstraint = validateConstraint("hcl:terraform-bug:ingress");

        // then
        assertThat(actualConstraint.getSeverity()).isEqualTo(Severity.MAJOR);
        assertThat(actualConstraint.getStatus()).isEqualTo(Status.FAILURE);

        this.store.beginTransaction();

        assertThat(actualConstraint.getRows()).hasSize(1);

        final TerraformBlock actualInvalidObject = (TerraformBlock) actualConstraint.getRows().get(0).get("n");
        assertThat(actualInvalidObject.getInternalName()).isEqualTo("test_ingress");

        this.store.rollbackTransaction();
    }
}
