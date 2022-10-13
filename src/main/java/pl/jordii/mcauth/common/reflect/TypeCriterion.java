package pl.jordii.mcauth.common.reflect;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;


@FunctionalInterface
public interface TypeCriterion extends Predicate<Class<?>> {


  static TypeCriterion annotatedWith(Class<? extends Annotation> annotation) {
    return c -> c.isAnnotationPresent(annotation);
  }


  static TypeCriterion subclassOf(Class<?> parentClass) {
    return c -> !parentClass.equals(c) && parentClass.isAssignableFrom(c);
  }


  static TypeCriterion locatedIn(String packageName) {
    return c -> c.getPackage().getName().equals(packageName);
  }

  static TypeCriterion fromPredicate(Predicate<Class<?>> predicate) {
    return predicate::test;
  }
}
