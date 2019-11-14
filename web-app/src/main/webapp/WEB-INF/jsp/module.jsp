<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix = "weasel" uri = "tags/propertyRender.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 
<%@ taglib prefix="templateTree" tagdir="/WEB-INF/tags" %> 

<!DOCTYPE HTML>
<html>

  <head>
    <jsp:directive.include file="page/elements/head.jsp" />
  </head>
  <body>
    <jsp:directive.include file="page/elements/header.jsp" />

    <button onclick="topFunction()" id="goToTopBtn" title="Go to top">Top</button>

    <div class="container-fluid">
      <div class="row px-4 py-4">

        <div class="col-md-6 px-2">

          <div class="mt-5 ml-2">
            <div class="">
              <jsp:directive.include file="page/elements/moduleTree.jsp" />
            </div>
          </div>
        </div>
        <div class="col-md-6 px-2">
          <div class="mt-5 ml-2">
            <div class="">
              <c:if test="${grouped_details}">
                <jsp:directive.include file="page/elements/viewGrouped.jsp" />
              </c:if>
            </div>
          </div>
        </div>
      </div>
    </div>
    <jsp:directive.include file="page/elements/footer.jsp" />
  </body>

</html>
