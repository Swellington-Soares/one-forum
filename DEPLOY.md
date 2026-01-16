# 🚀 Guia de Deploy - Forum ONE

## 📋 Índice
- [Pré-requisitos](#pré-requisitos)
- [Deploy no Render](#deploy-no-render)
- [Configuração de Banco de Dados (Railway)](#configuração-de-banco-de-dados-railway)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Configuração de Email (Gmail)](#configuração-de-email-gmail)
- [Upload de Imagens (Supabase)](#upload-de-imagens-supabase)
- [Verificação e Testes](#verificação-e-testes)
- [Troubleshooting](#troubleshooting)

---

## ✅ Pré-requisitos

Antes de iniciar o deploy, você precisa ter:

- ✅ Conta no [Render](https://render.com) (plano gratuito disponível)
- ✅ Conta no [Railway](https://railway.app) para MySQL (ou outro provedor)
- ✅ Conta no [Supabase](https://supabase.com) para storage de imagens
- ✅ Conta Gmail com senha de aplicativo (para envio de emails)
- ✅ Repositório Git (GitHub, GitLab ou Bitbucket)

---

## 🌐 Deploy no Render

### 0. Preparar o Repositório

Antes de fazer deploy, certifique-se de que os seguintes arquivos estão commitados:

- ✅ `Dockerfile` - Configuração Docker multi-stage
- ✅ `.dockerignore` - Otimiza build ignorando arquivos desnecessários
- ✅ `render.yaml` - Blueprint de configuração (opcional)
- ✅ `src/main/resources/application.yml` - Configuração base
- ✅ `src/main/resources/application-prod.yml` - Configuração de produção (se existir)
- ✅ `gradlew` com permissão de execução

```bash
# Garantir permissão de execução
git update-index --chmod=+x gradlew
git add .
git commit -m "chore: preparar para deploy no Render"
git push origin main
```

### 1. Conectar Repositório

1. Acesse [render.com](https://render.com) e faça login
2. Clique em **"New +"** → **"Web Service"**
3. Conecte sua conta do GitHub/GitLab
4. Selecione o repositório `one-forum-collaborative-project-`
5. Clique em **"Connect"**

### 2. Configurar o Serviço

Preencha os campos:

| Campo | Valor |
|-------|-------|
| **Name** | `forum-one-api` (ou nome de sua preferência) |
| **Region** | Escolha a região mais próxima (ex: Oregon, Frankfurt) |
| **Branch** | `main` (ou `develop`) |
| **Root Directory** | (deixe vazio) |
| **Runtime** | `Docker` ⚠️ |
| **Dockerfile Path** | `./Dockerfile` (padrão, pode deixar vazio) |

⚠️ **Importante**: Se você tem o `Dockerfile`, escolha **Runtime: Docker**. Caso prefira sem Docker, escolha **Java** e configure:
- **Build Command**: `./gradlew clean build -x test`
- **Start Command**: `java -Dserver.port=$PORT -jar build/libs/*.jar`

### 3. Configurar Plano

- **Instance Type**: 
  - Free (512 MB RAM, suspende após inatividade)
  - Starter ($7/mês, 512 MB RAM, sempre ativo)
  - Standard ($25/mês, 2 GB RAM, recomendado para produção)

⚠️ **Importante**: O plano Free hiberna após 15 minutos de inatividade e leva ~30s para "acordar".

### 4. Adicionar Variáveis de Ambiente

Na seção **"Environment"**, clique em **"Add Environment Variable"** e adicione todas as variáveis conforme a [seção de Variáveis de Ambiente](#variáveis-de-ambiente) abaixo.

### 5. Deploy Automático

- Marque **"Auto-Deploy"** para deploy automático a cada push no branch configurado
- Clique em **"Create Web Service"**

O Render iniciará o build automaticamente. Acompanhe os logs em tempo real.

### 6. Obter URL da API

Após o deploy:
- Sua API estará disponível em: `https://forum-one-api.onrender.com`
- Anote essa URL para configurar o frontend

---

## 🗄️ Configuração de Banco de Dados (Railway)

### 1. Criar Banco MySQL

1. Acesse [railway.app](https://railway.app) e faça login
2. Clique em **"New Project"**
3. Selecione **"Provision MySQL"**
4. Aguarde a criação do banco

### 2. Obter Credenciais

1. Clique no banco de dados criado
2. Vá na aba **"Connect"**
3. Copie as credenciais:

```
Host: interchange.proxy.rlwy.net
Port: 55679
Username: root
Password: BiDTNAvXKVIEKZGfkxoapJQosqawQHuy
Database: railway
```

### 3. Criar String de Conexão

Monte a URL JDBC:
```
jdbc:mysql://<host>:<port>/<database>?useSSL=true&requireSSL=true
```

Exemplo:
```
jdbc:mysql://interchange.proxy.rlwy.net:55679/railway?useSSL=true&requireSSL=true
```

### 4. (Opcional) Executar Migrations Manualmente

Se o Flyway estiver desabilitado, execute as migrations localmente:

```bash
# Conectar ao banco remoto
mysql -h interchange.proxy.rlwy.net \
      -P 55679 \
      -u root \
      -p railway

# Ou via Docker
docker run --rm -it mysql:8 \
  mysql -h interchange.proxy.rlwy.net \
        -P 55679 \
        -u root \
        -p railway
```

Depois execute os scripts SQL de `src/main/resources/db/migration/`.

---

## 🔐 Variáveis de Ambiente

Configure **todas** as variáveis abaixo no Render:

### Database

```bash
MYSQL_URL=jdbc:mysql://interchange.proxy.rlwy.net:55679/railway?useSSL=true&requireSSL=true
MYSQL_USER=root
MYSQL_PASSWORD=BiDTNAvXKVIEKZGfkxoapJQosqawQHuy
```

### JWT Secrets

⚠️ **Gere chaves fortes e únicas!**

```bash
# Gerar chaves seguras (Linux/Mac):
openssl rand -base64 64

# Ou use um gerador online:
# https://generate-random.org/api-key-generator
```

```bash
JWT_KEY_SECRET=sua-chave-secreta-super-forte-aqui-min-256-bits
JWT_REFRESH_KEY_SECRET=sua-chave-refresh-diferente-da-anterior
```

### URLs da Aplicação

```bash
API_BASE_URL=https://forum-one-api.onrender.com
FRONTEND_URL=https://seu-frontend.vercel.app
```

⚠️ **Importante**: Substitua pelas URLs reais após o deploy!

### Email (Gmail SMTP)

```bash
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=seu-email@gmail.com
EMAIL_PASSWORD=sua-senha-de-aplicativo-aqui
EMAIL_SMTP_AUTH=true
EMAIL_STARTTLS_ENABLE=true
```

📧 Veja [como configurar senha de aplicativo](#configuração-de-email-gmail) abaixo.

### Supabase Storage (Imagens)

```bash
SUPABASE_URL=https://seu-projeto.supabase.co
SUPABASE_ANON_KEY=sua-anon-key-publica
SUPABASE_BUCKET_NAME=avatars
```

🗂️ Veja [como configurar Supabase](#upload-de-imagens-supabase) abaixo.

### Exemplo Completo (Template)

```bash
# Database
MYSQL_URL=jdbc:mysql://interchange.proxy.rlwy.net:55679/railway?useSSL=true&requireSSL=true
MYSQL_USER=root
MYSQL_PASSWORD=BiDTNAvXKVIEKZGfkxoapJQosqawQHuy

# JWT
JWT_KEY_SECRET=abc123def456ghi789jkl012mno345pqr678stu901vwx234yz567890abcdefg
JWT_REFRESH_KEY_SECRET=xyz987wvu654tsr321qpo098nml765kji432hgf109edc876baz543yxw210vutsrqp

# URLs
API_BASE_URL=https://forum-one-api.onrender.com
FRONTEND_URL=https://forum-one.vercel.app

# Email
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=contato@seudominio.com
EMAIL_PASSWORD=abcd efgh ijkl mnop
EMAIL_SMTP_AUTH=true
EMAIL_STARTTLS_ENABLE=true

# Supabase
SUPABASE_URL=https://abcdefghijklmnop.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
SUPABASE_BUCKET_NAME=avatars
```

---

## 📧 Configuração de Email (Gmail)

### 1. Ativar Verificação em 2 Etapas

1. Acesse [myaccount.google.com/security](https://myaccount.google.com/security)
2. Clique em **"Verificação em duas etapas"**
3. Siga os passos para ativar

### 2. Criar Senha de Aplicativo

1. Acesse [myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords)
2. Se não aparecer a opção, ative a verificação em 2 etapas primeiro
3. Selecione:
   - **App**: Outro (nome personalizado) → digite "Forum ONE API"
   - **Device**: Escolha o dispositivo
4. Clique em **"Gerar"**
5. Copie a senha de 16 dígitos (formato: `xxxx xxxx xxxx xxxx`)
6. Use essa senha na variável `EMAIL_PASSWORD`

### 3. Configurações Importantes

```bash
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587                    # STARTTLS (recomendado)
EMAIL_SMTP_AUTH=true              # Obrigatório
EMAIL_STARTTLS_ENABLE=true        # Não use SSL na porta 587
```

⚠️ **Não usar**:
- Senha normal da conta Gmail (não funciona)
- Porta 465 com STARTTLS (use porta 465 apenas com SSL, mas 587 é melhor)

### 4. Alternativas ao Gmail

Se preferir outros provedores:

#### SendGrid
```bash
EMAIL_HOST=smtp.sendgrid.net
EMAIL_PORT=587
EMAIL_USERNAME=apikey
EMAIL_PASSWORD=sua-api-key-do-sendgrid
EMAIL_SMTP_AUTH=true
EMAIL_STARTTLS_ENABLE=true
```

#### Mailgun
```bash
EMAIL_HOST=smtp.mailgun.org
EMAIL_PORT=587
EMAIL_USERNAME=postmaster@seu-dominio.mailgun.org
EMAIL_PASSWORD=sua-senha-do-mailgun
EMAIL_SMTP_AUTH=true
EMAIL_STARTTLS_ENABLE=true
```

---

## 🗂️ Upload de Imagens (Supabase)

### 1. Criar Projeto no Supabase

1. Acesse [supabase.com](https://supabase.com) e faça login
2. Clique em **"New Project"**
3. Preencha:
   - **Name**: `forum-one-storage`
   - **Database Password**: (gere uma senha forte)
   - **Region**: Escolha a mais próxima
4. Clique em **"Create new project"**

### 2. Criar Bucket de Storage

1. No menu lateral, clique em **"Storage"**
2. Clique em **"Create a new bucket"**
3. Preencha:
   - **Name**: `avatars`
   - **Public bucket**: ✅ Marque (para permitir leitura pública)
4. Clique em **"Create bucket"**

### 3. Configurar Políticas de Acesso (RLS)

1. Clique no bucket `avatars`
2. Vá em **"Policies"**
3. Clique em **"New Policy"**

**Política de Leitura Pública:**
```sql
-- Nome: Public Read Access
CREATE POLICY "Public read access"
ON storage.objects FOR SELECT
USING (bucket_id = 'avatars');
```

**Política de Upload Autenticado:**
```sql
-- Nome: Authenticated Upload
CREATE POLICY "Authenticated upload"
ON storage.objects FOR INSERT
WITH CHECK (bucket_id = 'avatars');
```

**Política de Deleção (Apenas Próprios Arquivos):**
```sql
-- Nome: Delete own files
CREATE POLICY "Users can delete own files"
ON storage.objects FOR DELETE
USING (bucket_id = 'avatars' AND auth.uid() = owner);
```

### 4. Obter Credenciais

1. Vá em **"Settings"** → **"API"**
2. Copie:
   - **Project URL**: `https://abcdefghijklmnop.supabase.co`
   - **anon/public key**: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`

### 5. Adicionar ao Render

```bash
SUPABASE_URL=https://abcdefghijklmnop.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
SUPABASE_BUCKET_NAME=avatars
```

### 6. Testar Upload

Após deploy, teste o upload de avatar:

```bash
curl -X PUT https://forum-one-api.onrender.com/upload \
  -H "Authorization: Bearer SEU_JWT_TOKEN" \
  -F "file=@avatar.jpg"
```

---

## 🧪 Testar Localmente Antes do Deploy

Antes de fazer deploy, teste a aplicação localmente com Docker:

### Opção 1: Script Automatizado (Recomendado)

```bash
# Build e executar
./test-docker.sh run

# Ver logs em outro terminal
./test-docker.sh logs

# Parar
./test-docker.sh stop
```

### Opção 2: Comandos Manuais

```bash
# Build da imagem
docker build -t forum-one-api .

# Executar com variáveis de ambiente
docker run -p 8080:8080 --env-file env.properties forum-one-api

# Testar
curl http://localhost:8080/actuator/health
```

### Opção 3: Docker Compose (Desenvolvimento)

Se você tem `compose.yaml`:

```bash
docker-compose up -d
docker-compose logs -f
docker-compose down
```

---

## ✅ Verificação e Testes

### 1. Verificar Logs do Deploy

No Render:
1. Vá em **"Logs"**
2. Verifique se aparece:
   ```
   Started ForumApplication in X.XXX seconds
   Tomcat started on port 10000
   ```

### 2. Testar Health Check

```bash
curl https://forum-one-api.onrender.com/actuator/health
```

Resposta esperada:
```json
{
  "status": "UP"
}
```

### 3. Testar Endpoints Públicos

**Listar categorias:**
```bash
curl https://forum-one-api.onrender.com/categories
```

**Listar tópicos:**
```bash
curl https://forum-one-api.onrender.com/topics
```

### 4. Testar Autenticação

**Registrar usuário:**
```bash
curl -X POST https://forum-one-api.onrender.com/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@example.com",
    "password": "senha123",
    "passwordConfirmation": "senha123",
    "name": "Usuario Teste"
  }'
```

**Login:**
```bash
curl -X POST https://forum-one-api.onrender.com/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@example.com",
    "password": "senha123"
  }'
```

Resposta esperada:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

### 5. Testar Criação de Tópico (Autenticado)

```bash
curl -X POST https://forum-one-api.onrender.com/topics \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_ACCESS_TOKEN" \
  -d '{
    "title": "Meu primeiro tópico",
    "content": "Conteúdo do tópico",
    "categories": ["Tecnologia", "Java"]
  }'
```

---

## 🔧 Troubleshooting

### Problema: Build Falha no Render

**Erro:** `Permission denied: ./gradlew`

**Solução:** Adicione permissão de execução ao gradlew:
```bash
# No seu terminal local
git update-index --chmod=+x gradlew
git commit -m "chore: adicionar permissão de execução ao gradlew"
git push
```

---

### Problema: Conexão com Banco de Dados Falha

**Erro:** `Communications link failure`

**Soluções:**

1. **Verificar URL do banco:**
   - Certifique-se de incluir `?useSSL=true&requireSSL=true`
   - Exemplo: `jdbc:mysql://host:port/db?useSSL=true&requireSSL=true`

2. **Verificar credenciais:**
   - Host correto (sem `http://` ou `https://`)
   - Porta correta
   - Senha sem espaços extras

3. **Testar conexão diretamente:**
   ```bash
   mysql -h interchange.proxy.rlwy.net -P 55679 -u root -p
   ```

---

### Problema: Erro 401 em Endpoints Autenticados

**Erro:** `Unauthorized`

**Causas comuns:**

1. **Token JWT inválido:**
   - Verifique se o token está no formato `Bearer <token>`
   - Confirme que o token não expirou (60 minutos por padrão)

2. **JWT_KEY_SECRET diferente:**
   - O secret usado para gerar o token deve ser o mesmo do servidor
   - Verifique se a variável `JWT_KEY_SECRET` está correta

3. **Header incorreto:**
   ```bash
   # ✅ CORRETO
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   
   # ❌ ERRADO
   Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

---

### Problema: Email Não Envia

**Erro:** `AuthenticationFailedException`

**Soluções:**

1. **Verificar senha de aplicativo:**
   - Use senha de aplicativo, não a senha normal
   - Remova espaços: `abcd efgh ijkl mnop` → `abcdefghijklmnop`

2. **Verificar configurações SMTP:**
   ```bash
   EMAIL_HOST=smtp.gmail.com          # Sem http:// ou https://
   EMAIL_PORT=587                      # Porta correta
   EMAIL_SMTP_AUTH=true                # Deve ser true
   EMAIL_STARTTLS_ENABLE=true          # Deve ser true
   ```

3. **Testar SMTP manualmente:**
   ```bash
   telnet smtp.gmail.com 587
   ```

---

### Problema: Upload de Imagem Falha

**Erro:** `Access Denied` ou `403 Forbidden`

**Soluções:**

1. **Verificar políticas do Supabase:**
   - Certifique-se de que as políticas RLS estão ativas
   - Bucket deve estar público para leitura

2. **Verificar credenciais:**
   - `SUPABASE_URL` sem barra final
   - `SUPABASE_ANON_KEY` completa (token JWT longo)

3. **Testar diretamente no Supabase:**
   - Tente fazer upload manual pela interface web
   - Verifique se o bucket `avatars` existe

---

### Problema: Aplicação Lenta ou Timeout

**Causa:** Plano Free do Render hiberna após inatividade

**Soluções:**

1. **Upgrade para Starter ($7/mês):** Mantém aplicação sempre ativa

2. **Ping periódico (Free):**
   - Use um serviço como [UptimeRobot](https://uptimerobot.com) (gratuito)
   - Configure para fazer ping a cada 5 minutos
   - URL: `https://forum-one-api.onrender.com/actuator/health`

3. **Aviso no frontend:**
   ```javascript
   // Mostrar mensagem ao usuário
   if (response.status === 504) {
     alert("Servidor iniciando, aguarde 30 segundos e tente novamente");
   }
   ```

---

### Problema: CORS Error no Frontend

**Erro:** `Access-Control-Allow-Origin`

**Solução:** Verifique se a variável `FRONTEND_URL` está correta:

```bash
# Deve ser a URL exata do frontend (sem barra final)
FRONTEND_URL=https://forum-one.vercel.app
```

Se persistir, verifique `WebConfig.java`:
```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins(frontendUrl)  // Deve usar a variável
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true);
}
```

---

## 📊 Monitoramento

### Logs em Tempo Real

No Render, vá em **"Logs"** para ver logs em tempo real.

### Métricas

- **CPU**: Monitorar uso de CPU
- **Memory**: Verificar se não está excedendo o limite
- **Response Time**: Tempo de resposta das requisições

### Alerts

Configure alertas no Render para:
- Deploy failures
- High memory usage
- Application crashes

---

## ⚠️ Notas Importantes

### Segurança

- ✅ **JWT Secrets**: Use valores fortes e únicos (mínimo 256 bits)
- ✅ **Senhas**: Nunca commite arquivos `.env` ou `env.properties` com credenciais reais
- ✅ **CORS**: Certifique-se de configurar `FRONTEND_URL` corretamente
- ✅ **HTTPS**: Render fornece SSL/TLS automaticamente
- ✅ **API Keys**: Rotacione secrets periodicamente

### Performance

- ⚡ **Plano Free**: Hiberna após 15 minutos de inatividade
- ⚡ **Cold Start**: Primeira requisição pode demorar ~30 segundos
- ⚡ **Keep Alive**: Use [UptimeRobot](https://uptimerobot.com) para ping periódico (gratuito)
- ⚡ **Plano Pago**: Recomendado para produção (sempre ativo, melhor performance)

### Banco de Dados

- 🗄️ **DDL-Auto**: Atualmente configurado como `create` (ATENÇÃO!)
  - Para desenvolvimento inicial, mantém `create` para gerar schema
  - **Após primeira execução bem-sucedida**, mude para `validate` ou `update`
  - Para produção, use `validate` + Flyway para migrações controladas
  
- 🗄️ **Flyway**: Atualmente desabilitado
  - Recomendado ativar para controle de migrações em produção
  - Migrations ficam em `src/main/resources/db/migration/`
  
- 🗄️ **Backup**: Configure backup automático no Railway
  - Railway não faz backup automático no plano gratuito
  - Considere exportar SQL periodicamente

### Logs

- 📝 **Spring Boot**: Logs detalhados com `show-sql: true` (desabilite em produção)
- 📝 **Render**: Mantém logs por 7 dias no plano Free
- 📝 **Sentry**: Considere integrar para tracking de erros

### Custos

- 💰 **Render Free**: $0/mês, hiberna após 15min
- 💰 **Render Starter**: $7/mês, sempre ativo, 512 MB RAM
- 💰 **Railway MySQL**: $5/mês após limite do plano gratuito
- 💰 **Supabase**: Gratuito até 500 MB storage e 2 GB bandwidth
- 💰 **Gmail SMTP**: Gratuito (limite de ~500 emails/dia)

---

## 🔄 Atualizações e Redeploy

### Deploy Automático (Recomendado)

Com **Auto-Deploy** ativado, cada push no branch configurado dispara um novo deploy automaticamente.

```bash
git add .
git commit -m "feat: nova funcionalidade"
git push origin main
```

### Deploy Manual

No Render:
1. Vá em **"Manual Deploy"**
2. Selecione o branch
3. Clique em **"Deploy latest commit"**

### Rollback

Para voltar a uma versão anterior:
1. Vá em **"Events"**
2. Encontre o deploy anterior
3. Clique em **"Rollback to this version"**

---

## 📝 Checklist Final

Antes de considerar o deploy completo, verifique:

- [ ] ✅ Aplicação inicia sem erros
- [ ] ✅ Conexão com banco de dados funcionando
- [ ] ✅ Endpoints públicos respondem (categorias, tópicos)
- [ ] ✅ Registro de usuário funciona
- [ ] ✅ Login retorna token JWT válido
- [ ] ✅ Criação de tópico autenticado funciona
- [ ] ✅ Upload de imagem funciona
- [ ] ✅ Email de confirmação é enviado
- [ ] ✅ Frontend conecta com API (sem CORS errors)
- [ ] ✅ HTTPS ativo (Render fornece automaticamente)
- [ ] ✅ Logs não mostram erros críticos

---

## 🎉 Sucesso!

Sua aplicação agora está no ar! 🚀

**Próximos passos:**
- [ ] Configure domínio customizado (opcional)
- [ ] Configure SSL personalizado se usar domínio próprio
- [ ] Configure monitoramento com [Sentry](https://sentry.io) ou [DataDog](https://datadoghq.com)
- [ ] Configure backup automático do banco de dados
- [ ] Implemente CI/CD com GitHub Actions
- [ ] Configure ambiente de staging separado
- [ ] Adicione rate limiting (Spring Security)
- [ ] Configure cache (Redis) para melhor performance
- [ ] Documente API com Swagger/OpenAPI
- [ ] Configure health checks customizados
- [ ] Implemente log aggregation (ELK Stack ou similar)

---

**Última atualização**: Janeiro 2026
