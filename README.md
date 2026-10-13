# BuildCraft Studio — M1.1 — Precision and Creative UI (v0.1.1)

**Status: v0.1.1 experimental; compilação com Forge real e teste no Minecraft ainda necessários.**

Projeto novo e independente do IsoCraft Camera e do mod de terror. Objetivo: **editor virtual de casas e estruturas** para Minecraft **Forge 1.12.2**, compilação com **JDK 8 + Gradle 4.9**. Sem veículos, motores ou física própria.

## O que a implementação M1 contém

- Tecla `B` abre uma tela 3D de construção **em mundo individual**, sem pausar o mundo e sem movimentar o Steve.
- A câmera usa uma entidade local como ponto de vista; arrastar com **botão direito** orbita, **botão do meio** desloca o foco e **scroll** controla o zoom. Os movimentos são restringidos a chunks carregados.
- Clique esquerdo coloca um bloco **virtual**, **Shift+clique** seleciona o bloco virtual, `Delete` remove a seleção; `Ctrl+Z` desfaz e `Ctrl+Y` refaz.
- Grade/nível virtual ajustável por `PageUp/PageDown` ou `Shift+scroll`. Raycast do cursor no plano ou na face de blocos virtuais. Ghost com contorno colorido, renderizado em lotes sem modificar chunks.
- **Hotbar real e compacta**: ao entrar no editor, veja os mesmos nove espaços do jogador. `1–9` seleciona o item e `E` abre a tela vanilla do inventário Criativo (inclusive busca e todas as abas). Ao fechar, volta ao editor. O M1.1 **somente permite colocar os blocos da lista segura**, mesmo que outros apareçam no catálogo.
- **Cursor de precisão**: direção calculada pelas matrizes de projeção do mundo, capturadas antes de entrar na interface, considerando superfícies reais, peças virtuais e o plano ativo.
- **Grade branca de terreno** em torno do foco: cada célula acompanha a superfície carregada, apenas visualmente, sem modificar blocos/chunks. `G` mostra/oculta; não é um shader ou projeção de grade em faces verticais.
- Vistas rápidas: `I` isométrica, `T` superior, `O` frontal, `P` lateral. `R/F` reservadas para rotação de blocos em M2.
- Autosave a cada ~10s se houver alterações; salvar com `Ctrl+S` ou ao fechar. Salva em `<diretório do jogo>/buildcraftstudio/projects/<pasta_mundo>_dimN.dat` com `.bak`, onde o diretório é derivado da configuração Forge. Em caso de erro, o aviso mostra a causa resumida e o log `logs/latest.log` registra caminho e exceção.
- Botão **FINALIZAR** exige **dois cliques**, o primeiro informa quantas posições reais já estão ocupadas, o segundo solicita a finalização no servidor integrado.
- No M1, **somente Criativo**, até **128 blocos por finalização**, com validação de alcance de 64 blocos, chunks carregados, limites de altura, permissões, blocos com TileEntity protegidos e aplicação em lotes de 16 por tick. O projeto virtual é mantido após enviar o pedido e o resultado aparece no chat.
- Caso o Steve sofra dano durante a edição, o editor tenta salvar e fecha.

## Restrições **importantes**

Este é **M1**, não a especificação v1.0 inteira. Ainda não implementados: Sobrevivência e cobrança/reembolso de itens, carregamento de blocos arbitrários de outros mods, ferramentas de parede/linha/piso/caixa, espelhamento, seleção múltipla, blueprint exportável, rotação R/F, corte por andares, reversão de construções já finalizadas, journal em disco e recuperação transacional após crash, confirmação de compatibilidade em servidores. A prévia usa cubos coloridos em vez das texturas exatas. **Não use em mundos importantes.**

A finalização Criativo tenta reverter blocos alterados em caso de falha detectada, mas **não garante recuperação após crash nem efeitos colaterais de outros mods**. A finalização em chunks carregados e a validação simples podem precisar de correções após testes reais.

## Compilação recomendada: GitHub Actions

Este pacote contém `.github/workflows/build.yml`. Crie um repositório **separado** no GitHub (sugestão `BuildCraftStudio1122`) e envie o conteúdo completo desta pasta à raiz do repositório, **preservando pastas ocultas `.github`**. O workflow fará:

1. Configura Temurin Java 8;
2. Executa os testes independentes;
3. Baixa Gradle 4.9 verificando SHA-256;
4. Compila com ForgeGradle 2.3 e Forge 1.12.2-14.23.5.2847;
5. Confere a existência de `mcmod.info` e da classe principal no JAR;
6. Se passar, publica o artefato `BuildCraft-Studio-M1.1-Forge1122`.

**Compile no GitHub Actions para conferir a API real do Forge.** Um build aprovado não substitui testes de cursor, grade e inventário dentro do jogo.

### Compilação local

Requer **JDK 8 completo** e **Gradle 4.9** no `PATH`. Execute `BUILD.bat` e confira `BUILD_LOG.txt` em caso de erro. O JAR esperado é `build/libs/buildcraft-studio-forge1122-0.1.1.jar`. Não use JRE 8 sozinho.

## Teste funcional obrigatório antes de recomendar o mod

1. Faça backup do mundo; inicie um mundo Criativo novo apenas com Forge 1.12.2 e este mod.
2. Entre no editor com B, pressione E, busque um bloco simples (ex. pedra) no inventário Criativo, coloque na hotbar e volte ao editor fechando o inventário; confirme que a peça selecionada é a mesma.
3. Mova o cursor de uma borda a outra, gire a câmera, teste telas 16:9 e 4:3 e várias escalas de interface: o ghost deve apontar à superfície escolhida sem deslocamento constante.
4. Teste a grade branca em terreno plano e montanhoso; alterne com G. Nenhuma grade deve atravessar blocos altos como se estivesse no ar.
2. Confirme que aparece na lista de Mods, entre no mundo e pressione `B`.
3. Confira que Steve não se move, o tempo avança e órbita/pan/zoom não fazem chunks sumirem.
4. Coloque 10 blocos na grade, feche com `B` e reabra; confirme que o projeto reaparece.
5. Selecione com Shift+clique, delete e desfaça/refaça; altere altura com `PageUp`.
6. Confirme `FINALIZAR` duas vezes em local vazio e em local com blocos reais; confira o chat e o terreno.
7. Tente finalizar fora do alcance, em chunk não carregado, com bloco protegido e mais de 128 blocos: o jogo deve bloquear.
8. Teste dano durante a edição e recuperação após reiniciar Minecraft.
9. Envie `logs/latest.log`, prints ou vídeo se houver falha. Não considere estável até a validação visual e dos logs.

## Fontes consultadas

- [Forge 1.12 — networking SimpleImpl](https://docs.minecraftforge.net/en/1.12.x/networking/simpleimpl/)
- [Forge 1.12.2 GuiScreen JavaDocs](https://nekoyue.github.io/ForgeJavaDocs-NG/javadoc/1.12.2/net/minecraft/client/gui/GuiScreen.html)
- [Forge 1.12.2 World JavaDocs](https://nekoyue.github.io/ForgeJavaDocs-NG/javadoc/1.12.2/net/minecraft/world/World.html)

Veja `QA_REPORT.md`, `M1_SCOPE.md` e a especificação integral em `docs/`.
