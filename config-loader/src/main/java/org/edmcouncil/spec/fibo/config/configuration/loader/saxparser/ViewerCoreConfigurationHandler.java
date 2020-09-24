package org.edmcouncil.spec.fibo.config.configuration.loader.saxparser;

import org.edmcouncil.spec.fibo.config.configuration.model.ConfigItemType;
import org.edmcouncil.spec.fibo.config.configuration.model.GroupType;
import org.edmcouncil.spec.fibo.config.configuration.model.ConfigKeys;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.BooleanItem;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.GroupsItem;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.MissingLanguageItem;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.LabelPriority;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.StringItem;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.RenameItem;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.ViewerCoreConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.edmcouncil.spec.fibo.config.configuration.model.ConfigItem;
import org.edmcouncil.spec.fibo.config.configuration.model.searcher.TextSearcherConfig;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.DefaultLabelItem;
import org.edmcouncil.spec.fibo.config.configuration.model.searcher.SearcherField;

/**
 * Configuration reader from xml file
 *
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class ViewerCoreConfigurationHandler extends DefaultHandler {

  private static final Logger LOG = LoggerFactory.getLogger(ViewerCoreConfigurationHandler.class);

  private ViewerCoreConfiguration config = new ViewerCoreConfiguration();
  String key = null;
  String val = null;

  RenameItem cre = null;
  GroupsItem cge = null;
  DefaultLabelItem dli = null;
  TextSearcherConfig tsc = null;
  SearcherField sf = null;

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    switch (qName) {
      case ConfigKeys.PRIORITY_LIST:
      case ConfigKeys.IGNORE_TO_DISPLAYING:
      case ConfigKeys.IGNORE_TO_LINKING:
      case ConfigKeys.SCOPE_IRI:
      case ConfigKeys.GROUPS:
      case ConfigKeys.ONTOLOGY_URL:
      case ConfigKeys.ONTOLOGY_PATH:
      case ConfigKeys.ONTOLOGY_DIR:
      case ConfigKeys.ONTOLOGY_MAPPER:
      case ConfigKeys.DISPLAY_LABEL:
      case ConfigKeys.FORCE_LABEL_LANG:
      case ConfigKeys.LABEL_LANG:
      case ConfigKeys.LABEL_PRIORITY:
      case ConfigKeys.MISSING_LANGUAGE_ACTION:
      case ConfigKeys.USER_DEFAULT_NAME_LIST:
        this.key = qName;
        break;
      case ConfigKeys.GROUP:
        cge = new GroupsItem();
        cge.setGroupType(GroupType.DEFAULT);
        break;
      case ConfigKeys.USER_DEFINED_NAME:
        dli = new DefaultLabelItem();
        break;
      case ConfigKeys.TEXT_SEARCH_CONFIG:
        this.key = qName;
        tsc = new TextSearcherConfig();
        break;
      case ConfigKeys.HINT_FIELD:
      case ConfigKeys.SEARCH_FIELD:
        sf = new SearcherField();
        break;

    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {

    switch (qName) {
      case ConfigKeys.PRIORITY_LIST_ELEMENT:
      case ConfigKeys.IGNORED_ELEMENT:
      case ConfigKeys.URI_NAMESPACE:
      case ConfigKeys.LABEL_LANG:
        if (!val.trim().isEmpty()) {
          ConfigItem configEl = new StringItem(val);
          config.addCongigElement(key, configEl);
          configEl = null;
        }
        break;
      case ConfigKeys.GROUP:
        config.addCongigElement(key, cge);
        break;
      case ConfigKeys.GROUP_NAME:
        cge.setName(val);
        break;
      case ConfigKeys.GROUP_ITEM:
        cge.addElement(new StringItem(val));
        break;
      case ConfigKeys.ONTOLOGY_URL:
      case ConfigKeys.ONTOLOGY_DIR:
      case ConfigKeys.ONTOLOGY_PATH:
      case ConfigKeys.ONTOLOGY_MAPPER:
        StringItem ontologyPath = new StringItem(val);
        config.addCongigElement(key, ontologyPath);
        ontologyPath = null;
        break;
      case ConfigKeys.DISPLAY_LABEL:
      case ConfigKeys.FORCE_LABEL_LANG:
        BooleanItem cbe = new BooleanItem();
        cbe.setType(ConfigItemType.BOOLEAN);
        cbe.setValue(Boolean.valueOf(val));
        config.addCongigElement(key, cbe);
        cbe = null;
        break;
      case ConfigKeys.LABEL_PRIORITY:
        LabelPriority cpe = new LabelPriority();
        cpe.setType(ConfigItemType.PRIORITY);
        cpe.setValue(LabelPriority.Priority.valueOf(val));
        config.addCongigElement(key, cpe);
        cpe = null;
        break;
      case ConfigKeys.MISSING_LANGUAGE_ACTION:
        MissingLanguageItem cmle = new MissingLanguageItem();
        cmle.setType(ConfigItemType.MISSING_LANGUAGE_ACTION);
        cmle.setValue(MissingLanguageItem.Action.valueOf(val));
        config.addCongigElement(key, cmle);
        cmle = null;
        break;
      case ConfigKeys.RESOURCE_IRI_TO_NAME:
        dli.setIri(val);
        break;
      case ConfigKeys.RESOURCE_IRI_NAME:
        dli.setLabel(val);
        break;
      case ConfigKeys.USER_DEFINED_NAME:
        config.addCongigElement(key, dli);
        break;
      case ConfigKeys.HINT_FIELD:
        tsc.addHintField(sf);
        break;
      case ConfigKeys.SEARCH_FIELD:
        tsc.addSearchField(sf);
        break;
      case ConfigKeys.FIELD_IRI:
        sf.setIri(val);
        break;
      case ConfigKeys.FIELD_BOOST:
        sf.setBoost(Double.valueOf(val));
        break;
      case ConfigKeys.HINT_THRESHOLD:
        tsc.setHintThreshold(Double.valueOf(val));
        break;
      case ConfigKeys.SEARCH_THRESHOLD:
        tsc.setSearchThreshold(Double.valueOf(val));
        break;
      case ConfigKeys.SEARCH_DESCRIPTION:
        tsc.addSearchDescription(val);
        break;
      case ConfigKeys.TEXT_SEARCH_CONFIG:
        config.addCongigElement(key, tsc);
        tsc = null;
        break;

    }
  }

  @Override
  public void characters(char ch[], int start, int length) throws SAXException {
    val = new String(ch, start, length);
  }

  public ViewerCoreConfiguration getConfiguration() {
    return this.config;
  }
}
