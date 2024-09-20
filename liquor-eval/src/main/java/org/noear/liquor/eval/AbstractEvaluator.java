/*
 * Copyright 2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.liquor.eval;

import org.noear.liquor.DynamicCompiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 虚拟评估器
 *
 * @author noear
 * @since 1.2
 */
public abstract class AbstractEvaluator implements IEvaluator {
    protected boolean printable = false;

    private ClassLoader parentClassLoader;
    private DynamicCompiler compiler;

    private final Map<CodeSpec, IExecutable> cachedMap = new ConcurrentHashMap<>();
    private final Map<CodeSpec, String> nameMap = new ConcurrentHashMap<>();
    private final AtomicLong nameCounter = new AtomicLong(0L);

    /**
     * 获取编译器
     */
    protected DynamicCompiler getCompiler() {
        if (compiler == null) {
            compiler = new DynamicCompiler(parentClassLoader);
        }

        return compiler;
    }

    /**
     * 构建类
     */
    protected Class<?> build(CodeSpec codeSpec) {
        //1.分离导入代码

        StringBuilder importBuilder = new StringBuilder();
        StringBuilder codeBuilder = new StringBuilder();

        if (codeSpec.getImports() != null && codeSpec.getImports().length > 0) {
            for (Class<?> clz : codeSpec.getImports()) {
                importBuilder.append("import ").append(clz.getCanonicalName()).append(";\n");
            }
        }

        if (codeSpec.getCode().contains("import ")) {
            BufferedReader reader = new BufferedReader(new StringReader(codeSpec.getCode()));

            try {
                String line;
                String lineTrim;
                while ((line = reader.readLine()) != null) {
                    lineTrim = line.trim();
                    if (lineTrim.startsWith("import ")) {
                        importBuilder.append(lineTrim).append("\n");
                    } else {
                        codeBuilder.append(line).append("\n");
                    }
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } else {
            codeBuilder.append(codeSpec.getCode());
        }


        //2.构建代码申明

        String clazzName = "Executable$" + getKey(codeSpec);

        StringBuilder code = new StringBuilder();

        if (importBuilder.length() > 0) {
            code.append(importBuilder).append("\n");
        }

        code.append("public class ").append(clazzName).append(" {\n");
        {
            code.append("  public static ");
            if (codeSpec.getReturnType() != null) {
                code.append(codeSpec.getReturnType().getCanonicalName());
            } else {
                code.append("void");
            }
            code.append(" _main$(");

            if (codeSpec.getParameters() != null && codeSpec.getParameters().size() > 0) {
                for (Map.Entry<String, Class<?>> kv : codeSpec.getParameters().entrySet()) {
                    code.append(kv.getValue().getCanonicalName()).append(" ").append(kv.getKey()).append(",");
                }
                code.setLength(code.length() - 1);
            }
            code.append(")\n");
            code.append("  {\n");


            if (codeSpec.getCode().indexOf(';') < 0) {
                //没有 ";" 号（支持表达式）
                code.append("    return ").append(codeSpec.getCode()).append(";\n");
            } else {
                //有 ";" 号，说明是完整的语句
                code.append("    ").append(codeBuilder).append("\n");
            }


            code.append("  }\n");
        }
        code.append("}");

        if (printable) {
            System.out.println(code);
        }

        DynamicCompiler compiler = getCompiler();
        compiler.addSource(clazzName, code.toString()).build();

        try {
            return compiler.getClassLoader().loadClass(clazzName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 设置父类加载器
     */
    @Override
    public void setParentClassLoader(ClassLoader parentClassLoader) {
        this.parentClassLoader = parentClassLoader;
    }

    @Override
    public void setPrintable(boolean printable) {
        this.printable = printable;
    }

    /**
     * 获取标记
     */
    protected String getKey(CodeSpec codeSpec) {
        //中转一下，可避免有相同 hash 的情况
        return nameMap.computeIfAbsent(codeSpec, k -> String.valueOf(nameCounter.incrementAndGet()));
    }

    /**
     * 编译
     *
     * @param codeSpec 代码申明
     */
    @Override
    public IExecutable compile(CodeSpec codeSpec) {
        assert codeSpec != null;

        return cachedMap.computeIfAbsent(codeSpec, k -> new ExecutableImpl(build(codeSpec)));
    }

    /**
     * 评估
     *
     * @param codeSpec 代码申明
     * @param args     执行参数
     */
    @Override
    public Object eval(CodeSpec codeSpec, Object... args) throws InvocationTargetException {
        assert codeSpec != null;

        try {
            return compile(codeSpec).exec(args);
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Exception e) {
            throw new InvocationTargetException(e);
        }
    }
}