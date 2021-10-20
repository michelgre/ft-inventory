const baseConfig = require('@eclipse-scout/cli/scripts/webpack-defaults');

module.exports = (env, args) => {
  args.resDirArray = ['src/main/resources/WebContent', 'node_modules/@eclipse-scout/core/res'];
  const config = baseConfig(env, args);

  config.entry = {
    'inventory': './src/main/js/inventory.js',
    'login': './src/main/js/login.js',
    'logout': './src/main/js/logout.js',
    'inventory-theme': './src/main/js/inventory-theme.less',
    'inventory-theme-dark': './src/main/js/inventory-theme-dark.less',
    'inventory-theme-ft': './src/main/js/inventory-theme-ft.less'
  };

  return config;
};
