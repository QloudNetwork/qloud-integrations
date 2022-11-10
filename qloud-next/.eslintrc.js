module.exports = {
    extends: [
        'eslint:recommended',
        'plugin:@typescript-eslint/recommended',
        'plugin:@typescript-eslint/recommended-requiring-type-checking',
        'prettier',
    ],
    parser: '@typescript-eslint/parser',
    parserOptions: {
        tsconfigRootDir: __dirname,
        project: ['./tsconfig.eslint.json'],
    },
    plugins: ['@typescript-eslint'],
    ignorePatterns: [".eslintrc.js", "jest.config.js", "dist/**/*", "example/**/*"],
    root: true,
};
