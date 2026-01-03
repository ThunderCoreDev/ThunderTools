import 'package:GitSync/api/helper.dart';
import 'package:GitSync/api/manager/git_manager.dart';
import 'package:GitSync/global.dart';
import 'package:flutter/material.dart' as mat;
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:path/path.dart' as p;
import '../../../constant/colors.dart';
import '../../../constant/dimens.dart';
import '../../../constant/strings.dart';
import '../../../ui/dialog/base_alert_dialog.dart';

// Versión simple (existente) - para compatibilidad
Future<void> showDialog(BuildContext context, Function(String text) callback) {
  final textController = TextEditingController();
  return mat.showDialog(
    context: context,
    builder: (BuildContext context) => BaseAlertDialog(
      backgroundColor: secondaryDark,
      title: SizedBox(
        width: MediaQuery.of(context).size.width,
        child: Text(
          t.createAFile,
          style: TextStyle(color: primaryLight, fontSize: textXL, fontWeight: FontWeight.bold),
        ),
      ),
      content: SingleChildScrollView(
        child: ListBody(
          children: [
            SizedBox(height: spaceMD),
            TextField(
              contextMenuBuilder: globalContextMenuBuilder,
              controller: textController,
              maxLines: 1,
              style: TextStyle(
                color: primaryLight,
                fontWeight: FontWeight.bold,
                decoration: TextDecoration.none,
                decorationThickness: 0,
                fontSize: textMD,
              ),
              decoration: InputDecoration(
                fillColor: tertiaryDark,
                filled: true,
                border: const OutlineInputBorder(borderRadius: BorderRadius.all(cornerRadiusSM), borderSide: BorderSide.none),
                isCollapsed: true,
                label: Text(
                  t.fileName.toUpperCase(),
                  style: TextStyle(color: secondaryLight, fontSize: textSM, fontWeight: FontWeight.bold),
                ),
                floatingLabelBehavior: FloatingLabelBehavior.always,
                contentPadding: const EdgeInsets.symmetric(horizontal: spaceMD, vertical: spaceSM),
                isDense: true,
              ),
            ),
          ],
        ),
      ),
      actions: <Widget>[
        TextButton(
          child: Text(
            t.cancel.toUpperCase(),
            style: TextStyle(color: primaryLight, fontSize: textMD),
          ),
          onPressed: () {
            Navigator.of(context).canPop() ? Navigator.pop(context) : null;
          },
        ),
        TextButton(
          child: Text(
            t.create.toUpperCase(),
            style: TextStyle(color: primaryPositive, fontSize: textMD),
          ),
          onPressed: () async {
            callback(textController.text);
            Navigator.of(context).canPop() ? Navigator.pop(context) : null;
          },
        ),
      ],
    ),
  );
}

// NUEVA VERSIÓN: Crear archivo con contenido y commit
Future<void> showCreateFileWithCommitDialog(
  BuildContext context,
  String repositoryPath,
  String currentDirectory,
) {
  final relativePathController = TextEditingController();
  final fileNameController = TextEditingController();
  final contentController = TextEditingController();
  final commitMessageController = TextEditingController(text: 'Create ');
  
  return mat.showDialog(
    context: context,
    builder: (BuildContext context) {
      return StatefulBuilder(
        builder: (context, setState) {
          bool isLoading = false;
          
          // Función para autogenerar contenido basado en extensión
          void autoGenerateContent(String fileName) {
            final extension = p.extension(fileName).toLowerCase();
            final baseName = p.basenameWithoutExtension(fileName);
            
            if (baseName.isEmpty) return;
            
            final className = baseName[0].toUpperCase() + 
                (baseName.length > 1 ? baseName.substring(1) : '');
            
            String template = '';
            
            switch (extension) {
              case '.dart':
                template = '''
class $className {
  $className();
  
  // TODO: Add your methods here
}
''';
                break;
              case '.java':
                template = '''
public class $className {
    public static void main(String[] args) {
        System.out.println("Hello from $className!");
    }
}
''';
                break;
              case '.kt':
                template = '''
class $className {
    fun main() {
        println("Hello from $className!")
    }
}
''';
                break;
              case '.py':
                template = '''#!/usr/bin/env python3
"""
$baseName
"""

def main():
    print("Hello, World!")

if __name__ == "__main__":
    main()
''';
                break;
              case '.js':
                template = '''// $baseName

function main() {
    console.log("Hello, World!");
}

// Export if needed
// module.exports = { main };
''';
                break;
              case '.ts':
                template = '''// $baseName

function main(): void {
    console.log("Hello, World!");
}

export default main;
''';
                break;
              case '.cpp':
              case '.cc':
              case '.cxx':
                template = '''#include <iostream>

int main() {
    std::cout << "Hello, World!" << std::endl;
    return 0;
}
''';
                break;
              case '.c':
                template = '''#include <stdio.h>

int main() {
    printf("Hello, World!\\n");
    return 0;
}
''';
                break;
              case '.html':
                template = '''<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>$baseName</title>
</head>
<body>
    <h1>Hello, World!</h1>
</body>
</html>
''';
                break;
              case '.css':
                template = '''/* $baseName */

body {
    margin: 0;
    padding: 0;
    font-family: sans-serif;
}
''';
                break;
              case '.md':
                template = '''# $baseName

## Description

Write your description here.

## Usage

\`\`\`bash
# Usage example
\`\`\`

## License

Your license here.
''';
                break;
              case '.json':
                template = '''{
  "name": "$baseName",
  "version": "1.0.0",
  "description": "",
  "main": "index.js",
  "scripts": {
    "test": "echo \\"Error: no test specified\\" && exit 1"
  },
  "keywords": [],
  "author": "",
  "license": "ISC"
}
''';
                break;
              case '.yaml':
              case '.yml':
                template = '''# $baseName

version: "1.0"
description: ""
# Add your configuration here
''';
                break;
              case '.xml':
                template = '''<?xml version="1.0" encoding="UTF-8"?>
<!-- $baseName -->
<root>
    <!-- Add your XML content here -->
</root>
''';
                break;
              case '.sh':
              case '.bash':
                template = '''#!/bin/bash
# $baseName

echo "Hello, World!"
''';
                break;
              case '.txt':
                template = '$baseName\n\nCreated: ${DateTime.now().toString()}';
                break;
            }
            
            // Solo actualizar si el contenido actual está vacío
            if (contentController.text.isEmpty && template.isNotEmpty) {
              contentController.text = template;
            }
          }
          
          // Actualizar mensaje de commit y contenido cuando cambia el nombre del archivo
          void updateOnFileNameChange() {
            final fileName = fileNameController.text.trim();
            
            // Actualizar mensaje de commit
            if (fileName.isNotEmpty && 
                (commitMessageController.text.isEmpty || 
                 commitMessageController.text == 'Create ' || 
                 commitMessageController.text.startsWith('Create '))) {
              commitMessageController.text = 'Create $fileName';
            }
            
            // Autogenerar contenido si el campo está vacío
            if (fileName.isNotEmpty && fileName.contains('.') && !fileName.endsWith('.')) {
              autoGenerateContent(fileName);
            }
          }
          
          // Escuchar cambios en el nombre del archivo
          fileNameController.addListener(updateOnFileNameChange);
          
          return BaseAlertDialog(
            backgroundColor: secondaryDark,
            title: SizedBox(
              width: MediaQuery.of(context).size.width,
              child: Text(
                'Create File and Commit',
                style: TextStyle(color: primaryLight, fontSize: textXL, fontWeight: FontWeight.bold),
              ),
            ),
            content: SingleChildScrollView(
              child: ListBody(
                children: [
                  SizedBox(height: spaceMD),
                  
                  // Repositorio info
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Repository:',
                        style: TextStyle(
                          color: primaryLight,
                          fontSize: textSM,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      SizedBox(height: spaceXS),
                      Text(
                        repositoryPath,
                        style: TextStyle(
                          color: secondaryLight,
                          fontSize: textXS,
                        ),
                        maxLines: 2,
                        overflow: TextOverflow.ellipsis,
                      ),
                    ],
                  ),
                  SizedBox(height: spaceMD),
                  
                  // Ruta relativa
                  TextField(
                    contextMenuBuilder: globalContextMenuBuilder,
                    controller: relativePathController,
                    maxLines: 1,
                    style: TextStyle(
                      color: primaryLight,
                      fontWeight: FontWeight.bold,
                      decoration: TextDecoration.none,
                      decorationThickness: 0,
                      fontSize: textMD,
                    ),
                    decoration: InputDecoration(
                      fillColor: tertiaryDark,
                      filled: true,
                      border: const OutlineInputBorder(
                        borderRadius: BorderRadius.all(cornerRadiusSM),
                        borderSide: BorderSide.none,
                      ),
                      isCollapsed: true,
                      label: Text(
                        'Relative Path (optional)'.toUpperCase(),
                        style: TextStyle(color: secondaryLight, fontSize: textSM, fontWeight: FontWeight.bold),
                      ),
                      hintText: 'lib/screens',
                      hintStyle: TextStyle(color: tertiaryDark, fontSize: textMD),
                      floatingLabelBehavior: FloatingLabelBehavior.always,
                      contentPadding: const EdgeInsets.symmetric(horizontal: spaceMD, vertical: spaceSM),
                      isDense: true,
                    ),
                  ),
                  SizedBox(height: spaceLG),
                  
                  // Nombre del archivo
                  TextField(
                    contextMenuBuilder: globalContextMenuBuilder,
                    controller: fileNameController,
                    maxLines: 1,
                    style: TextStyle(
                      color: primaryLight,
                      fontWeight: FontWeight.bold,
                      decoration: TextDecoration.none,
                      decorationThickness: 0,
                      fontSize: textMD,
                    ),
                    decoration: InputDecoration(
                      fillColor: tertiaryDark,
                      filled: true,
                      border: const OutlineInputBorder(
                        borderRadius: BorderRadius.all(cornerRadiusSM),
                        borderSide: BorderSide.none,
                      ),
                      isCollapsed: true,
                      label: Text(
                        'File Name*'.toUpperCase(),
                        style: TextStyle(color: secondaryLight, fontSize: textSM, fontWeight: FontWeight.bold),
                      ),
                      hintText: 'main.dart, MyClass.java, index.html',
                      hintStyle: TextStyle(color: tertiaryDark, fontSize: textMD),
                      floatingLabelBehavior: FloatingLabelBehavior.always,
                      contentPadding: const EdgeInsets.symmetric(horizontal: spaceMD, vertical: spaceSM),
                      isDense: true,
                    ),
                  ),
                  SizedBox(height: spaceLG),
                  
                  // Botón para generar plantilla
                  Row(
                    children: [
                      Expanded(
                        child: OutlinedButton.icon(
                          onPressed: () {
                            final fileName = fileNameController.text.trim();
                            if (fileName.isNotEmpty && fileName.contains('.') && !fileName.endsWith('.')) {
                              autoGenerateContent(fileName);
                            } else {
                              Fluttertoast.showToast(
                                msg: "Enter a valid file name with extension first",
                                toastLength: Toast.LENGTH_SHORT,
                                gravity: null,
                              );
                            }
                          },
                          style: OutlinedButton.styleFrom(
                            foregroundColor: primaryInfo,
                            side: BorderSide(color: primaryInfo),
                            padding: const EdgeInsets.symmetric(vertical: spaceSM, horizontal: spaceMD),
                          ),
                          icon: const Icon(Icons.auto_awesome, size: 16),
                          label: const Text('Generate Template'),
                        ),
                      ),
                    ],
                  ),
                  SizedBox(height: spaceLG),
                  
                  // Contenido
                  TextField(
                    contextMenuBuilder: globalContextMenuBuilder,
                    controller: contentController,
                    maxLines: 8,
                    minLines: 4,
                    style: TextStyle(
                      color: primaryLight,
                      fontWeight: FontWeight.bold,
                      decoration: TextDecoration.none,
                      decorationThickness: 0,
                      fontSize: textMD,
                    ),
                    decoration: InputDecoration(
                      fillColor: tertiaryDark,
                      filled: true,
                      border: const OutlineInputBorder(
                        borderRadius: BorderRadius.all(cornerRadiusSM),
                        borderSide: BorderSide.none,
                      ),
                      isCollapsed: true,
                      label: Text(
                        'Content'.toUpperCase(),
                        style: TextStyle(color: secondaryLight, fontSize: textSM, fontWeight: FontWeight.bold),
                      ),
                      hintText: 'Write your code here...\n(Template will auto-generate when you enter file name)',
                      hintStyle: TextStyle(color: tertiaryDark, fontSize: textMD),
                      floatingLabelBehavior: FloatingLabelBehavior.always,
                      contentPadding: const EdgeInsets.symmetric(horizontal: spaceMD, vertical: spaceSM),
                      alignLabelWithHint: true,
                      isDense: true,
                    ),
                  ),
                  SizedBox(height: spaceLG),
                  
                  // Mensaje de commit
                  TextField(
                    contextMenuBuilder: globalContextMenuBuilder,
                    controller: commitMessageController,
                    maxLines: 2,
                    style: TextStyle(
                      color: primaryLight,
                      fontWeight: FontWeight.bold,
                      decoration: TextDecoration.none,
                      decorationThickness: 0,
                      fontSize: textMD,
                    ),
                    decoration: InputDecoration(
                      fillColor: tertiaryDark,
                      filled: true,
                      border: const OutlineInputBorder(
                        borderRadius: BorderRadius.all(cornerRadiusSM),
                        borderSide: BorderSide.none,
                      ),
                      isCollapsed: true,
                      label: Text(
                        'Commit Message*'.toUpperCase(),
                        style: TextStyle(color: secondaryLight, fontSize: textSM, fontWeight: FontWeight.bold),
                      ),
                      hintText: 'Create new file',
                      hintStyle: TextStyle(color: tertiaryDark, fontSize: textMD),
                      floatingLabelBehavior: FloatingLabelBehavior.always,
                      contentPadding: const EdgeInsets.symmetric(horizontal: spaceMD, vertical: spaceSM),
                      isDense: true,
                    ),
                  ),
                ],
              ),
            ),
            actions: <Widget>[
              TextButton(
                child: Text(
                  t.cancel.toUpperCase(),
                  style: TextStyle(color: primaryLight, fontSize: textMD),
                ),
                onPressed: isLoading
                    ? null
                    : () {
                        Navigator.of(context).canPop() ? Navigator.pop(context) : null;
                      },
              ),
              TextButton(
                child: isLoading
                    ? SizedBox(
                        width: textMD,
                        height: textMD,
                        child: CircularProgressIndicator(
                          strokeWidth: 2,
                          color: primaryPositive,
                        ),
                      )
                    : Text(
                        'CREATE & COMMIT'.toUpperCase(),
                        style: TextStyle(color: primaryPositive, fontSize: textMD),
                      ),
                onPressed: isLoading
                    ? null
                    : () async {
                        final relativePath = relativePathController.text.trim();
                        final fileName = fileNameController.text.trim();
                        final content = contentController.text;
                        final commitMessage = commitMessageController.text.trim();
                        
                        // Validaciones
                        if (fileName.isEmpty) {
                          Fluttertoast.showToast(
                            msg: "File name is required",
                            toastLength: Toast.LENGTH_SHORT,
                            gravity: null,
                          );
                          return;
                        }
                        
                        if (!fileName.contains('.') || fileName.endsWith('.')) {
                          Fluttertoast.showToast(
                            msg: "Invalid file name. Must include extension",
                            toastLength: Toast.LENGTH_SHORT,
                            gravity: null,
                          );
                          return;
                        }
                        
                        if (commitMessage.isEmpty) {
                          Fluttertoast.showToast(
                            msg: "Commit message is required",
                            toastLength: Toast.LENGTH_SHORT,
                            gravity: null,
                          );
                          return;
                        }
                        
                        setState(() => isLoading = true);
                        
                        try {
                          // Construir ruta completa
                          final targetDir = relativePath.isNotEmpty
                              ? p.join(currentDirectory, relativePath)
                              : currentDirectory;
                          
                          // Crear directorio si no existe
                          final dir = Directory(targetDir);
                          if (!await dir.exists()) {
                            await dir.create(recursive: true);
                          }
                          
                          // Crear archivo
                          final file = File(p.join(targetDir, fileName));
                          if (await file.exists()) {
                            Fluttertoast.showToast(
                              msg: "File already exists",
                              toastLength: Toast.LENGTH_LONG,
                              gravity: null,
                            );
                            setState(() => isLoading = false);
                            return;
                          }
                          
                          // Escribir contenido
                          await file.writeAsString(content);
                          
                          // Calcular ruta relativa para git (desde la raíz del repo)
                          final relativeFilePath = p.relative(
                            file.path,
                            from: repositoryPath,
                          );
                          
                          // Hacer commit
                          await GitManager.addAll([relativeFilePath]);
                          await GitManager.commit(commitMessage);
                          
                          Fluttertoast.showToast(
                            msg: "✓ File created and committed successfully",
                            toastLength: Toast.LENGTH_LONG,
                            gravity: null,
                          );
                          
                          Navigator.of(context).canPop() ? Navigator.pop(context) : null;
                          
                        } catch (e) {
                          Fluttertoast.showToast(
                            msg: "✗ Error: $e",
                            toastLength: Toast.LENGTH_LONG,
                            gravity: null,
                          );
                          setState(() => isLoading = false);
                        }
                      },
              ),
            ],
          );
        },
      );
    },
  );
}