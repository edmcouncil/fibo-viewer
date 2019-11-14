<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var = "context" value = "http://localhost:8080"/>

  <c:set var = "clazz" value = "${details}"/>
  <div class="my-3 px-3">

    <div class="row">
      <b class="custom-link text-primary">${clazz.label}</b>
    </div>

    <c:if test="${not empty clazz.taxonomy}">
      <div class="row my-3">
        <div class="col">
          <nav aria-label="breadcrumb" class="">
            <c:forEach items="${clazz.taxonomy.value}" var="taxonomyList">
              <div class="row my-0 py-0">
                <ol  class="breadcrumb my-0 py-0 col-12">
                  <c:forEach items="${taxonomyList}" var="taxEle">
                    <li class="breadcrumb-item" aria-current="page"><weasel:RenderTaxonomyElement element="${taxEle}" searchPath="${context}"/></li>
                  </c:forEach>
                </ol>
              </div>
            </c:forEach>
          </nav>
        </div>
      </div>
    </c:if>

    <c:forEach var="entry" items="${clazz.properties}">
      <div class="row">
        <c:choose>
          <c:when test="${fn:length(entry.value)==1}">
            <b class="mr-1">${entry.key}: </b>
          </c:when>
          <c:otherwise>
            <b class="col-12 px-0">${entry.key}: </b>
          </c:otherwise>
        </c:choose>
        <c:choose>
          <c:when test="${fn:length(entry.value)==1}">
            <c:forEach var="details" items="${entry.value}">
              <span class="mb-3"><weasel:Render property="${details}" searchPath="${context}"/></span>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <br />
            <ul>
              <c:forEach var="details" items="${entry.value}">
                <weasel:Render property="${details}" elementWrapper="li" searchPath="${context}"/>
              </c:forEach>
            </ul>

          </c:otherwise>
        </c:choose>
      </div>
    </c:forEach>

    <c:if test="${empty clazz.properties}">
      There is nothing to display...
    </c:if>
  </div>
