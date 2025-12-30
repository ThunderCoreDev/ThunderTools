# Configuración básica de ProGuard
-dontobfuscate
-keepattributes Signature
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }