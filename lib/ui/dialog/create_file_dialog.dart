import 'package:flutter/material.dart';
import 'package:path/path.dart' as p;
import 'package:git_sync/l10n/strings.g.dart';

class CreateFileDialog extends StatefulWidget {
  final String repositoryPath;
  final Function(String, String, String, String)? onCreateFile;

  const CreateFileDialog({
    super.key,
    required this.repositoryPath,
    this.onCreateFile,
  });

  @override
  State<CreateFileDialog> createState() => _CreateFileDialogState();
}

class _CreateFileDialogState extends State<CreateFileDialog> {
  final TextEditingController _pathController = TextEditingController();
  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _contentController = TextEditingController();
  final TextEditingController _commitController = TextEditingController();

  final _formKey = GlobalKey<FormState>();
  bool _isCreating = false;

  @override
  void initState() {
    super.initState();
    _nameController.addListener(_updateCommitMessage);
  }

  void _updateCommitMessage() {
    if (_nameController.text.isNotEmpty &&
        (_commitController.text.isEmpty ||
            _commitController.text == 'Create ' ||
            _commitController.text.startsWith('Create '))) {
      _commitController.text = 'Create ${_nameController.text}';
    }
  }

  @override
  Widget build(BuildContext context) {
    final t = context.t;

    return AlertDialog(
      title: Text(t.createFile),
      content: SingleChildScrollView(
        child: Form(
          key: _formKey,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Repositorio actual
              _buildRepositoryInfo(),
              const SizedBox(height: 16),

              // Ruta relativa
              TextFormField(
                controller: _pathController,
                decoration: InputDecoration(
                  labelText: t.relativePath,
                  hintText: 'lib/screens',
                  prefixIcon: const Icon(Icons.folder_outlined),
                  border: const OutlineInputBorder(),
                ),
              ),
              const SizedBox(height: 16),

              // Nombre del archivo
              TextFormField(
                controller: _nameController,
                decoration: InputDecoration(
                  labelText: t.fileName,
                  hintText: 'nuevo_archivo.dart',
                  prefixIcon: const Icon(Icons.insert_drive_file_outlined),
                  border: const OutlineInputBorder(),
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return t.fileNameRequired;
                  }
                  if (!value.contains('.') || value.endsWith('.')) {
                    return t.invalidFileName;
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),

              // Contenido
              TextFormField(
                controller: _contentController,
                decoration: InputDecoration(
                  labelText: t.content,
                  hintText: t.writeContentHere,
                  alignLabelWithHint: true,
                  border: const OutlineInputBorder(),
                ),
                maxLines: 8,
                minLines: 4,
              ),
              const SizedBox(height: 16),

              // Mensaje de commit
              TextFormField(
                controller: _commitController,
                decoration: InputDecoration(
                  labelText: t.commitMessage,
                  hintText: t.createFileCommitHint,
                  prefixIcon: const Icon(Icons.message_outlined),
                  border: const OutlineInputBorder(),
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return t.commitMessageRequired;
                  }
                  return null;
                },
              ),
            ],
          ),
        ),
      ),
      actions: [
        TextButton(
          onPressed: _isCreating ? null : () => Navigator.pop(context),
          child: Text(t.cancel),
        ),
        ElevatedButton(
          onPressed: _isCreating ? null : _createFile,
          child: _isCreating
              ? const SizedBox(
                  width: 20,
                  height: 20,
                  child: CircularProgressIndicator(strokeWidth: 2),
                )
              : Text(t.create),
        ),
      ],
    );
  }

  Widget _buildRepositoryInfo() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          context.t.repository,
          style: TextStyle(
            fontWeight: FontWeight.bold,
            color: Theme.of(context).colorScheme.primary,
            fontSize: 14,
          ),
        ),
        const SizedBox(height: 4),
        Text(
          widget.repositoryPath,
          style: TextStyle(
            fontSize: 12,
            color: Theme.of(context).colorScheme.onSurfaceVariant,
          ),
          overflow: TextOverflow.ellipsis,
          maxLines: 2,
        ),
      ],
    );
  }

  Future<void> _createFile() async {
    if (!_formKey.currentState!.validate()) {
      return;
    }

    setState(() => _isCreating = true);

    try {
      if (widget.onCreateFile != null) {
        await widget.onCreateFile!(
          _pathController.text.trim(),
          _nameController.text.trim(),
          _contentController.text,
          _commitController.text.trim(),
        );
      }

      if (mounted) {
        Navigator.pop(context);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(context.t.fileCreatedSuccess),
            behavior: SnackBarBehavior.floating,
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('${context.t.error}: $e'),
            backgroundColor: Theme.of(context).colorScheme.error,
            behavior: SnackBarBehavior.floating,
          ),
        );
      }
    } finally {
      if (mounted) {
        setState(() => _isCreating = false);
      }
    }
  }

  @override
  void dispose() {
    _pathController.dispose();
    _nameController.dispose();
    _contentController.dispose();
    _commitController.dispose();
    super.dispose();
  }
}