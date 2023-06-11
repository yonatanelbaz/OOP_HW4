package solution;

import junit.framework.ComparisonFailure;
import provided.StoryTestException;
import provided.StoryTester;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class StoryTesterImpl implements StoryTester
{
    @Override
    public void testOnInheritanceTree(String story, Class<?> testClass) throws Exception
    {
        if(story==null || testClass ==null)
        {
            throw new IllegalArgumentException();
        }
        String[] lines = story.split(System.lineSeparator());
        Object test = testClass.newInstance();//constructor default
        StoryTestException exception = null;

        int whenStreak =0;
        for (int i =0;i<lines.length;i++)
        {
             int firstSpace =lines[i].indexOf(" ");
             int lastSpace =lines[i].lastIndexOf(" ");

             String keyword= lines[i].substring(0,firstSpace);
             String statement = lines[i].substring(firstSpace+1,lastSpace);
             String argument =  lines[i].substring(lastSpace+1);

             switch(keyword)
             {
                 case "Given":
                     whenStreak=0;
                     //find given method and apply
                     Method m;
                     m.invoke(test,argument);
                     break;
                 case "When":
                     if (whenStreak == 0) {
                         //backup
                         //break;
                     }
                     whenStreak++;
                     //find when method
                     Method m;
                     m.invoke(test,argument);
                     break;
                 case "Then":
                     whenStreak=0;
                     Method m;
                     try{
                         m.invoke(test,argument);
                     }
                     catch(ComparisonFailure fail)
                     {
                        if(exception == null)
                        {
                            exception = new StoryTestExceptionImpl();

                        }
                     }
                     //find then method

                     break;
                 //default: //must be valid
             }

        }

    }

    @Override
    public void testOnNestedClasses(String story, Class<?> testClass) throws Exception
    {
        if(story==null || testClass ==null)
        {
            throw new IllegalArgumentException();
        }
    }
    public Map<String, String> getFields(String string) {

        String firstWords = string.substring(0, string.lastIndexOf(" "));
        String lastWord = string.substring(string.lastIndexOf(" ") + 2);//removes &.
        Map<String, String> hm = new HashMap<>();
        hm.put(firstWords, lastWord);//TODO - atoi if int
        return hm;
    }

    public Method getMethodByInheritance(String methodName, Class<?> testClass) {
        Method[] methods = testClass.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals(methodName)) {
                return m;
            }
        }
        if(testClass.getSuperclass() == null) {
            return null;
        }
        return getMethodByInheritance(methodName, testClass.getSuperclass());
    }

    public Method getMethodByInnerClass(String methodName, Class<?> testClass) {
        Method[] methods = testClass.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals(methodName)) {
                return m;
            }
        }
        Class[] Classes = testClass.getDeclaredClasses();
        if(Classes.length != 0) {
            for (Class c : Classes) {
                return getMethodByInnerClass(methodName, c);

            }
        }
        return null;
    }

}
