<header class="col-12 py-4 px-4 header">
  <div class="row">
    <div class="col-6">
      <div class="row">
        <h3 class="font-weight-bold">
          <a href="${pageContext.request.contextPath}/index" class="custom-header-text">FIBO Viewer</a>
        </h3>
        <h5 class="ml-3 mt-2"> <span class="text-white">${version}</span> </h5>
      </div>
    </div>
    <div class="col-6">
      <div class="row">
        <div class="col-12">
          <form:form method="POST" action="/search" modelAttribute="query"
                     class="ml-2 mr-4" autocomplete="off">
            <div class="input-group custom-search-form">
              <form:input path="value" placeholder="Search ..." id="search-query" 
                          aria-label="Search" class="form-control" type="text"
                          onkeyup="showHint(event)"/>
              <span class="input-group-btn">
                <button class="btn btn-primary" type="submit">
                  <span class="fas fa-search"></span>
                </button>
              </span>
              <div id="autocomplete" class="dropdown-menu col-12">
                <a class="dropdown-item" href="#">Action</a>
              </div>
            </div>
          </form:form>
        </div>
      </div>
    </div>
  </div>
</header>
