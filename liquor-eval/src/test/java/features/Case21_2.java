package features;

import org.junit.jupiter.api.Test;
import org.noear.liquor.eval.CodeSpec;
import org.noear.liquor.eval.Expr;
import org.noear.liquor.eval.Script;

/**
 * @author noear 2024/9/20 created
 */
public class Case21_2 {
    @Test
    public void test() throws Exception {
        // 数学运算 (Long)
        String exp1 = "1+2+3";
        Integer result = (Integer) Expr.eval(exp1);
        System.out.println(result); // 6

        // 数学运算 (Double)
        String exp4 = "1.1+2.2+3.3";
        Double result2 = (Double) Expr.eval(exp4);
        System.out.println(result2); // 6.6


        // 包含关系运算和逻辑运算
        String exp2 = "(1>0||0<1)&&1!=0";
        System.out.println(Expr.eval(exp2)); // true


        // 三元运算
        String exp3 = "4 > 3 ? \"4 > 3\" : 999";
        System.out.println(Expr.eval(exp3)); // 4 > 3


        CodeSpec exp5 = new CodeSpec("Math.min(1,2)").imports(Math.class);
        System.out.println(Expr.eval(exp5));

        Script.eval(new CodeSpec("System.out.println(Math.min(11,21));").imports(Math.class));
    }
}
