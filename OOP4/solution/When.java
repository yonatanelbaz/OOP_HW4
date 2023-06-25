package solution;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.METHOD;

// This annotation is used to mark a method as a "when" method.
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD})
public @interface When {
    String value() default "";
}
