<div class="container-fluid">
  <div class="row px-3 py-3">
    <div class="col-md-3 px-2" style="height: 100%">
      <div class="mt-5 ml-2">
        <div class="" style="height: 100%">
          <jsp:directive.include file="moduleTree.jsp" />
        </div>
      </div>
    </div>

    <div class="col-md-9 px-2">

      <div class="card">
        <h2 class="my-3 ml-2">Search results for <i>${result.query}</i>:</h2>
        <c:forEach items="${result.result}" var="resEl">
          <div class="ml-5">
            <div class="row">
              <span><h5> <a class="font-weight-bold" href="search?query=${resEl.iri}">${resEl.label}</a> </h5></span>
            </div>
            <div class="row">
              <span> <a class="text-secondary" href="search?query=${resEl.iri}">${resEl.iri}</a> </span>
            </div>
            <div class="row">
              <span> ${resEl.description} </span>
            </div>
          </div>
          <div class="border-bottom col-12 mt-1 mb-2 ml-0 mr-0"></div>
        </c:forEach>

      </div>
      <div class="mt-2 d-flex justify-content-center">
        <nav aria-label="Page navigation example">
          <ul class="pagination">

            <c:choose>
              <c:when test="${result.page==1}">
                <li class="page-item disabled">
                  <a class="page-link" href="#" aria-label="Previous">
                    <span aria-hidden="true">&laquo;</span>
                    <span class="sr-only">Previous</span>
                  </a>
                </li>
              </c:when>
              <c:otherwise>
                <li class="page-item">
                  <a class="page-link" href="search?query=${result.query}&page=${result.page-1}" aria-label="Previous">
                    <span aria-hidden="true">&laquo;</span>
                    <span class="sr-only">Previous</span>
                  </a>
                </li>
              </c:otherwise>
            </c:choose>

            <c:choose>
              <c:when test="${result.maxPage>8}">

                <c:choose>
                  <c:when test="${result.page<=3}">

                    <c:forEach begin="1" end="3" varStatus="loop" var="index">
                      <c:choose>
                        <c:when test="${index == result.page}">
                          <li class="page-item active"><a class="page-link" href="#">${index}</a></li>
                        </c:when>
                        <c:otherwise>
                          <li class="page-item"><a class="page-link" href="search?query=${result.query}&page=${index}">${index}</a></li>
                        </c:otherwise>
                      </c:choose>
                    </c:forEach>
                    <li class="page-item active"><span href="#" class="mx-1">...</span></li>
                    <li class="page-item"><a class="page-link" href="search?query=${result.query}&page=${result.maxPage}">${result.maxPage}</a></li>

                  </c:when>
                  <c:when test="${result.page >= result.maxPage-3}">
                    <li class="page-item"><a class="page-link" href="search?query=${result.query}&page=1">1</a></li>
                    <li class="page-item active"><span href="#" class="mx-1">...</span></li>


                    <c:forEach begin="${result.maxPage-3}" end="${result.maxPage}" varStatus="loop" var="index">
                      <c:choose>
                        <c:when test="${index == result.page}">
                          <li class="page-item active"><a class="page-link" href="#">${index}</a></li>
                        </c:when>
                        <c:otherwise>
                          <li class="page-item"><a class="page-link" href="search?query=${result.query}&page=${index}">${index}</a></li>
                        </c:otherwise>
                      </c:choose>
                    </c:forEach>

                  </c:when>
                  <c:otherwise>

                    <li class="page-item"><a class="page-link" href="search?query=${result.query}&page=1">1</a></li>
                    <li class="page-item active"><span href="#" class="mx-1">...</span></li>

                    <li class="page-item"><a class="page-link" href="search?query=${result.query}&page=${result.page-1}">${result.page-1}</a></li>
                    <li class="page-item active"><a class="page-link" href="#">${result.page}</a></li>
                    <li class="page-item"><a class="page-link" href="search?query=${result.query}&page=${result.page+1}">${result.page+1}</a></li>

                    <li class="page-item active"><span href="#" class="mx-1">...</span></li>
                    <li class="page-item"><a class="page-link" href="search?query=${result.query}&page=${result.maxPage}">${result.maxPage}</a></li>

                  </c:otherwise>
                </c:choose>
              </c:when>
                    
              <c:otherwise>
                <c:forEach begin="1" end="${result.maxPage}" varStatus="loop" var="index">
                  <c:choose>
                    <c:when test="${index == result.page}">
                      <li class="page-item active"><a class="page-link" href="#">${index}</a></li>
                    </c:when>
                    <c:otherwise>
                      <li class="page-item"><a class="page-link" href="search?query=${result.query}&page=${index}">${index}</a></li>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </c:otherwise>
            </c:choose>


            <c:choose>
              <c:when test="${result.hasMore == true}">
                <li class="page-item">
                  <a class="page-link" href="search?query=${result.query}&page=${result.page+1}" aria-label="Next">
                    <span aria-hidden="true">&raquo;</span>
                    <span class="sr-only">Next</span>
                  </a>
                </li>
              </c:when>
              <c:otherwise>
                <li class="page-item disabled">
                  <a class="page-link" href="#" aria-label="Next">
                    <span aria-hidden="true">&raquo;</span>
                    <span class="sr-only">Next</span>
                  </a>
                </li>
              </c:otherwise>
            </c:choose>
          </ul>
        </nav>
      </div>

    </div>

  </div>
</div>