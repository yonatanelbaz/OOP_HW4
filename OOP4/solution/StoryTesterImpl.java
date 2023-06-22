package solution;

import org.junit.ComparisonFailure;
import provided.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class StoryTesterImpl implements StoryTester
{

    protected void test(String story, Class<?> testClass,Object curr)throws Exception //curr is the object to test
    {
        if(story==null || testClass ==null) //check if arguments are valid
        {
            throw new IllegalArgumentException();
        }

        String[] lines = story.split("\n");
        Object test=curr;

        //exception related
        int failsCounter = 0;
        String expected="";
        String got="";
        String failedTest="";
        boolean isFailed = false;

        Map<Field,Object> backUpFields =null; //back up of all fields values
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
            switch (keyword) { //check which annotation it is and then find and run the function corresponding to that annotation
                case "Given":
                    isFailed = false;
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
                    if(isFailed)
                    {
                        //make backup
                        for (Map.Entry<Field, Object> fieldEntry : backUpFields.entrySet()) {
                            Field field = fieldEntry.getKey();
                            Object o = fieldEntry.getValue();
                            field.set(curr, o);
                        }
                    }
                    isFailed = false;
                    if (whenStreak == 0) {
                        backUpFields = makeBackUp(test);
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
                    } catch (InvocationTargetException asser)
                    {
                        ComparisonFailure fail = (ComparisonFailure)asser.getCause();
                        if (failsCounter == 0) // if first to fail
                        {
                            expected = fail.getExpected();
                            got = fail.getActual();
                            failedTest = lines[i];
                        }
                        failsCounter++;
                        isFailed = true;
                    }
                    break;
            }
        }
        if(failsCounter > 0)
            throw new StoryTestExceptionImpl(expected,got,failedTest,failsCounter);

    }
    //run a specific test on a specific method from the inheritance tree
    @Override
    public void testOnInheritanceTree(String story, Class<?> testClass) throws Exception
    {
        if(story == null || testClass == null)
            throw new IllegalArgumentException();

        test(story,testClass,testClass.newInstance());
    }

    //run a test on a method found within a nested class
    @Override
    public void testOnNestedClasses(String story, Class<?> testClass) throws Exception {
        if (story == null || testClass == null)
            throw new IllegalArgumentException();

        String firstRow = story.split("\n")[0];
        String statement = firstRow.substring(firstRow.indexOf(" ") + 1, firstRow.lastIndexOf(" "));
        Object createdObject = getClassByNestedClass(Given.class,statement,testClass,null);
        if(createdObject == null)
            throw new GivenNotFoundException();


        //find class to work on
        test(story,createdObject.getClass(), createdObject);
    }
    //when an error is caught on a specific method, save the object which the error was produced on.
    protected static Map<Field,Object> makeBackUp(Object test) throws Exception
    {
        Map<Field,Object> backupFields = new HashMap<>();

        List<Field> fields = Arrays.stream(test.getClass().getDeclaredFields()).toList();
        for(Field field : fields)
        {
            field.setAccessible(true);
            Class<?> c = field.get(test).getClass();
            if((field.get(test)) instanceof Cloneable)//check if cloneable
            {
                //access modifier here is surely true

                Method m =c.getMethod("clone");
                m.setAccessible(true);//just for making sure
                Object o = m.invoke(field.get(test));
                //calls clone
                backupFields.put(field,o);
                continue;
            }
            Class[] classes = {c};
            try {
                Constructor constructor = c.getConstructor(classes);//gets constructor with wanted type arguments
                Object o = constructor.newInstance(field.get(test));
                backupFields.put(field,o);

            }catch (NoSuchMethodException e)
            {
                backupFields.put(field,field.get(test));
            }

        }
        return backupFields;
    }
    protected Map<String, String> parseStory(String string)
    {
        String firstWords = string.substring(0, string.lastIndexOf(" "));
        String lastWord = string.substring(string.lastIndexOf(" ") + 2);//removes &.
        Map<String, String> hm = new HashMap<>();
        hm.put(firstWords, lastWord);//TODO - atoi if int
        return hm;
    }

    //find a specific method in the inheritance tree of a class
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
    //find the method in the nested class's of a class,  in those nested classes search their inheritance tree and so on
    protected static Object getClassByNestedClass(Class<? extends Annotation> requiredAnno, String statement, Class<?> testClass,Object pre) throws Exception {
        {
            Object curr;
            if(pre ==null)
                curr = testClass.newInstance();
            else
            {
                Constructor<?> ctor = testClass.getConstructor(pre.getClass());
                curr = ctor.newInstance(pre);
            }


            if(searchInheritance(requiredAnno,statement,testClass) != null)
            {
                return curr;
            }

            Class[] classes = testClass.getDeclaredClasses();
            for (Class c : classes)
            {
                Object o;
                if(Modifier.isStatic(c.getModifiers()))//checks if static nested
                     o= getClassByNestedClass(requiredAnno, statement, c,null);
                else
                    o = getClassByNestedClass(requiredAnno,statement,c,curr);
                if(o != null)
                    return o;
            }
            return null;
        }
    }
    //checks if method has annotation with statement that matches the given statement
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
