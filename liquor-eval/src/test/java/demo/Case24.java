package demo;

import org.noear.liquor.eval.CodeSpec;
import org.noear.liquor.eval.ScriptEvaluator;

/**
 * @author noear 2024/9/20 created
 */
public class Case24 {
    public static void main(String[] args) throws Exception {
        ScriptEvaluator scriptEvaluator = new ScriptEvaluator();
        scriptEvaluator.setPrintable(true);

        CodeSpec code1 = new CodeSpec("import demo.Case24.Person;\n" +
                "    import java.util.Arrays;\n" +
                "    import java.util.List;\n" +

                "    List<Person> personList = Arrays.asList(new Person(\"jack\", 15000),\n" +
                "                new Person(\"tom\", 13500),\n" +
                "                new Person(\"peter\", 18600));\n" +
                "\n" +
                "        int totalSalary = personList.stream().mapToInt(Person::getSalary).sum();\n" +
                "\n" +
                "        System.out.println(totalSalary);" +
                "    "); //name 为外部参数

        scriptEvaluator.eval(code1);
    }

    public static class Person {
        private String name;
        private int salary;

        public Person(String name, int salary) {
            this.name = name;
            this.salary = salary;
        }

        public String getName() {
            return name;
        }

        public int getSalary() {
            return salary;
        }
    }
}