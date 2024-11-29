package com.nc.pojo2yaml.parser;

import com.nc.pojo2yaml.POJOField;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.impl.source.javadoc.PsiDocTokenImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PsiUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UElement;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.events.CommentEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.ImplicitTuple;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author czx
 * @Date 2024/9/13 20:10
 */
public class POJO2YAMLParser {

    private final List<String> collectionTypes = List.of(
            "java.lang.Iterable",
            "java.util.Collection",
            "java.util.AbstractCollection",
            "java.util.List",
            "java.util.AbstractList",
            "java.util.Set",
            "java.util.AbstractSet");

    public String parse(UElement uElement) {
        StringWriter output = new StringWriter();
        Emitter emitter = init(output);
        if (uElement instanceof UClass uClass) {
            try {
                parseClass(uClass.getJavaPsi(), emitter);
                String result = output.toString();
                System.out.println(result);
                return result;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private String parseClass(PsiClass psiClass, Emitter emitter) throws IOException {
        emitter.emit(new StreamStartEvent(null, null));
        emitter.emit(new DocumentStartEvent(null, null, false, null, null));
        emitter.emit(new MappingStartEvent(null, "yaml.org,2002:map", true, null, null, DumperOptions.FlowStyle.BLOCK));

        PsiField[] allFields = psiClass.getAllFields();
        for (PsiField field : allFields) {
            POJOField pojoField = new POJOField(field.getName(), field.getType(), field.getDocComment());
            writeMap(pojoField, emitter);
        }

        emitter.emit(new MappingEndEvent(null, null));
        emitter.emit(new DocumentEndEvent(null, null, false));
        emitter.emit(new StreamEndEvent(null, null));
        return null;
    }

    void writeMap(POJOField field, Emitter emitter) throws IOException {
        ImplicitTuple allImplicit = new ImplicitTuple(true, true);
        PsiType type = field.getType();
        String fieldName = field.getName();
        String commentStr = getCommentStr(field.getComment());
        //写key
        if (fieldName != null && !"".equals(fieldName)) {
            emitter.emit(new ScalarEvent(null, "yaml.org,2002:str", allImplicit, fieldName, null, null, DumperOptions.ScalarStyle.PLAIN));
        }

        //写value
        if (isPrimitiveType(type)) {
            //基础数据处理
            emitter.emit(new ScalarEvent(null, "yaml.org,2002:str", allImplicit, "", null, null, DumperOptions.ScalarStyle.PLAIN));
            if (StringUtils.isNotBlank(commentStr)) {
                emitter.emit(new CommentEvent(CommentType.IN_LINE, commentStr, null, null));
            }
        } else if (isCollectionType(type)) {
            //集合对象处理
            if (StringUtils.isNotBlank(commentStr)) {
                emitter.emit(new CommentEvent(CommentType.IN_LINE, commentStr, null, null));
            }
            writeCollection(field, emitter);
        } else {
            //自定义对象处理
            if (StringUtils.isNotBlank(commentStr)) {
                emitter.emit(new CommentEvent(CommentType.IN_LINE, commentStr, null, null));
            }
            PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(type);
            emitter.emit(new MappingStartEvent(null, "yaml.org,2002:map", true, null, null, DumperOptions.FlowStyle.BLOCK));
            for (PsiField resolveField : psiClass.getAllFields()) {
                POJOField pojoField = new POJOField(resolveField.getName(), resolveField.getType(), resolveField.getDocComment());
                writeMap(pojoField, emitter);
            }
            emitter.emit(new MappingEndEvent(null, null));
        }
    }

    void writeCollection(POJOField field, Emitter emitter) throws IOException {
        ImplicitTuple allImplicit = new ImplicitTuple(true, true);
        PsiType type = field.getType();
        String fieldName = field.getName();
        String collectionItemName = fieldName + "Item";
        PsiType itemType = ((PsiClassReferenceType) type).getReference().getTypeParameters()[0];
        if (isCollectionType(itemType)) {
            POJOField pojoField = new POJOField(collectionItemName, itemType, null);
            emitter.emit(new SequenceStartEvent(null, null, true, null, null, DumperOptions.FlowStyle.BLOCK));
            emitter.emit(new MappingStartEvent(null, "yaml.org,2002:map", true, null, null, DumperOptions.FlowStyle.BLOCK));
            writeMap(pojoField, emitter);
            emitter.emit(new MappingEndEvent(null, null));
            emitter.emit(new SequenceEndEvent(null, null));
        }else if(isPrimitiveType(itemType)){
            POJOField pojoField = new POJOField("", itemType, null);
            emitter.emit(new SequenceStartEvent(null, null, true, null, null, DumperOptions.FlowStyle.BLOCK));
            writeMap(pojoField, emitter);
            emitter.emit(new SequenceEndEvent(null, null));
        } else {
            POJOField pojoField = new POJOField("", itemType, null);
            emitter.emit(new SequenceStartEvent(null, null, true, null, null, DumperOptions.FlowStyle.BLOCK));
            writeMap(pojoField, emitter);
            emitter.emit(new SequenceEndEvent(null, null));
        }
    }


    private Boolean isPrimitiveType(PsiType type) {
        String canonicalText = type.getCanonicalText();
        //StrUtil.startWithAny(variableType, "java.lang") || StrUtil.equalsAny(variableType, "java.util.Map")
        if (StringUtils.equalsAny(canonicalText, "boolean", "byte", "short", "int", "long", "float", "double", "char") || StringUtils.startsWithAny(canonicalText, "java.lang", "java.util.Map")) {
            return true;
        }
        return false;
    }

    private Boolean isCollectionType(PsiType type) {
        PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(type);
        PsiClass[] supers = psiClass.getSupers();
        List<String> typeNameList = new ArrayList<>();
        typeNameList.add(psiClass.getQualifiedName());
        typeNameList.addAll(Arrays.stream(supers).map(PsiClass::getQualifiedName).collect(Collectors.toList()));
        boolean isCollectionType = typeNameList.stream().anyMatch(collectionTypes::contains);
        return isCollectionType;
    }

    public String getCommentStr(PsiDocComment comment) {
        if (comment == null) {
            return null;
        }
        String commentStr = "";
        for (PsiElement element : comment.getDescriptionElements()) {
            if (element instanceof PsiDocTokenImpl) {
                commentStr += element.getText();
            }
        }
        return commentStr;
    }

    public Emitter init(StringWriter output) {
        DumperOptions options = new DumperOptions();
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
        options.setProcessComments(true);
        options.setPrettyFlow(true);
        return new Emitter(output, options);
    }

}
