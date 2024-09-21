package features;

import org.junit.jupiter.api.Test;
import org.noear.liquor.eval.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2024/9/19 created
 */
public class Case22 {
    @Test
    public void test() throws Exception {
        CodeSpec code1 = new CodeSpec("import java.util.HashMap;\n\n"+
                "    class Demo {\n" +
                "      public String hello(String word) {\n" +
                "        return word;\n" +
                "      }\n" +
                "    }\n" +
                "\n" +
                "    Demo demo = new Demo();\n" +
                "    return demo.hello(name);") //name 为外部参数
                .parameters(new ParamSpec("name", String.class))
                .returnType(String.class);

        //直接执行
        System.out.println(Script.eval(code1, "noear"));
        assert "noear".equals(Script.eval(code1, "noear"));
        assert "solon".equals(Script.eval(code1, "solon"));

        //转类再执行
        IExecutable executable1 = Script.compile(code1);
        System.out.println(executable1.exec( "noear"));

        ///////////////


        CodeSpec code2 = new CodeSpec("a+1").parameters(new ParamSpec("a",Integer.class));

        System.out.println(Expr.eval(code2, 2));
        assert 3 == (int) Expr.eval(code2, 2);

        ///////////////

        System.out.println(Expr.eval(new CodeSpec("a + 22").parameters(new ParamSpec("a", Integer.class)), 1));
        assert 24 == (int) Expr.eval(new CodeSpec("a + 22").parameters(new ParamSpec("a", Integer.class)), 2);

        Map<String, Object> context1 = new HashMap<>();
        context1.put("aa", 3);
        System.out.println(Expr.eval("aa + 22", context1));

        Map<String, Object> context2 = new HashMap<>();
        context2.put("aa", 4L);
        System.out.println(Expr.eval("aa + 22", context2));
    }
}
