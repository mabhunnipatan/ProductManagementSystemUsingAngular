# ProductFrontend1

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 19.2.15.

## Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Deploying to Render

This project includes a Render Blueprint in `render.yaml` for the Angular SSR frontend and Spring Boot API. Create a Blueprint in the [Render Dashboard](https://dashboard.render.com/blueprints), select this repository, and use the repository root as the blueprint path.

The backend requires an external MySQL database. When Render prompts for `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`, enter the JDBC URL and credentials for a publicly reachable database. Keep the password in Render's environment settings; do not commit it to the repository. After the API service is created, set the frontend's `API_URL` to its products endpoint, such as `https://product-api.onrender.com/api/products`. The local default (`http://localhost:8080/api/products`) is only for development.

## Running unit tests

To execute unit tests with the [Karma](https://karma-runner.github.io) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
