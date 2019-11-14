package org.edmcouncil.spec.fibo.view.controller;

import org.edmcouncil.spec.fibo.view.model.Query;
import org.edmcouncil.spec.fibo.view.service.SearchService;
import org.edmcouncil.spec.fibo.view.util.ModelBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.edmcouncil.spec.fibo.weasel.model.module.FiboModule;
import org.edmcouncil.spec.fibo.weasel.model.details.OwlDetails;
import org.edmcouncil.spec.fibo.weasel.ontology.DataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
@Controller
@RequestMapping("/search")
public class SearchController {

  private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);

  @Autowired
  private SearchService searchService;
  @Autowired
  private DataManager dataManager;

  @PostMapping
  public ModelAndView redirectSearch(@Valid @ModelAttribute("queryValue") Query query) {
    Map<String, Object> model = new HashMap<>();
    model.put("query", query.getValue());
    ModelAndView mv = new ModelAndView("redirect:/search", model);
    return mv;
  }

  @GetMapping
  public String search(@RequestParam("query") String query, Model model) {

    LOG.info("[REQ] GET : search ? query = {{}}", query);
    Query q = new Query();
    q.setValue(query);
    ModelBuilder modelBuilder = new ModelBuilder(model);
    List<FiboModule> modules = dataManager.getAllModulesData();
    searchService.search(query, modelBuilder);
    modelBuilder.modelTree(modules);

    return "search";
  }

  @PostMapping("/json")
  public <T extends OwlDetails> ResponseEntity<T> searchJson(@RequestBody String query, Model model) {

    LOG.info("[REQ] POST : search / json   RequestBody = {{}}", query);
    ModelBuilder modelBuilder = new ModelBuilder(model);

    OwlDetails search = searchService.search(query, modelBuilder);

    return ResponseEntity.ok((T) search);
  }
}
