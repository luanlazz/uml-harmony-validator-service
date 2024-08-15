package com.inconsistency.javakafka.kafkajava.entities.uml.models._class;

import java.util.ArrayList;
import java.util.List;

import com.inconsistency.javakafka.kafkajava.entities.uml.UMLElement;

public class ClassOperation extends UMLElement {

	private OperationReturn returnType;
	private List<OperationParameter> parameters = new ArrayList<>();
	private String body;
	private boolean _abstract;
	private boolean _final;
	private String oNonConditionBody = "";
	private String condition = "";
	private String conditionTrue_Body = "";
	private String conditionFalse_Body = "";

	public void addParameter(OperationParameter parameter) {
		this.parameters.add(parameter);
	}

	public String getConditionFalse_Body() {
		return conditionFalse_Body;
	}

	public void setConditionFalse_Body(String conditionFalse_Body) {
		this.conditionFalse_Body = conditionFalse_Body;
	}

	public String getConditionTrue_Body() {
		return conditionTrue_Body;
	}

	public void setConditionTrue_Body(String conditionTrue_Body) {
		this.conditionTrue_Body = conditionTrue_Body;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getoNonConditionBody() {
		return oNonConditionBody;
	}

	public void setoNonConditionBody(String oNonConditionBody) {
		this.oNonConditionBody = oNonConditionBody;
	}

	public boolean isAbstract() {
		return _abstract;
	}

	public void setAbstract(boolean _abstract) {
		this._abstract = _abstract;
	}

	public boolean isFinal() {
		return _final;
	}

	public void setFinal(boolean _final) {
		this._final = _final;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public List<OperationParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<OperationParameter> parameters) {
		this.parameters = parameters;
	}

	public OperationReturn getReturnType() {
		return returnType;
	}

	public void setReturnType(OperationReturn returnType) {
		this.returnType = returnType;
	}

	@Override
	public String toString() {
		String output = "\n\n+Function" + super.toString() + "\n returnType: " + this.getReturnType().toString()
				+ "\n body: " + this.getBody() + "\n _abstract: " + this.isAbstract() + "\n _final: " + this.isFinal()
				+ "\n oNonConditionBody: " + this.getoNonConditionBody() + "\n condition: " + this.getCondition()
				+ "\n conditionTrue_Body: " + this.getConditionTrue_Body() + "\n conditionFalse_Body: "
				+ this.getConditionFalse_Body();

		return output;
	}
}
