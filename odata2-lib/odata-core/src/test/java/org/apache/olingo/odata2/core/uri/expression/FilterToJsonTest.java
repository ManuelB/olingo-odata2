/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/
package org.apache.olingo.odata2.core.uri.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.uri.UriParser;
import org.apache.olingo.odata2.api.uri.expression.ExceptionVisitExpression;
import org.apache.olingo.odata2.api.uri.expression.ExpressionKind;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.apache.olingo.odata2.api.uri.expression.MethodOperator;
import org.apache.olingo.odata2.api.uri.expression.UnaryOperator;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

/**
 *  
 */
public class FilterToJsonTest {

  private static final String PARAMETERS = "parameters";
  private static final String NODETYPE = "nodeType";
  private static final String OPERATOR = "operator";
  private static final String LEFT = "left";
  private static final String RIGHT = "right";
  private static final String TYPE = "type";
  private static final String VALUE = "value";
  private static final Object OPERAND = "operand";
  private static final Object NAME = "name";
  private static final Object SOURCE = "source";
  private static final Object PATH = "path";

  @SuppressWarnings("unchecked")
  @Test
  public void testToJsonBinaryProperty() throws Exception {
    FilterExpression expression = UriParser.parseFilter(null, null, "a eq b");
    String jsonString = toJson(expression);
    Gson gsonConverter = new Gson();

    LinkedTreeMap<String, Object> jsonMap = gsonConverter.fromJson(jsonString, LinkedTreeMap.class);
    checkBinary(jsonMap, "eq", null);

    LinkedTreeMap<String, Object> left = (LinkedTreeMap<String, Object>) jsonMap.get(LEFT);
    checkProperty(left, null, "a");

    LinkedTreeMap<String, Object> right = (LinkedTreeMap<String, Object>) jsonMap.get(RIGHT);
    checkProperty(right, null, "b");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testToJsonBinaryLiteral() throws Exception {
    FilterExpression expression = UriParser.parseFilter(null, null, "'a' eq 'b'");
    String jsonString = toJson(expression);
    Gson gsonConverter = new Gson();

    LinkedTreeMap<String, Object> jsonMap = gsonConverter.fromJson(jsonString, LinkedTreeMap.class);
    checkBinary(jsonMap, "eq", "Edm.Boolean");

    LinkedTreeMap<String, Object> left = (LinkedTreeMap<String, Object>) jsonMap.get(LEFT);
    checkLiteral(left, "Edm.String", "a");

    LinkedTreeMap<String, Object> right = (LinkedTreeMap<String, Object>) jsonMap.get(RIGHT);
    checkLiteral(right, "Edm.String", "b");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testToJsonBinaryAdd() throws Exception {
    FilterExpression expression = UriParser.parseFilter(null, null, "1d add 2d add 3d add 4d");
    String jsonString = toJson(expression);
    Gson gsonConverter = new Gson();

    LinkedTreeMap<String, Object> jsonMap = gsonConverter.fromJson(jsonString, LinkedTreeMap.class);
    checkBinary(jsonMap, "add", "Edm.Double");

    LinkedTreeMap<String, Object> left1 = (LinkedTreeMap<String, Object>) jsonMap.get(LEFT);
    checkBinary(left1, "add", "Edm.Double");

    LinkedTreeMap<String, Object> left2 = (LinkedTreeMap<String, Object>) left1.get(LEFT);
    checkBinary(left2, "add", "Edm.Double");

    LinkedTreeMap<String, Object> literal1 = (LinkedTreeMap<String, Object>) left2.get(LEFT);
    checkLiteral(literal1, "Edm.Double", "1");

    LinkedTreeMap<String, Object> literal2 = (LinkedTreeMap<String, Object>) left2.get(RIGHT);
    checkLiteral(literal2, "Edm.Double", "2");

    LinkedTreeMap<String, Object> literal3 = (LinkedTreeMap<String, Object>) left1.get(RIGHT);
    checkLiteral(literal3, "Edm.Double", "3");

    LinkedTreeMap<String, Object> right1 = (LinkedTreeMap<String, Object>) jsonMap.get(RIGHT);
    checkLiteral(right1, "Edm.Double", "4");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testToJsonMethod() throws Exception {
    FilterExpression expression = UriParser.parseFilter(null, null, "concat('aa','b')");
    String jsonString = toJson(expression);
    Gson gsonConverter = new Gson();

    LinkedTreeMap<String, Object> jsonMap = gsonConverter.fromJson(jsonString, LinkedTreeMap.class);
    checkMethod(jsonMap, MethodOperator.CONCAT, "Edm.String");

    List<Object> parameter = (List<Object>) jsonMap.get(PARAMETERS);
    checkLiteral((LinkedTreeMap<String, Object>) parameter.get(0), "Edm.String", "aa");
    checkLiteral((LinkedTreeMap<String, Object>) parameter.get(1), "Edm.String", "b");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testToJsonUnary() throws Exception {
    FilterExpression expression = UriParser.parseFilter(null, null, "not 'a'");
    String jsonString = toJson(expression);

    LinkedTreeMap<String, Object> jsonMap = new Gson().fromJson(jsonString, LinkedTreeMap.class);
    checkUnary(jsonMap, UnaryOperator.NOT, null);

    LinkedTreeMap<String, Object> operand = (LinkedTreeMap<String, Object>) jsonMap.get(OPERAND);
    checkLiteral(operand, "Edm.String", "a");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testToJsonMember() throws Exception {
    FilterExpression expression = UriParser.parseFilter(null, null, "Location/Country");
    String jsonString = toJson(expression);
    Gson gsonConverter = new Gson();

    LinkedTreeMap<String, Object> jsonMap = gsonConverter.fromJson(jsonString, LinkedTreeMap.class);
    checkMember(jsonMap, null);

    LinkedTreeMap<String, Object> source = (LinkedTreeMap<String, Object>) jsonMap.get(SOURCE);
    checkProperty(source, null, "Location");

    LinkedTreeMap<String, Object> path = (LinkedTreeMap<String, Object>) jsonMap.get(PATH);
    checkProperty(path, null, "Country");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testToJsonMember2() throws Exception {
    FilterExpression expression = UriParser.parseFilter(null, null, "Location/Country/PostalCode");
    String jsonString = toJson(expression);
    Gson gsonConverter = new Gson();

    LinkedTreeMap<String, Object> jsonMap = gsonConverter.fromJson(jsonString, LinkedTreeMap.class);
    checkMember(jsonMap, null);

    LinkedTreeMap<String, Object> source1 = (LinkedTreeMap<String, Object>) jsonMap.get(SOURCE);
    checkMember(source1, null);

    LinkedTreeMap<String, Object> source2 = (LinkedTreeMap<String, Object>) source1.get(SOURCE);
    checkProperty(source2, null, "Location");

    LinkedTreeMap<String, Object> path1 = (LinkedTreeMap<String, Object>) source1.get(PATH);
    checkProperty(path1, null, "Country");

    LinkedTreeMap<String, Object> path = (LinkedTreeMap<String, Object>) jsonMap.get(PATH);
    checkProperty(path, null, "PostalCode");
  }

  private void
      checkUnary(final LinkedTreeMap<String, Object> unary, final UnaryOperator expectedOperator,
          final String expectedType) {
    assertEquals(ExpressionKind.UNARY.toString(), unary.get(NODETYPE));
    assertEquals(expectedOperator.toString(), unary.get(OPERATOR));
    assertEquals(expectedType, unary.get(TYPE));
    assertNotNull(unary.get(OPERAND));
  }

  private void checkMember(final LinkedTreeMap<String, Object> member, final String expectedType) {
    assertEquals(ExpressionKind.MEMBER.toString(), member.get(NODETYPE));
    assertEquals(expectedType, member.get(TYPE));
    assertNotNull(member.get(SOURCE));
    assertNotNull(member.get(PATH));
  }

  private void checkMethod(final LinkedTreeMap<String, Object> method, final MethodOperator expectedOperator,
      final String expectedType) {
    assertEquals(ExpressionKind.METHOD.toString(), method.get(NODETYPE));
    assertEquals(expectedOperator.toString(), method.get(OPERATOR));
    assertEquals(expectedType, method.get(TYPE));
    assertNotNull(method.get(PARAMETERS));
  }

  private void checkProperty(final LinkedTreeMap<String, Object> property, final String expectedType,
      final Object expectedValue) {
    assertEquals(ExpressionKind.PROPERTY.toString(), property.get(NODETYPE));
    assertEquals(expectedValue, property.get(NAME));
    assertEquals(expectedType, property.get(TYPE));
  }

  private void
      checkLiteral(final LinkedTreeMap<String, Object> literal, final String expectedType, final Object expectedValue) {
    assertEquals(ExpressionKind.LITERAL.toString(), literal.get(NODETYPE));
    assertEquals(expectedType, literal.get(TYPE));
    assertEquals(expectedValue, literal.get(VALUE));
  }

  private void checkBinary(final LinkedTreeMap<String, Object> binary, final String expectedOperator,
      final String expectedType)
      throws Exception {
    assertEquals(ExpressionKind.BINARY.toString(), binary.get(NODETYPE));
    assertEquals(expectedOperator, binary.get(OPERATOR));
    assertEquals(expectedType, binary.get(TYPE));
    assertNotNull(binary.get(LEFT));
    assertNotNull(binary.get(RIGHT));
  }

  private static String toJson(final FilterExpression expression) throws ExceptionVisitExpression,
      ODataApplicationException {
    return (String) expression.accept(new JsonVisitor());
  }
}
