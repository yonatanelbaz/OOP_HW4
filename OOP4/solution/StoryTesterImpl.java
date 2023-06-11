package solution;

import junit.framework.ComparisonFailure;
import provided.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoryTesterImpl implements StoryTester
{


    protected  void test(String story,Class<?> testClass,SearchFunction s)throws Exception
    {
        if(story==null || testClass ==null)
        {
            throw new IllegalArgumentException();
        }

        String[] lines = story.split(System.lineSeparator());
        Object test = testClass.newInstance();//constructor default

        //exception realted
        int failsCounter = 0;
        String expected="";
        String got="";
        String failedTest="";
        Object backUp =null;
        int whenStreak =0;
        for (int i =0; i<lines.length ;i++)
        {
            int firstSpace =lines[i].indexOf(" ");
            int lastSpace =lines[i].lastIndexOf(" ");

            String keyword= lines[i].substring(0,firstSpace);
            String statement = lines[i].substring(firstSpace+1,lastSpace);
            String argument =  lines[i].substring(lastSpace+1);
            boolean isNumeric = true;
            try
            {
                Integer.parseInt(argument);
            }
            catch(Exception e)
            {
                isNumeric = false;
            }

            Method m;
            switch(keyword)
            {
                case "Given":
                    whenStreak=0;
                     m = s.Search(Given.class,statement,testClass);
                    if(m == null)
                        throw new GivenNotFoundException();
                    //find given method and apply
                    if(isNumeric)
                    {
                        m.invoke(test,Integer.parseInt(argument));
                    }
                    else
                    {
                        m.invoke(test,argument);
                    }
                    break;
                case "When":
                    if (whenStreak == 0)
                    {
                        backUp = makeBackUp(test);
                    }
                    whenStreak++;
                    //find when method
                    m = s.Search(When.class,statement,testClass);
                    if(m == null)
                        throw new WhenNotFoundException();
                    if(isNumeric)
                    {
                        m.invoke(test,Integer.parseInt(argument));
                    }
                    else
                    {
                        m.invoke(test,argument);
                    }
                    break;
                case "Then":
                    whenStreak=0;
                    m = s.Search(Then.class,statement,testClass);
                    if(m == null)
                        throw new ThenNotFoundException();

                    try{
                        if(isNumeric)
                        {
                            m.invoke(test,Integer.parseInt(argument));
                        }
                        else
                        {
                            m.invoke(test,Integer.parseInt(argument));
                        }                    }
                    catch(ComparisonFailure fail)
                    {
                        if(failsCounter == 0) // if first to fail
                        {
                            expected = fail.getExpected();
                            got = fail.getActual();
                            failedTest = lines[i];
                        }
                        failsCounter++;
                        test = backUp;
                    }
                    break;
            }
        if(failsCounter > 0)
            throw new StoryTestExceptionImpl(expected,got,failedTest,failsCounter);
        }

    }
    @Override
    public void testOnInheritanceTree(String story, Class<?> testClass) throws Exception
    {
        test(story,testClass,StoryTesterImpl::getMethodByInheritance);
    }



    @Override
    public void testOnNestedClasses(String story, Class<?> testClass) throws Exception
    {
        test(story,testClass,StoryTesterImpl::getMethodByNestedClass);
    }
    protected static Object makeBackUp(Object test) throws Exception {
        Object backUpTest = (test.getClass()).newInstance();
        List<Field> fields = Arrays.stream(test.getClass().getFields()).toList();
        for(Field field : fields)
        {
            Class<?> c = field.get(test).getClass();
            if((field.get(test)) instanceof Cloneable)//check if cloneable
            {
                //access modifier here is surely true
                Object o =c.getMethod("clone").invoke(field.get(test)); //calls clone
                field.set(backUpTest,o);
                continue;
            }
            Class[] classes = {c};
            Constructor constructor = c.getConstructor(classes);//gets constructor with wanted type arguments
            if(constructor != null)
            {
                field.set(backUpTest,constructor.newInstance(field.get(test)));
                continue;
            }
            field.set(backUpTest,field.get(test));
        }
        return backUpTest;
    }
    protected Map<String, String> parseStory(String string)
    {
        String firstWords = string.substring(0, string.lastIndexOf(" "));
        String lastWord = string.substring(string.lastIndexOf(" ") + 2);//removes &.
        Map<String, String> hm = new HashMap<>();
        hm.put(firstWords, lastWord);//TODO - atoi if int
        return hm;
    }

    protected static Method getMethodByInheritance(Class<? extends Annotation> requiredAnno , String statement, Class<?> testClass) throws Exception {
        Method[] methods = testClass.getDeclaredMethods();
        for (Method m : methods) {
            //check for annotation
           if(checkAnnotation(m,requiredAnno,statement))
               return m;
        }
        if(testClass.getSuperclass() == null) {
            return null;
        }
        return getMethodByInheritance(requiredAnno,statement, testClass.getSuperclass());
    }

    protected static Method getMethodByNestedClass(Class<? extends Annotation> requiredAnno, String statement, Class<?> testClass) throws Exception {
        Method[] methods = testClass.getDeclaredMethods();
        for (Method m : methods) {
            if(checkAnnotation(m,requiredAnno,statement)){
                return m;
            }
        }
        Class[] classes = testClass.getDeclaredClasses();
        for (Class c : classes) {
            return getMethodByNestedClass(requiredAnno,statement, c);
        }
        return null;
    }
    protected static boolean checkAnnotation(Method m, Class<? extends Annotation> requiredAnno,String statement) throws Exception {
        Annotation methodAnno = m.getAnnotation(requiredAnno);
        if (methodAnno != null) {
            Field f = methodAnno.getClass().getField("value");
            //parse value
            String s = (String) f.get(methodAnno);
            s.substring(0, s.lastIndexOf(" "));
            if (f.get(methodAnno).equals(statement))
                return true;
        }
        return false;
    }
}
