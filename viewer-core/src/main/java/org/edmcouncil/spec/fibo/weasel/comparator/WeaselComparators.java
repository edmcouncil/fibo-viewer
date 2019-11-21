package org.edmcouncil.spec.fibo.weasel.comparator;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.element.StringItem;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class WeaselComparators {

  /**
   *
   * @param prioritySortList
   * @return
   */

  public static Comparator<String> getComparatorWithPriority(List<StringItem> prioritySortList) {
    return (String obj1, String obj2) -> {

      if (obj1 == obj2) {
        return 0;
      }
      if (obj1 == null) {
        return -1;
      }
      if (obj2 == null) {
        return 1;
      }

      Optional var1 = prioritySortList


              .stream()
              .map(StringItem::toString)
              .filter(obj1::equals)
              .findFirst();
      Optional var2 = prioritySortList
              .stream()
              .map(StringItem::toString)
              .filter(obj2::equals)
              .findFirst();

      boolean containsObj1 = var1.isPresent();
      boolean containsObj2 = var2.isPresent();
      if (containsObj1 && !containsObj2) {
        return -1;
      }
      if (!containsObj1 && containsObj2) {
        return 1;
      }
      if (containsObj1 && containsObj2) {
        List<String> conv = prioritySortList.stream().map(a -> a.toString()).collect(Collectors.toList());
        int idxObj1 = conv.indexOf(obj1);
        int idxObj2 = conv.indexOf(obj2);

        return idxObj1 < idxObj2 ? -1 : 1;
      }

      return obj1.toLowerCase().compareTo(obj2.toLowerCase());

    };
  }

  /**
   *
   * The compares compare the object
   *
   * @return
   */
  public static Comparator<Object> getComparatorWithAlphabeticalOrder() {
    return (Object obj1, Object obj2) -> {
      if (obj1 == obj2) {
        return 0;
      }
      if (obj1 == null) {
        return -1;
      }
      if (obj2 == null) {
        return 1;
      }
      return obj1.toString().toLowerCase()
          .compareTo(obj2.toString().toLowerCase());

    };
  }

}
