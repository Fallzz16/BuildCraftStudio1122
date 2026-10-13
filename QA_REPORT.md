# Relatório de QA — M1 v0.1.0

## Efetivamente executado no ambiente local

- [x] **22** asserções matemáticas de orientação de câmera, raycast de tela, plano, limites e sinais.
- [x] **26** asserções para documento virtual, bordas 64³, histórico, redo, carga limitada e máximo de 512 posições; classes Forge simuladas **somente para esta suíte pura**, não para o mod completo.
- [x] Verificação estrutural do projeto (arquivos, recursos, pacote e scripts).
- [x] Revisão manual das interfaces Java do Forge 1.12.2 (javadocs).

## Não executado e não aprovado

- [ ] `gradle clean build` contra as dependências Forge 1.12.2 reais. A máquina deste ambiente não tem JDK 8/Gradle 4.9 e não consegue resolver DNS para baixar o Gradle.
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
5. A compilação pode revelar diferenças de mappings/assinaturas não detectadas pelos testes puros.

**Conclusão:** protótipo-fonte pronto para a primeira compilação real, não aprovado como release de Minecraft.