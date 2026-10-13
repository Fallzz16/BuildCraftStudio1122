# QA — BuildCraft Studio v0.1.2

## Situação
Código revisado para Forge 1.12.2 / Java 8. Este relatório não substitui teste de jogo.

## Testes locais
- EditorMathTest: 22/22 asserts.
- BuildDocumentTest: 26/26 asserts.
- SafeSaveStreamTest: compactar para memória, escrever arquivo real, forçar sync em descritor aberto, descompactar e verificar bytes: aprovado.
- Alterações documentadas e rastreáveis no GitHub.

## Cenários necessários no Minecraft
1. Abrir em mundo Criativo de teste, escolher diferentes blocos (pedra, madeira e vidro) e confirmar os modelos reais na prévia.
2. Construir 10–30 blocos, forçar Ctrl+S, sair, reentrar, fechar/reabrir o mundo; não deve aparecer `SyncFailedException` nem perder projeto.
3. Finalizar projeto de até 128 blocos sem afetar TileEntity, conferir mensagem do servidor.
4. Com bloco virtual na tela, botão direito clique rápido remove; botão direito arrastando orbita; Shift+clique e Delete permanecem funcionais.
5. Tab alterna modo Steve/câmera e WASD funciona com menu exibido. Testar colisões, pulo, dano e retorno ao modo normal.
6. Grade branca em floresta/montanha, alta taxa de quadros; avaliar FPS com 0, 100 e 500 blocos virtuais e G ligado/desligado.
7. E abre inventário Criativo, fecha e retorna à sessão sem quebrar movimento ou renderização.

## Limitações
- Sem benchmark medido no jogo. Malhas de modelos de blocos carregam iluminação e vizinhança do mundo real; nem toda aparência de mod será perfeita.
- A visualização com blend alfa varia conforme o modelo, driver OpenGL e textura; a prévia pode ter opacidade diferente do esperado.
- O movimento do Steve depende do encaminhamento das KeyBindings no loop do cliente, e precisa de teste na versão real do jogo.
- M1 não fornece journal transacional de reversão no mundo; testá-lo em mundo descartável.
