package com.nc.pojo2yaml;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @Description
 * @Author czx
 * @Date 2024/10/30 10:43
 */
public class ProjectViewPopupMenuAction extends POJO2YAMLAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        final VirtualFile[] selectFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);

        if (selectFiles.length == 1) {
            pojo2Yaml(PsiUtil.getPsiFile(project, selectFiles[0]), null, null);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        final Project project = e.getProject();
        final VirtualFile[] selectFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);

        boolean menuAllowed =
                selectFiles != null
                        && selectFiles.length == 1
                        && Arrays.stream(selectFiles).noneMatch(VirtualFile::isDirectory)
                        && project != null;

        e.getPresentation().setEnabledAndVisible(menuAllowed);
    }
}
