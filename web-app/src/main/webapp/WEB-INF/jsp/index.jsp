<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE HTML>
<html>

  <head>
    <jsp:directive.include file="page/elements/head.jsp" />
  </head>
  <body>
    <jsp:directive.include file="page/elements/header.jsp" />
    
    <div class="container-fluid">
      <div class="row px-4 py-4">
        <div class="col-md-12 px-2">

          <div class="card">
            <div class="card-body">
              <c:choose>
                <c:when test="${details_display}">
                  <jsp:directive.include file="page/elements/view.jsp" />
                </c:when>
                <c:otherwise>
                  There is nothing to display... 
                </c:otherwise>
              </c:choose>
            </div>
          </div>
        </div>
      </div>
    </div>

    <jsp:directive.include file="page/elements/footer.jsp" />
  </body>

</html>
