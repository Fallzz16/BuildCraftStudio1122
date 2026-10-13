# BuildCraft Studio v0.1.2 — Build Mode Usability & Recovery

## Correções implementadas
- **Autosave:** compactação em memória antes da escrita em disco para impedir que `CompressedStreamTools.writeCompressed` feche prematuramente o `FileOutputStream` usado pelo `FileDescriptor.sync`. Verificação de leitura do arquivo temporário antes de substituir o projeto e backup anterior preservados.
- **Blocos com textura:** prévia dos blocos usa o renderizador de modelos vanilla/Forge (`BlockRendererDispatcher`) e agora segue o bloco selecionado na hotbar, em vez de mostrar todos os blocos como cubos azuis.
- **FPS:** modelos já construídos ficam armazenados em `BufferBuilder.State`, reconstruídos apenas quando o documento muda. A grade de terreno só consulta alturas quando seu centro se move de célula; agora desenha apenas duas arestas por célula. Cursor não executa duas seleções 3D completas por quadro.
- **Apagar:** clique direito simples em bloco virtual remove imediatamente, sem depender de seleção prévia; arrastar direito orbita. Shift+clique e Delete continuam disponíveis.
- **Mobilidade:** `Tab` alterna entre deslocar o Steve com WASD (mantendo o menu do editor na tela) e deslocar livremente a câmera com WASD; no modo câmera, espaço sobe e C desce. O modo Steve utiliza os controles de movimento vanilla e permanece vulnerável a danos, conforme especificação.
- **Segurança:** a aplicação ao mundo continua exclusiva do servidor, M1 no Criativo, máximo 128 blocos por operação e nenhum bloco real modificado na prévia.

## Validado localmente
- 22 asserts de EditorMath e 26 de BuildDocument passaram.
- Teste adicional de compactação em memória + escrita + `FileDescriptor.sync` + descompactação passou.

## Pendente até testar dentro do jogo
- Qualidade visual de blocos específicos, desempenho em mapas com shaders/modpacks, modo de caminhada com GUI e comportamento de salvamento em todas as instalações Windows.
- `Ctrl+Z` e `Ctrl+Y` continuam atuando apenas na prévia; não há undo de finalização na M1.
