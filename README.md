# Stockroom

A responsive product catalog built with Angular and Spring Boot. Stockroom provides a simple interface for browsing, adding, editing, and deleting products.

## Live application

- **Frontend:** [https://product-frontend1.onrender.com](https://product-frontend1.onrender.com)
- **Products API:** [https://product-api-xv02.onrender.com/api/products](https://product-api-xv02.onrender.com/api/products)

The backend currently uses an in-memory H2 database so it can run without an external database service. Product data is temporary and is lost when the Render service restarts or sleeps.

## Features

- Responsive product catalog with an empty state
- Add and edit products with required-field validation
- Delete products with confirmation
- Currency-formatted prices
- Angular server-side rendering
- Spring Boot REST API backed by H2

## Technology

- Angular 19
- TypeScript
- Spring Boot 3
- Spring Data JPA
- H2 in-memory database

## Run locally

### Frontend

```bash
npm ci
npm start
```

Open [http://localhost:4200](http://localhost:4200).

### Backend

```bash
cd springbootwithangular
mvn spring-boot:run
```

The API runs on [http://localhost:8080/api/products](http://localhost:8080/api/products). The backend uses the same in-memory H2 configuration locally.

## Deploy on Render

The repository includes a Render Blueprint in [`render.yaml`](./render.yaml) for the Angular SSR frontend and Spring Boot API. Create a Blueprint in the [Render Dashboard](https://dashboard.render.com/blueprints), select this repository, and use the repository root as the blueprint path.

Render deploys the frontend and backend from the `master` branch. The frontend receives the backend URL from the API service automatically.

For persistent product storage, replace the H2 dependency and configuration with a publicly reachable database, then configure its connection details as environment variables in Render. Do not commit database credentials to the repository.
