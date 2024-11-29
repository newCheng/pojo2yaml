// Copyright 2000-2023 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.nc.pojo2yaml;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Action class to demonstrate how to interact with the IntelliJ Platform.
 * The only action this class performs is to provide the user with a popup dialog as feedback.
 * Typically this class is instantiated by the IntelliJ Platform framework based on declarations
 * in the plugin.xml file. But when added at runtime this class is instantiated by an action group.
 */
public class EditorPopupMenuAction extends POJO2YAMLAction {

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.BGT;
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    // Using the event, create and show a dialog

    final Editor editor = e.getData(CommonDataKeys.EDITOR);
    final PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
    final PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);

    pojo2Yaml(psiFile, editor, psiElement);
  }

  @Override
  public void update(AnActionEvent e) {
    // Set the availability based on whether a project is open
    final Project project = e.getProject();
    final Editor editor = e.getData(CommonDataKeys.EDITOR);
    final PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

    boolean menuAllowed = false;
    if (psiFile != null && editor != null && project != null) {
      menuAllowed = uastSupported(psiFile);
    }
    e.getPresentation().setEnabledAndVisible(menuAllowed);
  }

}
