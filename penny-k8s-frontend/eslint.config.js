import js from "@eslint/js";
import globals from "globals";
import vue from "eslint-plugin-vue";
import prettier from "eslint-plugin-prettier";
import vueParser from "vue-eslint-parser";
import { defineConfig } from "eslint/config";

export default defineConfig([
  {
    files: ["**/*.{js}"],
    languageOptions: {
      globals: globals.browser,
      ecmaVersion: 2022,
      sourceType: "module",
    },
    plugins: { prettier },
    rules: {
      "prettier/prettier": "error",
    },
  },
  {
    files: ["**/*.vue"],
    languageOptions: {
      parser: vueParser,
      parserOptions: {
        ecmaVersion: 2022,
        sourceType: "module",
        extraFileExtensions: [".vue"],
      },
      globals: globals.browser,
    },
    plugins: {
      vue,
      prettier,
    },
    rules: {
      ...vue.configs["flat/recommended"].rules,
      "prettier/prettier": "error",
    },
  },
]);
