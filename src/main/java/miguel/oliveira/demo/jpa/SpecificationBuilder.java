package miguel.oliveira.demo.jpa;

import java.time.Instant;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class SpecificationBuilder {

  private static final char LIKE_ESCAPE_CHAR = '!';
  private static final String LIKE_ESCAPE_STRING = "!";

  <T> Specification<T> likeSpecification(
      Function<Root<T>, Expression<String>> expression,
      String value) {
    return likeSpecification((root, cb) -> expression.apply(root), value);
  }

  <T> Specification<T> likeSpecification(
      BiFunction<Root<T>, CriteriaBuilder, Expression<String>> expression,
      String value) {
    if (value != null) {
      return (root, query, cb) -> likePredicate(cb, expression.apply(root, cb), value);
    }
    return Specification.where(null);
  }

  Predicate likePredicate(CriteriaBuilder cb, Expression<String> expression, String value) {
    Expression<String> lowerExpression = cb.lower(expression);

    boolean requiresEscape = value.contains("%") || value.contains("_");
    if (requiresEscape) {
      String escapedValue = value
          .replace(LIKE_ESCAPE_STRING, LIKE_ESCAPE_CHAR + LIKE_ESCAPE_STRING)
          .replace("%", LIKE_ESCAPE_CHAR + "%")
          .replace("_", LIKE_ESCAPE_CHAR + "_");
      String likeExpression = "%".concat(escapedValue.toLowerCase(Locale.getDefault())).concat("%");
      return cb.like(lowerExpression, likeExpression, LIKE_ESCAPE_CHAR);
    } else {
      String likeExpression = "%".concat(value.toLowerCase(Locale.getDefault())).concat("%");
      return cb.like(lowerExpression, likeExpression);
    }
  }

  <T> Specification<T> instantEqualSpecification(
      Function<Root<T>, Expression<Instant>> expression,
      Long timestamp
  ) {
    if (timestamp != null) {
      return (root, query, cb) -> cb.equal(expression.apply(root), Instant.ofEpochMilli(timestamp));
    }
    return Specification.where(null);
  }

  <T> Specification<T> instantSpecification(
      Function<Root<T>, Expression<Instant>> expression,
      Long valueFrom,
      Long valueTo) {
    if (Objects.nonNull(valueFrom) && Objects.nonNull(valueTo)) {
      return (root, query, cb) -> cb
          .between(expression.apply(root), Instant.ofEpochMilli(valueFrom),
              Instant.ofEpochMilli(valueTo));
    } else if (Objects.nonNull(valueFrom)) {
      return (root, query, cb) -> cb
          .greaterThanOrEqualTo(expression.apply(root), Instant.ofEpochMilli(valueFrom));
    } else if (Objects.nonNull(valueTo)) {
      return (root, query, cb) -> cb
          .lessThanOrEqualTo(expression.apply(root), Instant.ofEpochMilli(valueTo));
    }
    return null;
  }

  <T, V> Specification<T> inSpecification(Function<Root<T>, Expression<V>> expression,
      Collection<V> values) {
    if (!CollectionUtils.isEmpty(values)) {
      return (root, query, cb) -> expression.apply(root).in(values);
    }
    return null;
  }

}
