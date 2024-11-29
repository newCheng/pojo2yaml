package com.nc.pojo2yaml;

import com.intellij.psi.PsiType;
import com.intellij.psi.javadoc.PsiDocComment;

/**
 * @Description
 * @Author czx
 * @Date 2024/9/23 13:54
 */
public class POJOField {

    private String name;

    private PsiType type;

    private PsiDocComment comment;

    public POJOField(String name, PsiType type, PsiDocComment comment) {
        this.name = name;
        this.type = type;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PsiType getType() {
        return type;
    }

    public void setType(PsiType type) {
        this.type = type;
    }

    public PsiDocComment getComment() {
        return comment;
    }

    public void setComment(PsiDocComment comment) {
        this.comment = comment;
    }
}
