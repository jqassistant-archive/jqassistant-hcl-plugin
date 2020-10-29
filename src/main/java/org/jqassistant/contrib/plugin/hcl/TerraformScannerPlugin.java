package org.jqassistant.contrib.plugin.hcl;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformLexer;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.FileContext;
import org.jqassistant.contrib.plugin.hcl.model.TerraformConfiguration;
import org.jqassistant.contrib.plugin.hcl.model.TerraformFileDescriptor;
import org.jqassistant.contrib.plugin.hcl.model.TerraformInputVariable;
import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.jqassistant.contrib.plugin.hcl.model.TerraformModule;
import org.jqassistant.contrib.plugin.hcl.model.TerraformOutputVariable;
import org.jqassistant.contrib.plugin.hcl.model.TerraformProvider;
import org.jqassistant.contrib.plugin.hcl.model.TerraformProviderResource;
import org.jqassistant.contrib.plugin.hcl.parser.ASTParser;
import org.jqassistant.contrib.plugin.hcl.parser.model.terraform.Configuration;
import org.jqassistant.contrib.plugin.hcl.parser.model.terraform.InputVariable;
import org.jqassistant.contrib.plugin.hcl.parser.model.terraform.LogicalModule;
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

@ScannerPlugin.Requires(FileDescriptor.class)
public class TerraformScannerPlugin extends AbstractScannerPlugin<FileResource, TerraformFileDescriptor> {
  private static final Logger logger = LoggerFactory.getLogger(TerraformScannerPlugin.class);

  @Override
  public boolean accepts(final FileResource item, final String path, final Scope scope) throws IOException {
    return path.toLowerCase().endsWith(".tf");
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
      logger.trace("File: {}", item.getFile().getAbsolutePath());

      final terraformLexer lexer = new terraformLexer(CharStreams.fromStream(item.createStream()));
      final CommonTokenStream tokens = new CommonTokenStream(lexer);
      final terraformParser parser = new terraformParser(tokens);

      final FileContext ast = parser.file();

      final ASTParser astParser = new ASTParser();
      final StoreHelper storeHelper = new StoreHelper(store);

      terraformFileDescriptor.setInternalName(item.getFile().getName());

      final Path moduleDirectory = Paths.get(path).getParent();
      final String logicalModuleName = moduleDirectory.getName(moduleDirectory.getNameCount() - 1).toString();

      // as this object does not exist in terraform, it can't be extracted from the
      // AST
      final TerraformLogicalModule currentLogicalModule = new LogicalModule(logicalModuleName).toStore(storeHelper,
          LogicalModule.calculateFullQualifiedName(Paths.get(path).getParent()), Paths.get(path), null,
          TerraformLogicalModule.class);

      terraformFileDescriptor.setModule(currentLogicalModule);

      ast.variable().forEach(inputVariableContext -> {
        final InputVariable inputVariable = astParser.extractInputVariable(inputVariableContext);

        final TerraformInputVariable terraformInputVariable = inputVariable.toStore(storeHelper,
            InputVariable.calculateFullQualifiedName(inputVariable.getName(), Paths.get(path)),
            Paths.get(path).getParent(), currentLogicalModule, TerraformInputVariable.class);

        terraformFileDescriptor.getBlocks().add(terraformInputVariable);
        currentLogicalModule.getInputVariables().add(terraformInputVariable);
      });

      ast.output().forEach(outputVariableContext -> {
        final OutputVariable outputVariable = astParser.extractOutputVariable(outputVariableContext);

        final TerraformOutputVariable terraFormOutputVariable = outputVariable.toStore(storeHelper,
            OutputVariable.calculateFullQualifiedName(outputVariable.getName(), Paths.get(path)),
            Paths.get(path).getParent(), currentLogicalModule, TerraformOutputVariable.class);

        terraformFileDescriptor.getBlocks().add(terraFormOutputVariable);
        currentLogicalModule.getOutputVariables().add(terraFormOutputVariable);
      });

      ast.module().forEach(moduleContext -> {
        final Module moduleCall = astParser.extractModuleCall(moduleContext);

        final TerraformModule terraFormModule = moduleCall.toStore(storeHelper,
            Module.calculateFullQualifiedName(moduleCall.getSource(), moduleCall.getName(), Paths.get(path)),
            Paths.get(path).getParent(), currentLogicalModule, TerraformModule.class);

        currentLogicalModule.getCalledModules().add(terraFormModule);
      });

      ast.provider().forEach(providerContext -> {
        final Provider provider = astParser.extractProvider(providerContext);

        final TerraformProvider terraformProvider = provider.toStore(storeHelper,
            Provider.calculateFullQualifiedName(provider.getName(), Paths.get(path)), Paths.get(path).getParent(),
            currentLogicalModule, TerraformProvider.class);

        currentLogicalModule.getProviders().add(terraformProvider);
      });

      ast.resource().forEach(cloudResourceContext -> {
        final ProviderResource providerResource = astParser.extractProviderResource(cloudResourceContext);
        final TerraformProviderResource terraformProviderResource = providerResource.toStore(
            storeHelper, ProviderResource.calculateFullQualifiedName(providerResource.getName(),
                providerResource.getType(), Paths.get(path)),
            Paths.get(path).getParent(), currentLogicalModule, TerraformProviderResource.class);

        currentLogicalModule.getProviderResources().add(terraformProviderResource);
      });

      ast.terraform().forEach(terraformContext -> {
        final Configuration configuration = astParser.extractConfiguration(terraformContext);
        final TerraformConfiguration terraformConfiguration = configuration.toStore(storeHelper,
            Configuration.calculateFullQualifiedName(Paths.get(path)), Paths.get(path).getParent(),
            currentLogicalModule, TerraformConfiguration.class);

        currentLogicalModule.getConfiguration().add(terraformConfiguration);
      });

      terraformFileDescriptor.setValid(true);
    } catch (final IOException e) {
      terraformFileDescriptor.setValid(false);

      logger.error(String.format("Error reading file {}", path), e);
    }

    return terraformFileDescriptor;
  }
}
