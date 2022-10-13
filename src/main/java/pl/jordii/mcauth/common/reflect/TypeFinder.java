package pl.jordii.mcauth.common.reflect;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.inject.Singleton;
import io.github.classgraph.*;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;


@Singleton
public class TypeFinder {


  public Stream<Class<?>> filter(String[] packageNames, TypeCriterion... typeCriteria) {
    // validate given parameters
    Preconditions.checkNotNull(packageNames);
    Preconditions.checkNotNull(typeCriteria);

    Set<Class<?>> output = Sets.newHashSet();

    try (ScanResult scanResult = new ClassGraph()
            .enableAllInfo()
            .whitelistPackages(packageNames)
            .scan()) {

      ClassInfoList allClasses = scanResult.getAllClasses();

      // iterate all classes in the packages and check if they
      // math the criteria
      for (ClassInfo current : allClasses) {
        Class<?> c = current.loadClass();
        boolean allMatch = true;
        for (TypeCriterion typeCriterion : typeCriteria) {
          if (!typeCriterion.test(c)) {
            allMatch = false;
            break;
          }
        }
        if (allMatch) {
          output.add(c);
        }
      }
    }

    return output.stream().filter(Objects::nonNull);
  }

}
