package org.edmcouncil.spec.fibo.view.controller;

import java.util.List;
import org.edmcouncil.spec.fibo.weasel.model.module.FiboModule;
import org.edmcouncil.spec.fibo.weasel.ontology.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.edmcouncil.spec.fibo.view.util.ModelBuilder;
import org.edmcouncil.spec.fibo.weasel.model.details.OwlDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
@Controller
@RequestMapping(value = {"/", "/index", "module"})
public class ModuleController {

  private static final Logger LOG = LoggerFactory.getLogger(ModuleController.class);

  @Autowired
  private DataManager ontologyManager;

  @GetMapping("/json")
  public ResponseEntity<List<FiboModule>> getAllModulesDataAsJson() {
    LOG.debug("[REQ] GET : module / json");
    List<FiboModule> modules = ontologyManager.getAllModulesData();
    return ResponseEntity.ok(modules);
  }

  @GetMapping
  public String getModulesMeta(
      @RequestParam(value = "meta", required = false) String query,
      Model model) {
    LOG.debug("[REQ] GET: module /");
    List<FiboModule> modules = ontologyManager.getAllModulesData();
    ModelBuilder mb = new ModelBuilder(model);

    if (query != null) {
      OwlDetails details = ontologyManager.getDetailsByIri(query);
      mb.ontoDetails(details).isGrouped(true);
    }

    mb.emptyQuery().modelTree(modules);

    model = mb.getModel();

    return "module";
  }

}
