const format = require('./internal/process-spec');

// specify paths we want the codegen to generate for (all methods for the specified path are included)
const paths = [
    "/api/v3/secrets/raw",
    "/api/v3/secrets/raw/{secretName}",
    "/api/v1/auth/universal-auth/login"
];

format(paths)