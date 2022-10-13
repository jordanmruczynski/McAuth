package pl.jordii.mcauth.common.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.function.Predicate;


@FunctionalInterface
public interface MethodCriterion extends Predicate<Method> {

  static MethodCriterion annotatedWith(Class<? extends Annotation> annotation) {
    return method -> method.isAnnotationPresent(annotation);
  }

  static MethodCriterion hasParameters(int parameterAmount) {
    return method -> method.getParameters().length == parameterAmount;
  }

  static MethodCriterion locatedIn(Class<?> clazz) {
    return method -> method
            .getDeclaringClass()
            .equals(clazz);
  }

  static MethodCriterion locatedIn(String packageName) {
    return method -> method
            .getDeclaringClass()
            .getPackage()
            .getName()
            .equals(packageName);
  }

  static MethodCriterion fromPredicate(Predicate<Method> predicate) {
    return predicate::test;
  }
}
