# ✅ Checklist de Deploy - Forum ONE

## 📋 Verificação Pré-Deploy

### 🔧 Arquivos de Configuração
- [x] ✅ `Dockerfile` - Multi-stage build configurado
- [x] ✅ `.dockerignore` - Otimização de build
- [x] ✅ `render.yaml` - Blueprint do Render
- [x] ✅ `test-docker.sh` - Script de teste local

### 🔐 Configurações de Segurança
- [x] ✅ `.gitignore` - Arquivos sensíveis ignorados (.env, env.properties)
- [x] ✅ SecurityConfiguration - Rotas públicas corretas
- [x] ✅ Swagger protegido - Apenas rotas de documentação públicas
- [x] ✅ CORS - Configurado para usar variável FRONTEND_URL

### ⚙️ Configurações da Aplicação

#### application.yml (Desenvolvimento)
- [x] ✅ `ddl-auto: update` - Não apaga dados
- [x] ✅ `show-sql: true` - Para debug local
- [x] ✅ Variáveis com defaults locais
- [x] ✅ SpringDoc configurado

#### application-prod.yml (Produção)
- [x] ✅ `ddl-auto: update` - Seguro para produção
- [x] ✅ `show-sql: false` - Performance
- [x] ✅ `flyway.enabled: false` - Desabilitado
- [x] ✅ Driver JDBC correto: `com.mysql.cj.jdbc.Driver`
- [x] ✅ Email SMTP configurado (STARTTLS)
- [x] ✅ Upload config presente
- [x] ✅ SpringDoc configurado

### 📦 Dependências
- [x] ✅ Spring Boot 3.5.9
- [x] ✅ Java 21
- [x] ✅ MySQL Connector
- [x] ✅ JWT (Auth0)
- [x] ✅ MapStruct
- [x] ✅ SpringDoc OpenAPI 2.7.0
- [x] ✅ Build Gradle funcionando

### 🗄️ Banco de Dados
- [x] ✅ Railway MySQL configurado
- [x] ⚠️ Schema com warnings de FK (não crítico)
- [ ] ⚠️ Considerar executar `reset-database.sql` para limpar warnings

### 🔑 Variáveis de Ambiente (render.yaml)
#### Database
- [x] MYSQL_URL
- [x] MYSQL_USER  
- [x] MYSQL_PASSWORD

#### JWT (Auto-gerado pelo Render)
- [x] JWT_KEY_SECRET
- [x] JWT_REFRESH_KEY_SECRET

#### URLs
- [x] API_BASE_URL
- [x] FRONTEND_URL

#### Supabase
- [x] SUPABASE_URL
- [x] SUPABASE_ANON_KEY
- [x] SUPABASE_BUCKET_NAME

#### Email
- [x] EMAIL_HOST
- [x] EMAIL_PORT
- [x] EMAIL_USERNAME
- [x] EMAIL_PASSWORD
- [x] EMAIL_SMTP_AUTH
- [x] EMAIL_STARTTLS_ENABLE

## 🚀 Passos para Deploy

### 1. Commit e Push
```bash
git add .
git commit -m "chore: configuração completa para deploy no Render"
git push origin main
```

### 2. No Render.com
1. ✅ Conectar repositório
2. ✅ Render detecta `render.yaml` automaticamente
3. ⚠️ **IMPORTANTE**: Adicionar manualmente as variáveis marcadas com `sync: false`:
   - `MYSQL_URL`, `MYSQL_USER`, `MYSQL_PASSWORD` (do Railway)
   - `FRONTEND_URL` (URL do Vercel/frontend)
   - `SUPABASE_URL`, `SUPABASE_ANON_KEY` (do Supabase)
   - `EMAIL_USERNAME`, `EMAIL_PASSWORD` (senha de aplicativo Gmail)
4. ✅ Deploy automático inicia

### 3. Após o Deploy
- [ ] Verificar logs: `Started ForumApplication`
- [ ] Testar health check: `https://seu-app.onrender.com/actuator/health`
- [ ] Testar Swagger: `https://seu-app.onrender.com/swagger-ui.html`
- [ ] Testar login: POST `/auth/login`
- [ ] Testar criação de tópico: POST `/topics` (com JWT)

## ⚠️ Problemas Corrigidos

### ✅ Críticos Resolvidos:
- ✅ `ddl-auto: create` mudado para `update` (evita perda de dados)
- ✅ `application-prod.yml` sincronizado com `application.yml`
- ✅ SpringDoc configurado corretamente
- ✅ Rotas do Swagger liberadas no Security
- ✅ `.gitignore` protegendo arquivos sensíveis
- ✅ `config.import: file:env.properties` removido (quebrava Docker)

### ⚠️ Warnings Conhecidos (Não Críticos):
- Foreign keys com tipos incompatíveis (INT vs BIGINT)
  - **Causa**: Tabelas antigas no banco
  - **Impacto**: Apenas warnings, não impede inicialização
  - **Solução**: Executar `reset-database.sql` se quiser limpar

## 📝 Comandos Úteis

### Testar Localmente
```bash
# Build Docker
./test-docker.sh build

# Executar
./test-docker.sh run

# Ver logs
./test-docker.sh logs
```

### Verificar Build
```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
./gradlew clean build -x test
```

### Conectar no Banco Railway (se necessário)
```bash
docker run --rm -it mysql:8 \
  mysql -h interchange.proxy.rlwy.net \
        -P 55679 \
        -u root \
        -p railway
# Senha: BiDTNAvXKVIEKZGfkxoapJQosqawQHuy
```

## 🎯 Status Final

### ✅ PRONTO PARA DEPLOY!

Todos os itens críticos foram corrigidos. A aplicação está pronta para subir no Render.

**Última verificação**: Build Gradle ✅ SUCESSO

---

**Criado em**: 15 de Janeiro de 2026  
**Próxima revisão**: Após primeiro deploy bem-sucedido
