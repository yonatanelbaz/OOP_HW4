package solution;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface SearchFunction
{
    public Method Search(Class<? extends Annotation> requiredAnno, String statement, Class<?> testClass) throws Exception;
}
