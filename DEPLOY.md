# Deploy no Render - Guia Rápido

## Pré-requisitos
- Conta no [Render](https://render.com)
- Banco de dados MySQL no Railway configurado
- Repositório no GitHub

## Passo a Passo

### 1. Preparar o Repositório
Certifique-se de que os seguintes arquivos estão commitados:
- ✅ `Dockerfile`
- ✅ `.dockerignore`
- ✅ `src/main/resources/application-prod.yml`

### 2. Criar Web Service no Render

1. Acesse [render.com](https://render.com) e faça login
2. Clique em **"New +"** → **"Web Service"**
3. Conecte seu repositório GitHub
4. Configure o serviço:

   **Basic Info:**
   - **Name:** `forum-api` (ou nome de sua preferência)
   - **Region:** escolha a mais próxima
   - **Branch:** `develop` ou `main`

   **Build & Deploy:**
   - **Runtime:** `Docker`
   - **Dockerfile Path:** `./Dockerfile` (padrão)

   **Instance Type:**
   - Selecione o plano (Free tier disponível)

### 3. Configurar Variáveis de Ambiente

No painel do Render, vá em **"Environment"** e adicione:

```bash
# Database (seu MySQL do Railway)
MYSQL_URL=jdbc:mysql://interchange.proxy.rlwy.net:55679/railway
MYSQL_USER=root
MYSQL_PASSWORD=BiDTNAvXKVIEKZGfkxoapJQosqawQHuy

# JWT (IMPORTANTE: Mude para valores seguros!)
JWT_KEY_SECRET=seu-secret-super-seguro-aqui-123456
JWT_REFRESH_KEY_SECRET=seu-refresh-secret-super-seguro-789

# Supabase
SUPABASE_URL=https://zelqttyudvymccgkfxyc.supabase.co
SUPABASE_KEY=sua-key-do-supabase
SUPABASE_BUCKET=uploads

# URLs (ajuste após deploy)
API_BASE_URL=https://seu-app.onrender.com
FRONTEND_URL=https://seu-frontend.com

# Swagger (deixe false em produção)
SWAGGER_ENABLED=false
```

### 4. Deploy

1. Clique em **"Create Web Service"**
2. Aguarde o build e deploy (pode levar 5-10 minutos)
3. Após o deploy, sua API estará disponível em: `https://seu-app.onrender.com`

### 5. Verificar Saúde da Aplicação

Acesse os endpoints:
- Health Check: `https://seu-app.onrender.com/actuator/health`
- API Docs: `https://seu-app.onrender.com/swagger-ui.html` (se habilitado)

## Comandos Úteis

### Testar build Docker localmente:
```bash
docker build -t forum-app .
docker run -p 8080:8080 --env-file .env forum-app
```

### Ver logs no Render:
- Acesse o painel do serviço
- Clique em **"Logs"** para ver logs em tempo real

## Notas Importantes

⚠️ **Segurança:**
- Sempre use secrets seguros para JWT em produção
- Nunca commite o arquivo `.env` com credenciais reais
- Configure CORS adequadamente no backend

⚠️ **Performance:**
- O plano Free do Render hiberna após 15 minutos de inatividade
- Primeira requisição após hibernação pode demorar ~30 segundos
- Considere plano pago para produção

⚠️ **Banco de Dados:**
- O ddl-auto está em `update` em produção
- Para migrações controladas, ative o Flyway
- Faça backup regular do banco de dados

## Troubleshooting

**Problema:** Build falha
- Verifique os logs de build no Render
- Teste o build localmente com Docker

**Problema:** Aplicação não inicia
- Verifique as variáveis de ambiente
- Confira os logs da aplicação
- Teste conexão com o banco MySQL

**Problema:** Timeout na primeira requisição
- Normal no plano Free após hibernação
- Considere usar um serviço de "keep alive" ou plano pago

## Próximos Passos

1. Configurar domínio customizado (opcional)
2. Configurar SSL (automático no Render)
3. Configurar CI/CD com GitHub Actions
4. Monitorar logs e métricas
