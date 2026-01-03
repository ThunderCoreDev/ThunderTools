import 'dart:io';
import 'dart:async';
import 'package:path/path.dart' as p;

class FileManager {
  /// Crea un archivo y hace commit automáticamente
  static Future<void> createFile({
    required String repositoryPath,
    required String relativePath,
    required String fileName,
    required String content,
    required String commitMessage,
  }) async {
    // Validaciones básicas
    if (fileName.isEmpty || !fileName.contains('.')) {
      throw Exception('Nombre de archivo inválido. Debe incluir extensión.');
    }

    // Sanitizar rutas
    final cleanRelativePath = relativePath.replaceAll(RegExp(r'\.\.'), '');
    
    // Construir directorio destino
    final targetDir = cleanRelativePath.isNotEmpty
        ? Directory(p.join(repositoryPath, cleanRelativePath))
        : Directory(repositoryPath);

    // Crear directorios si no existen
    if (!await targetDir.exists()) {
      await targetDir.create(recursive: true);
    }

    // Crear archivo
    final file = File(p.join(targetDir.path, fileName));
    if (await file.exists()) {
      throw Exception('El archivo ya existe: ${p.basename(fileName)}');
    }

    // Escribir contenido
    await file.writeAsString(content, flush: true);

    // Obtener ruta relativa para git
    final gitRelativePath = p.relative(file.path, from: repositoryPath);

    // Ejecutar comandos git
    await _runGitCommand(repositoryPath, ['add', gitRelativePath]);
    await _runGitCommand(repositoryPath, ['commit', '-m', commitMessage]);
  }

  /// Ejecuta un comando git
  static Future<String> _runGitCommand(
    String workingDir,
    List<String> args,
  ) async {
    final result = await Process.run('git', args,
        workingDirectory: workingDir, runInShell: true);

    if (result.exitCode != 0) {
      throw Exception('Error en git: ${result.stderr}');
    }

    return result.stdout.toString().trim();
  }

  /// Verifica si un repositorio git es válido
  static Future<bool> isValidGitRepository(String path) async {
    try {
      final dir = Directory(path);
      if (!await dir.exists()) return false;

      final result = await Process.run('git', ['status'],
          workingDirectory: path, runInShell: true);

      return result.exitCode == 0;
    } catch (_) {
      return false;
    }
  }

  /// Lista archivos en un directorio
  static Future<List<FileSystemEntity>> listFiles(String path) async {
    final dir = Directory(path);
    if (!await dir.exists()) return [];

    return dir.list().toList();
  }
}