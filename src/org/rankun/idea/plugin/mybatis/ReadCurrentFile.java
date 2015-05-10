package org.rankun.idea.plugin.mybatis;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created on 5/10/15.
 *
 * @author rankun203
 */
public class ReadCurrentFile extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) return;

        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null) return;

        final Document document = editor.getDocument();

        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
        if (virtualFile == null) return;

        final String contents;
        try {
            BufferedReader br = new BufferedReader(new FileReader(virtualFile.getPath()));
            String currentLine;
            StringBuilder sb = new StringBuilder();

            while ((currentLine = br.readLine()) != null) {
                sb.append(currentLine);
                sb.append("\n");
            }

            contents = sb.toString();
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

        final Runnable readRunner = () -> document.setText(contents);

        ApplicationManager.getApplication().invokeLater(
                () -> CommandProcessor.getInstance().executeCommand(
                        project, () -> ApplicationManager.getApplication().runWriteAction(readRunner),
                        "DiskRead", null
                )
        );
    }
}
