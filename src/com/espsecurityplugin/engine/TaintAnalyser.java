package com.espsecurityplugin.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

/**
 * Given a file and a list of source variables, traces all the taints through the system
 * For now, only traces:
 * Assignment
 * VariableDeclarationFragment
 * 
 * @author e
 *
 */
public class TaintAnalyser extends ASTVisitor {
	
	private Collection<String> taintedVariables;

	public TaintAnalyser() {
		taintedVariables = new ArrayList<String>();
	}
	
	@Override
	public boolean visit(Assignment node) {
		if(expressionContainsTaintedVariable(node.getRightHandSide())) {
			taintedVariables.addAll(expressionToString(node.getLeftHandSide()));
		}
		return true;
	}
	
	private boolean expressionContainsTaintedVariable(Expression node) {
		if(node instanceof SimpleName) {
			return expressionContainsTaintedVariable((SimpleName) node);
		} else if (node instanceof ArrayAccess) {
			return expressionContainsTaintedVariable((ArrayAccess)node);
		} else if (node instanceof ArrayCreation) {
			return expressionContainsTaintedVariable((ArrayCreation)node);
		} else if (node instanceof CastExpression) {
			return expressionContainsTaintedVariable((CastExpression) node);
		} else if (node instanceof ClassInstanceCreation) {
			return expressionContainsTaintedVariable((ClassInstanceCreation)node);
		} else if (node instanceof ConditionalExpression) {
			return expressionContainsTaintedVariable((ConditionalExpression)node);
		} else if(node instanceof FieldAccess) {
			return expressionContainsTaintedVariable((FieldAccess)node);
		} else if(node instanceof InfixExpression) {
			return expressionContainsTaintedVariable((InfixExpression) node);
		} else if(node instanceof MethodInvocation) {
			return expressionContainsTaintedVariable((MethodInvocation) node);
		}  else if(node instanceof ParenthesizedExpression) {
			return expressionContainsTaintedVariable((ParenthesizedExpression) node);
		} else if(node instanceof PostfixExpression) {
			return expressionContainsTaintedVariable((PostfixExpression) node);
		} else if(node instanceof PrefixExpression) {
			return expressionContainsTaintedVariable((PrefixExpression) node);
		} else if(node instanceof SuperMethodInvocation) {
			return expressionContainsTaintedVariable((SuperMethodInvocation) node);
		}
		
		return false;
	}
	
	private boolean expressionContainsTaintedVariable(ArrayAccess node) {
		if(expressionContainsTaintedVariable(node.getArray())) {
			return true;
		}
		return false;
	}
	
	private boolean expressionContainsTaintedVariable(ArrayCreation node) {
		if(expressionContainsTaintedVariable(node.getInitializer())) {
			return true;
		}
		return false;
	}
	
	private boolean expressionContainsTaintedVariable(ArrayInitializer node) {
		for(Object expression : node.expressions()) {
			if(expressionContainsTaintedVariable((Expression)expression)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean expressionContainsTaintedVariable(CastExpression node) {
		return expressionContainsTaintedVariable(node.getExpression());
	}

	
	private boolean expressionContainsTaintedVariable(ClassInstanceCreation node) {
		for(Object expression : node.arguments()) {
			if(expressionContainsTaintedVariable((Expression) expression)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean expressionContainsTaintedVariable(ConditionalExpression node) {
		if(expressionContainsTaintedVariable(node.getThenExpression()) 
				|| expressionContainsTaintedVariable(node.getElseExpression())) {
			return true;
		}
		return false;
	}
	
	private boolean expressionContainsTaintedVariable(FieldAccess node) {
		if(expressionContainsTaintedVariable(node.getExpression())) {
			return true;
		}
		return false;
	}
	
	private boolean expressionContainsTaintedVariable(InfixExpression node) {
		if(expressionContainsTaintedVariable(node.getLeftOperand()) ||
				expressionContainsTaintedVariable(node.getRightOperand())) {
			return true;
		}
		return false;
	}
	
	private boolean expressionContainsTaintedVariable(MethodInvocation node) {
		for(Object expression : node.arguments()) {
			if(expressionContainsTaintedVariable((Expression) expression)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean expressionContainsTaintedVariable(ParenthesizedExpression node) {
		return expressionContainsTaintedVariable(node.getExpression());
	}
	
	private boolean expressionContainsTaintedVariable(PostfixExpression node) {
		return expressionContainsTaintedVariable(node.getOperand());
	}
	
	private boolean expressionContainsTaintedVariable(PrefixExpression node) {
		return expressionContainsTaintedVariable(node.getOperand());
	}
	
	private boolean expressionContainsTaintedVariable(SuperMethodInvocation node) {
		for(Object expression : node.arguments()) {
			if(expressionContainsTaintedVariable((Expression)expression)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean expressionContainsTaintedVariable(SimpleName node) {
		String potentiallyTaintedVariable = node.getIdentifier();
		for(String taintedVariable : taintedVariables) {
			if(taintedVariable.equals(potentiallyTaintedVariable)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Filters out the SimpleName from the AST node.
	 * 
	 * Currently not done: multiple simplenames (ie: one = two = "3";)
	 * against convention and I've never actually seen it, so not priority
	 * Done by Q4 
	 * 
	 * @return
	 */
	protected Collection<? extends String> expressionToString(Expression expression) {
		
		Collection<String> result = new Vector<String>(1); // Will 1 be most common case?

		if(expression == null) {
			return result;
		}

		if(expression instanceof ArrayAccess) {
			result.addAll(expressionToString(((ArrayAccess) expression).getArray()));
		}  else if(expression instanceof Assignment) {
			result.addAll(expressionToString(((Assignment) expression).getLeftHandSide()));
		} else if(expression instanceof CastExpression) {
			result.addAll(expressionToString(((CastExpression) expression).getExpression()));
		}else if(expression instanceof ClassInstanceCreation) {
			// TODO determine if .getExpression() needs to be analysed.can't tell looking at docs
			result.addAll(expressionToSimpleName(CastUtils.castList(Expression.class, ((ClassInstanceCreation) expression).arguments())));
		}  else if(expression instanceof ConditionalExpression) {
			// TODO what can I put in an if statement? assignment?
			result.addAll(expressionToString(((ConditionalExpression) expression).getThenExpression()));
			result.addAll(expressionToString(((ConditionalExpression) expression).getElseExpression()));
		} else if(expression instanceof MethodInvocation) {
			result.addAll(expressionToSimpleName(CastUtils.castList(Expression.class, ((MethodInvocation) expression).arguments())));
			result.addAll(expressionToString(((MethodInvocation) expression).getExpression()));
		}  else if(expression instanceof QualifiedName) {
			result.add(((QualifiedName) expression).getName().getIdentifier());
		}  else if(expression instanceof SimpleName) {
			result.add(((SimpleName)expression).getIdentifier());
		}   else if(expression instanceof ParenthesizedExpression) {
			result.addAll(expressionToString(((ParenthesizedExpression) expression).getExpression()));
		}  else if(expression instanceof PostfixExpression) {
			result.addAll(expressionToString(((PostfixExpression) expression).getOperand()));
		}  else if(expression instanceof PrefixExpression) {
			result.addAll(expressionToString(((PrefixExpression) expression).getOperand()));
		}  else if (expression instanceof SuperMethodInvocation) {
			result.addAll(expressionToSimpleName(CastUtils.castList(Expression.class, ((SuperMethodInvocation) expression).arguments())));
		} 
		
		return result;
	}

	private Collection<? extends String> expressionToSimpleName(List<Expression> arguments) {
		Vector<String> result = new Vector<String>(1);
		
		for(Object expression : arguments) {
			result.addAll(expressionToString((Expression) expression));
		}
		
		return result;
	}

	public void setTaintedVariables(Collection<String> taintedVariables) {
		this.taintedVariables = taintedVariables;
		
	}

	public Collection<String> getTaintedVariables() {
		return this.taintedVariables;
	}
}
