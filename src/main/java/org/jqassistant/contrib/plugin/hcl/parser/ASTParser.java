package org.jqassistant.contrib.plugin.hcl.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.ArgumentContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.BlockContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.DataContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.LocalContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.ModuleContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.OutputContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.ProviderContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.ResourceContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.TerraformContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.VariableContext;
import org.jqassistant.contrib.plugin.hcl.parser.PropertyParseInstruction.ResultType;
import org.jqassistant.contrib.plugin.hcl.parser.model.terraform.Configuration;
import org.jqassistant.contrib.plugin.hcl.parser.model.terraform.InputVariable;
import org.jqassistant.contrib.plugin.hcl.parser.model.terraform.LocalVariable;
import org.jqassistant.contrib.plugin.hcl.parser.model.terraform.Module;
import org.jqassistant.contrib.plugin.hcl.parser.model.terraform.OutputVariable;
import org.jqassistant.contrib.plugin.hcl.parser.model.terraform.Provider;
import org.jqassistant.contrib.plugin.hcl.parser.model.terraform.ProviderDataResource;
import org.jqassistant.contrib.plugin.hcl.parser.model.terraform.ProviderResource;
import org.jqassistant.contrib.plugin.hcl.util.StringHelper;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

/**
 * A parser for the AST generated for the HCL grammar.
 *
 * @author Matthias Kay
 * @since 1.0
 *
 */
public class ASTParser {
    private static final String TERRAFORM_FILE_INVALID_MESSAGE = "Terraform file is invalid. Please run 'terraform validate'.";

    public Configuration extractConfiguration(final TerraformContext terraformContext) {
        Preconditions.checkArgument(terraformContext.getChildCount() >= 2, TERRAFORM_FILE_INVALID_MESSAGE);

        final Configuration terraform = new Configuration();

        final Consumer<String> setBackend = s -> terraform.setBackend(StringHelper.removeQuotes(s));
        final Consumer<String> setExperiments = s -> terraform.setExperiments(StringHelper.removeQuotes(s));
        final Consumer<String> setProviderMeta = s -> terraform.setProviderMeta(StringHelper.removeQuotes(s));
        final Consumer<String> setRequiredProviders = s -> terraform.setRequiredProviders(StringHelper.removeQuotes(s));
        final Consumer<String> setRequiredVersion = s -> terraform.setRequiredVersion(StringHelper.removeQuotes(s));

        final Map<String, PropertyParseInstruction> setter = ImmutableMap.of("backend",
                new PropertyParseInstruction(ResultType.BLOCK, setBackend), "experiments",
                new PropertyParseInstruction(ResultType.STRING, setExperiments), "provider_meta",
                new PropertyParseInstruction(ResultType.BLOCK, setProviderMeta), "required_providers",
                new PropertyParseInstruction(ResultType.BLOCK, setRequiredProviders), "required_version",
                new PropertyParseInstruction(ResultType.STRING, setRequiredVersion));

        parsePropertiesRecursivlyFromBlock(setter, terraformContext.getChild(1));

        return terraform;
    }

    /**
     * Extracts the properties of an input variable.
     *
     * @param inputVariableContext Points to an input variable of the AST and is
     *                             extracted.
     * @return The {@link InputVariable} extracted from the AST.
     */
    public InputVariable extractInputVariable(final VariableContext inputVariableContext) {
        Preconditions.checkArgument(inputVariableContext.getChildCount() >= 3, TERRAFORM_FILE_INVALID_MESSAGE);

        final InputVariable inputVariable = new InputVariable();
        inputVariable.setName(StringHelper.removeQuotes(inputVariableContext.getChild(1).getText()));

        final Consumer<String> setDefault = s -> inputVariable.setDefaultValue(StringHelper.removeQuotes(s));
        final Consumer<String> setDescription = s -> inputVariable.setDescription(StringHelper.removeQuotes(s));
        final Consumer<String> setType = s -> inputVariable.setType(StringHelper.removeQuotes(s));
        final Consumer<String> setValidationErrorMessage = s -> inputVariable
                .setValidationErrorMessage(StringHelper.removeQuotes(s));
        final Consumer<String> setValidationRule = s -> inputVariable.setValidationRule(StringHelper.removeQuotes(s));

        final Map<String, PropertyParseInstruction> setter = ImmutableMap.of("default",
                new PropertyParseInstruction(ResultType.STRING, setDefault), "type",
                new PropertyParseInstruction(ResultType.STRING, setType), "description",
                new PropertyParseInstruction(ResultType.STRING, setDescription), "validation.condition",
                new PropertyParseInstruction(ResultType.STRING, setValidationRule), "validation.error_message",
                new PropertyParseInstruction(ResultType.STRING, setValidationErrorMessage));

        parsePropertiesRecursivlyFromBlock(setter, inputVariableContext.getChild(2));

        return inputVariable;
    }

    /**
     * Extracts the properties of an output variable.
     *
     * @param localVariableContext Points to an output variable of the AST and is
     *                             extracted.
     * @return The {@link OutputVariable} extracted from the AST.
     */
    public LocalVariable extractLocalVariable(final LocalContext localVariableContext) {
        Preconditions.checkArgument(localVariableContext.getChildCount() == 2, TERRAFORM_FILE_INVALID_MESSAGE);

        final LocalVariable localVariable = new LocalVariable();
        localVariable
                .setName(StringHelper.removeQuotes(localVariableContext.getChild(1).getChild(1).getChild(0).getText()));
        localVariable.setValue(
                StringHelper.removeQuotes(localVariableContext.getChild(1).getChild(1).getChild(2).getText()));

        return localVariable;
    }

    /**
     * Extracts the properties of a module call.
     *
     * @param moduleContext Points to a module definition in the AST and is
     *                      extracted.
     * @return The {@link Module} extracted from the AST.
     */
    public Module extractModuleCall(final ModuleContext moduleContext) {
        Preconditions.checkArgument(moduleContext.getChildCount() >= 3, TERRAFORM_FILE_INVALID_MESSAGE);

        final Module module = new Module();
        module.setName(StringHelper.removeQuotes(moduleContext.getChild(1).getText()));

        final Consumer<String> addDependentObject = s -> module.addDependantResource(StringHelper.removeQuotes(s));
        final Consumer<String> setCount = s -> module.setCount(StringHelper.removeQuotes(s));
        final Consumer<String> setForEach = s -> module.setForEach(StringHelper.removeQuotes(s));
        final Consumer<String> setProviders = s -> module.setProviders(StringHelper.removeQuotes(s));
        final Consumer<String> setSource = s -> module.setSource(StringHelper.removeQuotes(s));
        final Consumer<String> setVersion = s -> module.setVersion(StringHelper.removeQuotes(s));

        final Map<String, PropertyParseInstruction> setter = new HashMap<>();
        setter.put("count", new PropertyParseInstruction(ResultType.STRING, setCount));
        setter.put("depends_on", new PropertyParseInstruction(ResultType.LIST, addDependentObject));
        setter.put("for_each", new PropertyParseInstruction(ResultType.STRING, setForEach));
        setter.put("providers", new PropertyParseInstruction(ResultType.STRING, setProviders));
        setter.put("source", new PropertyParseInstruction(ResultType.STRING, setSource));
        setter.put("version", new PropertyParseInstruction(ResultType.STRING, setVersion));

        parsePropertiesRecursivlyFromBlock(setter, moduleContext.getChild(2));

        final BiConsumer<String, String> matchInputVariable = (variable, value) -> module
                .addInputVariableMapping(variable, StringHelper.removeQuotes(value));

        parseUnknownPropertiesFromBlock(setter.keySet(), matchInputVariable, moduleContext.getChild(2));

        return module;
    }

    /**
     * Extracts the properties of an output variable.
     *
     * @param outputVariableContext Points to an output variable of the AST and is
     *                              extracted.
     * @return The {@link OutputVariable} extracted from the AST.
     */
    public OutputVariable extractOutputVariable(final OutputContext outputVariableContext) {
        Preconditions.checkArgument(outputVariableContext.getChildCount() >= 3, TERRAFORM_FILE_INVALID_MESSAGE);

        final OutputVariable outputVariable = new OutputVariable();
        outputVariable.setName(StringHelper.removeQuotes(outputVariableContext.getChild(1).getText()));

        final Consumer<String> setDescription = s -> outputVariable.setDescription(StringHelper.removeQuotes(s));
        final Consumer<String> setSensitive = s -> outputVariable.setSensitive(StringHelper.removeQuotes(s));
        final Consumer<String> setValue = s -> outputVariable.setValue(StringHelper.removeQuotes(s));
        final Consumer<String> addDependentObject = s -> outputVariable.addDependentObject(s);

        final Map<String, PropertyParseInstruction> setter = ImmutableMap.of("depends_on",
                new PropertyParseInstruction(ResultType.LIST, addDependentObject), "description",
                new PropertyParseInstruction(ResultType.STRING, setDescription), "sensitive",
                new PropertyParseInstruction(ResultType.STRING, setSensitive), "value",
                new PropertyParseInstruction(ResultType.STRING, setValue));

        parsePropertiesRecursivlyFromBlock(setter, outputVariableContext.getChild(2));

        return outputVariable;
    }

    /**
     * Extracts the properties of a provider definition.
     *
     * @param providerContext Points to an output variable of the AST and is
     *                        extracted.
     * @return The {@link Provider} extracted from the AST.
     */
    public Provider extractProvider(final ProviderContext providerContext) {
        Preconditions.checkArgument(providerContext.getChildCount() >= 3, TERRAFORM_FILE_INVALID_MESSAGE);

        final Provider provider = new Provider();
        provider.setName(StringHelper.removeQuotes(providerContext.getChild(1).getText()));

        final BiConsumer<String, String> setProperty = (name, value) -> {
            provider.setProperty(StringHelper.removeQuotes(name), StringHelper.removeQuotes(value));
        };

        parseUnknownPropertiesFromBlock(Collections.emptySet(), setProperty, providerContext.getChild(2));

        return provider;
    }

    /**
     * Extracts the properties of a data resource.
     *
     * @param providerDataResourceContext Points to a data resource which is
     *                                    extracted.
     * @return The {@link ProviderDataResource} extracted from the AST.
     */
    public ProviderDataResource extractProviderDataResource(final DataContext providerDataResourceContext) {
        Preconditions.checkArgument(providerDataResourceContext.getChildCount() >= 4, TERRAFORM_FILE_INVALID_MESSAGE);

        final ProviderDataResource providerDataResource = new ProviderDataResource();
        final String resourceType = StringHelper.removeQuotes(providerDataResourceContext.getChild(1).getText());

        providerDataResource.setProviderName(extractProviderNameFromResourceType(resourceType));
        providerDataResource.setType(resourceType);
        providerDataResource.setName(StringHelper.removeQuotes(providerDataResourceContext.getChild(2).getText()));

        final BiConsumer<String, String> setProperty = (name, value) -> {
            providerDataResource.setProperty(StringHelper.removeQuotes(name), StringHelper.removeQuotes(value));
        };

        parseUnknownPropertiesFromBlock(Collections.emptySet(), setProperty, providerDataResourceContext.getChild(3));

        return providerDataResource;
    }

    private String extractProviderNameFromResourceType(final String resourceType) {
        Preconditions.checkArgument(resourceType.contains("_"), TERRAFORM_FILE_INVALID_MESSAGE);

        return resourceType.substring(0, resourceType.indexOf('_'));
    }

    /**
     * Extracts the properties of a resource.
     *
     * @param providerResourceContext Points to a resource which is extracted.
     * @return The {@link ProviderResource} extracted from the AST.
     */
    public ProviderResource extractProviderResource(final ResourceContext providerResourceContext) {
        Preconditions.checkArgument(providerResourceContext.getChildCount() >= 4, TERRAFORM_FILE_INVALID_MESSAGE);

        final ProviderResource providerResource = new ProviderResource();
        final String resourceType = StringHelper.removeQuotes(providerResourceContext.getChild(1).getText());

        providerResource.setProviderName(extractProviderNameFromResourceType(resourceType));
        providerResource.setType(resourceType);
        providerResource.setName(StringHelper.removeQuotes(providerResourceContext.getChild(2).getText()));

        // both setters overwrite the old value if more than one ingress/egress block
        // exists
        final Consumer<String> setEgress = s -> providerResource.setProperty("egress", StringHelper.removeQuotes(s));
        final Consumer<String> setIngress = s -> providerResource.setProperty("ingress", StringHelper.removeQuotes(s));

        final Map<String, PropertyParseInstruction> setter = ImmutableMap.of("egress",
                new PropertyParseInstruction(ResultType.BLOCK, setEgress), "ingress",
                new PropertyParseInstruction(ResultType.BLOCK, setIngress));

        parsePropertiesRecursivlyFromBlock(setter, providerResourceContext.getChild(3));

        final BiConsumer<String, String> setProperty = (name, value) -> {
            providerResource.setProperty(StringHelper.removeQuotes(name), StringHelper.removeQuotes(value));
        };

        parseUnknownPropertiesFromBlock(Collections.emptySet(), setProperty, providerResourceContext.getChild(3));

        return providerResource;
    }

    private void parseList(final Consumer<String> setter, final ParseTree listContext, final String blockName) {
        // skip the terminals for "[" and "]"
        for (int i = 1; i < listContext.getChildCount() - 1; i++) {
            final ParseTree property = listContext.getChild(i);

            // skip list separator ","
            if (!(property instanceof TerminalNode)) {
                setter.accept(property.getText());
            }
        }
    }

    /**
     * Extracts the properties from the <code>node</code> and stores them via a
     * <code>propertySetter</code> in an object.
     *
     * Nested blocks can be extracted by prepending the blockname and a '.' to the
     * property name, e.g. "validation.error_message" to extract the
     * <i>error_message</i> property from the nested block <i>validation</i>.
     *
     * @param propertySetter The key references the propery name and the value is
     *                       the {@link Consumer} which stores the value.
     * @param node           The BlockbodyContext from the AST.
     */
    private void parsePropertiesRecursivlyFromBlock(final Map<String, PropertyParseInstruction> propertySetter,
            final ParseTree node) {
        parsePropertiesRecursivlyFromBlock(propertySetter, node, "");
    }

    /**
     * @see #parsePropertiesRecursivlyFromBlock(Map, ParseTree)
     */
    private void parsePropertiesRecursivlyFromBlock(final Map<String, PropertyParseInstruction> propertySetter,
            final ParseTree node, final String blockName) {
        // skip the terminals for "{" and "}"
        Preconditions.checkArgument(node.getChildCount() > 2, TERRAFORM_FILE_INVALID_MESSAGE);

        for (int i = 1; i < node.getChildCount() - 1; i++) {
            final ParseTree property = node.getChild(i);

            if (property instanceof ArgumentContext) {
                // identifier = value setting
                Preconditions.checkArgument(property.getChildCount() >= 3, TERRAFORM_FILE_INVALID_MESSAGE);

                final PropertyParseInstruction propertyInstruction = propertySetter
                        .getOrDefault(blockName + property.getChild(0).getText(), PropertyParseInstruction.IGNORE);

                switch (propertyInstruction.getResultType()) {
                case LIST:
                    Preconditions.checkArgument(property.getChild(2).getChildCount() >= 1,
                            TERRAFORM_FILE_INVALID_MESSAGE);
                    Preconditions.checkArgument(property.getChild(2).getChild(0).getChildCount() >= 1,
                            TERRAFORM_FILE_INVALID_MESSAGE);

                    parseList(
                            propertySetter.getOrDefault(blockName + property.getChild(0).getText(),
                                    PropertyParseInstruction.IGNORE).getSetter(),
                            property.getChild(2).getChild(0).getChild(0), blockName);
                    break;

                case STRING:
                    propertyInstruction.setValue(property.getChild(2).getText());
                    break;

                default:
                    throw new IllegalStateException(String.format("Type of result unknown: %s",
                            propertyInstruction.getResultType().toString()));
                }

            } else if (property instanceof BlockContext) {
                // a nested block
                Preconditions.checkArgument(property.getChildCount() >= 2, TERRAFORM_FILE_INVALID_MESSAGE);
                Preconditions.checkArgument(property.getChild(0).getChildCount() >= 1, TERRAFORM_FILE_INVALID_MESSAGE);

                final PropertyParseInstruction propertyInstruction = propertySetter
                        .getOrDefault(blockName + property.getChild(0).getText(), PropertyParseInstruction.IGNORE);

                switch (propertyInstruction.getResultType()) {
                case BLOCK:
                    propertyInstruction.setValue(property.getChild(property.getChildCount() - 1).getText());
                    break;

                default:
                    parsePropertiesRecursivlyFromBlock(propertySetter, property.getChild(1),
                            blockName + property.getChild(0).getChild(0).getText() + ".");

                }
            }
        }
    }

    /**
     * Finds all properties which should not be ignored and calls the
     * {@link BiConsumer} for them.
     *
     * @param ignoreProperties these properties are ignored
     * @param propertySetter   called for each unknown property
     * @param node             The BlockbodyContext from the AST.
     */
    private void parseUnknownPropertiesFromBlock(final Set<String> ignoreProperties,
            final BiConsumer<String, String> propertySetter, final ParseTree node) {
        // skip the terminals for "{" and "}"
        Preconditions.checkArgument(node.getChildCount() > 2, TERRAFORM_FILE_INVALID_MESSAGE);

        for (int i = 1; i < node.getChildCount() - 1; i++) {
            final ParseTree property = node.getChild(i);

            if (property instanceof ArgumentContext) {
                // identifier = value setting
                Preconditions.checkArgument(property.getChildCount() >= 3, TERRAFORM_FILE_INVALID_MESSAGE);

                if (!ignoreProperties.contains(property.getChild(0).getText())) {
                    propertySetter.accept(property.getChild(0).getText(), property.getChild(2).getText());
                }
            }
        }
    }
}
