# FileMaster — protótipo Android

Protótipo inicial em Kotlin + Jetpack Compose. Inclui painel de armazenamento interno, tema claro/escuro manual, seletor de pastas do Android via Storage Access Framework, listagem de arquivos/pastas, filtro de ocultos e abertura de arquivos com apps compatíveis.

## Abrir e compilar
1. Instale o Android Studio (versão atual) e o JDK 17.
2. Abra a pasta `FileMaster` no Android Studio.
3. Aguarde a sincronização do Gradle e instale as plataformas Android SDK solicitadas.
4. Execute em um dispositivo/emulador ou use Build > Build APK(s).

## Estado do protótipo
Este ZIP é código-fonte inicial, não um APK compilado. Este ambiente não tem Android SDK/Gradle instalado para validar uma compilação aqui.

Ainda precisam ser implementados e testados antes de uso como gerenciador completo: operações copiar/mover/renomear/excluir, lixeira persistente e limpeza de 30 dias, volumes SD e permissões de acesso, extração ZIP/7z/TAR/GZ e suporte RAR com tratamento seguro de caminhos, senhas, arquivos multipartes e arquivos maliciosos. A dependência Commons Compress cobre diversos formatos, mas o suporte real depende da configuração do leitor para cada formato. Junrar foi declarada como dependência candidata e precisa de validação de compatibilidade/licença e testes de arquivos reais.

## Limites importantes do Android
O Storage Access Framework permite ao usuário conceder acesso a pastas escolhidas, mas o Android restringe `Android/data`, `Android/obb` e diretórios privados de outros apps. Um app comum não pode ignorar essas proteções. O cartão SD aparece quando o sistema o expõe e o usuário concede acesso.
