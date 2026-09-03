/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.internal.resource.v1_0;

import com.liferay.exportimport.constants.ExportImportConstants;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.headless.admin.workflow.dto.v1_0.Node;
import com.liferay.headless.admin.workflow.dto.v1_0.Transition;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowDefinition;
import com.liferay.headless.admin.workflow.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.admin.workflow.internal.dto.v1_0.util.NodeUtil;
import com.liferay.headless.admin.workflow.internal.dto.v1_0.util.TransitionUtil;
import com.liferay.headless.admin.workflow.internal.odata.entity.v1_0.WorkflowDefinitionEntityModel;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowDefinitionResource;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.permission.ModelPermissionsUtil;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.portal.workflow.comparator.WorkflowComparatorFactory;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/workflow-definition.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE, service = WorkflowDefinitionResource.class
)
@CTAware
public class WorkflowDefinitionResourceImpl
	extends BaseWorkflowDefinitionResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate
		<WorkflowDefinition> {

	@Override
	public void deleteWorkflowDefinition(Long workflowDefinitionId)
		throws Exception {

		WorkflowDefinition workflowDefinition = getWorkflowDefinition(
			workflowDefinitionId);

		postWorkflowDefinitionUpdateActive(
			false, workflowDefinition.getName(),
			workflowDefinition.getVersion());

		deleteWorkflowDefinitionUndeploy(
			workflowDefinition.getName(), workflowDefinition.getVersion());
	}

	@Override
	public void deleteWorkflowDefinitionByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		WorkflowDefinition workflowDefinition = _toWorkflowDefinition(
			null,
			() -> _workflowDefinitionManager.getWorkflowDefinition(
				contextCompany.getCompanyId(), externalReferenceCode));

		postWorkflowDefinitionUpdateActive(
			false, workflowDefinition.getName(),
			workflowDefinition.getVersion());

		deleteWorkflowDefinitionUndeploy(
			workflowDefinition.getName(), workflowDefinition.getVersion());
	}

	@Override
	public void deleteWorkflowDefinitionUndeploy(String name, String version)
		throws Exception {

		_workflowDefinitionManager.undeployWorkflowDefinition(
			contextCompany.getCompanyId(), name, contextUser.getUserId(),
			GetterUtil.getInteger(version));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public ExportImportDescriptor<KaleoDefinition> getExportImportDescriptor() {
		return new ExportImportDescriptor<KaleoDefinition>() {

			@Override
			public String getKey() {
				return WorkflowDefinitionResourceImpl.class.getName();
			}

			@Override
			public String getLabelLanguageKey() {
				return "workflow-definitions";
			}

			@Override
			public Class<KaleoDefinition> getModelClass() {
				return KaleoDefinition.class;
			}

			@Override
			public String getPortletId() {
				return WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW;
			}

			@Override
			public int getRank() {
				return 99;
			}

			@Override
			public Scope getScope() {
				return Scope.COMPANY;
			}

			@Override
			public String getSectionKey() {
				return ExportImportConstants.SECTION_KEY_CONTENT_AND_DATA;
			}

		};
	}

	@Override
	public WorkflowDefinition getWorkflowDefinition(Long workflowDefinitionId)
		throws Exception {

		return _toWorkflowDefinition(
			null,
			() -> _workflowDefinitionManager.getWorkflowDefinition(
				workflowDefinitionId));
	}

	@Override
	public WorkflowDefinition getWorkflowDefinitionByName(
			String name, String contentFormat, Integer version)
		throws Exception {

		return _toWorkflowDefinition(
			contentFormat,
			() -> {
				if (version == null) {
					return _workflowDefinitionManager.
						getLatestWorkflowDefinition(
							contextCompany.getCompanyId(), name);
				}

				return _workflowDefinitionManager.getWorkflowDefinition(
					contextCompany.getCompanyId(), name, version);
			});
	}

	@Override
	public Page<WorkflowDefinition> getWorkflowDefinitionsPage(
			Boolean active, String scope, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		Map<String, Map<String, String>> actions =
			HashMapBuilder.<String, Map<String, String>>put(
				"create",
				addAction(
					ActionKeys.ADD_DEFINITION, "postWorkflowDefinition",
					WorkflowConstants.RESOURCE_NAME, null)
			).put(
				"createBatch",
				addAction(
					ActionKeys.ADD_DEFINITION, "postWorkflowDefinitionBatch",
					WorkflowConstants.RESOURCE_NAME, null)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteWorkflowDefinitionBatch",
					WorkflowConstants.RESOURCE_NAME, null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getWorkflowDefinitionsPage",
					WorkflowConstants.RESOURCE_NAME, null)
			).put(
				"updateActive",
				addAction(
					ActionKeys.UPDATE, "postWorkflowDefinitionUpdateActive",
					WorkflowConstants.RESOURCE_NAME, null)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putWorkflowDefinitionBatch",
					WorkflowConstants.RESOURCE_NAME, null)
			).build();

		if (filter == null) {
			return Page.of(
				actions,
				transform(
					_workflowDefinitionManager.getLatestWorkflowDefinitions(
						active, contextCompany.getCompanyId(),
						pagination.getEndPosition(),
						_toOrderByComparator(
							(Sort)ArrayUtil.getValue(sorts, 0)),
						GetterUtil.getString(
							scope, WorkflowDefinitionConstants.SCOPE_ALL),
						pagination.getStartPosition(), contextUser.getUserId()),
					this::_toWorkflowDefinition),
				pagination,
				_workflowDefinitionManager.getLatestWorkflowDefinitionsCount(
					active, contextCompany.getCompanyId()));
		}

		return SearchUtil.search(
			actions,
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter("latest", "true"), BooleanClauseOccur.MUST);

				if (active != null) {
					booleanFilter.add(
						new TermFilter("active", active ? "1" : "0"),
						BooleanClauseOccur.MUST);
				}

				if (Validator.isNotNull(scope) &&
					!StringUtil.equals(
						scope, WorkflowDefinitionConstants.SCOPE_ALL)) {

					booleanFilter.add(
						new TermFilter("scope", scope),
						BooleanClauseOccur.MUST);
				}
			},
			filter, KaleoDefinitionVersion.class.getName(), null, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> {
				KaleoDefinitionVersion kaleoDefinitionVersion =
					_kaleoDefinitionVersionLocalService.
						fetchKaleoDefinitionVersion(
							GetterUtil.getLong(
								document.get(Field.ENTRY_CLASS_PK)));

				if (kaleoDefinitionVersion == null) {
					return null;
				}

				return _toWorkflowDefinition(
					_workflowDefinitionManager.getWorkflowDefinition(
						kaleoDefinitionVersion.getKaleoDefinitionId()));
			});
	}

	@Override
	public WorkflowDefinition postWorkflowDefinition(
			WorkflowDefinition workflowDefinition)
		throws Exception {

		return postWorkflowDefinitionDeploy(workflowDefinition);
	}

	@Override
	public WorkflowDefinition postWorkflowDefinitionDeploy(
			WorkflowDefinition workflowDefinition)
		throws Exception {

		String content = workflowDefinition.getContent();

		return _toWorkflowDefinition(
			_workflowDefinitionManager.deployWorkflowDefinition(
				GetterUtil.getBoolean(workflowDefinition.getActive(), true),
				content.getBytes(), contextCompany.getCompanyId(),
				workflowDefinition.getExternalReferenceCode(),
				_getGroupId(workflowDefinition.getGroupExternalReferenceCode()),
				_getModelPermissions(workflowDefinition),
				workflowDefinition.getName(),
				GetterUtil.getString(
					workflowDefinition.getScope(),
					WorkflowDefinitionConstants.SCOPE_ALL),
				GetterUtil.getBoolean(workflowDefinition.getSystem()),
				_getTitle(workflowDefinition), contextUser.getUserId(),
				GetterUtil.getInteger(workflowDefinition.getVersion(), 1)));
	}

	@Override
	public WorkflowDefinition postWorkflowDefinitionSave(
			WorkflowDefinition workflowDefinition)
		throws Exception {

		String content = workflowDefinition.getContent();

		return _toWorkflowDefinition(
			_workflowDefinitionManager.saveWorkflowDefinition(
				content.getBytes(), contextCompany.getCompanyId(),
				workflowDefinition.getExternalReferenceCode(),
				_getGroupId(workflowDefinition.getGroupExternalReferenceCode()),
				workflowDefinition.getName(),
				GetterUtil.getString(
					workflowDefinition.getScope(),
					WorkflowDefinitionConstants.SCOPE_ALL),
				GetterUtil.getBoolean(workflowDefinition.getSystem()),
				_getTitle(workflowDefinition), contextUser.getUserId()));
	}

	@Override
	public WorkflowDefinition postWorkflowDefinitionUpdateActive(
			Boolean active, String name, String version)
		throws Exception {

		return _toWorkflowDefinition(
			_workflowDefinitionManager.updateActive(
				active, contextCompany.getCompanyId(), name,
				contextUser.getUserId(), GetterUtil.getInteger(version)));
	}

	@Override
	public WorkflowDefinition putWorkflowDefinition(
			Long workflowDefinitionId, WorkflowDefinition workflowDefinition)
		throws Exception {

		_workflowDefinitionManager.getLatestWorkflowDefinition(
			contextCompany.getCompanyId(), workflowDefinition.getName());

		return postWorkflowDefinitionDeploy(workflowDefinition);
	}

	@Override
	public WorkflowDefinition putWorkflowDefinitionByExternalReferenceCode(
			String externalReferenceCode, WorkflowDefinition workflowDefinition)
		throws Exception {

		workflowDefinition.setExternalReferenceCode(
			() -> externalReferenceCode);

		return postWorkflowDefinitionDeploy(workflowDefinition);
	}

	private long _getGroupId(String externalReferenceCode) throws Exception {
		if (Validator.isNull(externalReferenceCode)) {
			return 0;
		}

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		if (group == null) {
			return 0;
		}

		return group.getGroupId();
	}

	private ModelPermissions _getModelPermissions(
			WorkflowDefinition workflowDefinition)
		throws Exception {

		if (workflowDefinition.getPermissions() == null) {
			return null;
		}

		return ModelPermissionsUtil.toModelPermissions(
			contextCompany.getCompanyId(), workflowDefinition.getPermissions(),
			GetterUtil.getLong(workflowDefinition.getId()),
			KaleoDefinition.class.getName(), _resourceActionLocalService,
			_resourcePermissionLocalService, _roleLocalService);
	}

	private String _getTitle(WorkflowDefinition workflowDefinition)
		throws Exception {

		if (MapUtil.isEmpty(workflowDefinition.getTitle_i18n())) {
			return workflowDefinition.getTitle();
		}

		return _localization.getXml(
			workflowDefinition.getTitle_i18n(),
			_language.getLanguageId(contextCompany.getLocale()), "title");
	}

	private OrderByComparator
		<com.liferay.portal.kernel.workflow.WorkflowDefinition>
			_toOrderByComparator(Sort sort) {

		if (sort == null) {
			return _workflowComparatorFactory.
				getDefinitionModifiedDateComparator(false);
		}

		if (StringUtil.equals(sort.getFieldName(), "name")) {
			return _workflowComparatorFactory.getDefinitionNameComparator(
				!sort.isReverse());
		}

		return _workflowComparatorFactory.getDefinitionModifiedDateComparator(
			!sort.isReverse());
	}

	private WorkflowDefinition _toWorkflowDefinition(
			String contentFormat,
			UnsafeSupplier
				<com.liferay.portal.kernel.workflow.WorkflowDefinition,
				 Exception> unsafeSupplier)
		throws Exception {

		try {
			return _toWorkflowDefinition(contentFormat, unsafeSupplier.get());
		}
		catch (Exception exception) {
			Throwable throwable = exception.getCause();

			if (throwable instanceof NoSuchModelException) {
				throw (NoSuchModelException)throwable;
			}

			throw exception;
		}
	}

	private WorkflowDefinition _toWorkflowDefinition(
		String contentFormat,
		com.liferay.portal.kernel.workflow.WorkflowDefinition
			workflowDefinition) {

		return new WorkflowDefinition() {
			{
				setActions(
					() -> HashMapBuilder.put(
						"delete",
						addAction(
							ActionKeys.DELETE,
							workflowDefinition.getWorkflowDefinitionId(),
							"deleteWorkflowDefinition",
							_workflowDefinitionModelResourcePermission)
					).put(
						"update",
						addAction(
							ActionKeys.UPDATE,
							workflowDefinition.getWorkflowDefinitionId(),
							"putWorkflowDefinition",
							_workflowDefinitionModelResourcePermission)
					).build());
				setActive(workflowDefinition::isActive);
				setContent(
					() -> {
						if (StringUtil.equalsIgnoreCase(contentFormat, "xml")) {
							return workflowDefinition.getContentAsXML();
						}

						return workflowDefinition.getContent();
					});
				setCreator(
					() -> CreatorUtil.toCreator(
						_portal,
						_userLocalService.fetchUser(
							workflowDefinition.getUserId())));
				setDateCreated(workflowDefinition::getCreateDate);
				setDateModified(workflowDefinition::getModifiedDate);
				setDescription(workflowDefinition::getDescription);
				setExternalReferenceCode(
					workflowDefinition::getExternalReferenceCode);
				setGroupExternalReferenceCode(
					workflowDefinition::getGroupExternalReferenceCode);
				setId(workflowDefinition::getWorkflowDefinitionId);
				setName(workflowDefinition::getName);
				setNodes(
					() -> transformToArray(
						workflowDefinition.getWorkflowNodes(),
						workflowNode -> NodeUtil.toNode(
							contextAcceptLanguage.getPreferredLocale(),
							workflowNode),
						Node.class));
				setScope(workflowDefinition::getScope);
				setSystem(workflowDefinition::isSystem);
				setTitle(
					() -> workflowDefinition.getTitle(
						_language.getLanguageId(
							contextAcceptLanguage.getPreferredLocale())));
				setTitle_i18n(
					() -> {
						Map<String, String> title_i18n = new HashMap<>();

						Map<Locale, String> map =
							_localization.getLocalizationMap(
								workflowDefinition.getTitle());

						for (Map.Entry<Locale, String> entry : map.entrySet()) {
							title_i18n.put(
								_language.getLanguageId(entry.getKey()),
								entry.getValue());
						}

						return title_i18n;
					});
				setTransitions(
					() -> transformToArray(
						workflowDefinition.getWorkflowTransitions(),
						workflowTransition -> TransitionUtil.toTransition(
							contextAcceptLanguage.getPreferredLocale(),
							workflowTransition),
						Transition.class));
				setVersion(
					() -> String.valueOf(workflowDefinition.getVersion()));
			}
		};
	}

	private WorkflowDefinition _toWorkflowDefinition(
		com.liferay.portal.kernel.workflow.WorkflowDefinition
			workflowDefinition) {

		return _toWorkflowDefinition(null, workflowDefinition);
	}

	private static final EntityModel _entityModel =
		new WorkflowDefinitionEntityModel();

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private KaleoDefinitionVersionLocalService
		_kaleoDefinitionVersionLocalService;

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowComparatorFactory _workflowComparatorFactory;

	@Reference
	private WorkflowDefinitionManager _workflowDefinitionManager;

	@Reference(
		target = "(model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoDefinition)"
	)
	private ModelResourcePermission<?>
		_workflowDefinitionModelResourcePermission;

}