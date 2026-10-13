# Relatório de QA — M1 v0.1.0

## Efetivamente executado no ambiente local

- [x] **22** asserções matemáticas de orientação de câmera, raycast de tela, plano, limites e sinais.
- [x] **26** asserções para documento virtual, bordas 64³, histórico, redo, carga limitada e máximo de 512 posições; classes Forge simuladas **somente para esta suíte pura**, não para o mod completo.
- [x] Verificação estrutural do projeto (arquivos, recursos, pacote e scripts).
- [x] Revisão manual das interfaces Java do Forge 1.12.2 (javadocs).

## Compilação real executada no GitHub Actions

- [x] **Gradle 4.9 + Java 8 + Forge 1.12.2-14.23.5.2847**: `gradle clean build` concluído com sucesso.
- [x] **48 testes independentes** concluídos no runner do GitHub.
- [x] JAR verificado: contém `mcmod.info` e `dev/buildcraftstudio/BuildCraftStudio.class`.
- [x] Artefato publicado em [GitHub Actions #2](https://github.com/Fallzz16/BuildCraftStudio1122/actions/runs/38330901527).
- [x] Integridade do JAR verificada por SHA-256: `696981731c7b2110fc4e0aa9e0488e10f3e60a4c666d6f7e15cda328e9a968e5`.

O primeiro build falhou por acesso a `Minecraft.mcDataDir`, corrigido para usar `Loader.instance().getConfigDir().getParentFile()` na segunda compilação.

## Ainda não executado e não aprovado
- [ ] Inicialização do cliente Forge com `.jar` instalado.
- [ ] Câmera/view entity + GuiScreen + render world no Minecraft.
- [ ] Colocação real/validade de packet e TickEvent.ServerTickEvent.
- [ ] Testes de regressão de chunk, sombras, shaders, mods e FPS.
- [ ] Reinício/crash do jogo durante a aplicação e recuperação garantida.
- [ ] Persistência de dados com blocos ausentes, alterações de mods e eventos de mundo.

## Riscos conhecidos

1. O uso de `setRenderViewEntity` com entidade local precisa ser testado para frustum, som, chunks, cursor e renderização quando GuiScreen está aberto.
2. O cálculo do cursor usa o FOV configurado, não efeitos temporários de FOV; pode exigir calibração durante jogo.
3. O renderer M1 desenha *volumes coloridos*, não modelos exatos de escadas, lajes ou blocos com texturas.
4. O servidor aplica somente conjunto pequeno de blocos estáticos e não oferece transação persistida ou undo de mundo.
5. A compilação real passou, mas isso não confirma que a câmera, as interações e os efeitos gráficos funcionem corretamente durante o jogo.

**Conclusão:** compilação real do M1 aprovada no GitHub; release **experimental** que ainda requer teste de inicialização e jogabilidade no Minecraft.