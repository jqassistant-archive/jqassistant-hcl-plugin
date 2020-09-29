package org.jqassistant.contrib.plugin.hcl;

import java.io.IOException;
import java.nio.file.Paths;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformLexer;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.FileContext;
import org.jqassistant.contrib.plugin.hcl.model.TerraformFileDescriptor;
import org.jqassistant.contrib.plugin.hcl.model.TerraformInputVariable;
import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.jqassistant.contrib.plugin.hcl.model.TerraformModule;
import org.jqassistant.contrib.plugin.hcl.model.TerraformOutputVariable;
import org.jqassistant.contrib.plugin.hcl.model.TerraformProvider;
import org.jqassistant.contrib.plugin.hcl.model.TerraformProviderResource;
import org.jqassistant.contrib.plugin.hcl.parser.ASTParser;
import org.jqassistant.contrib.plugin.hcl.parser.model.terraform.InputVariable;
import org.jqassistant.contrib.plugin.hcl.parser.model.terraform.Module;
import org.jqassistant.contrib.plugin.hcl.parser.model.terraform.OutputVariable;
import org.jqassistant.contrib.plugin.hcl.parser.model.terraform.Provider;
import org.jqassistant.contrib.plugin.hcl.parser.model.terraform.ProviderResource;
import org.jqassistant.contrib.plugin.hcl.util.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.core.scanner.api.ScannerPlugin;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import com.google.common.collect.ImmutableMap;

@ScannerPlugin.Requires(FileDescriptor.class)
public class TerraformScannerPlugin extends AbstractScannerPlugin<FileResource, TerraformFileDescriptor> {
  private static final Logger logger = LoggerFactory.getLogger(TerraformScannerPlugin.class);

  @Override
  public boolean accepts(final FileResource item, final String path, final Scope scope) throws IOException {
    return path.toLowerCase().endsWith(".tf");
  }

  private TerraformLogicalModule createOrRetrieveModule(final String path, final StoreHelper storeHelper) {
    final String moduleName = Paths.get(path).getParent().normalize().toString().replace('\\', '/');

    final TerraformLogicalModule module = storeHelper.createOrRetrieveObject(
        ImmutableMap.of(TerraformLogicalModule.FieldName.FULL_QUALIFIED_NAME, moduleName),
        TerraformLogicalModule.class);

    module.setFullQualifiedName(moduleName);

    // identify the ROOT module
    module.setInternalName(Paths.get(path).getParent().getParent() == null ? "ROOT"
        : Paths.get(path).getParent().getFileName().toString());

    return module;
  }

  @Override
  public TerraformFileDescriptor scan(final FileResource item, final String path, final Scope scope,
      final Scanner scanner) {
    final ScannerContext context = scanner.getContext();
    final Store store = context.getStore();

    // add the file
    final FileDescriptor fileDescriptor = context.getCurrentDescriptor();
    final TerraformFileDescriptor terraformFileDescriptor = store.addDescriptorType(fileDescriptor,
        TerraformFileDescriptor.class);

    try {
      final terraformLexer lexer = new terraformLexer(CharStreams.fromStream(item.createStream()));
      final CommonTokenStream tokens = new CommonTokenStream(lexer);
      final terraformParser parser = new terraformParser(tokens);

      final FileContext ast = parser.file();

      final ASTParser astParser = new ASTParser();
      final StoreHelper storeHelper = new StoreHelper(store);

      terraformFileDescriptor.setInternalName(item.getFile().getName());

      final TerraformLogicalModule currentLogicalModule = createOrRetrieveModule(path, storeHelper);
      terraformFileDescriptor.setModule(currentLogicalModule);

      ast.variable().forEach(inputVariableContext -> {
        final InputVariable inputVariable = astParser.extractInputVariable(inputVariableContext);

        final TerraformInputVariable terraformInputVariable = inputVariable.toStore(storeHelper,
            "var." + inputVariable.getInternalName(), currentLogicalModule, TerraformInputVariable.class);

        terraformFileDescriptor.getBlocks().add(terraformInputVariable);
        currentLogicalModule.getInputVariables().add(terraformInputVariable);
      });

      ast.output().forEach(outputVariableContext -> {
        final OutputVariable outputVariable = astParser.extractOutputVariable(outputVariableContext);

        final TerraformOutputVariable terraFormOutputVariable = outputVariable.toStore(storeHelper,
            outputVariable.getName(), currentLogicalModule, TerraformOutputVariable.class);

        terraformFileDescriptor.getBlocks().add(terraFormOutputVariable);
        currentLogicalModule.getOutputVariables().add(terraFormOutputVariable);
      });

      ast.module().forEach(moduleContext -> {
        final Module moduleCall = astParser.extractModuleCall(moduleContext);

        final TerraformModule terraFormModule = moduleCall.toStore(storeHelper, moduleCall.getName(),
            currentLogicalModule, TerraformModule.class);

        currentLogicalModule.getCalledModules().add(terraFormModule);
      });

      ast.provider().forEach(providerContext -> {
        final Provider provider = astParser.extractProvider(providerContext);

        final TerraformProvider terraformProvider = provider.toStore(storeHelper, provider.getName(),
            currentLogicalModule, TerraformProvider.class);

        currentLogicalModule.getProviders().add(terraformProvider);
      });

      ast.block().forEach(cloudResourceContext -> {
        final ProviderResource providerResource = astParser.extractProviderResource(cloudResourceContext);
        final TerraformProviderResource terraformProviderResource = providerResource.toStore(storeHelper,
            providerResource.getName(), currentLogicalModule, TerraformProviderResource.class);

        currentLogicalModule.getProviderResources().add(terraformProviderResource);
      });

      terraformFileDescriptor.setValid(true);
    } catch (final IOException e) {
      terraformFileDescriptor.setValid(false);

      logger.error(String.format("Error reading file {}", path), e);
    }

    return terraformFileDescriptor;
  }

}
