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
package org.apache.olingo.odata2.jpa.processor.core.jpql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmMapping;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmSimpleType;
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.uri.KeyPredicate;
import org.apache.olingo.odata2.api.uri.SelectItem;
import org.apache.olingo.odata2.api.uri.info.GetEntityUriInfo;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPAModelException;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException;
import org.apache.olingo.odata2.jpa.processor.api.jpql.JPQLContext;
import org.apache.olingo.odata2.jpa.processor.api.jpql.JPQLContext.JPQLContextBuilder;
import org.apache.olingo.odata2.jpa.processor.api.jpql.JPQLContextType;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class JPQLSelectSingleStatementBuilderTest {

  /**
   * @throws java.lang.Exception
   */
  private JPQLSelectSingleStatementBuilder JPQLSelectSingleStatementBuilder;

  @Before
  public void setUp() throws Exception {

  }

  private JPQLSelectSingleContext createSelectContext() throws ODataJPARuntimeException, EdmException {
    // Object Instantiation

    JPQLSelectSingleContext JPQLSelectSingleContextImpl = null;// new JPQLSelectSingleContextImpl();
    GetEntityUriInfo getEntityView = EasyMock.createMock(GetEntityUriInfo.class);

    EdmEntitySet edmEntitySet = EasyMock.createMock(EdmEntitySet.class);
    EdmEntityType edmEntityType = EasyMock.createMock(EdmEntityType.class);
    List<SelectItem> selectItemList = null;

    // Setting up the expected value
    KeyPredicate keyPredicate = EasyMock.createMock(KeyPredicate.class);
    EdmProperty kpProperty = EasyMock.createMock(EdmProperty.class);
    EdmSimpleType edmType = EdmSimpleTypeKind.Int32.getEdmSimpleTypeInstance();
    EdmMapping edmMapping = EasyMock.createMock(EdmMapping.class);
    EasyMock.expect(edmMapping.getInternalName()).andStubReturn("Field1");
    EasyMock.expect(keyPredicate.getLiteral()).andStubReturn("1");
    try {
      EasyMock.expect(kpProperty.getName()).andStubReturn("Field1");
      EasyMock.expect(kpProperty.getType()).andStubReturn(edmType);

      EasyMock.expect(kpProperty.getMapping()).andStubReturn(edmMapping);

    } catch (EdmException e2) {
      fail("this should not happen");
    }
    EasyMock.expect(keyPredicate.getProperty()).andStubReturn(kpProperty);
    EasyMock.replay(edmMapping, kpProperty, keyPredicate);
    EasyMock.expect(getEntityView.getTargetEntitySet()).andStubReturn(edmEntitySet);
    EasyMock.expect(getEntityView.getSelect()).andStubReturn(selectItemList);

    EasyMock.expect(edmEntitySet.getEntityType()).andStubReturn(edmEntityType);
    EasyMock.replay(edmEntitySet);
    EasyMock.expect(edmEntityType.getMapping()).andStubReturn(null);
    EasyMock.expect(edmEntityType.getName()).andStubReturn("SalesOrderHeader");
    EasyMock.replay(edmEntityType);
    ArrayList<KeyPredicate> arrayList = new ArrayList<KeyPredicate>();
    arrayList.add(keyPredicate);
    EasyMock.expect(getEntityView.getKeyPredicates()).andStubReturn(arrayList);
    EasyMock.replay(getEntityView);

    JPQLContextBuilder contextBuilder1 = JPQLContext.createBuilder(JPQLContextType.SELECT_SINGLE, getEntityView);
    try {
      JPQLSelectSingleContextImpl = (JPQLSelectSingleContext) contextBuilder1.build();
    } catch (ODataJPAModelException e) {
      fail("Model Exception thrown");
    }

    return JPQLSelectSingleContextImpl;
  }

  /**
   * Test method for {@link org.apache.olingo.odata2.processor.jpa.jpql.JPQLSelectSingleStatementBuilder#build)}.
   * @throws EdmException
   * @throws ODataJPARuntimeException
   */

  @Test
  public void testBuildSimpleQuery() throws EdmException, ODataJPARuntimeException {
    JPQLSelectSingleContext JPQLSelectSingleContextImpl = createSelectContext();
    JPQLSelectSingleStatementBuilder = new JPQLSelectSingleStatementBuilder(JPQLSelectSingleContextImpl);

    assertEquals("SELECT E1 FROM SalesOrderHeader E1 WHERE E1.Field1 = 1", JPQLSelectSingleStatementBuilder.build()
        .toString());
  }

}
