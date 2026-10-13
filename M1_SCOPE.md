# Marco M1 — Relação com a especificação v1.0

A especificação v1.0 contém 59 perguntas rastreadas, 58 decisões vigentes, a pergunta 18 personalizada, e a pergunta 40B cancelada após exclusão definitiva dos veículos. O escopo está preservado como backlog, **não como funcionalidades prontas**.

## Entregues em forma de código-fonte experimental

- Entrada pelo B, câmera local, pan/orbit/zoom, foco inicial no bloco apontado ou 5 blocos à frente do olhar.
- Editar no plano virtual; 9 blocos rápidos de um subconjunto de blocos vanilla, até 512 blocos na prévia.
- Colocar, selecionar um bloco, remover seleção, desfazer/refazer, ajustar altura.
- Ghost colorido com duas chamadas de draw para peças e contornos, preview virtual fora do mundo real.
- Salvamento local atômico quando suportado, `.bak` e formato NBT com esquema versionado.
- Finalização criativa simples em até 128 blocos, preflight no servidor integrado, aplicação em lotes 16 por tick, tentativa de rollback.

## Não entregues nesta fase

- Preview com modelos e texturas exatas; orientação avançada, rotacionar R/F, seleção múltipla e simetria.
- Linha/parede/piso/caixa e editor de regiões dimensionáveis além da janela inicial de 64³.
- Catálogo completo de blocos do Forge e suporte a dados seguros/TileEntities, placeholders preservados de mods ausentes.
- Sistema de materiais de Sobrevivência e reembolso; baús próximos.
- Journal persistente para colocação/reversão com recuperação de crash e undo de mundo.
- Exportação/importação de blueprints, camadas, corte por andares, multiplayer e configurações por usuário/servidor.

Todas estas funcionalidades permanecem planejadas. Nenhum sistema de veículos faz parte do projeto.