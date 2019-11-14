<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:forEach items="${details_list}" var="clazz">
  <div class="my-3 px-3">

    <div class="row">
      <b class="custom-link text-primary">${clazz.label}</b>
    </div>

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
              <c:choose>
                <c:when test="${details.type eq 'STRING'}"> 
                  <span class="mb-3">${details.value}</span>
                </c:when>
                <c:when test="${details.type eq 'IRI'}"> 
                  <a href = "${pageContext.request.contextPath}/search?query=${details.value}"
                     class = "custom-link mb-3">${details.value}</a>
                </c:when>
                <c:when test="${details.type eq 'ANY_URI'}"> 
                  <a href="${details.value}"
                     class="custom-link mb-3">${details.value}</a>
                </c:when>
                <c:otherwise>
                  <span class="mb-3">${details.value}</span>
                </c:otherwise>
              </c:choose>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <br />
            <ul>
              <c:forEach var="details" items="${entry.value}">

                <c:choose>
                  <c:when test="${details.type eq 'STRING'}"> 
                    <li>${details.value}</li>
                  </c:when>
                  <c:when test="${details.type eq 'IRI'}"> 
                    <li><a href = "${pageContext.request.contextPath}/search?query=${details.value}"
                           class = "custom-link">${details.value}</a></li>
                  </c:when>
                  <c:when test="${details.type eq 'ANY_URI'}"> 
                    <li><a href="${details.value}"
                           class="custom-link">${details.value}</a></li>
                  </c:when>
                  <c:otherwise>
                    <li>${details.value}</li>
                  </c:otherwise>
                </c:choose>

              </c:forEach>
            </ul>

          </c:otherwise>
        </c:choose>
      </div>
    </c:forEach>

    <c:if test="${empty clazz.properties}">
      There is nothing to display...
    </c:if>
    <div class="border-bottom col-12 mt-1"></div>
  </div>
</c:forEach>
