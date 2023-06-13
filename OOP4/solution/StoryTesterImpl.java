package solution;

import org.junit.ComparisonFailure;
import provided.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class StoryTesterImpl implements StoryTester
{


    protected void test(String story, Class<?> testClass,Object curr,Object pre)throws Exception
    {
        if(story==null || testClass ==null)
        {
            throw new IllegalArgumentException();
        }

        String[] lines = story.split("\n");
        Object test=curr;

        //exception realted
        int failsCounter = 0;
        String expected="";
        String got="";
        String failedTest="";

        Object backUp =null;
        int whenStreak =0;
        for (int i =0; i < lines.length; i++) {
            int firstSpace = lines[i].indexOf(" ");
            int lastSpace = lines[i].lastIndexOf(" ");

            String keyword = lines[i].substring(0, firstSpace);
            String statement = lines[i].substring(firstSpace + 1, lastSpace);
            String argument = lines[i].substring(lastSpace + 1);
            boolean isNumeric = true;

            try {
                Integer.parseInt(argument);
            } catch (Exception e) {
                isNumeric = false;
            }

            Method m;
            switch (keyword) {
                case "Given":
                    whenStreak = 0;
                    m = searchInheritance(Given.class, statement, testClass);
                    if (m == null)
                        throw new GivenNotFoundException();
                    m.setAccessible(true);
                    //find given method and apply
                    if (isNumeric)
                    {
                        m.invoke(test, Integer.parseInt(argument));
                    }
                    else
                    {
                        m.invoke(test, argument);
                    }
                    break;
                case "When":
                    if (whenStreak == 0) {
                        backUp = makeBackUp(test,pre);
                    }
                    whenStreak++;
                    //find when method
                    m = searchInheritance(When.class, statement, testClass);
                    if (m == null)
                        throw new WhenNotFoundException();
                    m.setAccessible(true);
                    if (isNumeric) {
                        m.invoke(test, Integer.parseInt(argument));
                    } else {
                        m.invoke(test, argument);
                    }
                    break;
                case "Then":
                    whenStreak = 0;
                    m = searchInheritance(Then.class, statement, testClass);
                    if (m == null)
                        throw new ThenNotFoundException();
                    m.setAccessible(true);
                    try {
                        if (isNumeric) {
                            m.invoke(test, Integer.parseInt(argument));
                        } else {
                            m.invoke(test, argument);
                        }
                    } catch (InvocationTargetException asser) {
                        ComparisonFailure fail = (ComparisonFailure) asser.getCause();
                        if (failsCounter == 0) // if first to fail
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
        }
        if(failsCounter > 0)
            throw new StoryTestExceptionImpl(expected,got,failedTest,failsCounter);

    }
    @Override
    public void testOnInheritanceTree(String story, Class<?> testClass) throws Exception
    {
        if(story == null || testClass == null)
            throw new IllegalArgumentException();

        test(story,testClass,testClass.newInstance(),null);
    }


    @Override
    public void testOnNestedClasses(String story, Class<?> testClass) throws Exception {
        if (story == null || testClass == null)
            throw new IllegalArgumentException();

        String firstRow = story.split("\n")[0];
        String statement = firstRow.substring(firstRow.indexOf(" ") + 1, firstRow.lastIndexOf(" "));
        List<Object> objectsToCreate = getClassByNestedClass(Given.class,statement,testClass,null);
        if(objectsToCreate == null)
            throw new GivenNotFoundException();


        //find class to work on
        test(story,objectsToCreate.get(0).getClass(), objectsToCreate.get(0),objectsToCreate.get(1));
    }
    protected static Object makeBackUp(Object test,Object pre) throws Exception
    {
        Object backUpTest;
        if(pre == null)
             backUpTest = (test.getClass()).newInstance();
        else//if non-static nested
        {
            Constructor<?> ctor = (test.getClass()).getDeclaredConstructor(pre.getClass());
            backUpTest = ctor.newInstance(pre);
        }

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

    protected static Method searchInheritance(Class<? extends Annotation> requiredAnno , String statement, Class<?> testClass) throws Exception {
        Method[] methods = testClass.getDeclaredMethods();
        for (Method m : methods) {
            //check for annotation
           if(checkAnnotation(m,requiredAnno,statement))
               return m;
        }
        if(testClass.getSuperclass() == null) {
            return null;
        }
        return searchInheritance(requiredAnno,statement, testClass.getSuperclass());
    }

    protected static List<Object> getClassByNestedClass(Class<? extends Annotation> requiredAnno, String statement, Class<?> testClass,Object pre) throws Exception {
        {
            Object curr;
            if(pre ==null)
                curr = testClass.newInstance();
            else
            {
                Constructor<?> ctor = testClass.getConstructor(pre.getClass());
                curr = ctor.newInstance(pre);
            }

            Method[] methods = testClass.getDeclaredMethods();
            for (Method m : methods)
            {
                if(searchInheritance(requiredAnno,statement,testClass) != null)
                {
                    List list = new ArrayList();
                    list.add(curr);
                    list.add(pre);
                    return list;
                }
            }
            Class[] classes = testClass.getDeclaredClasses();
            for (Class c : classes)
            {
                List l;
                if(Modifier.isStatic(c.getModifiers()))//checks if static nested
                     l = getClassByNestedClass(requiredAnno, statement, c,null);
                else
                    l = getClassByNestedClass(requiredAnno,statement,c,curr);
                if(l != null)
                    return l;
            }
            return null;
        }
    }
    protected static boolean checkAnnotation(Method m, Class<? extends Annotation> requiredAnno,String statement) throws Exception {
        Annotation methodAnno = m.getAnnotation(requiredAnno);
        if (methodAnno != null) {
            Method f = methodAnno.getClass().getMethod("value");
            //parse value

            String s = (String) f.invoke(methodAnno);
            s=s.substring(0, s.lastIndexOf(" "));
            if (s.equals(statement))
                return true;
        }
        return false;
    }
}
