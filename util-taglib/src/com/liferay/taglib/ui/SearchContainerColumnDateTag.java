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

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.dao.search.SearchEntry;
import com.liferay.portal.kernel.dao.search.TextSearchEntry;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

import java.text.Format;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.portlet.PortletURL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

/**
 * @author Raymond Augé
 */
public class SearchContainerColumnDateTag<R> extends SearchContainerColumnTag {

	@Override
	public int doEndTag() {
		try {
			SearchContainerRowTag<R> searchContainerRowTag =
				(SearchContainerRowTag<R>)findAncestorWithClass(
					this, SearchContainerRowTag.class);

			ResultRow resultRow = searchContainerRowTag.getRow();

			if (Validator.isNotNull(_property)) {
				_value = (Date)BeanPropertiesUtil.getObject(
					resultRow.getObject(), _property);
			}

			if (index <= -1) {
				List<SearchEntry> searchEntries = resultRow.getEntries();

				index = searchEntries.size();
			}

			if (resultRow.isRestricted()) {
				_href = null;
			}

			TextSearchEntry textSearchEntry = new TextSearchEntry();

			textSearchEntry.setAlign(getAlign());
			textSearchEntry.setColspan(getColspan());
			textSearchEntry.setCssClass(getCssClass());
			textSearchEntry.setHref((String)getHref());

			if (Validator.isNotNull(_value)) {
				Object[] localeAndTimeZone = getLocaleAndTimeZone(pageContext);

				Format dateFormatDateTime =
					FastDateFormatFactoryUtil.getDateTime(
						(Locale)localeAndTimeZone[0],
						(TimeZone)localeAndTimeZone[1]);

				StringBundler sb = new StringBundler(5);

				sb.append(
					"<span onmouseover=\"Liferay.Portal.ToolTip.show(this, '");
				sb.append(dateFormatDateTime.format(_value));
				sb.append("')\">");
				sb.append(
					LanguageUtil.format(
						pageContext, "x-ago",
						LanguageUtil.getTimeDescription(
							pageContext,
							System.currentTimeMillis() - _value.getTime(),
							true)));
				sb.append("</span>");

				textSearchEntry.setName(sb.toString());
			}
			else {
				textSearchEntry.setName(StringPool.BLANK);
			}

			textSearchEntry.setValign(getValign());

			resultRow.addSearchEntry(index, textSearchEntry);

			return EVAL_PAGE;
		}
		finally {
			index = -1;
			_value = null;

			if (!ServerDetector.isResin()) {
				align = SearchEntry.DEFAULT_ALIGN;
				colspan = SearchEntry.DEFAULT_COLSPAN;
				cssClass = SearchEntry.DEFAULT_CSS_CLASS;
				_href = null;
				name = null;
				_orderable = false;
				_orderableProperty = null;
				_property = null;
				_timeZone = null;
				valign = SearchEntry.DEFAULT_VALIGN;
			}
		}
	}

	@Override
	public int doStartTag() throws JspException {
		if (_orderable && Validator.isNull(_orderableProperty)) {
			_orderableProperty = name;
		}

		SearchContainerRowTag<R> searchContainerRowTag =
			(SearchContainerRowTag<R>)findAncestorWithClass(
				this, SearchContainerRowTag.class);

		if (searchContainerRowTag == null) {
			throw new JspTagException(
				"Requires liferay-ui:search-container-row");
		}

		if (!searchContainerRowTag.isHeaderNamesAssigned()) {
			List<String> headerNames = searchContainerRowTag.getHeaderNames();

			String name = getName();

			if (Validator.isNull(name) && Validator.isNotNull(_property)) {
				name = _property;
			}

			headerNames.add(name);

			if (_orderable) {
				Map<String, String> orderableHeaders =
					searchContainerRowTag.getOrderableHeaders();

				if (Validator.isNotNull(_orderableProperty)) {
					orderableHeaders.put(name, _orderableProperty);
				}
				else if (Validator.isNotNull(_property)) {
					orderableHeaders.put(name, _property);
				}
				else if (Validator.isNotNull(name)) {
					orderableHeaders.put(name, name);
				}
			}
		}

		return EVAL_BODY_INCLUDE;
	}

	public Object getHref() {
		if (Validator.isNotNull(_href) && (_href instanceof PortletURL)) {
			_href = _href.toString();
		}

		return _href;
	}

	public String getOrderableProperty() {
		return _orderableProperty;
	}

	public String getProperty() {
		return _property;
	}

	public Date getValue() {
		return _value;
	}

	public boolean isOrderable() {
		return _orderable;
	}

	public void setHref(Object href) {
		_href = href;
	}

	public void setOrderable(boolean orderable) {
		_orderable = orderable;
	}

	public void setOrderableProperty(String orderableProperty) {
		_orderableProperty = orderableProperty;
	}

	public void setProperty(String property) {
		_property = property;
	}

	public void setValue(Date value) {
		_value = value;
	}

	protected Object[] getLocaleAndTimeZone(PageContext pageContext) {
		if ((_locale != null) && (_timeZone != null)) {
			return new Object[] {_locale, _timeZone};
		}

		HttpServletRequest request =
			(HttpServletRequest)pageContext.getRequest();

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		_locale = themeDisplay.getLocale();
		_timeZone = themeDisplay.getTimeZone();

		return new Object[] {_locale, _timeZone};
	}

	private Object _href;
	private Locale _locale;
	private boolean _orderable;
	private String _orderableProperty;
	private String _property;
	private TimeZone _timeZone;
	private Date _value;

}