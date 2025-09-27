const baseConfig = require('@eclipse-scout/cli/scripts/webpack-defaults');

module.exports = (env, args) => {
  args.resDirArray = ['src/main/resources/WebContent', 'node_modules/@eclipse-scout/core/res'];
  const config = baseConfig(env, args);

  config.entry = {
    'inventory': './src/main/js/inventory.ts',
    'login': './src/main/js/login.ts',
    'logout': './src/main/js/logout.ts',
    'inventory-theme': './src/main/js/inventory-theme.less',
    'inventory-theme-dark': './src/main/js/inventory-theme-dark.less'
  };

  return config;
};
