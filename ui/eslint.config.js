const { defineConfig, globalIgnores } = require("eslint/config");
const nextVitals = require("eslint-config-next/core-web-vitals");

const eslintConfig = defineConfig([
  ...nextVitals,
  globalIgnores([
    ".next/**",
    "out/**",
    "build/**"
  ]),
]);

module.exports = eslintConfig;
