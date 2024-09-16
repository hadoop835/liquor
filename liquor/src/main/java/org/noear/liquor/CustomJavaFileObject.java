package org.noear.liquor;


import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import java.io.*;
import java.net.URI;

/**
 * This code comes from: Arthas project
 * */
public class CustomJavaFileObject implements JavaFileObject {
    private final String className;
    private final URI uri;

    public CustomJavaFileObject(String className, URI uri) {
        this.uri = uri;
        this.className = className;
    }

    public URI toUri() {
        return uri;
    }

    public InputStream openInputStream() throws IOException {
        return uri.toURL().openStream();
    }

    public OutputStream openOutputStream() {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        return this.className;
    }

    public Reader openReader(boolean ignoreEncodingErrors) {
        throw new UnsupportedOperationException();
    }

    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        throw new UnsupportedOperationException();
    }

    public Writer openWriter() throws IOException {
        throw new UnsupportedOperationException();
    }

    public long getLastModified() {
        return 0;
    }

    public boolean delete() {
        throw new UnsupportedOperationException();
    }

    public Kind getKind() {
        return Kind.CLASS;
    }

    public boolean isNameCompatible(String simpleName, Kind kind) {
        return Kind.CLASS.equals(getKind())
                && this.className.endsWith(simpleName);
    }

    public NestingKind getNestingKind() {
        throw new UnsupportedOperationException();
    }

    public Modifier getAccessLevel() {
        throw new UnsupportedOperationException();
    }

    public String getClassName() {
        return this.className;
    }


    public String toString() {
        return this.getClass().getName() + "[" + this.toUri() + "]";
    }
}

