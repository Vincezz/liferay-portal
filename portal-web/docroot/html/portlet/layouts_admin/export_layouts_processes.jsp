<%--
/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/html/portlet/layouts_admin/init.jsp" %>

<%
long groupId = ParamUtil.getLong(request, "groupId");

PortletURL portletURL = liferayPortletResponse.createRenderURL();

portletURL.setParameter("struts_action", "/layouts_admin/export_layouts");
portletURL.setParameter("tabs2", "all-export-processes");
portletURL.setParameter("groupId", String.valueOf(groupId));

String orderByCol = ParamUtil.getString(request, "orderByCol");
String orderByType = ParamUtil.getString(request, "orderByType");

if (Validator.isNotNull(orderByCol) && Validator.isNotNull(orderByType)) {
	portalPreferences.setValue(PortletKeys.BACKGROUND_TASK, "entries-order-by-col", orderByCol);
	portalPreferences.setValue(PortletKeys.BACKGROUND_TASK, "entries-order-by-type", orderByType);
}
else {
	orderByCol = portalPreferences.getValue(PortletKeys.BACKGROUND_TASK, "entries-order-by-col", "create-date");
	orderByType = portalPreferences.getValue(PortletKeys.BACKGROUND_TASK, "entries-order-by-type", "desc");
}

OrderByComparator orderByComparator = BackgroundTaskUtil.getBackgroundTaskOrderByComparator(orderByCol, orderByType);
%>

<liferay-ui:search-container
	emptyResultsMessage="no-export-processes-were-found"
	iteratorURL="<%= portletURL %>"
	orderByCol="<%= orderByCol %>"
	orderByComparator="<%= orderByComparator %>"
	orderByType="<%= orderByType %>"
	total="<%= BackgroundTaskLocalServiceUtil.getBackgroundTasksCount(groupId, LayoutExportBackgroundTaskExecutor.class.getName()) %>"
>
	<liferay-ui:search-container-results
		results="<%= BackgroundTaskLocalServiceUtil.getBackgroundTasks(groupId, LayoutExportBackgroundTaskExecutor.class.getName(), searchContainer.getStart(), searchContainer.getEnd(), searchContainer.getOrderByComparator()) %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.portal.model.BackgroundTask"
		modelVar="backgroundTask"
	>
		<liferay-ui:search-container-column-text
			name="user-name"
			value="<%= backgroundTask.getUserName() %>"
		/>

		<liferay-ui:search-container-column-text
			name="status"
			value="<%= LanguageUtil.get(pageContext, backgroundTask.getStatusLabel()) %>"
		/>

		<liferay-ui:search-container-column-text
			name="create-date"
			orderable="<%= true %>"
			orderableProperty="createDate"
			value="<%= dateFormatDateTime.format(backgroundTask.getCreateDate()) %>"
		/>

		<liferay-ui:search-container-column-text
			name="completion-date"
			orderable="<%= true %>"
			orderableProperty="completionDate"
			value="<%= backgroundTask.getCompletionDate() != null ? dateFormatDateTime.format(backgroundTask.getCompletionDate()) : StringPool.BLANK %>"
		/>

		<liferay-ui:search-container-column-text
			name="download"
		>

			<%
			List<FileEntry> attachmentsFileEntries = backgroundTask.getAttachmentsFileEntries();
			%>

			<c:choose>
				<c:when test="<%= !attachmentsFileEntries.isEmpty() %>">

					<%
					for (FileEntry fileEntry : attachmentsFileEntries) {
					%>

						<portlet:actionURL var="attachmentURL" windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">
							<portlet:param name="struts_action" value="/group_pages/get_background_task_attachment" />
							<portlet:param name="backgroundTaskId" value="<%= String.valueOf(backgroundTask.getBackgroundTaskId()) %>" />
							<portlet:param name="attachment" value="<%= fileEntry.getTitle() %>" />
						</portlet:actionURL>

						<%
						StringBundler sb = new StringBundler(4);

						sb.append(fileEntry.getTitle());
						sb.append(StringPool.OPEN_PARENTHESIS);
						sb.append(TextFormatter.formatStorageSize(fileEntry.getSize(), locale));
						sb.append(StringPool.CLOSE_PARENTHESIS);
						%>

						<liferay-ui:icon
							image="download"
							label="<%= true %>"
							message="<%= sb.toString() %>"
							url="<%= attachmentURL %>"
						/>

					<%
					}
					%>

				</c:when>
				<c:otherwise>

					<%
					Map taskContextMap = backgroundTask.getTaskContextMap();
					%>

					<liferay-ui:icon
						image="download"
						label="<%= true %>"
						message='<%= MapUtil.getString(taskContextMap, "fileName") %>'
					/>
				</c:otherwise>
			</c:choose>

		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text>
			<portlet:actionURL var="deleteBackgroundTaskURL">
				<portlet:param name="struts_action" value="/group_pages/delete_background_task" />
				<portlet:param name="redirect" value="<%= portletURL.toString() %>" />
				<portlet:param name="backgroundTaskId" value="<%= String.valueOf(backgroundTask.getBackgroundTaskId()) %>" />
			</portlet:actionURL>

			<liferay-ui:icon-delete
				label="true"
				url="<%= deleteBackgroundTaskURL %>"
			/>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>