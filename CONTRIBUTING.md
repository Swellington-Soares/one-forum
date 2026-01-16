# 📚 Guia de Contribuição - Forum ONE

## 📋 Índice
- [Visão Geral](#visão-geral)
- [Stack Tecnológica](#stack-tecnológica)
- [Arquitetura do Projeto](#arquitetura-do-projeto)
- [Padrões de Código](#padrões-de-código)
- [Convenções de Nomenclatura](#convenções-de-nomenclatura)
- [Melhores Práticas Spring Boot](#melhores-práticas-spring-boot)
- [Configuração de Ambiente](#configuração-de-ambiente)
- [Banco de Dados](#banco-de-dados)
- [Testes](#testes)
- [Git Workflow](#git-workflow)

---

## 🎯 Visão Geral

Forum ONE é uma aplicação web de fórum colaborativo construída com **Spring Boot 3.5.9** e **Java 21**. O projeto segue os princípios de **Clean Architecture**, separando responsabilidades em camadas bem definidas.

### Principais Funcionalidades
- ✅ Autenticação JWT (Access Token + Refresh Token)
- ✅ CRUD de Tópicos e Comentários
- ✅ Sistema de Likes
- ✅ Upload de Avatar com processamento de imagem
- ✅ Confirmação de email
- ✅ Recuperação de senha
- ✅ Internacionalização (i18n)
- ✅ Filtros e paginação

---

## 🛠️ Stack Tecnológica

### Backend
- **Java**: 21 (OpenJDK)
- **Spring Boot**: 3.5.9
- **Spring Security**: 6.x (JWT Authentication)
- **Spring Data JPA**: Hibernate 6.6.39
- **Flyway**: 11.7.2 (Migrações de banco)
- **MySQL**: 8.0
- **MapStruct**: 1.6.3 (Mapeamento DTO ↔ Entity)

### Bibliotecas Auxiliares
- **Auth0 Java JWT**: 4.5.0 (Geração/validação de tokens)
- **Lombok**: Redução de boilerplate
- **Jsoup**: 1.21.2 (Sanitização de HTML)
- **Thumbnailator**: 0.4.21 (Processamento de imagens)
- **Thymeleaf**: Templates de email/páginas web

### Testes
- **JUnit 5**: Framework de testes
- **Mockito**: Mocks e stubs
- **Spring Security Test**: Testes de segurança
- **Testcontainers**: MySQL em containers para testes de integração

---

## 🏗️ Arquitetura do Projeto

### Estrutura de Pacotes

```
br.one.forum/
├── api/                    # Exceções específicas da API
├── controller/             # Endpoints REST (Controllers)
├── dto/                    # Data Transfer Objects
│   ├── request/           # DTOs de entrada
│   └── response/          # DTOs de saída
├── entity/                 # Entidades JPA
├── exception/              # Gerenciamento global de exceções
├── infra/                  # Infraestrutura (Security, Config, Utils)
│   ├── security/          # Configuração JWT, UserDetails
│   ├── spec/              # Specifications (Criteria API)
│   └── validation/        # Validadores customizados
├── mapper/                 # MapStruct Mappers
├── repository/             # Repositórios JPA
└── service/                # Lógica de negócio
```

### 🔄 Fluxo de Requisição

```
HTTP Request
    ↓
Controller (validação @Valid)
    ↓
Service (lógica de negócio)
    ↓
Repository (acesso ao banco)
    ↓
Entity (Hibernate)
    ↓
Mapper (Entity → DTO)
    ↓
Controller → HTTP Response
```

---

## 📝 Padrões de Código

### 1. Controllers

```java
@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor  // Lombok para injeção de dependência
public class ResourceController {
    
    private final ResourceService service;
    private final CurrentUser auth;  // Usuário autenticado
    
    @GetMapping
    public ResponseEntity<Page<ResourceDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }
    
    @PostMapping
    @PreAuthorize("isAuthenticated()")  // Requer autenticação
    public ResponseEntity<ResourceDto> create(@RequestBody @Valid ResourceRequestDto dto) {
        var resource = service.create(dto, auth.getUser().getId());
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(resource.getId())
            .toUri();
        return ResponseEntity.created(location).body(resource);
    }
}
```

**Regras:**
- ✅ Usar `@RequiredArgsConstructor` para injeção de dependência
- ✅ Sempre validar DTOs com `@Valid`
- ✅ Retornar `ResponseEntity<T>` com status HTTP apropriado
- ✅ Usar `@PreAuthorize` para controle de acesso
- ✅ Retornar `201 Created` com header `Location` em POST
- ❌ Nunca colocar lógica de negócio no controller

### 2. Services

```java
@Service
@RequiredArgsConstructor
public class ResourceService {
    
    private final ResourceRepository repository;
    private final ResourceMapper mapper;
    
    @Transactional(readOnly = true)  // Otimização para leitura
    public Page<ResourceDto> findAll(Pageable pageable) {
        return repository.findAll(pageable)
            .map(mapper::toDto);
    }
    
    @Transactional  // Escrita no banco
    public ResourceDto create(ResourceRequestDto dto, Long userId) {
        var entity = mapper.toEntity(dto);
        entity.setUserId(userId);
        entity.setCreatedAt(Instant.now());
        return mapper.toDto(repository.save(entity));
    }
    
    @Transactional
    public void delete(Long id, Long userId) {
        var entity = repository.findById(id)
            .orElseThrow(ResourceNotFoundException::new);
        
        if (!entity.getUserId().equals(userId)) {
            throw new ForbiddenException();
        }
        
        repository.delete(entity);
    }
}
```

**Regras:**
- ✅ Usar `@Transactional` em métodos que modificam dados
- ✅ Usar `@Transactional(readOnly = true)` para otimizar leituras
- ✅ Lançar exceções customizadas (`ResourceNotFoundException`)
- ✅ Validar permissões no service (ex: autor pode editar)
- ✅ Usar `Instant.now()` para timestamps UTC
- ❌ Nunca expor entidades diretamente, sempre usar DTOs

### 3. Repositories

```java
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    
    Optional<Resource> findBySlug(String slug);
    
    Page<Resource> findAllByUserId(Long userId, Pageable pageable);
    
    @Query("""
        SELECT r FROM Resource r
        WHERE r.deleted = false
        AND (:userId IS NULL OR r.userId = :userId)
        ORDER BY r.createdAt DESC
    """)
    Slice<Resource> findAllActive(
        @Param("userId") Long userId, 
        Pageable pageable
    );
}
```

**Regras:**
- ✅ Extender `JpaRepository<Entity, ID>`
- ✅ Usar Query Methods quando possível (`findBy...`, `existsBy...`)
- ✅ Usar `@Query` com JPQL para queries complexas
- ✅ Usar `Slice` para paginação infinita (sem count)
- ✅ Usar `Page` quando precisar do total de registros
- ❌ Evitar `@Query` nativo (SQL) a menos que estritamente necessário

### 4. DTOs

```java
// Request DTO (entrada)
public record ResourceRequestDto(
    @NotBlank(message = "{validation.title.required}")
    @Size(min = 3, max = 100)
    String title,
    
    @NotBlank
    String content,
    
    @NotNull
    List<String> categories
) {}

// Response DTO (saída)
public record ResourceResponseDto(
    Long id,
    String title,
    String content,
    AuthorDto author,
    LocalDateTime createdAt,
    Long likesCount
) {}
```

**Regras:**
- ✅ Usar `record` para DTOs imutáveis (Java 17+)
- ✅ Validar com Bean Validation (`@NotNull`, `@Size`, `@Email`, etc.)
- ✅ Usar mensagens i18n: `{validation.field.error}`
- ✅ Separar DTOs de Request e Response
- ✅ Nunca incluir senha ou dados sensíveis em Response DTOs
- ❌ Não reutilizar o mesmo DTO para input/output

### 5. Entities

```java
@Entity
@Table(name = "resources")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "deleted")
    private boolean deleted = false;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
```

**Regras:**
- ✅ Usar `@Entity` + `@Table(name = "nome_tabela")`
- ✅ Usar `Instant` para timestamps UTC
- ✅ Usar `FetchType.LAZY` por padrão em relacionamentos
- ✅ Usar `@Builder` do Lombok para facilitar criação
- ✅ Implementar soft delete com campo `deleted` booleano
- ✅ Usar `@PrePersist` para valores automáticos
- ❌ Nunca usar `FetchType.EAGER` sem necessidade
- ❌ Evitar relacionamentos bidirecionais desnecessários

### 6. Mappers (MapStruct)

```java
@Mapper(componentModel = "spring")
public interface ResourceMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Resource toEntity(ResourceRequestDto dto);
    
    @Mapping(source = "author.id", target = "author.id")
    @Mapping(source = "author.name", target = "author.name")
    ResourceResponseDto toDto(Resource entity);
    
    List<ResourceResponseDto> toDtoList(List<Resource> entities);
}
```

**Regras:**
- ✅ Usar `@Mapper(componentModel = "spring")` para injeção
- ✅ Ignorar campos automáticos (`id`, `createdAt`)
- ✅ Mapear relacionamentos explicitamente
- ✅ Criar métodos para listas quando necessário

---

## 🏷️ Convenções de Nomenclatura

### Classes

| Tipo | Convenção | Exemplo |
|------|-----------|---------|
| Entity | Substantivo singular | `User`, `Topic`, `Comment` |
| DTO Request | `<Entidade>RequestDto` | `TopicCreateRequestDto` |
| DTO Response | `<Entidade>ResponseDto` | `TopicResponseDto` |
| Service | `<Entidade>Service` | `TopicService` |
| Repository | `<Entidade>Repository` | `TopicRepository` |
| Controller | `<Entidade>Controller` | `TopicController` |
| Mapper | `<Entidade>Mapper` | `TopicMapper` |
| Exception | `<Motivo>Exception` | `TopicNotFoundException` |

### Métodos

| Operação | Convenção | Exemplo |
|----------|-----------|---------|
| Criar | `create<Entidade>` | `createTopic()` |
| Buscar um | `find<Entidade>ById` | `findTopicById()` |
| Buscar lista | `findAll<Critério>` | `findAllByAuthorId()` |
| Atualizar | `update<Entidade>` | `updateTopic()` |
| Deletar | `delete<Entidade>` | `deleteTopic()` |
| Verificar | `is<Condição>` | `isTopicOwner()` |

### Endpoints REST

| Operação | Método HTTP | URL | Exemplo |
|----------|-------------|-----|---------|
| Listar | GET | `/resources` | `GET /topics` |
| Buscar | GET | `/resources/{id}` | `GET /topics/123` |
| Criar | POST | `/resources` | `POST /topics` |
| Atualizar | PUT | `/resources/{id}` | `PUT /topics/123` |
| Deletar | DELETE | `/resources/{id}` | `DELETE /topics/123` |
| Ação customizada | POST | `/resources/{id}/<ação>` | `POST /topics/123/like` |

---

## ⚡ Melhores Práticas Spring Boot

### 1. Injeção de Dependência

```java
// ✅ CORRETO - Constructor injection com Lombok
@Service
@RequiredArgsConstructor
public class MyService {
    private final MyRepository repository;
    private final MyMapper mapper;
}

// ❌ ERRADO - Field injection
@Service
public class MyService {
    @Autowired
    private MyRepository repository;  // Dificulta testes
}
```

### 2. Tratamento de Exceções

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorDto("NOT_FOUND", ex.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDto> handleValidation(
        MethodArgumentNotValidException ex
    ) {
        var errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage
            ));
        return ResponseEntity.badRequest().body(new ValidationErrorDto(errors));
    }
}
```

### 3. Paginação

```java
// Controller
@GetMapping
public ResponseEntity<Slice<TopicDto>> getTopics(
    @RequestParam(required = false) String title,
    Pageable pageable  // Spring injeta automaticamente
) {
    return ResponseEntity.ok(topicService.findAll(title, pageable));
}

// Request: GET /topics?page=0&size=20&sort=createdAt,desc
```

### 4. Security

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Habilita @PreAuthorize
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

### 5. Validação Customizada

```java
// Annotation
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
public @interface UniqueEmail {
    String message() default "{validation.email.unique}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// Validator
@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return !userRepository.existsByEmail(email);
    }
}

// Uso
public record RegisterDto(
    @UniqueEmail
    @Email
    String email
) {}
```

---

## 🔧 Configuração de Ambiente

### Pré-requisitos

- **Java 21** (OpenJDK)
- **MySQL 8.0**
- **Gradle 8.14+** (wrapper incluído)
- **Docker** (opcional, para banco local)

### Setup Local

1. **Clone o repositório**
```bash
git clone <repo-url>
cd one-forum-collaborative-project-
```

2. **Configure Java 21**
```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

3. **Configure variáveis de ambiente**

Crie o arquivo `env.properties` na raiz:
```properties
# Database
MYSQL_URL=jdbc:mysql://localhost:3306/forum
MYSQL_USER=root
MYSQL_PASSWORD=password

# JWT
JWT_KEY_SECRET=your-super-secret-key-minimum-256-bits
JWT_REFRESH_KEY_SECRET=your-refresh-secret-key

# URLs
API_BASE_URL=http://localhost:8080
FRONTEND_URL=http://localhost:4200

# Email (desenvolvimento)
EMAIL_HOST=localhost
EMAIL_PORT=1025
EMAIL_USERNAME=
EMAIL_PASSWORD=
EMAIL_SMTP_AUTH=false
EMAIL_STARTTLS_ENABLE=false

# Supabase (se usar)
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-anon-key
SUPABASE_BUCKET_NAME=avatars
```

4. **Inicie o banco de dados**

Opção A - Docker Compose:
```bash
docker-compose up -d
```

Opção B - MySQL local:
```bash
mysql -u root -p
CREATE DATABASE forum CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

5. **Execute a aplicação**
```bash
export $(cat env.properties | xargs)
./gradlew bootRun
```

A API estará disponível em `http://localhost:8080`

### Servidor de Email de Desenvolvimento

Para testar emails localmente:
```bash
docker run -d -p 1025:1025 -p 8025:8025 mailhog/mailhog
```
Acesse `http://localhost:8025` para ver os emails enviados.

---

## 🗄️ Banco de Dados

### Migrações (Flyway)

As migrações ficam em `src/main/resources/db/migration/`:

```
db/migration/
├── V1__create_users_table.sql
├── V2__create_topics_table.sql
└── V3__add_email_verified_column.sql
```

**Convenção de nomeação:**
- `V{versão}__{descrição}.sql`
- Versão sequencial: `V1`, `V2`, `V3`...
- Descrição em snake_case

**Exemplo de migração:**
```sql
-- V4__add_likes_to_topics.sql
ALTER TABLE topics
ADD COLUMN likes_count INT DEFAULT 0 NOT NULL;

CREATE INDEX idx_topics_likes ON topics(likes_count);
```

**Comandos úteis:**
```bash
# Aplicar migrações pendentes
./gradlew flywayMigrate

# Verificar status
./gradlew flywayInfo

# Limpar banco (CUIDADO!)
./gradlew flywayClean
```

### Hibernate DDL-Auto

No `application.yml`:
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # PRODUÇÃO
      # ddl-auto: update  # DESENVOLVIMENTO (cuidado!)
      # ddl-auto: create  # TESTES (apaga e recria)
```

**Valores:**
- `validate` - Valida schema mas não altera (RECOMENDADO)
- `update` - Atualiza schema automaticamente (PERIGOSO)
- `create` - Recria schema a cada inicialização (APENAS TESTES)
- `create-drop` - Recria e apaga ao finalizar

---

## 🧪 Testes

### Estrutura de Testes

```
src/test/java/
├── controller/        # Testes de API (MockMvc)
├── service/          # Testes de lógica de negócio
├── repository/       # Testes de repositório (Testcontainers)
└── configuration/    # Configurações de teste
```

### 1. Testes de Controller (Web Layer)

```java
@WebMvcTest(TopicController.class)
@Import(TestSecurityConfiguration.class)
class TopicControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private TopicService topicService;
    
    @MockitoBean
    private CurrentUser auth;
    
    @Test
    @WithMockUser
    void shouldCreateTopic() throws Exception {
        var request = new TopicCreateRequestDto("Title", "Content", List.of("Tech"));
        var response = new TopicResponseDto(1L, "Title", "Content", null, null, 0L);
        
        when(topicService.create(any(), anyLong())).thenReturn(response);
        
        mockMvc.perform(post("/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("Title"));
    }
    
    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isUnauthorized());
    }
}
```

### 2. Testes de Service

```java
@ExtendWith(MockitoExtension.class)
class TopicServiceTest {
    
    @Mock
    private TopicRepository repository;
    
    @Mock
    private TopicMapper mapper;
    
    @InjectMocks
    private TopicService service;
    
    @Test
    void shouldCreateTopic() {
        var dto = new TopicCreateRequestDto("Title", "Content", List.of());
        var entity = new Topic();
        entity.setId(1L);
        
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(new TopicResponseDto(...));
        
        var result = service.create(dto, 1L);
        
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(repository).save(any(Topic.class));
    }
    
    @Test
    void shouldThrowExceptionWhenTopicNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(TopicNotFoundException.class, () -> service.findById(1L));
    }
}
```

### 3. Testes de Repository (Integração)

```java
@DataJpaTest
@Testcontainers
class TopicRepositoryTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8");
    
    @Autowired
    private TopicRepository repository;
    
    @Test
    void shouldFindTopicsByAuthor() {
        var author = new User();
        author.setId(1L);
        
        var topic = new Topic();
        topic.setTitle("Test");
        topic.setAuthor(author);
        repository.save(topic);
        
        var result = repository.findAllByAuthorId(1L, Pageable.unpaged());
        
        assertFalse(result.isEmpty());
        assertEquals("Test", result.getContent().get(0).getTitle());
    }
}
```

### Executar Testes

```bash
# Todos os testes
./gradlew test

# Testes de uma classe específica
./gradlew test --tests TopicServiceTest

# Testes com relatório
./gradlew test jacocoTestReport
# Ver: build/reports/tests/test/index.html
```

---

## 🔀 Git Workflow

### Branches

- `main` - Produção (apenas via PR)
- `develop` - Desenvolvimento principal
- `feature/<nome>` - Novas funcionalidades
- `fix/<nome>` - Correções de bugs
- `hotfix/<nome>` - Correções urgentes em produção

### Commits (Conventional Commits)

```
<tipo>(<escopo>): <descrição>

[corpo opcional]

[rodapé opcional]
```

**Tipos:**
- `feat`: Nova funcionalidade
- `fix`: Correção de bug
- `docs`: Documentação
- `style`: Formatação (não afeta lógica)
- `refactor`: Refatoração
- `test`: Adicionar/corrigir testes
- `chore`: Tarefas de build/config

**Exemplos:**
```bash
git commit -m "feat(topics): adicionar filtro por categoria"
git commit -m "fix(auth): corrigir validação de token expirado"
git commit -m "docs: atualizar README com instruções de setup"
git commit -m "refactor(services): extrair lógica de email para EmailService"
```

### Pull Request Checklist

- [ ] Código segue os padrões do projeto
- [ ] Testes unitários adicionados/atualizados
- [ ] Testes de integração passando
- [ ] Documentação atualizada (se necessário)
- [ ] Sem conflitos com `develop`
- [ ] Build Gradle sem erros
- [ ] Migrations Flyway criadas (se houver mudança no schema)

---

## 📧 Contato e Suporte

Para dúvidas ou sugestões, abra uma issue no repositório ou entre em contato com a equipe de desenvolvimento.

---

**Última atualização**: Janeiro 2026
