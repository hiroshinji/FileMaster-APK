# Gerar o APK do FileMaster usando somente o celular

Este projeto inclui um fluxo de compilação automática no GitHub Actions. O APK ainda precisa ser compilado e o resultado precisa ser verificado.

## Passos
1. No navegador do celular, entre em https://github.com/ e crie uma conta ou entre na sua conta.
2. Toque no botão `+` e escolha `New repository`.
3. Dê o nome `FileMaster`, marque `Public` (necessário para usar a configuração mais simples) e crie o repositório.
4. No repositório, escolha `Add file` > `Upload files`.
5. Extraia o ZIP do projeto no celular usando o gerenciador de arquivos. Envie o **conteúdo da pasta `filemaster`**, incluindo a pasta oculta `.github`, para a raiz do repositório. Se o navegador não mostrar arquivos/pastas ocultos, será preciso ativar a opção de mostrar arquivos ocultos no gerenciador.
6. Faça o commit dos arquivos enviados.
7. Abra a aba `Actions`, selecione `Build FileMaster APK` e toque em `Run workflow` se a execução não começar automaticamente.
8. Aguarde a execução terminar. Se aparecer uma marca verde, abra a execução e procure `Artifacts`.
9. Baixe `FileMaster-debug-apk`, extraia o ZIP baixado e localize `app-debug.apk`.
10. Toque em `app-debug.apk` para instalar. Se o Android pedir, permita temporariamente a instalação de apps dessa origem nas configurações e, depois da instalação, desative essa permissão novamente.

## Importante
- O workflow compila um APK de depuração; não é uma versão assinada para distribuição pública.
- A compilação pode falhar se houver dependências ou erros de código. Nesse caso, abra a execução em `Actions`, copie o trecho vermelho do erro e peça ajuda para corrigir.
- O projeto é um protótipo inicial. A compatibilidade com RAR/7z, lixeira, operações de arquivos e permissões do Android ainda precisa ser implementada e testada; não considere esses recursos completos só porque o APK compilou.
- Não envie senhas, chaves privadas ou dados pessoais para o repositório.
