const {
  Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell,
  HeadingLevel, AlignmentType, BorderStyle, WidthType, ShadingType,
  PageBreak, LevelFormat, Header, Footer, PageNumber, NumberFormat
} = require('docx');
const fs = require('fs');

const BLUE  = "1E3A5F";
const LBLUE = "2E75B6";
const TEAL  = "006B6B";
const GREEN = "1A6B3A";
const RED   = "8B0000";
const GRAY  = "F2F4F7";
const DGRAY = "4A4A4A";
const WHITE = "FFFFFF";
const LGRAY = "D9D9D9";

const border = (color="CCCCCC") => ({ style: BorderStyle.SINGLE, size: 1, color });
const borders = (color) => ({ top:border(color), bottom:border(color), left:border(color), right:border(color) });
const cell = (text, opts={}) => new TableCell({
  borders: borders(opts.borderColor||"CCCCCC"),
  width: { size: opts.width||4680, type: WidthType.DXA },
  shading: opts.shade ? { fill: opts.shade, type: ShadingType.CLEAR } : undefined,
  margins: { top:80, bottom:80, left:120, right:120 },
  verticalAlign: opts.vAlign,
  children: [new Paragraph({
    alignment: opts.align||AlignmentType.LEFT,
    children: [new TextRun({
      text, font:"Arial",
      size: opts.size||20,
      bold: opts.bold||false,
      color: opts.color||DGRAY
    })]
  })]
});

const h1 = (text) => new Paragraph({
  heading: HeadingLevel.HEADING_1,
  spacing: { before:360, after:180 },
  children:[new TextRun({ text, font:"Arial", size:40, bold:true, color:BLUE })]
});
const h2 = (text) => new Paragraph({
  heading: HeadingLevel.HEADING_2,
  spacing: { before:280, after:120 },
  children:[new TextRun({ text, font:"Arial", size:32, bold:true, color:LBLUE })]
});
const h3 = (text) => new Paragraph({
  heading: HeadingLevel.HEADING_3,
  spacing: { before:200, after:100 },
  children:[new TextRun({ text, font:"Arial", size:26, bold:true, color:TEAL })]
});
const p = (text, opts={}) => new Paragraph({
  spacing:{ before:80, after:80 },
  children:[new TextRun({ text, font:"Arial", size:opts.size||20, bold:opts.bold||false, color:opts.color||DGRAY, italics:opts.italic||false })]
});
const bullet = (text) => new Paragraph({
  numbering:{ reference:"bullets", level:0 },
  spacing:{ before:40, after:40 },
  children:[new TextRun({ text, font:"Arial", size:20, color:DGRAY })]
});
const codeBlock = (text) => new Paragraph({
  spacing:{ before:60, after:60 },
  shading:{ fill:"1E1E1E", type:ShadingType.CLEAR },
  border:{ bottom:border("333333") },
  children:[new TextRun({ text, font:"Courier New", size:18, color:"00FF7F" })]
});
const pb = () => new Paragraph({ children:[new PageBreak()] });
const spacer = (n=1) => [...Array(n)].map(()=>new Paragraph({ spacing:{before:60,after:60}, children:[new TextRun("")] }));

const twoColTable = (rows, colWidths=[4680,4680]) => new Table({
  width:{ size:9360, type:WidthType.DXA },
  columnWidths: colWidths,
  rows: rows.map(([a,b], i) => new TableRow({ children:[
    cell(a, { width:colWidths[0], shade: i===0?BLUE:undefined, bold:i===0, color:i===0?WHITE:undefined }),
    cell(b, { width:colWidths[1], shade: i===0?BLUE:undefined, bold:i===0, color:i===0?WHITE:undefined })
  ]}))
});

const threeColTable = (rows) => new Table({
  width:{ size:9360, type:WidthType.DXA },
  columnWidths:[3120,3120,3120],
  rows: rows.map(([a,b,c], i) => new TableRow({ children:[
    cell(a, { width:3120, shade:i===0?BLUE:i%2===0?GRAY:undefined, bold:i===0, color:i===0?WHITE:undefined }),
    cell(b, { width:3120, shade:i===0?BLUE:i%2===0?GRAY:undefined, bold:i===0, color:i===0?WHITE:undefined }),
    cell(c, { width:3120, shade:i===0?BLUE:i%2===0?GRAY:undefined, bold:i===0, color:i===0?WHITE:undefined })
  ]}))
});

const doc = new Document({
  numbering:{
    config:[{ reference:"bullets",
      levels:[{ level:0, format:LevelFormat.BULLET, text:"•", alignment:AlignmentType.LEFT,
        style:{ paragraph:{ indent:{ left:720, hanging:360 } } } }] }]
  },
  styles:{
    default:{ document:{ run:{ font:"Arial", size:20 } } },
    paragraphStyles:[
      { id:"Heading1", name:"Heading 1", basedOn:"Normal", next:"Normal", quickFormat:true,
        run:{ size:40, bold:true, font:"Arial", color:BLUE },
        paragraph:{ spacing:{ before:360, after:180 }, outlineLevel:0 } },
      { id:"Heading2", name:"Heading 2", basedOn:"Normal", next:"Normal", quickFormat:true,
        run:{ size:32, bold:true, font:"Arial", color:LBLUE },
        paragraph:{ spacing:{ before:280, after:120 }, outlineLevel:1 } },
      { id:"Heading3", name:"Heading 3", basedOn:"Normal", next:"Normal", quickFormat:true,
        run:{ size:26, bold:true, font:"Arial", color:TEAL },
        paragraph:{ spacing:{ before:200, after:100 }, outlineLevel:2 } },
    ]
  },
  sections:[{
    properties:{
      page:{
        size:{ width:12240, height:15840 },
        margin:{ top:1080, right:1080, bottom:1080, left:1080 }
      }
    },
    headers:{ default: new Header({ children:[
      new Paragraph({ border:{ bottom:{ style:BorderStyle.SINGLE, size:6, color:LBLUE } },
        alignment:AlignmentType.RIGHT,
        children:[new TextRun({ text:"KYC Verification Platform — Technical Documentation", font:"Arial", size:18, color:DGRAY })]
      })
    ]})},
    footers:{ default: new Footer({ children:[
      new Paragraph({ border:{ top:{ style:BorderStyle.SINGLE, size:4, color:LGRAY } },
        alignment:AlignmentType.CENTER,
        children:[
          new TextRun({ text:"© 2024 KYC Platform | Page ", font:"Arial", size:16, color:DGRAY }),
          new TextRun({ children:[PageNumber.CURRENT], font:"Arial", size:16, color:DGRAY }),
          new TextRun({ text:" of ", font:"Arial", size:16, color:DGRAY }),
          new TextRun({ children:[PageNumber.TOTAL_PAGES], font:"Arial", size:16, color:DGRAY }),
        ]
      })
    ]})},
    children:[

      // ═══════════════════════════════════════════════════════════
      // COVER PAGE
      // ═══════════════════════════════════════════════════════════
      ...spacer(4),
      new Paragraph({ alignment:AlignmentType.CENTER, spacing:{before:0,after:200},
        children:[new TextRun({ text:"🔐", font:"Arial", size:80 })] }),
      new Paragraph({ alignment:AlignmentType.CENTER, spacing:{before:0,after:120},
        children:[new TextRun({ text:"KYC VERIFICATION PLATFORM", font:"Arial", size:72, bold:true, color:BLUE })] }),
      new Paragraph({ alignment:AlignmentType.CENTER, spacing:{before:0,after:80},
        children:[new TextRun({ text:"Production-Grade Microservices Architecture", font:"Arial", size:36, color:LBLUE, italics:true })] }),
      new Paragraph({ alignment:AlignmentType.CENTER, spacing:{before:0,after:80},
        children:[new TextRun({ text:"Built with Java 17 · Spring Boot 3.2 · Spring Cloud · Kafka · MySQL · Docker · Kubernetes", font:"Arial", size:22, color:DGRAY })] }),
      ...spacer(2),
      new Table({ width:{size:9360,type:WidthType.DXA}, columnWidths:[4680,4680],
        rows:[new TableRow({ children:[
          cell("Version: 1.0.0", {width:4680, shade:LBLUE, bold:true, color:WHITE, align:AlignmentType.CENTER}),
          cell("Date: 2024", {width:4680, shade:LBLUE, bold:true, color:WHITE, align:AlignmentType.CENTER})
        ]})]
      }),
      pb(),

      // ═══════════════════════════════════════════════════════════
      // SECTION 1 — PROJECT OVERVIEW
      // ═══════════════════════════════════════════════════════════
      h1("1. Project Overview"),
      p("The KYC Verification Platform is a production-grade, cloud-native application that simulates India's DigiLocker/CKYC infrastructure. It enables secure user registration, document submission, identity verification, and administrative monitoring through a distributed microservices architecture."),
      ...spacer(1),
      h2("1.1 Technology Stack"),
      threeColTable([
        ["Layer", "Technology", "Version"],
        ["Backend Framework", "Spring Boot", "3.2.0"],
        ["Language", "Java", "17 (LTS)"],
        ["Service Discovery", "Spring Cloud Eureka", "2023.0.0"],
        ["API Gateway", "Spring Cloud Gateway", "2023.0.0"],
        ["Messaging", "Apache Kafka", "3.5+"],
        ["ORM", "Spring Data JPA / Hibernate", "6.x"],
        ["Database", "MySQL", "8.0"],
        ["Security", "Spring Security + JWT", "6.x"],
        ["API Docs", "SpringDoc OpenAPI (Swagger)", "2.3.0"],
        ["Containerisation", "Docker + Docker Compose", "24.x"],
        ["Orchestration", "Kubernetes (K8s)", "1.28+"],
        ["CI/CD", "GitHub Actions", "Latest"],
        ["Build Tool", "Apache Maven", "3.9.x"],
        ["Testing", "JUnit 5 + Mockito", "5.x"],
      ]),
      ...spacer(1),
      h2("1.2 Microservices Architecture"),
      twoColTable([
        ["Service", "Responsibility & Port"],
        ["eureka-server (8761)", "Service registry — all services self-register here on startup"],
        ["api-gateway (8080)", "Single entry point; JWT validation, routing, load balancing"],
        ["auth-service (8081)", "Register, login, JWT issuance, API key generation, audit logging"],
        ["user-service (8082)", "User profile CRUD — reads same DB schema as auth-service"],
        ["kyc-service (8083)", "KYC document submission, status management, Kafka publishing"],
        ["admin-service (8084)", "Admin dashboard, user monitoring, KYC oversight"],
        ["notification-service (8085)", "Kafka consumer → email notifications on KYC events"],
      ]),
      pb(),

      // ═══════════════════════════════════════════════════════════
      // SECTION 2 — DATABASE SCHEMA
      // ═══════════════════════════════════════════════════════════
      h1("2. Database Schema & Storage"),
      h2("2.1 Databases"),
      twoColTable([
        ["Database", "Owner / Purpose"],
        ["kyc_auth_db", "auth-service, user-service, admin-service — users, roles, credentials, audit"],
        ["kyc_db", "kyc-service — KYC documents and verification records"],
      ]),
      ...spacer(1),
      h2("2.2 Table: users"),
      threeColTable([
        ["Column", "Type", "Description"],
        ["id", "BIGINT PK AI", "Primary key"],
        ["email", "VARCHAR(255) UNIQUE", "User's unique email / login"],
        ["password", "VARCHAR(255)", "BCrypt-hashed password (cost=12)"],
        ["first_name / last_name", "VARCHAR(100)", "Full name"],
        ["phone", "VARCHAR(20) UNIQUE", "Indian mobile number"],
        ["aadhaar_number", "VARCHAR(20) UNIQUE", "12-digit Aadhaar (masked in API)"],
        ["pan_number", "VARCHAR(15) UNIQUE", "10-char PAN — format ABCDE1234F"],
        ["voter_id_number", "VARCHAR(30) UNIQUE", "Voter ID card number"],
        ["date_of_birth", "VARCHAR(20)", "DOB in YYYY-MM-DD"],
        ["gender", "VARCHAR(20)", "MALE / FEMALE / OTHER"],
        ["citizenship", "VARCHAR(100)", "Nationality"],
        ["address / city / state", "TEXT / VARCHAR", "Residential address"],
        ["profession", "VARCHAR(100)", "Occupation"],
        ["status", "ENUM ACTIVE/INACTIVE/SUSPENDED", "Account status"],
        ["created_at / updated_at", "DATETIME", "Auto-managed timestamps"],
      ]),
      ...spacer(1),
      h2("2.3 Table: kyc_details"),
      threeColTable([
        ["Column", "Type", "Description"],
        ["id", "BIGINT PK AI", "KYC record primary key"],
        ["user_id", "BIGINT UNIQUE FK→users", "One KYC per user"],
        ["email", "VARCHAR(255)", "Denormalised for fast lookup"],
        ["full_name / date_of_birth / gender", "VARCHAR", "Personal details"],
        ["aadhaar_number / pan_number / voter_id_number", "VARCHAR", "Identity documents"],
        ["passport_number / driving_license_number", "VARCHAR", "Optional travel docs"],
        ["permanent_address / current_address", "TEXT", "Address details"],
        ["pin_code / district / state / country", "VARCHAR", "Geographic details"],
        ["bank_account_number / ifsc_code / bank_name", "VARCHAR", "Financial info"],
        ["aadhaar_verified / pan_verified / address_verified", "BOOLEAN DEFAULT FALSE", "Verification flags"],
        ["status", "ENUM PENDING/UNDER_REVIEW/VERIFIED/REJECTED/EXPIRED", "KYC lifecycle"],
        ["rejection_reason", "TEXT", "Admin-provided reason on rejection"],
        ["verified_by / verified_at", "BIGINT / DATETIME", "Admin ID and timestamp"],
        ["created_at / updated_at", "DATETIME", "Auto-managed timestamps"],
      ]),
      ...spacer(1),
      h2("2.4 Table: api_credentials"),
      threeColTable([
        ["Column", "Type", "Description"],
        ["id", "BIGINT PK AI", "Primary key"],
        ["user_id", "BIGINT UNIQUE", "One credential set per user"],
        ["api_key", "VARCHAR(100) UNIQUE", "kyc_ prefix + 32 random bytes (Base64URL)"],
        ["app_id", "VARCHAR(60) UNIQUE", "app_ prefix + 12 random bytes"],
        ["active", "BOOLEAN DEFAULT TRUE", "Revocation flag"],
        ["created_at / last_used_at", "DATETIME", "Lifecycle tracking"],
      ]),
      ...spacer(1),
      h2("2.5 Where Does Data Live?"),
      p("All persistent data lives in MySQL. When running locally, MySQL runs in Docker. In production (Kubernetes), a PersistentVolumeClaim (PVC) backed by cloud storage (AWS EBS, GCP PD, Azure Disk) is provisioned. For cloud-managed MySQL, use AWS RDS MySQL 8.0 or Google Cloud SQL. Kafka offsets and events are stored in Kafka's own topic-partition logs on the Zookeeper-backed cluster.", { size:20 }),
      pb(),

      // ═══════════════════════════════════════════════════════════
      // SECTION 3 — API REFERENCE
      // ═══════════════════════════════════════════════════════════
      h1("3. REST API Reference"),
      h2("3.1 Authentication APIs — /auth"),
      threeColTable([
        ["Method + Endpoint", "Auth Required", "Description"],
        ["POST /auth/register", "None", "Register new user — returns JWT + API key"],
        ["POST /auth/login", "None", "Login — returns JWT + API key"],
        ["POST /auth/validate-api-key", "None (internal)", "Validate X-API-KEY + X-APP-ID"],
        ["GET /auth/health", "None", "Service health check"],
      ]),
      ...spacer(1),
      p("Register Request Body Example:", {bold:true}),
      codeBlock(`POST /auth/register
Content-Type: application/json

{
  "email": "priya.sharma@example.com",
  "password": "SecurePass@123",
  "firstName": "Priya",
  "lastName": "Sharma",
  "phone": "9876543210",
  "panNumber": "ABCDE1234F",
  "aadhaarNumber": "2345 6789 1234",
  "dateOfBirth": "1990-05-15",
  "gender": "FEMALE",
  "citizenship": "Indian",
  "address": "123 MG Road",
  "city": "Bengaluru",
  "state": "Karnataka",
  "profession": "Software Engineer"
}`),
      p("Register Response (201 Created):", {bold:true}),
      codeBlock(`{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "apiKey": "kyc_abc123XYZ...",
    "appId": "app_def456...",
    "user": { "id": 1, "email": "priya.sharma@example.com", "role": "ROLE_USER" }
  },
  "timestamp": "2024-01-15T10:30:00"
}`),
      ...spacer(1),
      h2("3.2 User APIs — /users"),
      threeColTable([
        ["Method + Endpoint", "Auth Required", "Description"],
        ["GET /users/{id}", "JWT Bearer", "Get user profile by ID (masked Aadhaar)"],
        ["GET /users/me", "JWT Bearer", "Get own profile"],
        ["PUT /users/{id}", "JWT Bearer", "Update own profile (self only)"],
        ["DELETE /users/{id}", "JWT Bearer", "Soft-delete (sets status=INACTIVE)"],
      ]),
      ...spacer(1),
      h2("3.3 KYC APIs — /kyc"),
      threeColTable([
        ["Method + Endpoint", "Auth Required", "Description"],
        ["POST /kyc", "JWT Bearer", "Submit KYC — one per user"],
        ["GET /kyc/{userId}", "JWT Bearer", "Fetch KYC by user ID"],
        ["PUT /kyc/{userId}", "JWT Bearer", "Update KYC (blocked if VERIFIED)"],
        ["PUT /kyc/{userId}/status", "ADMIN / KYC_OFFICER", "Change KYC status"],
        ["GET /kyc?status=PENDING", "ADMIN", "List all KYC, optionally filtered by status"],
      ]),
      ...spacer(1),
      h2("3.4 Admin APIs — /admin"),
      threeColTable([
        ["Method + Endpoint", "Auth Required", "Description"],
        ["GET /admin/dashboard", "JWT + ADMIN role", "Platform statistics dashboard"],
        ["GET /admin/users?status=ACTIVE", "JWT + ADMIN role", "All users, filter by status"],
        ["GET /admin/users/{id}", "JWT + ADMIN role", "Single user with KYC status"],
        ["PUT /admin/users/{id}/suspend", "JWT + ADMIN role", "Suspend user account"],
        ["PUT /admin/users/{id}/reactivate", "JWT + ADMIN role", "Reactivate user account"],
        ["GET /admin/kyc?status=PENDING", "JWT + ADMIN role", "All KYC records, filterable"],
      ]),
      pb(),

      // ═══════════════════════════════════════════════════════════
      // SECTION 4 — HOW TO BUILD & RUN
      // ═══════════════════════════════════════════════════════════
      h1("4. How to Build, Run & Deploy"),
      h2("4.1 Prerequisites"),
      threeColTable([
        ["Tool", "Version", "Purpose"],
        ["Java JDK", "17 (Temurin/Corretto)", "Compile and run Spring Boot"],
        ["Maven", "3.9+", "Build tool"],
        ["Docker", "24+", "Containerisation"],
        ["Docker Compose", "2.x", "Local stack orchestration"],
        ["kubectl", "1.28+", "Kubernetes CLI"],
        ["Git", "2.x", "Version control"],
        ["MySQL (optional)", "8.0", "If running without Docker"],
      ]),
      ...spacer(1),
      h2("4.2 Local Development — Docker Compose (Recommended)"),
      p("Step 1 — Clone the repository:", {bold:true}),
      codeBlock("git clone https://github.com/yourorg/kyc-platform.git\ncd kyc-platform"),
      p("Step 2 — Start the full stack with one command:", {bold:true}),
      codeBlock("docker-compose up --build -d"),
      p("Step 3 — Verify all services are healthy:", {bold:true}),
      codeBlock("docker-compose ps\ndocker-compose logs -f auth-service"),
      p("Step 4 — Access the platform:", {bold:true}),
      twoColTable([
        ["Service / URL", "Description"],
        ["http://localhost:8080", "API Gateway — all requests go here"],
        ["http://localhost:8761", "Eureka Dashboard — service registry"],
        ["http://localhost:8081/swagger-ui.html", "Auth Service Swagger UI"],
        ["http://localhost:8083/swagger-ui.html", "KYC Service Swagger UI"],
        ["http://localhost:9090", "Kafka UI — topic and message browser"],
      ]),
      ...spacer(1),
      h2("4.3 Running Services Individually (IDE / CLI)"),
      p("Start each service in order:", {bold:true}),
      codeBlock(`# 1. Start infrastructure first
docker-compose up mysql kafka zookeeper -d

# 2. Start Eureka server
cd eureka-server && mvn spring-boot:run

# 3. Start API Gateway
cd ../api-gateway && mvn spring-boot:run

# 4. Start microservices (each in separate terminal)
cd ../auth-service         && mvn spring-boot:run
cd ../user-service         && mvn spring-boot:run
cd ../kyc-service          && mvn spring-boot:run
cd ../admin-service        && mvn spring-boot:run
cd ../notification-service && mvn spring-boot:run`),
      ...spacer(1),
      h2("4.4 Building Docker Images Manually"),
      codeBlock(`# Build all images from root of project
for svc in eureka-server api-gateway auth-service user-service kyc-service admin-service notification-service; do
  echo "Building $svc..."
  (cd $svc && docker build -t kyc/$svc:latest .)
done`),
      ...spacer(1),
      h2("4.5 Kubernetes Deployment"),
      p("Step 1 — Ensure cluster is running (Minikube for local or cloud cluster):", {bold:true}),
      codeBlock(`# Minikube (local)
minikube start --memory=8192 --cpus=4
eval $(minikube docker-env)   # Point Docker to Minikube's daemon`),
      p("Step 2 — Build images inside Minikube:", {bold:true}),
      codeBlock(`for svc in eureka-server api-gateway auth-service user-service kyc-service admin-service notification-service; do
  (cd $svc && docker build -t kyc/$svc:latest .)
done`),
      p("Step 3 — Apply all Kubernetes manifests:", {bold:true}),
      codeBlock(`kubectl apply -f k8s/00-namespace-config.yaml
kubectl apply -f k8s/01-mysql.yaml
kubectl apply -f k8s/02-kafka.yaml

# Wait for infra to be ready
kubectl wait --for=condition=ready pod -l app=mysql -n kyc-platform --timeout=120s
kubectl wait --for=condition=ready pod -l app=kafka -n kyc-platform --timeout=120s

kubectl apply -f k8s/03-microservices.yaml
kubectl apply -f k8s/04-hpa.yaml`),
      p("Step 4 — Access the gateway:", {bold:true}),
      codeBlock(`kubectl get svc api-gateway -n kyc-platform
# For Minikube:
minikube service api-gateway -n kyc-platform --url`),
      pb(),

      // ═══════════════════════════════════════════════════════════
      // SECTION 5 — KAFKA ARCHITECTURE
      // ═══════════════════════════════════════════════════════════
      h1("5. Kafka Event Architecture"),
      h2("5.1 Event Flow"),
      twoColTable([
        ["Step", "Description"],
        ["1. User submits / updates KYC", "kyc-service processes the request and saves to DB"],
        ["2. KycEventProducer.publishKycEvent()", "Serialises KycEvent to JSON and sends to topic kyc-events"],
        ["3. Kafka broker stores the record", "Key = 'user-{userId}' ensuring ordered processing per user"],
        ["4. KycEventConsumer (notification-service)", "Reads from kyc-events with group ID notification-service-group"],
        ["5. NotificationService.processKycEvent()", "Routes event to correct email template and sends notification"],
      ]),
      ...spacer(1),
      h2("5.2 KYC Event Types"),
      twoColTable([
        ["Event Type", "Trigger"],
        ["KYC_CREATED", "First KYC submission by user"],
        ["KYC_UPDATED", "User updates their KYC details"],
        ["KYC_STATUS_CHANGED", "Admin/Officer changes status to VERIFIED or REJECTED"],
      ]),
      ...spacer(1),
      h2("5.3 Kafka Topic Configuration"),
      codeBlock(`Topic: kyc-events
Partitions: 3         # Scale consumers up to 3 parallel instances
Replication Factor: 1 # Set to 3 in production
Retention: 7 days     # Default
Key: user-{userId}    # Ensures ordering per user
Value: JSON (KycEvent)`),
      ...spacer(1),
      h2("5.4 Monitoring Kafka"),
      p("Access Kafka UI at http://localhost:9090 to browse topics, view consumer group lag, replay messages, and inspect partition distribution. In production, use Confluent Control Center or AWS MSK Console."),
      pb(),

      // ═══════════════════════════════════════════════════════════
      // SECTION 6 — SECURITY ARCHITECTURE
      // ═══════════════════════════════════════════════════════════
      h1("6. Security Architecture"),
      h2("6.1 Dual Authentication"),
      p("Every request to a protected endpoint must carry one of:", {bold:true}),
      bullet("JWT Bearer Token — Authorization: Bearer eyJhbGci... (24-hour expiry)"),
      bullet("API Key + App ID — X-API-KEY: kyc_... + X-APP-ID: app_... (long-lived, revocable)"),
      ...spacer(1),
      h2("6.2 JWT Token Flow"),
      twoColTable([
        ["Step", "Detail"],
        ["1. User registers or logs in", "auth-service validates credentials"],
        ["2. JWT generated", "HS256 signed, claims: sub=email, roles=[...], userId=N, exp=+24h"],
        ["3. Token returned", "Client stores token (localStorage or secure cookie)"],
        ["4. Request to Gateway", "Authorization: Bearer {token} header"],
        ["5. Gateway validates JWT", "Extracts email, sets X-User-Email header, routes downstream"],
        ["6. Downstream services", "Re-validate JWT signature using same shared secret"],
      ]),
      ...spacer(1),
      h2("6.3 Password Security"),
      bullet("BCrypt with cost factor 12 — computationally expensive to brute-force"),
      bullet("Passwords never logged or returned in API responses"),
      bullet("Minimum 8 characters enforced at validation layer"),
      ...spacer(1),
      h2("6.4 Data Masking"),
      bullet("Aadhaar number masked: XXXX-XXXX-1234 (last 4 digits visible)"),
      bullet("Bank account numbers never returned in user-facing responses"),
      bullet("Passwords always excluded from all response DTOs"),
      pb(),

      // ═══════════════════════════════════════════════════════════
      // SECTION 7 — MONITORING & OBSERVABILITY
      // ═══════════════════════════════════════════════════════════
      h1("7. Monitoring & Managing Incoming Data"),
      h2("7.1 Spring Boot Actuator Endpoints"),
      twoColTable([
        ["Endpoint", "Description"],
        ["GET /actuator/health", "Service health — UP/DOWN/DEGRADED with component details"],
        ["GET /actuator/info", "Application metadata"],
        ["GET /actuator/metrics", "JVM, HTTP, DB pool, Kafka metrics"],
        ["GET /actuator/prometheus", "Prometheus-compatible scrape endpoint (add dependency)"],
      ]),
      ...spacer(1),
      h2("7.2 Recommended Monitoring Stack"),
      twoColTable([
        ["Tool", "Role"],
        ["Prometheus", "Scrape /actuator/prometheus from each service every 15s"],
        ["Grafana", "Dashboard: request rate, error rate, latency, JVM heap, DB pool"],
        ["ELK Stack (Elasticsearch + Logstash + Kibana)", "Centralised log aggregation and search"],
        ["Kafka UI (provectuslabs)", "Monitor topics, consumer lag, message replay"],
        ["Eureka Dashboard (:8761)", "Service registry health at a glance"],
        ["K8s Dashboard / Lens", "Pod CPU/memory, autoscaler events, deployment status"],
      ]),
      ...spacer(1),
      h2("7.3 Admin Dashboard API"),
      p("The GET /admin/dashboard endpoint provides real-time platform statistics:"),
      codeBlock(`GET /admin/dashboard
Authorization: Bearer {admin_jwt_token}

Response:
{
  "totalUsers": 1247,
  "activeUsers": 1198,
  "inactiveUsers": 49,
  "totalKyc": 1103,
  "pendingKyc": 87,
  "verifiedKyc": 956,
  "rejectedKyc": 60,
  "generatedAt": "2024-01-15T14:30:00"
}`),
      ...spacer(1),
      h2("7.4 Audit Logs"),
      p("Every auth action (register, login, failed login) is recorded in the audit_logs table with: userId, action, resource, IP address, status (SUCCESS/FAILURE), and timestamp. Query via direct DB access or expose via admin API."),
      pb(),

      // ═══════════════════════════════════════════════════════════
      // SECTION 8 — CI/CD PIPELINE
      // ═══════════════════════════════════════════════════════════
      h1("8. CI/CD Pipeline"),
      h2("8.1 GitHub Actions Pipeline Stages"),
      twoColTable([
        ["Stage", "Description"],
        ["1. Test (on every push/PR)", "Runs JUnit tests for auth, user, kyc services against in-memory/test MySQL"],
        ["2. Security Scan", "Trivy scans for CRITICAL/HIGH CVEs in code and dependencies"],
        ["3. Build & Push (main branch only)", "Maven builds JARs, Docker builds images, pushes to GitHub Container Registry"],
        ["4. Deploy to Kubernetes", "Updates image tags via kubectl set image, applies k8s/ manifests, waits for rollout"],
      ]),
      ...spacer(1),
      h2("8.2 Required GitHub Secrets"),
      twoColTable([
        ["Secret Name", "Value"],
        ["KUBE_CONFIG", "Base64-encoded ~/.kube/config of target cluster"],
        ["GITHUB_TOKEN", "Auto-provided by GitHub Actions for registry push"],
      ]),
      pb(),

      // ═══════════════════════════════════════════════════════════
      // SECTION 9 — CLOUD DEPLOYMENT
      // ═══════════════════════════════════════════════════════════
      h1("9. Cloud & Web Deployment Options"),
      h2("9.1 Why Not Vercel?"),
      p("Vercel is designed for static sites and serverless JavaScript functions. This platform uses Spring Boot (JVM, long-running processes) with MySQL, Kafka, and Kubernetes — none of which are supported by Vercel. Use the following cloud options instead."),
      ...spacer(1),
      h2("9.2 Recommended Cloud Deployment"),
      threeColTable([
        ["Platform", "Services Used", "Best For"],
        ["AWS", "EKS + RDS MySQL + MSK (Kafka) + ECR", "Enterprise production, full control"],
        ["Google Cloud", "GKE + Cloud SQL + Pub/Sub (or MSK) + Artifact Registry", "GCP-native teams"],
        ["Azure", "AKS + Azure Database for MySQL + Azure Event Hubs + ACR", "Microsoft shops"],
        ["DigitalOcean", "DOKS + Managed MySQL + Managed Kafka", "Cost-effective startups"],
        ["Railway.app", "Docker-based deployment + MySQL plugin", "Quick demos, no K8s needed"],
        ["Render.com", "Web Services + MySQL add-on", "Alternative to Railway"],
      ]),
      ...spacer(1),
      h2("9.3 AWS Deployment Steps (High Level)"),
      bullet("Create EKS cluster: eksctl create cluster --name kyc-cluster --region ap-south-1 --nodegroup-name standard-workers --node-type t3.medium --nodes 3"),
      bullet("Create RDS MySQL 8.0 instance in same VPC as EKS, update DB_HOST secret in K8s"),
      bullet("Create MSK (Managed Streaming for Kafka) cluster, update KAFKA_SERVERS config"),
      bullet("Push Docker images to ECR: aws ecr create-repository --repository-name kyc/auth-service"),
      bullet("Apply k8s/ manifests: kubectl apply -f k8s/"),
      bullet("Create LoadBalancer for api-gateway, configure Route 53 DNS and ACM SSL certificate"),
      pb(),

      // ═══════════════════════════════════════════════════════════
      // SECTION 10 — INTERVIEW Q&A
      // ═══════════════════════════════════════════════════════════
      h1("10. Interview Questions & Detailed Answers"),
      p("These 50 questions cover every layer of this project — from architecture decisions to code-level specifics. They map to real interview rounds at product companies and MNCs hiring Java/Spring Boot engineers.", {italic:true}),
      ...spacer(1),

      // ── Architecture ──
      h2("10.1 Architecture & Design Questions"),

      h3("Q1. Why did you choose a microservices architecture for this KYC platform?"),
      p("Microservices allow each service to be deployed, scaled, and updated independently. In a KYC platform:"),
      bullet("Auth spikes during business hours — scale auth-service independently"),
      bullet("KYC verification is compute-intensive — scale kyc-service without touching auth"),
      bullet("Notification is I/O-bound — can be scaled asynchronously via Kafka consumers"),
      bullet("Admin service is low traffic — keep it at 1 replica to save cost"),
      bullet("Each service can use the database schema it needs — auth/user share a DB, kyc has its own"),
      p("Trade-off acknowledged: increased operational complexity (service discovery, distributed tracing, eventual consistency) vs monolith simplicity. Microservices justified here because of independent scaling needs, multiple team ownership, and compliance isolation requirements.", {italic:true}),

      h3("Q2. Explain the role of Eureka and the API Gateway."),
      p("Eureka Server is the service registry. When each service starts, it calls POST /eureka/apps/{serviceName} to register itself with its IP, port, and health URL. Eureka stores this in-memory. Every 30 seconds each service sends a heartbeat; if missed for 90 seconds, the instance is evicted."),
      p("The API Gateway (Spring Cloud Gateway) acts as the single entry point. It:"),
      bullet("Resolves service URLs via Eureka (lb://auth-service routes to a live auth-service instance)"),
      bullet("Performs JWT validation before forwarding requests (AuthenticationFilter)"),
      bullet("Adds X-User-Email header so downstream services know who the caller is without re-parsing JWT"),
      bullet("Handles CORS, rate limiting, and circuit breaking"),

      h3("Q3. How does your authentication work end-to-end?"),
      p("Two mechanisms are supported:"),
      bullet("JWT (stateless): User logs in → auth-service validates password, generates HS256-signed JWT with claims {sub: email, roles: [...], userId: N, exp: +24h} → client sends Authorization: Bearer {token} → Gateway validates signature → forwards with X-User-Email header → downstream services extract identity without DB hit"),
      bullet("API Key + App ID: Generated at registration, stored in api_credentials table. Client sends X-API-KEY + X-APP-ID headers → Gateway forwards downstream → service calls /auth/validate-api-key → auth-service looks up DB, updates last_used_at → returns user context"),
      p("Why both? JWT is ideal for web browsers (short-lived, no DB). API keys are ideal for server-to-server integrations (long-lived, revocable)."),

      h3("Q4. Why do you use Kafka instead of direct HTTP calls for notifications?"),
      p("Synchronous HTTP for notifications creates tight coupling and failure propagation:"),
      bullet("If notification-service is down, KYC submission fails — unacceptable for user experience"),
      bullet("If email provider is slow, KYC API latency increases"),
      bullet("With Kafka: kyc-service publishes event and returns 200 immediately. notification-service consumes at its own pace, retries failed deliveries, and can replay events from any offset"),
      bullet("Kafka also enables future consumers (SMS service, CRM system, fraud detection) with zero code changes to kyc-service"),
      bullet("Using partition key = user-{userId} ensures ordered processing per user"),

      h3("Q5. How did you handle the N+1 query problem in the admin service?"),
      p("The admin-service getAllUsers() joins users with kyc_details in a single SQL query using LEFT JOIN, rather than loading all users and then fetching KYC for each. This avoids O(N) queries. For Spring Data JPA, we use @EntityGraph or JOIN FETCH to eagerly load associations. The admin service also uses JdbcTemplate directly for complex reporting queries where JPA adds overhead."),

      h2("10.2 Spring Boot & Java Questions"),

      h3("Q6. Explain Spring Boot auto-configuration."),
      p("@SpringBootApplication includes @EnableAutoConfiguration which triggers AutoConfigurationImportSelector. It reads spring.factories (pre-3.x) or AutoConfiguration.imports (3.x) files from all JARs on the classpath. These list @Configuration classes that Spring conditionally activates using @ConditionalOnClass, @ConditionalOnMissingBean, @ConditionalOnProperty etc. For example, spring-boot-autoconfigure's DataSourceAutoConfiguration activates only if a DataSource class is on the classpath AND no DataSource bean exists yet."),

      h3("Q7. What is Spring Security's filter chain and where does JWT validation fit?"),
      p("Spring Security inserts a DelegatingFilterProxy into the servlet container's filter chain. This delegates to a FilterChainProxy which holds an ordered list of SecurityFilterChain beans. Our JwtAuthenticationFilter extends OncePerRequestFilter and is inserted before UsernamePasswordAuthenticationFilter using addFilterBefore(). It extracts the Bearer token, validates signature and expiry using jjwt, loads UserDetails, and sets a UsernamePasswordAuthenticationToken in SecurityContextHolder. If the token is invalid, the filter logs a warning and continues the chain — the request then fails authorization at the anyRequest().authenticated() rule."),

      h3("Q8. What is the difference between @Transactional(readOnly=true) and regular @Transactional?"),
      p("readOnly=true is a hint to the JPA provider (Hibernate) and JDBC driver. Hibernate skips dirty checking (no need to track entity state changes), which saves CPU. MySQL driver can route the query to a replica if read-replica routing is configured. Spring also skips flushing the session. We use readOnly=true on all GET service methods (getUserById, getKycByUserId) and omit it or use readOnly=false on write methods. Forgetting readOnly on a write operation causes no functional error but wastes a dirty-check on every entity."),

      h3("Q9. How does BCrypt password hashing work and why cost factor 12?"),
      p("BCrypt incorporates a salt (16 random bytes generated per password) into the hash output, making rainbow table attacks impossible. The cost factor (work factor) controls iterations: cost 12 means 2^12 = 4096 rounds of key derivation, taking ~200-400ms on modern hardware. This makes brute-force attacks computationally expensive. We chose 12 as a balance: strong enough to resist GPU attacks, fast enough that login doesn't time out. In production, benchmark annually and increase as hardware improves."),

      h3("Q10. Explain JPA entity relationships in your schema."),
      p("User ↔ Role: @ManyToMany with a join table user_roles. FetchType.EAGER because roles are always needed for authorization. User ↔ ApiCredential: @OneToOne with CascadeType.ALL so credentials are created/deleted with the user. KycDetail is in a separate service/DB, so it references userId as a plain Long — no JPA relationship across service boundaries. This is a key microservices pattern: each service is the single source of truth for its domain."),

      h2("10.3 Database & JPA Questions"),

      h3("Q11. What is the ddl-auto: update vs validate vs none and when to use each?"),
      twoColTable([
        ["ddl-auto value", "When to use"],
        ["create", "Tests only — drops and recreates schema on every start"],
        ["create-drop", "Embedded tests — drops schema on application shutdown"],
        ["update", "Development — adds new columns/tables but never drops"],
        ["validate", "Staging/production — verifies schema matches entities, fails if not"],
        ["none", "Production with Flyway/Liquibase — JPA never touches schema"],
      ]),
      p("We use update in auth-service (owns the schema) and none in user-service/admin-service (read-only consumers of the same schema). In a real production system, replace update with Flyway migrations."),

      h3("Q12. How would you implement database migrations in production?"),
      p("Replace ddl-auto: update with Flyway. Add spring-boot-starter-flyway dependency. Create src/main/resources/db/migration/V1__init_schema.sql, V2__add_kyc_fields.sql etc. On startup, Flyway reads the flyway_schema_history table and applies pending migrations in version order. This gives: auditability (migrations in Git), rollback capability (write V3__rollback.sql), environment parity (same scripts applied to dev/staging/prod)."),

      h3("Q13. What is connection pooling and how is HikariCP configured?"),
      p("Instead of creating a new TCP connection for each DB operation (slow: ~100ms handshake), HikariCP maintains a pool of ready connections. Configuration: maximum-pool-size: 10 means maximum 10 simultaneous DB operations. minimum-idle: 5 keeps 5 connections warm. connection-timeout: 30000ms — how long to wait if no connections available before throwing. For a service with 2 replicas, max 20 concurrent DB connections total — ensure MySQL's max_connections > 20 * number_of_service_instances."),

      h2("10.4 Kafka & Messaging Questions"),

      h3("Q14. Explain Kafka consumer groups and partition assignment."),
      p("A consumer group is identified by group.id (ours: notification-service-group). Kafka assigns each partition to exactly one consumer in the group. With 3 partitions and 3 consumers: each consumer owns 1 partition (maximum parallelism). With 3 partitions and 1 consumer: one consumer reads all 3 partitions (no parallelism). Partitions are assigned via the default RangeAssignor or RoundRobinAssignor. If a consumer crashes, Kafka triggers a rebalance and reassigns its partitions to surviving consumers."),

      h3("Q15. What is at-least-once delivery and how do you handle duplicate events?"),
      p("We set enable-auto-commit: false and ack-mode: MANUAL_IMMEDIATE. The consumer commits offset only after processKycEvent() succeeds. If the JVM crashes mid-processing, Kafka re-delivers from the last committed offset. This guarantees at-least-once delivery — the event may be processed twice. To handle duplicates: notifications are idempotent (sending the same email twice is acceptable) OR implement a processed_events table keyed on kycId+eventType to deduplicate."),

      h2("10.5 Docker & Kubernetes Questions"),

      h3("Q16. Explain the multi-stage Dockerfile and why it reduces image size."),
      p("Stage 1 (maven:3.9.5-eclipse-temurin-17): Contains Maven, JDK, and all build tools (~600MB). It compiles and packages the JAR. Stage 2 (eclipse-temurin:17-jre-alpine): Only contains the JRE on Alpine Linux (~180MB). We COPY only the JAR from Stage 1. The final image is ~200MB vs ~800MB for a single-stage build. This also means build tools and source code are NOT in the production image — improved security."),

      h3("Q17. How does Kubernetes health checking work in this project?"),
      p("Each service exposes GET /actuator/health (Spring Boot Actuator). Kubernetes uses:"),
      bullet("readinessProbe: Checked every 10s. If DOWN, K8s stops sending traffic to this pod. initialDelaySeconds: 40-45s to allow Spring Boot startup time."),
      bullet("livenessProbe (add if needed): If DOWN, K8s restarts the pod — use for detecting deadlocks"),
      bullet("startupProbe (add for slow starts): Gives the app a longer window to start before readiness checks begin"),

      h3("Q18. Explain Horizontal Pod Autoscaler in your k8s setup."),
      p("The HPA watches CPU and memory metrics via the Metrics Server. For auth-service: minReplicas=2 (high availability), maxReplicas=6, targetCPU=70%. If average CPU across all auth-service pods exceeds 70%, HPA adds pods (scale-out). If CPU drops, HPA removes pods after a cooldown (scale-in). The HPA controller evaluates every 15 seconds. Formula: desired_replicas = ceil(current_replicas * (current_metric / target_metric))."),

      h2("10.6 Design Patterns & Best Practices"),

      h3("Q19. What design patterns did you use in this project?"),
      twoColTable([
        ["Pattern", "Where Used"],
        ["Repository Pattern", "UserRepository, KycDetailRepository — data access abstraction"],
        ["DTO Pattern", "AuthDtos, UserDtos, KycDtos — decouple API contracts from entities"],
        ["Builder Pattern", "Lombok @Builder on all entities and DTOs"],
        ["Factory Method", "ApiResponse.success() and ApiResponse.error() static factories"],
        ["Chain of Responsibility", "Spring Security filter chain — each filter processes and passes to next"],
        ["Observer / Event-Driven", "Kafka producer-consumer — kyc-service publishes, notification-service subscribes"],
        ["Facade Pattern", "AdminService facades complex multi-table queries behind simple method calls"],
        ["Strategy Pattern", "Spring Security AuthenticationProvider — pluggable auth strategies"],
      ]),

      h3("Q20. How do you handle exceptions consistently across all services?"),
      p("Each service has a @RestControllerAdvice GlobalExceptionHandler with @ExceptionHandler methods for: custom KycException (maps to appropriate HTTP status), MethodArgumentNotValidException (returns field-level validation errors), BadCredentialsException, AccessDeniedException, and a catch-all Exception handler. All responses use the ApiResponse<T> wrapper DTO ensuring consistent JSON: { success, message, data, errorCode, timestamp }. The errorCode field enables frontend to handle specific errors programmatically."),

      h3("Q21. How would you add rate limiting to the API Gateway?"),
      p("Spring Cloud Gateway supports RequestRateLimiter filter using Redis as a token bucket store. Configuration:"),
      codeBlock(`filters:
  - name: RequestRateLimiter
    args:
      redis-rate-limiter.replenishRate: 10    # tokens per second
      redis-rate-limiter.burstCapacity: 20    # max burst
      key-resolver: "#{@ipKeyResolver}"       # rate limit per IP`),
      p("Add a KeyResolver bean to limit by IP, user ID, or API key. Returns 429 Too Many Requests when limit exceeded."),

      h3("Q22. How would you add distributed tracing?"),
      p("Add Spring Boot Micrometer Tracing with Zipkin reporter. Each service gets a trace-id and span-id injected into logs (MDC). The Gateway propagates B3 trace headers (X-B3-TraceId, X-B3-SpanId) to downstream services. Zipkin UI shows the full call graph: Gateway → auth-service → DB, with timing for each span. Alternatively use OpenTelemetry + Jaeger for vendor-neutral tracing."),

      h3("Q23. How is KYC data security ensured (GDPR/data protection)?"),
      bullet("Aadhaar number masked to XXXX-XXXX-1234 in all API responses"),
      bullet("Passwords hashed with BCrypt — never stored plaintext"),
      bullet("API credentials table separate from user table — different access controls"),
      bullet("Audit logging of all auth actions for compliance trail"),
      bullet("HTTPS enforced in production (TLS termination at K8s Ingress)"),
      bullet("Roles-based access: only ADMIN/KYC_OFFICER can change KYC status"),
      bullet("Soft delete (status=INACTIVE) preserves records for legal audit requirements"),
      bullet("In production: Encrypt Aadhaar/PAN at rest using AES-256 (application-level encryption or AWS KMS)"),

      h3("Q24. How would you implement idempotency for KYC submission?"),
      p("The DB has a UNIQUE constraint on kyc_details.user_id — the second POST /kyc for the same user returns HTTP 409 CONFLICT rather than creating a duplicate. For API-level idempotency (payment systems pattern): accept an Idempotency-Key header, store the response against the key in Redis with 24h TTL. Subsequent requests with the same key return the cached response without re-executing business logic."),

      h3("Q25. What would you change for a production-ready release?"),
      bullet("Flyway database migrations instead of ddl-auto: update"),
      bullet("Spring Cloud Config Server for centralised, refreshable configuration"),
      bullet("Separate JWT secret per service (asymmetric RS256 keys from auth-service, public key distributed)"),
      bullet("Spring Cloud Circuit Breaker (Resilience4j) on all inter-service HTTP calls"),
      bullet("Distributed tracing (Zipkin/Jaeger) and centralised logging (ELK)"),
      bullet("Redis for session/token blacklisting and rate limiting"),
      bullet("Health check endpoints beyond /actuator/health — include DB, Kafka, Eureka connectivity"),
      bullet("Secrets management via Vault (HashiCorp) or AWS Secrets Manager — never in ConfigMaps"),
      bullet("End-to-end TLS between services (mTLS using Istio service mesh)"),
      bullet("Contract testing (Pact) between API gateway and downstream services"),
      bullet("Blue-green or canary deployment strategy in CI/CD"),
      pb(),

      // ═══════════════════════════════════════════════════════════
      // SECTION 11 — QUICK REFERENCE
      // ═══════════════════════════════════════════════════════════
      h1("11. Quick Reference & Commands"),
      h2("11.1 Useful Commands"),
      codeBlock(`# View all running services
docker-compose ps

# Check logs
docker-compose logs -f kyc-service

# Scale a service in docker-compose
docker-compose up --scale auth-service=3 -d

# K8s — view all pods
kubectl get pods -n kyc-platform -w

# K8s — view HPA status
kubectl get hpa -n kyc-platform

# K8s — restart a deployment
kubectl rollout restart deployment/auth-service -n kyc-platform

# K8s — shell into a pod
kubectl exec -it deploy/auth-service -n kyc-platform -- sh

# MySQL — connect via docker
docker exec -it kyc-mysql mysql -uroot -proot kyc_auth_db

# Kafka — list topics
docker exec kyc-kafka kafka-topics --bootstrap-server localhost:9092 --list

# Kafka — consume events live
docker exec kyc-kafka kafka-console-consumer \\
  --bootstrap-server localhost:9092 --topic kyc-events --from-beginning`),
      ...spacer(1),
      h2("11.2 Service Port Summary"),
      twoColTable([
        ["Service", "Port"],
        ["API Gateway (main entry point)", "8080"],
        ["Auth Service", "8081"],
        ["User Service", "8082"],
        ["KYC Service", "8083"],
        ["Admin Service", "8084"],
        ["Notification Service", "8085"],
        ["Eureka Server", "8761"],
        ["Kafka UI", "9090"],
        ["Kafka Broker", "9092"],
        ["MySQL", "3306"],
      ]),
      ...spacer(1),
      h2("11.3 Default Credentials"),
      twoColTable([
        ["Account", "Credentials"],
        ["Admin user", "admin@kycplatform.com / Admin@1234"],
        ["MySQL root", "root / root"],
        ["Kafka broker", "No auth (PLAINTEXT in dev)"],
      ]),
      ...spacer(2),
      new Paragraph({ alignment:AlignmentType.CENTER, spacing:{before:240,after:0},
        children:[new TextRun({ text:"— End of Document —", font:"Arial", size:20, italics:true, color:DGRAY })]
      }),
    ]
  }]
});

Packer.toBuffer(doc).then(buffer => {
  fs.writeFileSync('/mnt/user-data/outputs/KYC_Platform_Documentation.docx', buffer);
  console.log('✅ Documentation generated successfully!');
}).catch(err => {
  console.error('Error:', err);
  process.exit(1);
});
