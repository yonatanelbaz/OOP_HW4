package solution;

import junit.framework.ComparisonFailure;
import provided.StoryTestException;
import provided.StoryTester;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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

        //exception realted
        int failsCounter = 0;
        String expected;
        String got;
        String failedTest;

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
                         //break
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
                        if(failsCounter == 0)
                        {
                            expected = fail.getExpected();
                            got = fail.getActual();
                            failedTest = lines[i];
                        }
                        failsCounter++;
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
    protected Map<String, String> getFields(String string)
    {

        String firstWords = string.substring(0, string.lastIndexOf(" "));
        String lastWord = string.substring(string.lastIndexOf(" ") + 2);//removes &.
        Map<String, String> hm = new HashMap<>();
        hm.put(firstWords, lastWord);//TODO - atoi if int
        return hm;
    }

}
