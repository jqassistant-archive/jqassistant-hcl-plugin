package org.jqassistant.contrib.plugin.hcl.parser;

import java.util.Map;
import java.util.function.Consumer;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.ArgumentContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.BlockContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.OutputContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.VariableContext;
import org.jqassistant.contrib.plugin.hcl.parser.PropertyParseInstruction.ResultType;
import org.jqassistant.contrib.plugin.hcl.parser.model.terraform.InputVariable;
import org.jqassistant.contrib.plugin.hcl.parser.model.terraform.OutputVariable;
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
          Preconditions.checkArgument(property.getChild(2).getChildCount() >= 1, TERRAFORM_FILE_INVALID_MESSAGE);
          Preconditions.checkArgument(property.getChild(2).getChild(0).getChildCount() >= 1,
              TERRAFORM_FILE_INVALID_MESSAGE);

          parseList(propertySetter
              .getOrDefault(blockName + property.getChild(0).getText(), PropertyParseInstruction.IGNORE).getSetter(),
              property.getChild(2).getChild(0).getChild(0), blockName);
          break;

        case STRING:
          propertyInstruction.setValue(property.getChild(2).getText());
          break;

        default:
          throw new IllegalStateException(
              String.format("Type of result unknown: %s", propertyInstruction.getResultType().toString()));
        }

      } else if (property instanceof BlockContext) {
        // a nested block
        Preconditions.checkArgument(property.getChildCount() >= 2, TERRAFORM_FILE_INVALID_MESSAGE);
        Preconditions.checkArgument(property.getChild(0).getChildCount() >= 1, TERRAFORM_FILE_INVALID_MESSAGE);

        parsePropertiesRecursivlyFromBlock(propertySetter, property.getChild(1),
            blockName + property.getChild(0).getChild(0).getText() + ".");
      }
    }
  }
}
