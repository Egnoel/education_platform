# EduConnect - Plataforma Educacional

EduConnect é uma aplicação frontend desenvolvida em Next.js 14 para integração com um backend Spring Boot, destinada a conectar professores e alunos através de turmas, materiais didáticos, quizzes e avaliações.

## Características

- **Autenticação JWT**: Sistema completo de login e registo
- **Interface em Português (Portugal)**: Toda a UI está em português de Portugal
- **Design Responsivo**: Funciona em dispositivos móveis, tablets e desktops
- **Integração com API**: Preparado para se conectar a um backend Spring Boot
- **Componentes Reutilizáveis**: Utiliza Shadcn/UI para componentes consistentes

## Tecnologias Utilizadas

- **Next.js 14** com App Router
- **TypeScript**
- **Tailwind CSS**
- **Shadcn/UI**
- **React Query** para gestão de estado e chamadas à API
- **React Context** para gestão de autenticação

## Requisitos

- Node.js 18.17 ou superior
- NPM ou Yarn

## Instalação

1. Clone o repositório:
   \`\`\`bash
   git clone https://github.com/seu-usuario/educonnect.git
   cd educonnect
   \`\`\`

2. Instale as dependências:
   \`\`\`bash
   npm install
   # ou
   yarn
   \`\`\`

3. Configure as variáveis de ambiente:
   Crie um arquivo `.env.local` na raiz do projeto com o seguinte conteúdo:
   \`\`\`
   NEXT_PUBLIC_API_URL=http://localhost:8080
   \`\`\`

4. Inicie o servidor de desenvolvimento:
   \`\`\`bash
   npm run dev
   # ou
   yarn dev
   \`\`\`

5. Acesse a aplicação em [http://localhost:3000](http://localhost:3000)

## Estrutura do Projeto

\`\`\`
/app                    # Diretório principal do App Router
  /login                # Página de login
  /register             # Página de registo
  /dashboard            # Painel de controlo
  /classes              # Gestão de turmas
  /materials            # Materiais didáticos
  /quizzes              # Quizzes
  /assessments          # Avaliações
  /profile              # Perfil do utilizador
  /layout.tsx           # Layout global da aplicação
  /page.tsx             # Página inicial (landing page)
/components             # Componentes reutilizáveis
  /ui                   # Componentes Shadcn/UI
  /Navbar.tsx           # Barra de navegação
  /Sidebar.tsx          # Barra lateral
  /AppLayout.tsx        # Layout para páginas autenticadas
  /ProtectedRoute.tsx   # Componente para proteger rotas
/lib                    # Utilitários e funções auxiliares
  /api.ts               # Cliente HTTP para chamadas à API
  /auth.ts              # Funções de autenticação
  /context              # Contextos React
    /AuthContext.tsx    # Contexto de autenticação
    /QueryProvider.tsx  # Provedor do React Query
/hooks                  # Hooks personalizados
  /use-mobile.tsx       # Hook para detetar dispositivos móveis
/public                 # Arquivos estáticos
\`\`\`

## Integração com o Backend

A aplicação está configurada para se conectar a um backend Spring Boot em `http://localhost:8080`. Os endpoints esperados são:

- **Autenticação**:
  - `POST /api/auth/login`: Login com email e password
  - `POST /api/auth/register`: Registo de novos utilizadores

- **Dashboard**:
  - `GET /dashboard`: Dados do painel de controlo

- **Turmas**:
  - `GET /classes`: Listar turmas
  - `POST /classes`: Criar turma
  - `GET /classes/student/:id`: Turmas do aluno
  - `POST /classes/:id/students`: Adicionar alunos à turma
  - `DELETE /classes/:id/students`: Remover alunos da turma

- **Materiais**:
  - `GET /materials`: Listar materiais
  - `POST /materials`: Criar material

- **Quizzes**:
  - `GET /quizzes`: Listar quizzes
  - `POST /quizzes`: Criar quiz

- **Avaliações**:
  - `GET /assessments/student/:id`: Avaliações do aluno
  - `GET /assessments/classe/:id`: Avaliações da turma
  - `POST /assessments`: Criar avaliação

## Personalização

### Cores

As cores principais da aplicação são:
- **Azul escuro** (#1E40AF): Elementos primários como cabeçalhos e botões
- **Azul claro** (#60A5FA): Elementos de destaque e acentos
- **Branco/Cinza claro** (#F9FAFB): Fundos e áreas de conteúdo

Para alterar as cores, edite o arquivo `tailwind.config.ts`.

### Componentes Shadcn/UI

Para adicionar ou personalizar componentes Shadcn/UI:

\`\`\`bash
npx shadcn@latest add [nome-do-componente]
\`\`\`

Os componentes serão adicionados ao diretório `/components/ui`.

## Licença

Este projeto está licenciado sob a licença MIT.
