package org.edmcouncil.spec.fibo.weasel.ontology.generator;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static org.springframework.util.StringUtils.capitalize;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.WeaselOwlType;
import org.edmcouncil.spec.fibo.weasel.model.details.OwlGroupedDetails;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlAnnotationPropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlAxiomPropertyEntity;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlAxiomPropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.taxonomy.OwlTaxonomyElementImpl;
import org.edmcouncil.spec.fibo.weasel.model.taxonomy.OwlTaxonomyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DescriptionGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(DescriptionGenerator.class);

  static final String IS_A_RESTRICTIONS_LABEL = "IS-A restrictions";
  static final String IS_A_RESTRICTIONS_INHERITED_LABEL = "IS-A restrictions inherited from superclasses";
  static final String ONTOLOGICAL_CHARACTERISTIC_LABEL = "Ontological characteristic";
  private static final String ARG_PATTERN = "/arg\\d+/";
  private static final String COMPLEX_PROPERTY_PATTERN = "/arg\\d+/( [a-zA-Z0-9]+ )\\(/arg\\d+/.*";
  private static final String PROPERTY_PATTERN = "/arg\\d+/( .* )/arg\\d+/";
  private static final Map<String, String> REPLACEMENTS = new HashMap<>();
  private static final String SPLIT_DELIMITER = "SPLIT_HERE";

  static {
    REPLACEMENTS.put("[", "");
    REPLACEMENTS.put("]", "");
    REPLACEMENTS.put("(", "");
    REPLACEMENTS.put(")", "");
    REPLACEMENTS.put("min", "at least");
    REPLACEMENTS.put("max", "at most");
  }

  public Optional<List<OwlAnnotationPropertyValue>> prepareDescriptionString(OwlGroupedDetails groupedDetails) {
    Map<String, List<PropertyValue>> ontologicalCharacteristics =
        groupedDetails.getProperties().getOrDefault(ONTOLOGICAL_CHARACTERISTIC_LABEL, Collections.emptyMap());

    String description = prepareDescriptionString(
        groupedDetails.getLabel(),
        (OwlTaxonomyImpl) groupedDetails.getTaxonomy(),
        ontologicalCharacteristics);

    if (description.trim().isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(prepareListOfPropertyValues(description));
  }

  private List<OwlAnnotationPropertyValue> prepareListOfPropertyValues(String description) {
    return Arrays.stream(description.split(SPLIT_DELIMITER))
        .map(part -> {
          part = part.trim();
          OwlAnnotationPropertyValue descriptionVaLue = new OwlAnnotationPropertyValue();
          descriptionVaLue.setValue(part);
          descriptionVaLue.setType(WeaselOwlType.OTHER);
          return descriptionVaLue;
        })
        .collect(Collectors.toList());
  }

  private String prepareDescriptionString(String label,
                                          OwlTaxonomyImpl taxonomy,
                                          Map<String, List<PropertyValue>> ontologicalCharacteristics) {
    GeneratorManager manager = new GeneratorManager(label);

    var superClasses = getSuperClasses(taxonomy);
    if (!superClasses.isEmpty()) {
      manager.getSb().append(capitalize(label)).append(" is a kind of ").append(superClasses).append(".\n");
    }

    appendRestrictions(
        ontologicalCharacteristics,
        manager,
        IS_A_RESTRICTIONS_LABEL,
        "");
    manager.getSb().append(SPLIT_DELIMITER);
    appendRestrictions(
        ontologicalCharacteristics,
        manager,
        IS_A_RESTRICTIONS_INHERITED_LABEL,
        "Inherited from the ancestor classes:\n");

    return cleanAndPolishGeneratedDescription(manager.getSb());
  }

  private String cleanAndPolishGeneratedDescription(StringBuilder sb) {
    String result = sb.toString().trim();

    result = improveGeneratedDescription(result);
    result = improveReadabilityOfRestrictions(result);

    return result;
  }

  private String improveGeneratedDescription(String result) {
    for (Map.Entry<String, String> entry : REPLACEMENTS.entrySet()) {
      result = result.replace(entry.getKey(), entry.getValue());
    }
    return result;
  }

  private void appendRestrictions(Map<String, List<PropertyValue>> ontologicalCharacteristics,
                                  GeneratorManager manager,
                                  String groupRestrictionsName,
                                  String toAppendBefore) {

    List<PropertyValue> propertyValues = ontologicalCharacteristics.getOrDefault(groupRestrictionsName, emptyList());

    if (propertyValues.size() > 1) {
      manager.getSb().append(toAppendBefore);
    }

    for (PropertyValue property : propertyValues) {
      if (property instanceof OwlAxiomPropertyValue) {
        OwlAxiomPropertyValue axiomProperty = (OwlAxiomPropertyValue) property;
        Map<String, OwlAxiomPropertyEntity> entityMapping = axiomProperty.getEntityMaping();

        String propertyPattern = axiomProperty.getValue().trim();

        try {
          if (entityMapping.size() == 2) {
            handlePropertyWithTwoArguments(manager, axiomProperty, propertyPattern);
          } else if (entityMapping.size() == 4) {
            handlePropertyWithFourArguments(manager, axiomProperty, propertyPattern);
          }
        } catch (GeneratorException ex) {
          LOG.warn("Exception thrown while processing property '{}'. Details: {}", axiomProperty, ex.getMessage());
        }
      }
    }
  }

  private void handlePropertyWithTwoArguments(GeneratorManager manager,
                                              OwlAxiomPropertyValue property,
                                              String propertyPattern) throws GeneratorException {
    Map<String, OwlAxiomPropertyEntity> entityMapping = property.getEntityMaping();

    String firstArgumentId = propertyPattern.substring(0, propertyPattern.indexOf(" "));
    String secondArgumentId = propertyPattern.substring(propertyPattern.lastIndexOf(" ") + 1);

    if (entityMapping.containsKey(firstArgumentId) && entityMapping.containsKey(secondArgumentId)) {
      String firstArgument = entityMapping.get(firstArgumentId).getLabel();
      String secondArgument = entityMapping.get(secondArgumentId).getLabel();

      manager.getSb()
          .append("- ")
          .append(capitalize(manager.getLabel()))
          .append(" ");

      if (firstArgument.trim().startsWith("has")) {
        String partOfProperty = extractPartAfterFirstSpace(firstArgument);
        if (partOfProperty.equalsIgnoreCase(secondArgument)) {
          manager.getSb().append("has")
              .append(extractCenterOfPattern(propertyPattern))
              .append(secondArgument);
        } else {
          manager.getSb().append("has")
              .append(extractCenterOfPattern(propertyPattern))
              .append(partOfProperty)
              .append(" that is ")
              .append(secondArgument);
        }
      } else {
        manager.getSb().append(
            propertyPattern
                .replaceFirst(ARG_PATTERN, firstArgument)
                .replaceFirst(ARG_PATTERN, secondArgument));
      }
      manager.getSb().append(".\n");
    } else {
      LOG.debug("Entity mapping for property '{}' doesn't contains arguments '{}' or '{}'. Entity mapping: {}",
          property.getValue(), firstArgumentId, secondArgumentId, entityMapping);
    }
  }

  private void handlePropertyWithFourArguments(GeneratorManager manager,
                                               OwlAxiomPropertyValue property,
                                               String propertyPattern) throws GeneratorException {
    var entityMapping = property.getEntityMaping();

    var editedPattern = propertyPattern;
    var mappingKeys = entityMapping.keySet().stream().sorted().collect(Collectors.toList());
    for (String entityId : mappingKeys) {
      editedPattern = editedPattern.replace(entityId, entityMapping.get(entityId).getLabel());
    }

    manager.getSb()
        .append("- ")
        .append(capitalize(manager.getLabel()))
        .append(" ");

    var firstArgument = entityMapping.get(mappingKeys.get(0)).getLabel();
    if (firstArgument.startsWith("has")) {
      var centerOfPattern = extractCenterOfComplexPattern(propertyPattern);
      manager.getSb()
          .append("has")
          .append(centerOfPattern)
          .append(extractPartAfterFirstSpace(firstArgument))
          .append(" that is ")
          .append(editedPattern.substring(editedPattern.indexOf(centerOfPattern) + centerOfPattern.length()));
    } else {
      manager.getSb().append(editedPattern);
    }
    manager.getSb().append(".\n");
  }

  private String extractCenterOfPattern(String propertyPattern) throws GeneratorException {
    var pattern = Pattern.compile(PROPERTY_PATTERN);
    return extractGroupFromPattern(propertyPattern, pattern);
  }

  private String extractCenterOfComplexPattern(String propertyPattern) throws GeneratorException {
    var pattern = Pattern.compile(COMPLEX_PROPERTY_PATTERN);
    return extractGroupFromPattern(propertyPattern, pattern);
  }

  private String extractGroupFromPattern(String propertyPattern, Pattern regex) throws GeneratorException {
    var matcher = regex.matcher(propertyPattern);
    if (matcher.matches()) {
      return matcher.group(1);
    } else {
      throw new GeneratorException(
          String.format(
              "Not able to find a matching group with regex '%s' in the property pattern '%s",
              regex.pattern(),
              propertyPattern));
    }
  }

  private List<String> getSuperClasses(OwlTaxonomyImpl taxonomy) {
    var result = new HashSet<String>();

    if (taxonomy != null) {
      for (List<OwlTaxonomyElementImpl> taxonomyLine : taxonomy.getValue()) {
        if (taxonomyLine.size() > 1) {
          result.add(taxonomyLine.get(taxonomyLine.size() - 2).getLabel());
        }
      }
    }

    return result.stream().sorted().collect(Collectors.toList());
  }

  private String extractPartAfterFirstSpace(String firstArgument) {
    var splitted = firstArgument.split(" ");
    if (splitted.length > 1) {
      return String.join(" ", Arrays.copyOfRange(splitted, 1, splitted.length));
    } else {
      return firstArgument;
    }
  }


  private String improveReadabilityOfRestrictions(String restrictionsString) {
    return Arrays.stream(restrictionsString.split("\n"))
        .map(DescriptionGenerator::improveReadabilityOfRestriction)
        .collect(joining("\n"));
  }

  private static String improveReadabilityOfRestriction(String restriction) {
    if (restriction.contains("has at least 0")) {
      // X 'has at least 0' Y   ->   X 'may have' Y
      return restriction.replace("has at least 0", "may have")
          .replaceFirst(" by ", " by some");
    } else if (restriction.contains("at least 0")) {
      // X 'is' Y 'at least 0' Z   ->   X 'may be' Y Z
      return restriction.replaceFirst("is", "may be")
          .replaceFirst("at least 0", "")
          .replaceFirst(" by ", " by some");
    } else {
      return restriction;
    }
  }
}