<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var = "context" value = "*"/>

<div class="my-3 px-3">

  <div class="row">
    <h3><b class="">${clazz.label}</b></h3>
  </div>
  <div class="row">
    <h6><span class="text-secondary">${clazz.iri}</span></h6>
  </div>
  <c:if test="${not empty clazz.qName}">
    <div class="row">
      <h6>
        <span class="text-secondary">${clazz.qName}</span>
      </h6>
    </div>
  </c:if>
  <div class="border-bottom col-12 mt-1 mb-2 ml-0 mr-0"></div>

  <c:if test="${not empty clazz.taxonomy}">
    <div class="row my-3">
      <div class="col ml-0 pl-0">
        <nav aria-label="breadcrumb" class="ml-0 pl-0">
          <c:forEach items="${clazz.taxonomy.value}" var="taxonomyList">
            <div class="row my-1 py-0">
              <ol  class="breadcrumb my-0 py-0 col-12 bg-white">
                <c:forEach items="${taxonomyList}" var="taxEle">
                  <li class="breadcrumb-item" aria-current="page"><b><weasel:RenderTaxonomyElement element="${taxEle}" searchPath="${context}"/></b></li>
                </c:forEach>
              </ol>
            </div>
          </c:forEach>
        </nav>
      </div>
    </div>
    <div class="border-bottom col-12 mt-1 mb-2 ml-0 mr-0"></div>       
  </c:if>
  <c:forEach var="prop" items="${clazz.properties}">
    <div class="row">
      <h5><b class="col-12 px-0">${prop.key} </b></h5>
    </div>
    <c:forEach var="entry" items="${prop.value}">
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
    <div class="border-bottom col-12 mt-1 mb-2 ml-0 mr-0"></div>       
  </c:forEach>

  <c:if test="${empty clazz.properties}">
    There is nothing to display...
  </c:if>
</div>
