# BuildCraft Studio — Especificação técnica mestre v1.0

**Status:** referência consolidada para planejamento e futura implementação. **Nenhum código do mod foi criado nesta etapa.**  
**Versão-alvo inicial:** Minecraft Java Edition 1.12.2 · Forge · Java 8.  
**Produto:** editor tridimensional de arquitetura para casas, prédios, pontes, castelos, cenários e outras estruturas estáticas.  
**Exclusões definitivas:** veículos, motores, rodas, juntas mecânicas e física própria.  
**Data de consolidação:** 12/10/2026.  
**Origem:** especificação v0.7, mantida como registro integral das decisões 1–39 e 41–59, com pergunta 18 personalizada e antiga 40B cancelada.

## 0. Como ler e utilizar a especificação

Este documento distingue quatro categorias de informação:

- **[CONFIRMADO]**: escolha dada explicitamente pelo usuário no planejamento, incluindo o padrão bloco por bloco com ferramentas opcionais e a exclusão de veículos.
- **[APROVADO NO CONTEXTO]**: aceitação do encaminhamento imediatamente proposto, especificamente os valores **16 blocos no Sobrevivência e 64 blocos no Criativo, configuráveis**, quando o usuário disse «ok pode fazer» após a proposta de fechar a v1.0.
- **[PADRÃO TÉCNICO PROPOSTO]**: política ou valor que esta revisão recomenda para a futura implementação. É uma proposta operacional conservadora, **não uma escolha explícita do usuário**, e pode ser ajustada antes ou durante os testes sem alterar os requisitos de produto.
- **[VALIDAÇÃO PENDENTE]**: comportamento cuja correção depende de compilar com Forge real e executar testes no Minecraft, com e sem outros mods.

**Regra de precedência:** decisões explícitas do usuário > esclarecimentos posteriores > padrões técnicos propostos > hipóteses de implementação. Se uma recomendação contradisser uma decisão confirmada, prevalece a decisão confirmada.

**Limite de autorização:** este documento fecha o projeto conceitual, mas não autoriza programar, publicar no GitHub, modificar mundos, usar contas ou criar um novo repositório. Uma autorização específica de implementação deverá ser obtida antes de iniciar o código.

---

## 1. Visão do produto

O jogador pressiona **B** em qualquer lugar do mundo para entrar no editor. O personagem fica parado, sem controles normais de deslocamento, mas o mundo continua em execução. A câmera passa a orbitar uma área configurável, com foco, zoom, translação e atalhos de vistas ortogonais. O jogador seleciona blocos em catálogo e monta uma **estrutura virtual**, um bloco por clique por padrão. Ferramentas opcionais permitem linhas, paredes, pisos, caixas, simetria e transformações de seleções. Enquanto edita, nenhum bloco virtual modifica o mundo real. Ao finalizar, o jogador vê a prévia global, pode mover e girar a construção e solicita a aplicação ao servidor.

- **Criativo:** construção livre, sem consumo de recursos, com substituição de blocos existentes somente na finalização e após confirmação explícita.
- **Sobrevivência:** exige terreno previamente limpo e recursos válidos e suficientes; informa faltantes; consome os recursos na finalização aprovada; regras adicionais para blocos sem custo identificável.
- **Persistência:** autosave, blueprints próprios compartilháveis e recuperação de sessões; suporte a outros formatos de schematic fica para etapa futura.
- **Escopo futuro permitido:** ferramentas de arquitetura, decoração, controle de andares e compatibilidade ampliada com blocos de outros mods. **Não existe roadmap de veículos.**

## 2. Inventário das decisões aprovadas

### Rodada 1 — 1–12

| ID | Decisão confirmada | Requisito resultante |
|---|---|---|
| 1A | Construir em qualquer lugar do mundo | Entrada por tecla, sem bancada ou dimensão exclusiva. Sujeito às regras de alcance e chunks confirmadas depois. |
| 2C | Área configurável, inclusive suspensa | Volume e origem editáveis, com planos de construção no ar. |
| 3C | Órbita livre + isometria | Câmera 360°, botão de retorno ao ângulo isométrico. |
| 4A | Direito arrasta / meio arrasta / scroll | Órbita, deslocamento do foco e zoom, respectivamente. |
| 5D | Blocos vanilla e de outros mods | Catálogo por registro de blocos; compatibilidade condicionada a suporte efetivo. |
| 6C | Grade de 1 bloco preparada para subdivisão | Resolução inicial de 1 bloco; não implementar sub-blocos na primeira versão. |
| 7B | Ferramentas intermediárias | Colocar, remover, girar, desfazer/refazer, seleção múltipla, copiar/colar, mover e espelhar. |
| 8B | Simetria de um eixo | Um plano de espelhamento configurável. |
| 9A | Interface minimalista | Priorizar espaço de edição e controles contextuais. |
| 10A | Catálogo lateral | Categorias, pesquisa e favoritos. |
| 11C | Autosave e blueprints | Guardar automaticamente e permitir exportar/importar arquivo próprio. |
| 12B | Materializar só ao finalizar | Edição totalmente virtual, sem efeitos reais anteriores à confirmação. |

### Rodada 2 — 13–22

| ID | Decisão confirmada | Requisito resultante |
|---|---|---|
| 13A | Tecla B | Abre/fecha o modo de edição; remapeável. |
| 14A | Steve parado, mundo ativo | O jogador não se desloca pelo editor; o jogo não pausa. |
| 15A | Origem no bloco visado | Ponto de construção inicial ancorado no alvo, depois reposicionável. |
| 16A | Clique, Shift+clique, Delete, Ctrl+Z | Clique esquerdo coloca; Shift+clique seleciona; Delete exclui seleção; Ctrl+Z desfaz. |
| 17C | Volume inicial máximo 64×64×64 | Tamanho configurável com proteções de desempenho. |
| 18 (personalizada) | Criativo substitui; Sobrevivência limpa antes | Criativo pode destruir/substituir blocos existentes ao confirmar. Sobrevivência exige preparar a área manualmente. |
| 19B | Blocos suspensos aceitos na prévia | Avisar sobre gravidade, suporte e atualizações físicas quando finalizados. |
| 20B | Estados e dados especiais quando seguro | Preservação compatível e diagnóstico dos casos não suportados. |
| 21C | Criativo + Sobrevivência | Criativo livre; Sobrevivência consome recursos válidos. |
| 22C | Blueprint próprio + formatos futuros | Formato versionado exclusivo; import/export de outros formatos em fase futura. |

### Rodada 3 — 23–32

| ID | Decisão confirmada | Requisito resultante |
|---|---|---|
| 23C | Linhas, paredes, pisos, caixas | Ferramentas opcionais; padrão continua bloco individual. |
| 24B | R e F | R gira 90°; F alterna eixo de rotação. |
| 25B | Grade 3D invisível e blocos flutuantes | Posicionamento sem contato obrigatório com bloco existente. |
| 26B | Transformar seleções | Mover, copiar, girar e espelhar grupos. |
| 27C | Foco automático, seleção e manual | Modos de foco da câmera. |
| 28B | Fantasma verde/vermelho | Contorno mostra posicionamento válido/inválido. |
| 29C | Barra inferior + catálogo expansível | Peças recentes e biblioteca lateral sob demanda. |
| 30C | Seções editáveis + otimização | Particionar preview e ocultar trabalho fora da visão. |
| 31C | Materiais no encerramento | Relatório de recursos faltantes, sem cobrança ao editar. |
| 32B | Prévia global antes de concluir | Mover/girar conjunto completo antes da aplicação. |

### Rodada 4 — 33–40

| ID | Decisão confirmada | Requisito resultante |
|---|---|---|
| 33B | Plano ajustável + encaixe em faces | Seleção da face visível ou do plano ativo como superfícies de posicionamento. |
| 34C | PageUp/PageDown e Shift+scroll | Alteram a altura do plano, com atalhos remapeáveis. |
| 35C | Arrastar / prévia / soltar | Ferramentas de área mostram dimensões antes de confirmar ao soltar. |
| 36C | Seleção avançada | Retângulo, individual, adicionar/remover e selecionar por tipo. |
| 37B | Transparência da oclusão virtual | Peças virtuais que escondem a edição podem desbotar temporariamente. |
| 38A | Sofrer dano fecha o editor e salva | Prioridade de recuperação e restauração de controle ao Steve. |
| 39B | Custo especial válido para blocos sem item | Sobrevivência só finaliza se houver regra explícita e verificada de custo. |
| 40B | **CANCELADA** | Nenhum conector, pivô, motor ou sistema preparado para veículos; escopo alterado expressamente. |

### Rodada 5 — 41–46

| ID | Decisão confirmada | Requisito resultante |
|---|---|---|
| 41B | Céu como alvo: plano à frente do jogador | Fallback se não houver bloco sob a mira. |
| 42C | Grade discreta alternável | Realce do plano de altura ativo e opção mostrar/ocultar. |
| 43C | Transparência + corte por altura | Visualização de andares sem excluir peças virtuais. |
| 44B | Orientação sugerida e corrigível | Alertar se o bloco não suportar a rotação solicitada. |
| 45A | Autosave periódico e em eventos | Persistência com snapshots/recovery, inclusive ao sair. |
| 46A | Reversão da última finalização com validação | Registro de alteração do mundo e opção segura de desfazer a última operação aprovada. |

### Rodada 6 — 47–53

| ID | Decisão confirmada | Requisito resultante |
|---|---|---|
| 47A | Câmera restrita a chunks carregados | Nunca gerar chunks apenas pelo deslocamento da câmera. |
| 48A | Sobrevivência: todas posições livres | Mesmo blocos substituíveis (vegetação, neve fina etc.) precisam ser removidos antes. |
| 49B | Blocos compostos reconhecidos em conjunto | Portas, camas e estruturas multibloco tratadas de modo coerente quando houver adaptador. |
| 50A | Um conflito cancela toda reversão | Nunca reverter parcialmente uma finalização por escolha automática. |
| 51A | Referências ausentes preservadas | Exibir placeholder de alerta; impedir finalizar posições com bloco ausente. |
| 52B | Limites maiores configuráveis | Expandir além de 64³ apenas sujeito a orçamento de desempenho e controles. |
| 53B | Vistas superior/frontal/lateral | Atalhos para vistas predefinidas além da órbita livre. |

### Rodada 7 — 54–59

| ID | Decisão confirmada | Requisito resultante |
|---|---|---|
| 54C | Alcance configurável por modo | Criativo e Sobrevivência podem ter políticas/distâncias distintas; servidor valida. |
| 55B | Colocação em lotes por tick | Evitar pico de congelamento ao finalizar estruturas grandes. |
| 56C | Inventário inicialmente; baús futuramente | V1 consome apenas inventário do jogador; futura integração com baús próximos. |
| 57A | Devolver materiais após reversão integral | Reembolso apenas quando reversão completa for validada e concluída sem conflitos. |
| 58A | Singleplayer inicial, preparado para multiplayer | Primeiro lançamento em mundo individual, arquitetura com autoridade do servidor integrado. |
| 59A | Nunca copiar conteúdo de inventários | Guardar somente estrutura e estado seguro, sem NBT com itens/energia internos copiáveis. |

### Decisão complementar posterior à Rodada 4

**Construção bloco por bloco como padrão, com ferramentas de múltiplos blocos opcionais.** As ferramentas de linha, parede, piso e caixa permanecem no escopo porque o usuário confirmou expressamente esta combinação. Todas as funcionalidades de veículos estão excluídas.

---

## 3. Requisitos funcionais consolidados

### RF-01 — Entrada/saída de edição

- Tecla B em qualquer local permitido; se mirar um bloco, usar esse alvo como origem. Se mirar o céu, inicializar plano flutuante alguns blocos à frente. Distância sugerida do fallback: **5 blocos** à frente do ponto de vista, limitada à região carregada e ao alcance permitido; **[PADRÃO TÉCNICO PROPOSTO]**.
- Suspender entrada de movimento normal do Steve durante a edição, mas não pausar ticks do mundo. Restaurar câmera, cursor, teclas e HUD após saída/erro.
- Dano sofrido pelo personagem provoca salvamento de emergência e saída do editor. Definir o que ocorre quando o salvamento falhar antes de implementar.
- Mudança de dimensão, morte, fechamento do mundo ou desconexão não podem resultar em aplicação acidental de blocos ou perda silenciosa do blueprint.

### RF-02 — Câmara orbitária e navegação

- Arrastar com botão direito: órbita ao redor de um ponto de foco. Arrastar com botão do meio: pan. Scroll: zoom.
- Foco automático, foco em seleção, foco manual; reset para isometria; vistas frontal, lateral e superior.
- Limitar movimento à região carregada e limites configurados; não acessar ou gerar chunks indisponíveis por interação remota.
- Controles remapeáveis e sem dependência de teclado numérico. Interação com UI deve suspender o drag 3D.

### RF-03 — Grade, projeção do cursor e posicionamento

- Posição do cursor calculada por raio da câmera e interseção com a peça virtual ou com o plano ativo.
- Prioridade de alvo proposta: peça virtual visível sob cursor -> face de encaixe -> plano ativo. Regra final de prioridade sujeita a teste de usabilidade.
- Grade padrão de 1 bloco; nenhuma fração de bloco no primeiro lançamento; planos podem ser elevados por Page Up/Down ou Shift+scroll.
- Ghost block mostrando modelo e orientação; contorno verde para candidato válido e vermelho para inválido, incluindo informações de motivo em tooltip.
- Suporte a blocos suspensos sem suporte no projeto, com alertas de dependência/gravitacional antes da finalização.

### RF-04 — Ferramentas de edição

- Clique esquerdo coloca 1 bloco quando nenhuma ferramenta de massa está ativa. Shift+clique seleciona; Delete exclui; Ctrl+Z/Y desfaz/refaz.
- Catálogo: favoritos, busca, filtros por mod/categoria e barra de usados recentemente.
- R gira 90° e F alterna eixo respeitando propriedades válidas do bloco.
- Ferramentas opcionais: linha, parede, piso, caixa, seleção por área, seleção por tipo, cópia, colagem, espelho de eixo único, mover e girar seleção.
- Arrastar exibe prévia da ferramenta ativa, soltar confirma toda a operação como **uma entrada do histórico**.
- Não aplicar nenhuma dessas alterações ao mundo real antes de Finalizar.

### RF-05 — Visualização de edifícios

- Prévia seccionada para que a renderização não precise redesenhar todas as peças a cada alteração.
- Possibilidade de desbotar as peças virtuais que ocultam a seleção e cortar visualmente níveis superiores.
- Visualização e recortes não apagam as peças e não alteram o mundo de verdade.
- Transparência de modelos especiais e renderizadores de TileEntity de outros mods podem exigir fallback próprio.

### RF-06 — Catálogo e compatibilidade

- Ler registro de blocos Forge e manter IDs nominais estáveis (`modid:nome`) mais propriedades de estado compatíveis.
- Validar orientações disponíveis em cada bloco. Não criar orientações inexistentes.
- Blocos multipeça reconhecidos recebem adaptadores por categoria; os desconhecidos são sinalizados.
- Dados de TileEntity só entram no blueprint mediante uma política de campos seguros. Inventários, fluidos, energia, identidade de proprietário, links remotos ou campos desconhecidos não devem ser copiados automaticamente.
- Um bloco de mod ausente permanece referenciado no blueprint em forma de placeholder; nenhuma finalização desse bloco é permitida enquanto a dependência estiver ausente.

### RF-07 — Salvamento e projetos

- Formato proprietário versionado: identificador, versão do esquema, dimensão/origem de referência, dimensões, paleta de blocos/estados, peças esparsas locais, grupos reconhecidos, dados seguros, metadados de criação/edição e verificação de integridade.
- Autosave periódico e em operações importantes. Escrita atômica de arquivo temporário + substituição e pelo menos uma cópia de recuperação são recomendações técnicas a validar no sistema real.
- Exportar/importar blueprint independente da localização; ignorar caminhos perigosos, NBT malformado, arquivos excessivos e dados não reconhecidos.
- Importação de formatos externos fica para versões posteriores.

### RF-08 — Finalização no mundo

1. Apresentar prévia da estrutura inteira; permitir translação e rotações compatíveis.
2. Calcular a região de impacto no mundo para a posição final.
3. Verificar modo do jogador, distância configurada, chunks carregados, altura válida, permissões, ausência de entidades/obstáculos relevantes, componentes multibloco e adaptadores de compatibilidade.
4. Criativo: mostrar tudo que será substituído; exigir confirmação inequívoca para destruição/substituição.
5. Sobrevivência: exigir que todas as posições-alvo que requerem espaço estejam livres, incluindo plantas e neve; confirmar custo de cada componente, inventário suficiente e integridade do plano.
6. Reservar os materiais elegíveis e preparar registro de recuperação antes de iniciar as alterações de mundo.
7. Colocar em lotes de tamanho configurável por tick, verificando as condições que possam ter mudado; controlar dependências e ordem de colocação.
8. Apenas após terminar e verificar o estado final, registrar conclusão e liquidar os recursos consumidos; manter registro da última finalização para reversão segura.

**Atenção:** a possibilidade de colocar blocos em lotes enquanto o mundo roda significa que alterações intermediárias serão observáveis. O Forge e os blocos de outros mods podem executar efeitos não reversíveis. Não prometer atomicidade absoluta sem uma política de bloqueio/compatibilidade, validações reais e testes exaustivos.

### RF-09 — Reversão

- Só a última finalização registrada está inicialmente elegível.
- Preflight completo compara o estado atual dos blocos impactados com a saída esperada. **Qualquer divergência cancela toda a reversão** (50A), antes de modificar o mundo.
- Se aprovada, reverter por operação controlada, com journal e plano de recuperação para falha intermediária. Não reverter silenciosamente só parte da estrutura.
- No Sobrevivência, devolver recursos somente após reversão completa e auditada; bloquear devoluções duplicadas e não creditar recursos com conflito.
- Dados internos perigosos de TileEntities e ações/efeitos colaterais externos podem impedir a reversão segura e devem ser bloqueados ou avisados antecipadamente.

### RF-10 — Limites e disponibilidade

- Região padrão: até 64×64×64 posições (262.144 posições possíveis). Isso não equivale ao número recomendado de peças ativas.
- Sugestão de subdivisões de 16×16×16 e armazenamento esparso; 64³ representa 64 células de 16³ se o volume for alinhado e completo.
- Limites maiores configuráveis exigem orçamento de peças, seções, memória, renderização e processamento por tick. Valores concretos: **a medir**.
- Regra de alcance por modo é de autoridade do servidor; valores iniciais aprovados no contexto: **16 blocos no Sobrevivência e 64 blocos no Criativo**, configuráveis.

---

## 4. Arquitetura proposta (recomendação, não decisão já aprovada)

| Subsistema | Responsabilidade | Não deve fazer |
|---|---|---|
| Project Core | Paleta, coordenadas locais, estados de peças, transações de edição | Acessar mundo real durante alteração virtual |
| Editor State Machine | Entrar, editar, pausar interações, sair por dano/fechamento | Esquecer restaurar controles do jogador |
| Camera & Picking | Órbita, pan, zoom, visão predefinida, raycast no editor | Gerar chunks para desenhar alvo remoto |
| Preview Renderer | Malhas seccionadas, corte por altura, transparência, ghosts | Modificar blocos ou chunks reais |
| Tools & History | Command pattern para cada operação e agrupamento de ações | Gerar histórico gigante por bloco para uma única parede |
| Block Compatibility | Registry, estados, orientação e adaptadores de grupos/TileEntity | Copiar dados perigosos arbitrariamente |
| Blueprint Storage | Escrita versionada, importação e recuperação segura | Tratar nome de bloco desconhecido como ar e descartar dados |
| Placement Planner | Preflight, custo, substituição, dependência e posicionamento | Confiar em prévia do cliente como autorização |
| Placement Executor | Journal, reserva, lotes, validação por tick e relatório de erros | Considerar transação concluída antes do último lote |
| Undo Executor | Checagem integral do estado pós-placement, reversão e reembolso | Reverter parcialmente se houver conflito |
| Network Layer | Validação cliente-servidor; singleplayer inicial | Aplicar alterações do mundo em thread de rede |

**Separação crucial:** cliente controla o editor/preview; servidor integrado autoriza e aplica mudanças reais. A futura versão multiplayer reaproveita esta separação, mas não faz parte do primeiro lançamento.

---

## 5. Matriz de conflitos e encaminhamentos

| Decisões afetadas | Conflito / risco | Direção recomendada | Falta confirmar? |
|---|---|---|---|
| 1A + 47A + 54C + 58A | Construir em qualquer lugar × chunks carregados × alcance | Abrir onde houver permissão, mas bloquear câmera e finalização fora dos limites efetivos | Não: valores padrão 16/64 adotados; métrica e aplicação exata exigem teste |
| 14A + 38A + 45A | Mundo ativo e Steve vulnerável × encerramento por dano | Snapshot imediato, fechar interface, restaurar controles; nenhum placement em andamento enquanto só editando | Política proposta na seção 9; teste real pendente |
| 25B + 33B + 42C | Bloco suspenso × plano e snap × grade invisível | Plano de edição com altura; snap em faces ou no plano ativo | Teste de precisão, não conflito decisório |
| 18 personalizada + 32B + 48A | Prévia pode mover estrutura inteira sobre terreno ocupado | Validar novamente o destino final após mover/girar, com regras por modo | Não quanto à regra; implementação a testar |
| 19B + 49B + 55B | Quedas, suporte e conjuntos multibloco × colocação gradual | Dependências, classificação por segurança, simulação conservadora e bloqueio de casos perigosos | Política conservadora proposta na seção 9; ampliar após testes |
| 20B + 39B + 51A + 59A | Estados de mod × custo × mods ausentes × dados sensíveis | Adaptadores explícitos, NBT seguro, placeholder, custo válido obrigatório no Sobrevivência | Sim: aceitar/bloquear tipos incompatíveis |
| 55B + 46A + 50A + 57A | Lotes × reversão integral × devolução de recursos | Pré-validação total, journal persistente, reservar recursos, rechecagem; rollback conservador | Política e limites da garantia descritos na seção 9; validar na prática |
| 9A + 10A + 29C + 43C | Interface minimalista × catálogo + ferramentas avançadas | Painéis colapsáveis, barra inferior e menus contextuais | Não; opções se complementam |
| 17C + 30C + 52B | Expansão de volume × desempenho | Seções e orçamento de renderização/processamento com medição real | Orçamento inicial de teste proposto na seção 9; calibrar por benchmark |
| 58A + 54C | Mundo individual × configuração autoritativa | Usar servidor integrado como autoridade desde o começo | Não |
| 56C + 57A + 59A | Inventário hoje; baús futuros; sem copiar conteúdo | Resource ledger com fontes suportadas, inicialmente só o jogador | Não para primeira fase |
| 40B (cancelada) | Preparação para física/veículos contradiz escopo atual | Eliminar todo suporte ou roadmap de veículos | Resolvido pelo usuário |

### Riscos que o documento não considera resolvidos

1. **Atomicidade real do mundo:** a 1.12.2 não fornece uma transação universal capaz de desfazer efeitos arbitrários de blocos e mods. A política de segurança deve aceitar recusar certos blocos/operações.
2. **Ordem e efeitos colaterais:** gravidade, updates de vizinhos, fluídos, portas e TileEntities podem produzir estado final diferente do blueprint; precisam de testes por classe de bloco.
3. **Acúmulo de operações:** salvar snapshot completo de muitos blocos ou renderizar dezenas de milhares de modelos sem orçamento pode travar. Faltam benchmarks reais.
4. **Dano do Steve e falha de disco:** salvar ao receber dano exige política explícita caso não se consiga gravar o projeto.
5. **Reembolso contra duplicação:** devolução no undo precisa estar atrelada a um registro de gasto concreto, e não a uma contagem estimada pelo cliente.

---

## 6. Proposta de UX e regras de teclado

| Contexto | Entrada inicial | Ação |
|---|---|---|
| Mundo | B | Entra no editor |
| Edição, ferramenta de bloco | Clique esquerdo | Coloca peça virtual |
| Edição, seleção | Shift + clique esquerdo | Inclui/seleciona peça(s) |
| Edição, órbita | Arrastar botão direito | Gira câmera |
| Edição, pan | Arrastar botão do meio | Move foco |
| Edição, zoom | Scroll | Zoom |
| Edição, plano | Shift + scroll ou PageUp/PageDown | Sobe/desce plano ativo |
| Edição, orientação | R / F | Rotaciona 90° / troca eixo |
| Edição, histórico | Ctrl+Z / Ctrl+Y | Desfaz/refaz |
| Edição, seleção | Delete | Exclui seleção |
| Ferramenta de área | Arrastar esquerdo | Prévia da região; soltar confirma |
| Revisão | Ação explícita Finalizar | Abre posicionamento global e relatório de preflight |

**Observações de UX:** não atribuir teclas finais para funções ainda não definidas; atalhos serão remapeáveis. Na UI (catálogo, busca, configuração), entradas devem ser consumidas pela interface, não pelo mundo ou editor. O clique direito continua dedicado à órbita **dentro do editor**, não à colocação de blocos.

---

## 7. Critérios de aceitação e plano de QA (ainda não executado)

### Gate A — Fundamentos do editor
- Abrir B mirando bloco ou céu, sem mover Steve e sem inserir blocos reais.
- Órbita, pan e zoom funcionam sem câmera atravessar restrições de chunk; sem desaparecimento inesperado de partes do terreno.
- Colocar dezenas de blocos em coordenadas virtuais com alinhamento correto; remover, selecionar e desfazer/refazer consistentemente.
- Fechar/reabrir editor ou mundo recupera exatamente o projeto; interromper por dano preserva histórico.

### Gate B — Ferramentas de arquitetura
- Escadas, lajes, portas e camas mantêm orientações válidas após rotacionar/espelhar.
- Linha/parede/piso/caixa geram geometria exata e uma única entrada de histórico por gesto.
- Eixos de simetria e visualização de andares não alteram blocos ocultos.
- Blueprints importados mantêm IDs e sinalizam corretamente blocos ausentes.

### Gate C — Validação do mundo
- Criativo: prévia correta de blocos destruídos e nenhuma substituição sem confirmação.
- Sobrevivência: bloqueio por qualquer obstáculo, falta de recurso ou falta de regra de custo; inventário só consumido quando finalização autorizada.
- Operação em lotes não provoca duplicação de inventário se o jogo fechar no meio; journal sinaliza e recupera estados pendentes.
- Tentar desfazer quando houve uma alteração posterior em qualquer bloco cancela a reversão inteira; sem reembolso.
- Após reversão integral bem-sucedida, inventário recebe recursos exatamente uma vez.

### Gate D — Compatibilidade e escala
- Blocos vanilla principais, incluindo escadas/lajes/cercas/portas/camas, renderizam e finalizam sem inconsistências.
- Testar pelo menos duas famílias representativas de blocos de mods: bloco simples e TileEntity. Os casos não seguros devem ser sinalizados/bloqueados.
- Benchmarks em estruturas de 16³, 32³ e até 64³ com variação de densidade; registrar FPS, memória, tempo de abertura e tempo de commit.
- Construção perto do limite de chunks carregados não gera chunks por acidente.

**Nenhum critério acima foi implementado ou testado até esta data.** São obrigações para as versões de desenvolvimento, não resultados.

---

## 8. Marcos propostos (não representam versões já construídas)

**Milestone M1 — Building Foundation:** câmera, raycast virtual, plano, catálogo básico, bloco individual, remover, histórico, autosave e finalização de construção pequena no Criativo.

**M2 — Architecture Tools:** seleção, linhas, paredes, pisos, caixas, cópia/colagem, simetria, corte por altura, blueprint exportável e orientação robusta.

**M3 — Survival & Safety:** materiais do inventário, custo de blocos, relatório de faltantes, finalização em lotes, registro de recuperação, reversão integral e reembolso auditado.

**M4 — Compatibility & Performance:** adaptadores de blocos compostos, modded blocks, limites maiores, otimização e testes extensivos. Multiplayer e baús próximos são futuras expansões separadas, não metas da primeira versão.

**Regra de qualidade:** cada milestone só é considerado aprovado depois de compilar com Forge real e passar por testes dentro do Minecraft. Testes com APIs simuladas não são suficientes para afirmar compatibilidade.

---


---

## 9. Fechamento técnico da v1.0 — políticas e valores

### 9.1 Alcance, dimensões e chunks — fechamento da questão 54C

**[APROVADO NO CONTEXTO]** Os valores iniciais recomendados de alcance ficam definidos em **16 blocos (Sobrevivência)** e **64 blocos (Criativo)**. Serão expostos em configurações apropriadas e validados no servidor integrado; eventual modo multiplayer terá configuração autoritativa do servidor.

**[PADRÃO TÉCNICO PROPOSTO]**

1. Medir distância euclidiana em três dimensões entre a posição do Steve e **cada posição-alvo de bloco que será finalizada**, não apenas o centro do projeto. Isso impede que um projeto com centro próximo coloque blocos arbitrariamente longe.
2. A câmera pode orbitar dentro da região carregada e dos limites de navegação, sem provocar a geração automática de chunks. **Foco da câmera e elegibilidade de finalização são restrições diferentes**: visualizar algo não autoriza construí-lo.
3. Se um bloco do projeto exceder o alcance na localização final, mostrar a quantidade e destacar as posições inválidas; impedir Finalizar até reposicionar o projeto ou mover o personagem **fora do editor**.
4. Respeitar limites de altura da dimensão e restrições adicionais de permissões antes de calcular ou tocar blocos de mundo. Não confundir «dimensão/alcance da área virtual» com «área efetivamente aplicável».
5. O plano inicial quando o jogador aponta para o céu surge a 5 blocos à frente, ajustando-se a um espaço carregado e permitido; esse valor é proposto e ajustável depois dos primeiros testes de usabilidade.

**[VALIDAÇÃO PENDENTE]** Testar casos-limite a exatamente 16/64 blocos, diagonal 3D, câmera perto da borda de chunk, dimensões distintas e projetos com extensão além do centro.

### 9.2 Classe de segurança de blocos e compatibilidade — fechamento das questões 19B, 20B, 39B, 49B, 51A e 59A

**[PADRÃO TÉCNICO PROPOSTO]** Usar uma política por capacidade, e não por nome do mod:

| Categoria | Prévia virtual | Finalização v1 | Reversão | Motivo |
|---|---|---|---|---|
| Blocos simples estáticos, com estado reconhecido | Sim | Sim, após validação | Sim, se íntegro | Estado reproduzível. |
| Escadas, lajes e outros blocos orientáveis vanilla | Sim | Sim, após checagem de estado | Sim, se íntegro | Metadados/orientações controlados. |
| Porta/cama e outros multiblocos conhecidos | Sim, como conjunto | Somente com adaptador verificado | Somente como conjunto | Dependências e integridade entre partes. |
| Areia, cascalho e peças dependentes de suporte | Sim, com alerta | Somente se o plano de finalização demonstrar estabilidade compatível | Restrita | Gravidade e atualizações de vizinhança podem alterar o resultado. |
| Água, lava, fogo e blocos de propagação agressiva | Sim, se modelo válido | **Bloqueada por padrão** | Não prometida | Efeitos de mundo difíceis de reverter. |
| Bloco com `TileEntity` simples e adaptador explícito | Sim, com aviso | Somente campos permitidos e seguros | Condicionada | Dados internos exigem whitelist. |
| Baús com itens, máquinas energizadas, armazenamento, links remotos, entidades complexas | Placeholder/modelo apenas | **Bloqueada por padrão** até adaptador seguro | Não prometida | Risco de duplicação, efeitos externos e perda de conteúdo. |
| Bloco de mod ausente ou estado desconhecido | Placeholder de aviso | **Proibida** | Não aplicável | Não converter silenciosamente em ar. |

A proibição inicial não contradiz o requisito **5D** de catálogo de blocos vanilla e de outros mods: **estar no catálogo e no blueprint não equivale a autorização automática de finalização**. O editor deve explicar o motivo do bloqueio e preservar a referência original. Não copiar automaticamente NBT com inventário, energia, fluido, proprietário, identidade de rede ou dados não reconhecidos.

**[VALIDAÇÃO PENDENTE]** Expandir a compatibilidade somente após testes in-game com blocos representativos; nenhuma garantia de suporte total a mods terceiros.

### 9.3 Finalização em lotes e operação interrompida — fechamento de 55B + 46A + 50A + 57A

**[PADRÃO TÉCNICO PROPOSTO]** Definir uma máquina de estados com identificador único da operação:

`DRAFT -> PREFLIGHT -> RESERVED -> JOURNALED -> APPLYING -> VERIFYING -> COMMITTED`

Saídas controladas: `BLOCKED` (sem alterações), `RECOVERY_REQUIRED` (operação incompleta), `REVERT_PREFLIGHT -> REVERTING -> REVERTED`.

**Protocolos de segurança:**

1. **Preflight integral**: verificar identidade/dimensão do mundo, chunks carregados, alcance por modo, conflito com terreno, custo, permissões, classe do bloco, integridade de multiblocos e quantidade de alterações.
2. **Reserva de recursos, apenas no Sobrevivência**: criar registro idempotente da operação e dos itens efetivamente reservados antes de alterar blocos; inventário é a única fonte na primeira fase. Cancelamento pré-commit sem mudança de mundo libera reserva com auditoria.
3. **Snapshot e journal persistentes antes do primeiro lote**: registrar estados anteriores e estados esperados das posições afetadas, IDs de operação, versão, checksum, progresso e entradas de inventário, em armazenamento apropriado ao mundo. Escrita deve ser recuperável e concluída antes do começo de cada etapa crítica. `WorldSavedData` pode servir a metadados, mas **não deve ser tratado sozinho como garantia de fsync ou atomicidade entre blocos**.
4. **Aplicação incremental no thread principal do servidor**: lotes com orçamento por tick, tratando grupos como unidades lógicas, recalculando incompatibilidades relevantes antes de continuar. Não acessar diretamente o mundo na thread de rede.
5. **Ponto de corte**: se uma validação falhar durante APPLYING, interromper. Nunca declarar sucesso nem creditar materiais enquanto o estado observado divergir do estado esperado.
6. **Recuperação de reinício/crash**: operação incompleta é identificada no próximo carregamento do mundo; bloquear novas finalizações na região afetada e iniciar análise do journal. Resolver por procedimento seguro e explícito (restaurar quando as condições forem verificáveis, ou apresentar intervenção manual). Não continuar cegamente.
7. **Commit**: marcar COMMITTED somente depois de verificação final; registrar a operação como candidata à reversão. Eventuais materiais reservados são então consolidados no registro auditável.
8. **Reversão**: antes de restaurar, comparar **todas** as posições afetadas com o resultado esperado. Um conflito cancela a reversão inteira sem mexer em nenhum bloco (50A). Se tudo corresponder, executar reversão controlada e só liberar reembolso após verificação integral, **uma única vez** (57A).

**Limite honesto da garantia:** não existe transação universal que reverta automaticamente todos os efeitos arbitrários de blocos e mods no Minecraft 1.12.2. Alterações de vizinhos, entidades e TileEntities podem ocorrer fora das posições do journal. Logo, a aplicação deve **recusar antecipadamente operações cuja segurança não possa ser demonstrada**, e a recuperação automática nunca deve ser anunciada como infalível.

### 9.4 Falha no autosave, dano, morte e fechamento inesperado — fechamento 38A + 45A

**[PADRÃO TÉCNICO PROPOSTO]**

- Autosave em intervalos moderados (sugestão inicial: **30 segundos se houver alterações**, não a cada clique) e nos marcos de sair, exportar, trocar projeto, iniciar finalização e sofrer dano.
- Persistência por arquivo temporário + fechamento + substituição segura quando o sistema de arquivos permitir; manter pelo menos **duas gerações** de recuperação (`latest` e `previous`) com checksum e versão de esquema.
- Se Steve receber dano, a UI é fechada e o controle normal é restaurado **mesmo que a gravação falhe**; tentar snapshot de emergência sem prender o jogador em uma interface insegura. O projeto continua marcado **não salvo** e um aviso explícito é exibido.
- Uma falha de gravação impede fechar silenciosamente sem aviso e impede iniciar a finalização a partir de estado não persistido. Uma cópia apenas em memória não deve ser apresentada como recuperação garantida após crash.
- Para morte/desconexão, restaurar controles e abrir recuperação quando possível; nenhum blueprint virtual deve materializar blocos automaticamente por causa desses eventos.
- Blueprints importados são tratados como dados não confiáveis: limitar tamanho, validar caminhos/formatos/NBT, impedir referência de arquivo fora da pasta permitida e bloquear informações sensíveis.

**[VALIDAÇÃO PENDENTE]** Simular disco sem espaço, arquivo corrompido, queda durante salvamento, morte, troca de dimensão e fechamento forçado.

### 9.5 Limites de desempenho e ajustes iniciais — fechamento 17C + 30C + 52B

**[CONFIRMADO]** Volume de área padrão de **64 × 64 × 64**; maiores volumes são configuráveis mediante proteção de desempenho.

**[PADRÃO TÉCNICO PROPOSTO]** Parâmetros de partida para protótipo, **não metas já medidas**:

| Parâmetro | Proposta inicial | Observação |
|---|---:|---|
| Blocos virtuais ocupados permitidos por projeto | 16.384 | Área geométrica pode ser 64³, mas não se deve prometer prévia densa sem benchmark. |
| Seção de índice/renderização | 16×16×16 | Atualização apenas de seções sujas. |
| Mudanças de blocos reais por tick | 32 | Ajuste por tempo consumido, não valor rígido se o tick estiver sobrecarregado. |
| Salvamento automático com alterações | 30 segundos | E imediatamente nos marcos críticos. |
| Histórico na memória | Orçamento configurável por bytes | Agrupar uma parede/piso como uma transação, não por cada bloco. |
| Volume acima de 64³ | Opção avançada | Exigir confirmação e avisos de memória/tempo. |

**Metas de qualidade a validar:** sem longos travamentos perceptíveis durante navegação normal; estabilidade do tempo de tick do servidor; uso de memória previsível e ausência de recálculo integral a cada colocação. Qualquer meta numérica definitiva de FPS/TPS ou memória deverá ser baseada em benchmark com Forge real.

**Plano de benchmark:** cenas com 1.000, 4.000, 8.000 e 16.384 peças; madeira/pedra simples e estados orientáveis; câmera próxima e distante; edição contínua e ferramentas de parede; salvar/reabrir; aplicação e reversão em lotes; comparação de memória, FPS, tempo por tick e tempo de persistência.

### 9.6 Multiplayer e permissões — fechamento 58A

**[CONFIRMADO]** O primeiro lançamento é singleplayer. Multiplayer real fica para etapa futura.

**[PADRÃO TÉCNICO PROPOSTO]** Mesmo no singleplayer, tratar o servidor integrado como autoridade para consumo de itens, validação de alcance, modificação do mundo e reembolso. Preparar mensagens de rede versionadas, limitadas e validadas; verificar que o chunk está carregado antes de consultar posições enviadas pelo cliente; processar mudanças no thread correto. Isso cria uma arquitetura apta a evoluir sem declarar compatibilidade multiplayer agora. A documentação 1.12.x do Forge alerta especificamente para risco de geração arbitrária de chunks por mensagens de cliente mal validadas.

### 9.7 Regras simples de experiência do usuário

**[PADRÃO TÉCNICO PROPOSTO]** Todas as proibições devem ter feedback legível e acionável: posição inválida em vermelho, causa exibida (fora do alcance, chunk não carregado, falta de material, bloco desconhecido, estado não suportado, mundo ocupado), detalhes por posição em um painel opcional e contador de erros na prévia final.

Ao usar ferramenta de área, soltar o mouse cria uma única ação undo/redo. Ao alternar ferramenta, não aplicar seleção pendente sem confirmação. Ao fechar o editor, restaurar controles da câmera e do personagem em todos os caminhos (sair normal, dano, mundo fechado, erro do renderizador).

---

## 10. Contratos de dados e invariantes para implementação futura

### 10.1 Entidades lógicas (não são classes nem código já existente)

| Entidade | Campos conceituais essenciais | Invariante |
|---|---|---|
| `BuildingProject` | ID, versão, origem local, dimensões, paleta, seções, histórico, status | Mudanças no projeto não escrevem no mundo. |
| `BlockEntry` | posição local inteira, id `modid:nome`, propriedades de estado, metadados seguros | Sem dados arbitrários de inventário ou energia. |
| `BlockGroup` | peças membros, transformações/validações, tipo reconhecido | Não quebrar multiblocos conhecidos ao transformar. |
| `BlueprintSnapshot` | versão, checksum, informações de dependências, paleta, seções | Desconhecidos são preservados, não substituídos por ar. |
| `PlacementPlan` | dimensões, posições de mundo, conflitos, custos, dependências, substituições previstas | É recalculado depois de mover/girar a estrutura. |
| `PlacementJournal` | ID único, estado, antes/depois por posição, origem dos recursos, progresso, checksum | Nenhuma finalização é considerada íntegra sem journal persistido. |
| `CostLedger` | itens reais reservados/consumidos, estado de liquidação/reembolso, operação | A mesma operação nunca reembolsa duas vezes. |

### 10.2 Invariantes que bloqueiam release

1. Preview virtual não escreve blocos reais nem força geração de chunks.
2. Toda posição materializada passa por validação autoritativa e está dentro do alcance permitido.
3. Um blueprint de bloco ausente ou incompatível não pode finalizar como ar ou bloco inesperado.
4. Sobrevivência não modifica terreno existente, nem mesmo blocos substituíveis; o jogador prepara o local.
5. Um conjunto de blocos reconhecido como indivisível não pode ser colocado parcialmente por engano.
6. Se uma finalização falhar, seu estado é recuperável/diagnosticável; não anunciar `COMMITTED` sem verificação.
7. Reversão com qualquer conflito é cancelada inteira antes de qualquer mudança.
8. Reembolso depende do ledger real e de reversão verificada, uma vez por ID de operação.
9. Falha de salvamento é exibida, nunca disfarçada como sucesso.
10. Nenhuma funcionalidade de veículos ou sistemas mecânicos é adicionada ao escopo.

---

## 11. Plano de testes e critérios de liberação

| Cenário | Resultado exigido |
|---|---|
| Abre B mirando bloco ou céu | Câmera passa ao editor sem deslocar Steve; nada real muda. |
| Órbita / pan / zoom e visões laterais | Controle previsível, sem sumiço de chunks; clamp na região carregada. |
| Clique normal e ferramentas de área | Posições exatas, estados preservados e undo/redo corretos. |
| Seleção, rotação, espelho de um eixo | Mesma geometria e orientações previstas após reabrir blueprint. |
| Danos/morte/desconexão | Controle restaurado, projeto recuperado ou falha claramente reportada. |
| Salvamento interrompido | Recuperar última versão íntegra, sem fingir que a alteração foi persistida. |
| Criativo com terreno ocupado | Prévia de substituição e confirmação explícita antes de executar. |
| Sobrevivência com grama/neve/bloco ocupante | Impedir finalização; nenhum material consumido. |
| Material suficiente/insuficiente | Débito correto ou bloqueio completo, sem diferença após reinício. |
| Interrupção no meio de aplicação em lotes | Estado INCOMPLETE/RECOVERY_REQUIRED observável e recuperável conservadoramente; sem commit falso. |
| Undo integral intacto | Mundo restaurado e materiais reembolsados uma vez. |
| Undo com uma divergência | Cancelar por completo, sem alterar bloco nem inventário. |
| Blocos desconhecidos de mod | Placeholder não destrutivo, nenhuma materialização silenciosa. |
| Blocos complexos/TileEntities | Recusa explícita quando não houver adaptador seguro. |
| Limites 16/64 e fronteiras de chunk | Nenhum bypass; nenhuma geração involuntária. |
| Estresse 1k–16k peças | Métricas de FPS/TPS/memória registradas e regressões analisadas. |

**Portões de entrega**:
- **G0 — revisão de projeto:** decisões do usuário preservadas, documentação consistente, aprovação para implementar.
- **G1 — compilação real:** ForgeGradle com JDK 8, sem falhas de `compileJava` ou empacotamento; `.jar` validado por inspeção de conteúdo.
- **G2 — editor no jogo:** câmera, cursor, edição e recuperação testados em Minecraft real.
- **G3 — mundo Criativo:** finalização e undo testados em mundo descartável/backup.
- **G4 — Sobrevivência:** materiais, bloqueios, reembolso e recuperação de falhas testados em mundo descartável.
- **G5 — mods e escala:** matriz de compatibilidade e benchmarks publicados, sem chamar todos os mods de compatíveis.

**Importante:** nenhuma etapa G1–G5 foi realizada neste documento. Não há `.jar`, demonstração dentro do Minecraft ou benchmark do BuildCraft Studio nesta entrega.

---

## 12. Roadmap de implementação recomendado (não iniciado)

1. **M0 — Scaffold e testes de infraestrutura:** projeto Forge 1.12.2/Java 8 independente, build local e GitHub Actions apenas após autorização; testes geométricos e de serialização.
2. **M1 — Building Foundation:** modo B, Steve imóvel, câmera orbitária, controle de chunks, plano 3D, bloco fantasma, catálogo básico, colocar/remover, histórico, autosave, primeira finalização restrita a bloco seguro no Criativo.
3. **M2 — Architecture Tools:** linhas, paredes, pisos, caixas, seleção, cópia/colagem, simetria, cortes de altura, edição em andares, import/export do blueprint proprietário.
4. **M3 — Survival & Safety:** custos do inventário, planejamento de ocupação, reservas, aplicação em lotes, journal, reversão integral, reembolso auditado, erro de disco.
5. **M4 — Compatibility & Scale:** estados orientáveis, adaptadores de multiblocos/TileEntities, modded blocks classificados por segurança, benchmark, limites maiores.
6. **M5 — Aperfeiçoamento opcional:** formatos de esquema de terceiros e baús como fontes de materiais; multiplayer somente se decidido explicitamente depois.

**Critério de sequência:** nenhuma fase substitui as decisões já aprovadas; funcionalidades adiadas permanecem no escopo geral e devem ser descritas como ainda não implementadas no respectivo marco. Não fundir lógica de câmera, edição e modificação do mundo em uma classe única.

---

## 13. Matriz de rastreabilidade das decisões

- **[CONFIRMADO] 1–17, 19–39, 41–59:** preservadas integralmente nas tabelas da seção 2, sem mudança de sentido.
- **[CONFIRMADO] 18 personalizada:** Criativo pode substituir com confirmação; Sobrevivência exige limpar área previamente.
- **[CONFIRMADO] 40B cancelada:** nada de carros, veículos, motores, rodas, conectores, pivôs e física de veículos.
- **[CONFIRMADO] Complementar:** montagem individual por padrão; linha/parede/piso/caixa são ferramentas opcionais.
- **[APROVADO NO CONTEXTO] 54C valores iniciais:** Sobrevivência 16 blocos, Criativo 64 blocos; valores configuráveis.
- **[PADRÃO TÉCNICO PROPOSTO]** valores de orçamento, autosave, fallback para céu, métrica exata de distância, filtros de compatibilidade e política de recuperação; verificar aceitação/ajustes em revisão de implementação.

### Registro de mudanças da v0.7 para v1.0

**Adicionado:** resolução do alcance inicial 16/64, classificação de risco de blocos, estados de journal e recuperação, política de falha de autosave, orçamento inicial de desempenho, contratos de dados, invariantes, testes de aceitação e roadmap mais detalhado.

**Alterado:** «alcance inicial a decidir» passa a valor aprovado no contexto; problemas antes abertos passam a padrões técnicos explícitos com limites de garantia. Regras originais foram mantidas.

**Removido do escopo:** nada além dos veículos e seus sistemas, cuja exclusão já havia sido confirmada anteriormente. Nenhuma decisão vigente foi eliminada.

**Não realizado:** programação, estrutura de projeto Forge, criação de classes, edição de mod existente, compilação, GitHub Actions, produção de `.jar`, playtest e medição de desempenho.

---

## 14. Fontes técnicas consultadas

- Forge 1.12.x — World Saved Data e separação de dados por dimensão: https://docs.minecraftforge.net/en/1.12.x/datastorage/worldsaveddata/
- Forge 1.12.x — SimpleImpl, validação de mensagens, thread principal e proteção contra chunks não carregados: https://docs.minecraftforge.net/en/1.12.x/networking/simpleimpl/
- Forge 1.12.x — TileEntity, NBT e sincronização: https://docs.minecraftforge.net/en/1.12.x/tileentities/tileentity/
- Forge 1.12.x — Block States: https://docs.minecraftforge.net/en/1.12.x/blocks/states/

**Estas fontes sustentam a viabilidade de APIs e cuidados gerais, mas não comprovam que a implementação planejada já funciona.**