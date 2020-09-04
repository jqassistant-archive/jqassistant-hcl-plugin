package org.jqassistant.contrib.plugin.hcl.visitor;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jqassistant.contrib.plugin.hcl.model.TerraformInputVariable;

public class InputVariableParseTreeVisitor implements ParseTreeVisitor<TerraformInputVariable> {

	@Override
	public TerraformInputVariable visit(final ParseTree tree) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TerraformInputVariable visitChildren(final RuleNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TerraformInputVariable visitErrorNode(final ErrorNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TerraformInputVariable visitTerminal(final TerminalNode node) {
		System.out.println("(" + node.getText() + ")");
		return null;
	}

}
