# Guia rápido: CI/CD com GitHub Actions, Docker Hub e Render

## 1. Secrets do GitHub (Settings > Secrets and variables > Actions)
| Nome | Valor |
|---|---|
| DOCKERHUB_USERNAME | seu usuário do Docker Hub |
| DOCKERHUB_TOKEN | token (Read & Write) gerado no Docker Hub |
| RENDER_DEPLOY_HOOK | URL do Deploy Hook do serviço no Render |

## 2. Variáveis de ambiente no Render
| Nome | Valor |
|---|---|
| SPRING_MONGODB_URI | connection string do MongoDB Atlas |
| APP_COOKIE_SECURE | true |
| PORT | 8081 |

## 3. Fluxo
push na main -> GitHub Actions (job build) -> imagem no Docker Hub (latest + SHA)
-> job deploy chama o Deploy Hook -> Render baixa a imagem e publica -> URL pública.

## 4. Como testar
- Local: `docker build --platform linux/amd64 -t login-api:teste .`
  e `docker run --rm -p 8081:8081 -e SPRING_MONGODB_URI="..." login-api:teste`
- Aba Actions do GitHub: jobs build e deploy verdes.
- Docker Hub: tags latest e o SHA do commit.
- Render: novo deploy nos Events/Logs.
- URL pública: /swagger-ui/index.html e /build-info.txt (data do build).
