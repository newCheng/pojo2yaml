package com.nc.pojo2yaml;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.nc.pojo2yaml.parser.POJO2YAMLParser;
import com.nc.pojo2yaml.util.ClipboardUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UVariable;
import org.jetbrains.uast.UastContextKt;
import org.jetbrains.uast.UastLanguagePlugin;
import org.jetbrains.uast.UastUtils;

/**
 * @Description
 * @Author czx
 * @Date 2024/9/13 20:01
 */
public abstract class POJO2YAMLAction extends AnAction {

    private POJO2YAMLParser pojo2YAMLParser = new POJO2YAMLParser();

    public void pojo2Yaml(@NotNull final PsiFile psiFile,
                          @Nullable final Editor editor,
                          @Nullable final PsiElement psiElement){

        Project project = psiFile.getProject();

        if (!uastSupported(psiFile)) {
            Notify.notifyWarn("This file can't convert to json.", project);
            return;
        }

        UElement uElement = null;
        if (psiElement != null) {
            uElement = UastContextKt.toUElement(psiElement, UVariable.class);
        }

        if (uElement == null) {
            if (editor != null) {
                PsiElement elementAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
                uElement = UastUtils.findContaining(elementAt, UClass.class);
            }
        }

        if (uElement == null) {
            String fileText = psiFile.getText();
            int offset = fileText.contains("class") ? fileText.indexOf("class") : fileText.indexOf("record");
            if (offset < 0) {
                Notify.notifyWarn("Can't find class scope.", project);
                return;
            }
            PsiElement elementAt = psiFile.findElementAt(offset);
            uElement = UastUtils.findContaining(elementAt, UClass.class);
        }
        String yamlText = pojo2YAMLParser.parse(uElement);

        ClipboardUtil.copyToClipboard(yamlText);
    }

    public boolean uastSupported(@NotNull final PsiFile psiFile) {
        return UastLanguagePlugin.Companion.getInstances()
                .stream()
                .anyMatch(l -> l.isFileSupported(psiFile.getName()));
    }

}
