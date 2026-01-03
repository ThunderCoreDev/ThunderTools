import 'dart:async';
import 'dart:convert';
import 'package:GitSync/api/helper.dart';
import 'package:GitSync/api/manager/settings_manager.dart';
import 'package:GitSync/api/manager/storage.dart';
import 'package:GitSync/global.dart';
import 'package:flutter/foundation.dart';

class PremiumManager {
  final ValueNotifier<bool?> hasPremiumNotifier = ValueNotifier(null);

  Future<void> init() async {
    // COMENTA la verificación de GitHub Sponsor
    // await updateGitHubSponsorPremium();

    final isPremium = await _readPremiumStatus();
    hasPremiumNotifier.value = isPremium;
  }

  Future<bool> _readPremiumStatus() async {
    // ANTES:
    // return kDebugMode || await repoManager.getBool(StorageKey.repoman_hasGHSponsorPremium);
    
    // DESPUÉS: Siempre retorna true
    return true;
  }

  Future<void> updateGitHubSponsorPremium() async {
    // COMENTA toda esta función o haz que siempre establezca premium como true
    if (!await hasNetworkConnection()) {
      return;
    }

    // Establece premium como true directamente
    await repoManager.setBool(StorageKey.repoman_hasGHSponsorPremium, true);
    hasPremiumNotifier.value = await _readPremiumStatus();
    
    // COMENTA el resto del código original:
    /*
    final userToken = await repoManager.getStringNullable(StorageKey.repoman_ghSponsorToken);
    if (userToken == null) {
      await repoManager.setBool(StorageKey.repoman_hasGHSponsorPremium, false);
      hasPremiumNotifier.value = await _readPremiumStatus();
      return;
    }

    final userRes = await httpGet(
      Uri.parse('https://api.github.com/user'),
      headers: {'Authorization': 'token $userToken', 'Accept': 'application/vnd.github.v3+json'},
    );

    if (userRes.statusCode != 200 && userRes.statusCode != 408) {
      await repoManager.setBool(StorageKey.repoman_hasGHSponsorPremium, false);
      hasPremiumNotifier.value = await _readPremiumStatus();
    }

    if (userRes.statusCode != 200) return;

    final userNodeId = jsonDecode(userRes.body)['node_id'].toString();

    final fileRes = await httpGet(Uri.parse('https://raw.githubusercontent.com/ViscousPot/sponsors-gitsync/refs/heads/main/sponsors.txt'));

    if (userNodeId.isEmpty || (fileRes.statusCode != 200 && fileRes.statusCode != 408)) {
      await repoManager.setBool(StorageKey.repoman_hasGHSponsorPremium, false);
      hasPremiumNotifier.value = await _readPremiumStatus();
    }

    if (userRes.statusCode != 200) {
      throw Exception('Failed to load sponsors.txt: ${fileRes.statusCode}');
    }

    final content = utf8.decode(fileRes.bodyBytes);
    final lines = LineSplitter.split(content).map((e) => e.trim()).toList();
    final isSponsor = lines.contains(userNodeId);

    await repoManager.setBool(StorageKey.repoman_hasGHSponsorPremium, isSponsor);
    hasPremiumNotifier.value = await _readPremiumStatus();
    */
  }

  Future<void> cullNonPremium() async {
    // COMENTA esta función completa para que NO elimine repositorios
    // Simplemente retorna sin hacer nada
    
    print("cullNonPremium called but disabled (always premium mode)");
    return;
    
    /*
    // Código original (COMENTADO):
    final repomanReponames = await repoManager.getStringList(StorageKey.repoman_repoNames);
    if (repomanReponames.length > 1) {
      List.generate(repomanReponames.length - 1, (index) async {
        final manager = await SettingsManager().reinit(repoIndex: 1 + index);
        await manager.clearAll();
      });
      await repoManager.setInt(StorageKey.repoman_repoIndex, 0);
      await repoManager.setStringList(StorageKey.repoman_repoNames, [repomanReponames[0]]);
    }
    */
  }

  void dispose() async {
    hasPremiumNotifier.dispose();
  }
}