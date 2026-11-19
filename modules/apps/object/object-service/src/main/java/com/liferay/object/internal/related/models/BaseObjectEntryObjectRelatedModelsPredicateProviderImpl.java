/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.related.models;

import com.liferay.object.internal.entry.util.ObjectEntrySearchUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionLocalizationTable;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTable;
import com.liferay.object.related.models.ObjectRelatedModelsPredicateProvider;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.odata.filter.InvalidFilterException;

import java.util.Collection;

/**
 * @author Luis Miguel Barcos
 */
public abstract class BaseObjectEntryObjectRelatedModelsPredicateProviderImpl
	implements ObjectRelatedModelsPredicateProvider {

	public BaseObjectEntryObjectRelatedModelsPredicateProviderImpl(
		ObjectDefinition objectDefinition,
		ObjectFieldLocalService objectFieldLocalService) {

		this.objectDefinition = objectDefinition;
		this.objectFieldLocalService = objectFieldLocalService;
	}

	@Override
	public String getClassName() {
		return objectDefinition.getClassName();
	}

	@Override
	public Predicate getPredicate(
			Long[] groupIds, ObjectRelationship objectRelationship,
			Predicate predicate)
		throws PortalException {

		ObjectDefinition relatedObjectDefinition =
			ObjectRelationshipUtil.getRelatedObjectDefinition(
				objectDefinition, objectRelationship);

		if (relatedObjectDefinition.isUnmodifiableSystemObject()) {
			throw new InvalidFilterException(
				"Filtering is not supported for system objects");
		}

		return getPredicate(
			groupIds, objectRelationship, predicate, relatedObjectDefinition);
	}

	public abstract Predicate getPredicate(
			Long[] groupIds, ObjectRelationship objectRelationship,
			Predicate predicate, ObjectDefinition relatedObjectDefinition)
		throws PortalException;

	protected DynamicObjectDefinitionTable getDynamicObjectDefinitionTable(
		ObjectDefinition objectDefinition) {

		return new DynamicObjectDefinitionTable(
			objectDefinition,
			objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId(),
				objectDefinition.getDBTableName()),
			objectDefinition.getDBTableName());
	}

	protected DynamicObjectDefinitionTable
		getExtensionDynamicObjectDefinitionTable(
			ObjectDefinition objectDefinition) {

		return new DynamicObjectDefinitionTable(
			objectDefinition,
			objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId(),
				objectDefinition.getExtensionDBTableName()),
			objectDefinition.getExtensionDBTableName());
	}

	protected Predicate getExtensionLeftJoinPredicate(
		DynamicObjectDefinitionTable dynamicObjectDefinitionTable,
		DynamicObjectDefinitionTable extensionDynamicObjectDefinitionTable) {

		Collection<Column<DynamicObjectDefinitionTable, ?>> columns =
			extensionDynamicObjectDefinitionTable.getColumns();

		if (columns.size() > 1) {
			Column<DynamicObjectDefinitionTable, Long> primaryKeyColumn =
				dynamicObjectDefinitionTable.getPrimaryKeyColumn();

			return primaryKeyColumn.eq(
				extensionDynamicObjectDefinitionTable.getPrimaryKeyColumn());
		}

		return null;
	}

	protected Predicate getInPredicate(
			Column<?, ?> column,
			DynamicObjectDefinitionLocalizationTable
				dynamicObjectDefinitionLocalizationTable,
			DynamicObjectDefinitionTable dynamicObjectDefinitionTable,
			DynamicObjectDefinitionTable extensionDynamicObjectDefinitionTable,
			FromStep fromStep, Long[] groupIds,
			ObjectDefinition objectDefinition, Predicate predicate)
		throws PortalException {

		return column.in(
			fromStep.from(
				dynamicObjectDefinitionTable
			).innerJoinON(
				ObjectEntryTable.INSTANCE,
				ObjectEntryTable.INSTANCE.objectEntryId.eq(
					dynamicObjectDefinitionTable.getPrimaryKeyColumn())
			).leftJoinOn(
				extensionDynamicObjectDefinitionTable,
				getExtensionLeftJoinPredicate(
					dynamicObjectDefinitionTable,
					extensionDynamicObjectDefinitionTable)
			).leftJoinOn(
				dynamicObjectDefinitionLocalizationTable,
				ObjectEntrySearchUtil.getLeftJoinLocalizationTablePredicate(
					dynamicObjectDefinitionLocalizationTable,
					dynamicObjectDefinitionTable, null)
			).where(
				ObjectEntrySearchUtil.getObjectEntryIndexPredicate(
					groupIds, objectDefinition, predicate)
			));
	}

	protected ObjectDefinition getObjectDefinition1(
			ObjectRelationship objectRelationship)
		throws PortalException {

		if (objectRelationship.getObjectDefinitionId1() ==
				objectDefinition.getObjectDefinitionId()) {

			return objectDefinition;
		}

		return ObjectDefinitionLocalServiceUtil.getObjectDefinition(
			objectRelationship.getObjectDefinitionId1());
	}

	protected ObjectDefinition getObjectDefinition2(
			ObjectRelationship objectRelationship)
		throws PortalException {

		if (objectRelationship.getObjectDefinitionId2() ==
				objectDefinition.getObjectDefinitionId()) {

			return objectDefinition;
		}

		return ObjectDefinitionLocalServiceUtil.getObjectDefinition(
			objectRelationship.getObjectDefinitionId2());
	}

	protected Column<DynamicObjectDefinitionTable, ?>
		getObjectRelationshipColumn(
			DynamicObjectDefinitionTable dynamicObjectDefinitionTable,
			DynamicObjectDefinitionTable extensionDynamicObjectDefinitionTable,
			ObjectDefinition objectDefinition1,
			ObjectRelationship objectRelationship) {

		String columnName =
			ObjectRelationshipUtil.getObjectRelationshipFieldName(
				objectDefinition1, objectRelationship.getName());

		Column<DynamicObjectDefinitionTable, ?> column =
			dynamicObjectDefinitionTable.getColumn(columnName);

		if (column != null) {
			return column;
		}

		return extensionDynamicObjectDefinitionTable.getColumn(columnName);
	}

	protected <T extends BaseTable<T>> Column<?, ?> getPKObjectFieldColumn(
		BaseTable<T> baseTable, String pkObjectFieldDBColumnName) {

		return baseTable.getColumn(pkObjectFieldDBColumnName);
	}

	protected final ObjectDefinition objectDefinition;
	protected final ObjectFieldLocalService objectFieldLocalService;

}